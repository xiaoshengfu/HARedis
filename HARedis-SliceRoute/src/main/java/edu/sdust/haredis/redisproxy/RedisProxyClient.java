package edu.sdust.haredis.redisproxy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.sdust.haredis.common.Configuration;
import edu.sdust.haredis.common.ExecuteLinuxCommand;
import edu.sdust.haredis.common.JSchSSH;
import edu.sdust.haredis.common.RedisMessageUtils;
import edu.sdust.haredis.heartbeat.HeartBeatServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.handler.codec.redis.RedisMessage;

/**
 * ClassName: RedisClient
 * @Description: Redis代理客户端
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月10日 上午9:20:53
 */
public final class RedisProxyClient {

	//分片路由表
	public static final ConcurrentHashMap<RedisInfo, ConcurrentHashMap<Integer, RedisProxyClient>> routingTable = new ConcurrentHashMap<RedisInfo, ConcurrentHashMap<Integer, RedisProxyClient>>();
	//管道实现queue
	private LinkedBlockingQueue<RedisMessage> bufferQueue = new LinkedBlockingQueue<RedisMessage>();

	//Redis信息
	private RedisInfo redisInfo;
	//连接库号
	private Integer databaseNumber;
	//是否已经连接成功
	private Boolean connected = false;
	//处理Handler
	private RedisProxyClientHandler redisClientHandler = null;
	//Redis任务锁(保证响应顺序)
	public final Lock taskLock = new ReentrantLock();
	//Redis Pipeline锁
	public final Lock pipelineLock = new ReentrantLock();
	//换库锁(防止重复new RedisProxyClient对象)
	public static List<ReentrantLock> selectLockList = null;
	private Channel channel = null;
	private EventLoopGroup group = null;
	private Bootstrap bootstrap = null;

	static {
		selectLockList = new ArrayList<ReentrantLock>(16);
		for (int i = 0; i < 16; i++) {
			selectLockList.add(new ReentrantLock());
		}
	}

	public RedisProxyClient() {
		super();
	}

	public RedisProxyClient(RedisInfo redisInfo, Integer databaseNumber) {
		super();
		this.redisInfo = redisInfo;
		this.databaseNumber = databaseNumber;
	}

	public LinkedBlockingQueue<RedisMessage> getBufferQueue() {
		return bufferQueue;
	}

	public void setBufferQueue(LinkedBlockingQueue<RedisMessage> bufferQueue) {
		this.bufferQueue = bufferQueue;
	}

	public RedisInfo getRedisInfo() {
		return redisInfo;
	}

	public void setRedisInfo(RedisInfo redisInfo) {
		this.redisInfo = redisInfo;
	}

	public Integer getDatabaseNumber() {
		return databaseNumber;
	}

	public void setDatabaseNumber(Integer databaseNumber) {
		this.databaseNumber = databaseNumber;
	}

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public RedisProxyClientHandler getRedisClientHandler() {
		return redisClientHandler;
	}

	public void setRedisClientHandler(RedisProxyClientHandler redisClientHandler) {
		this.redisClientHandler = redisClientHandler;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public EventLoopGroup getGroup() {
		return group;
	}

	public void setGroup(EventLoopGroup group) {
		this.group = group;
	}

	public Bootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	public void addRedisTask(RedisTask redisTask) {
		redisClientHandler.getTaskQueue().add(redisTask);
	}

	/**
	 * @Description: Redis pipeline实现
	 * @param c
	 * @param task 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月17日 下午2:35:55
	 */
	private static void pipelineWrite(RedisProxyClient c, RedisTask task) {
		c.taskLock.lock();
		try {
			c.addRedisTask(task);
			c.bufferQueue.add(task.getRedisMessage());
		} finally {
			c.taskLock.unlock();
		}
		if (c.pipelineLock.tryLock()) {
			int i = 0;
			try {
				while (c.bufferQueue.size() != 0) {
					c.channel.write(c.bufferQueue.poll());
					i++;
					if (i == Configuration.LOCAL_PROXY_CACHE_TASK_NUM) {
						c.channel.flush();
						i = 0;
					}
				}
			} finally {
				c.pipelineLock.unlock();
				if (i != 0) {
					c.channel.flush();
				}
			}
		}
	}

	/**
	 * @Description: 检查Redis状态 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月21日 上午10:53:35
	 */
	private void checkRedisState() {
		RedisProxyClient c = this;
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				checkALLRedisState(c);
			}
		}, 5000, 5000);
	}

	/**
	 * @Description: 检查Redis状态 
	 * @param c Redis代理客户端连接 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月21日 上午10:54:13
	 */
	private static void checkALLRedisState(RedisProxyClient c) {
		if (c.connected) {
			LinkedBlockingQueue<RedisTask> taskQueue = c.redisClientHandler.getTaskQueue();
			RedisTask task = taskQueue.peek();
			if (task != null) {
				long waiteTime = System.currentTimeMillis() - task.getTime();
				if (waiteTime >= 2000) {
					if (task.getMessageType() != RedisTask.FLUSHALL_MESSAGE_TYPE) {
						c.connected = false;
						redisDown(c.redisInfo, c.channel.alloc());
					} else {
						if (waiteTime >= 20000) {
							c.connected = false;
							redisDown(c.redisInfo, c.channel.alloc());
						}
					}
				}
			} else {
				RedisTask pingTask = new RedisTask(System.currentTimeMillis(),
						RedisMessageUtils.getRedisMessage(c.channel.alloc(), "PING"), null, null, 0,
						RedisTask.PING_MESSAGE_TYPE);
				pipelineWrite(c, pingTask);
			}
		}
	}

	/**
	 * @Description: 初始化RedisProxyClient连接
	 * @param redisInfo
	 * @param databaseNumber
	 * @return RedisProxyClient 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午3:10:32
	 */
	public static RedisProxyClient initRedisProxyClient(RedisInfo redisInfo, Integer databaseNumber) {
		ConcurrentHashMap<Integer, RedisProxyClient> clients = routingTable.get(redisInfo);
		RedisProxyClient redisClient = getAndStartNewRedisProxyClient(redisInfo, databaseNumber);
		if (clients != null) {
			clients.put(databaseNumber, redisClient);
		} else {
			clients = new ConcurrentHashMap<Integer, RedisProxyClient>();
			clients.put(databaseNumber, redisClient);
			routingTable.put(new RedisInfo(redisInfo.getMaster(), redisInfo.getSliceNumber()), clients);
		}
		return redisClient;
	}

	private static RedisProxyClient getAndStartNewRedisProxyClient(RedisInfo redisInfo, Integer databaseNumber) {
		final RedisProxyClient redisClient = new RedisProxyClient(redisInfo, databaseNumber);
		new Thread(() -> {
			redisClient.start(databaseNumber);
		}).start();
		return redisClient;
	}

	/**
	 * @Description: 初始化数据库
	 * @param databaseNumber 库号 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月17日 上午10:32:34
	 */
	public static void initDatabase(Integer databaseNumber) {
		ReentrantLock lock = selectLockList.get(databaseNumber);
		if (lock != null) {
			if (lock.tryLock()) {
				try {
					initSelectRedisProxyClientList(true, databaseNumber);
					initSelectRedisProxyClientList(false, databaseNumber);
				} finally {
					lock.unlock();
				}
			}
		}
	}

	/**
	 * @Description: 初始化本地或远程RedisProxyClient连接
	 * @param local 是否为本地
	 * @param databaseNumber  库号
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:01:30
	 */
	private static void initSelectRedisProxyClientList(Boolean local, Integer databaseNumber) {
		List<RedisInfo> redisInfoList = null;
		if (local) {
			redisInfoList = HeartBeatServer.localRedisInfoList;
		} else {
			redisInfoList = HeartBeatServer.remoteRedisInfoList;
		}
		if (redisInfoList != null) {
			for (int i = 0; i < redisInfoList.size(); i++) {
				initSelectRedisProxyClient(redisInfoList.get(i), databaseNumber);
			}
		}
	}

	private static void initSelectRedisProxyClient(RedisInfo info, Integer databaseNumber) {
		if (routingTable.get(info) != null && routingTable.get(info).get(databaseNumber) != null) {
			return;
		}
		initRedisProxyClient(info, databaseNumber);
	}

	/**
	 * @Description: 
	 * @param master 是否是Master
	 * @param sliceNumber 分片号
	 * @param databaseNumber 库号
	 * @return RedisProxyClient Redis连接
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午6:41:49
	 */
	private static RedisProxyClient getRedisProxyClientByRoutingTable(Boolean master, Integer sliceNumber,
			Integer databaseNumber) {
		ConcurrentHashMap<Integer, RedisProxyClient> clients = routingTable.get(new RedisInfo(master, sliceNumber));
		if (clients != null) {
			return clients.get(databaseNumber);
		}
		return null;
	}

	/**
	 * @Description: 执行Redis任务
	 * @param redisTask Redis任务 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月17日 下午2:25:02
	 */
	public static void executeRedisTask(RedisTask redisTask) {
		RedisProxyClient client = null;
		if (redisTask.getMessageType() == RedisTask.MASTER_MESSAGE_TYPE) {
			client = getRedisProxyClientByRoutingTable(true, redisTask.getSliceNumber(), redisTask.getDatabaseNumber());
		} else if (redisTask.getMessageType() == RedisTask.SLAVE_MESSAGE_TYPE) {
			client = getRedisProxyClientByRoutingTable(false, redisTask.getSliceNumber(),
					redisTask.getDatabaseNumber());
			if (client == null) {
				client = getRedisProxyClientByRoutingTable(true, redisTask.getSliceNumber(),
						redisTask.getDatabaseNumber());
			}
		} else {
			client = getRedisProxyClientByRoutingTable(redisTask.getMaster(), redisTask.getSliceNumber(),
					redisTask.getDatabaseNumber());
		}
		if (client != null && client.connected) {
			redisTask.setTime(System.currentTimeMillis());
			pipelineWrite(client, redisTask);
		} else {
			redisTask.getChannelHandlerContext().writeAndFlush(new ErrorRedisMessage("ERR Service is busy"));
		}
	}

	/**
	 * @Description: 与Redis建立连接并进行初始化
	 * @param databaseNumber 库号 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月17日 下午2:17:33
	 */
	public void start(Integer databaseNumber) {
		redisClientHandler = new RedisProxyClientHandler(this);
		group = new NioEventLoopGroup(4);
		try {
			bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline pipeline = socketChannel.pipeline();
					pipeline.addLast(new RedisDecoder());
					pipeline.addLast(new RedisBulkStringAggregator());
					pipeline.addLast(new RedisArrayAggregator());
					pipeline.addLast(new RedisEncoder());
					pipeline.addLast(redisClientHandler);
				}
			});
			doConnect(databaseNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description: 与Redis建立连接并进行初始化
	 * @param databaseNumber 库号 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月17日 下午2:20:23
	 */
	private void doConnect(Integer databaseNumber) {
		if (channel != null && channel.isActive()) {
			return;
		}
		ChannelFuture future = bootstrap.connect(redisInfo.getHost(), redisInfo.getPort());
		RedisProxyClient c = this;
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture futureListener) throws Exception {
				if (futureListener.isSuccess()) {
					System.out.println("Redis连接成功！" + redisInfo + databaseNumber);
					channel = future.channel();
					authAndSelect(c, databaseNumber);
					connected = true;
					if (databaseNumber == 0) {
						redisInfo.setRun(true);
						if (HeartBeatServer.haveVirtualIp) {
							sendSlaveof(c);
						}
						initDatabase();
						checkRedisState();
					}
				} else {
					System.out.println("Redis连接失败！" + redisInfo + databaseNumber);
					if (databaseNumber == 0) {
						futureListener.channel().eventLoop().schedule(new Runnable() {
							@Override
							public void run() {
								doConnect(0);
							}
						}, 5, TimeUnit.SECONDS);
					}
				}
			}
		});
	}

	/**
	 * @Description: 发送Slaveof命令
	 * @param c  
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月21日 上午11:02:17
	 */
	private void sendSlaveof(RedisProxyClient c) {
		if (redisInfo.getMaster()) {
			RedisTask task = new RedisTask(RedisMessageUtils.getRedisMessage(channel.alloc(), "SLAVEOF NO ONE"));
			task.setMessageType(RedisTask.SLAVEOF_MESSAGE_TYPE);
			task.setTime(System.currentTimeMillis());
			pipelineWrite(c, task);
		} else {
			RedisTask task = new RedisTask(RedisMessageUtils.getRedisMessage(channel.alloc(), "SLAVEOF "
					+ redisInfo.getSliceRedisInfo().getHost() + " " + redisInfo.getSliceRedisInfo().getPort()));
			pipelineWrite(c, task);
		}
	}

	/**
	 * @Description: 初始化某一库号连接 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月21日 上午11:05:50
	 */
	private void initDatabase() {
		if (!redisInfo.getMaster()) {
			ConcurrentHashMap<Integer, RedisProxyClient> masterClients = routingTable
					.get(new RedisInfo(true, redisInfo.getSliceNumber()));
			if (masterClients != null) {
				Enumeration<Integer> keys = masterClients.keys();
				while (keys.hasMoreElements()) {
					Integer num = keys.nextElement();
					if (num != 0) {
						initRedisProxyClient(redisInfo, num);
					}
				}
			}
		}
	}

	/**
	 * @Description: Redis连接认证和换库
	 * @param c 
	 * @param databaseNumber 库号 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月17日 下午2:22:44
	 */
	private void authAndSelect(RedisProxyClient c, Integer databaseNumber) {
		if (!c.redisInfo.getPassword().equals("")) {
			RedisTask authTask = new RedisTask(System.currentTimeMillis(),
					RedisMessageUtils.getRedisMessage(c.channel.alloc(), "AUTH " + c.redisInfo.getPassword()),
					redisInfo.getSliceNumber(), redisInfo.getMaster(), databaseNumber, RedisTask.AUTH_MESSAGE_TYPE);
			authTask.setMessageType(RedisTask.AUTH_MESSAGE_TYPE);
			pipelineWrite(c, authTask);
		}
		RedisTask selectTask = new RedisTask(System.currentTimeMillis(),
				RedisMessageUtils.getRedisMessage(c.channel.alloc(), "SELECT " + databaseNumber),
				redisInfo.getSliceNumber(), redisInfo.getMaster(), databaseNumber, RedisTask.SELECT_MESSAGE_TYPE);
		selectTask.setMessageType(RedisTask.SELECT_MESSAGE_TYPE);
		pipelineWrite(c, selectTask);
	}

	/**
	 * @Description: Redis挂掉
	 * @param redisInfo 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午5:40:44
	 */
	public static void redisDown(RedisInfo redisInfo, ByteBufAllocator b) {
		System.out.println("redisDown:" + redisInfo);
		ConcurrentHashMap<Integer, RedisProxyClient> downClients = routingTable.remove(redisInfo);
		if (downClients != null) {
			if (redisInfo.getMaster()) {
				RedisInfo slaveRedisInfo = new RedisInfo(false, redisInfo.getSliceNumber());
				ConcurrentHashMap<Integer, RedisProxyClient> slaveClients = routingTable.remove(slaveRedisInfo);
				if (slaveClients != null) {
					RedisProxyClient slaveClient = slaveClients.get(0);
					if (slaveClient != null) {
						RedisTask task = new RedisTask(RedisMessageUtils.getRedisMessage(b, "SLAVEOF NO ONE"));
						task.setMessageType(RedisTask.SLAVE_MESSAGE_TYPE);
						pipelineWrite(slaveClient, task);
					}
					routingTable.put(new RedisInfo(true, redisInfo.getSliceNumber()), slaveClients);
				}
			}
			redisInfo.setRun(false);
			redisInfo.setMaster(false);
			redisInfo.getSliceRedisInfo().setMaster(true);
			if (HeartBeatServer.haveVirtualIp) {
				Iterator<Entry<Integer, RedisProxyClient>> iterator = downClients.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<Integer, RedisProxyClient> entry = iterator.next();
					LinkedBlockingQueue<RedisTask> q = entry.getValue().getRedisClientHandler().getTaskQueue();
					for (RedisTask redisTask : q) {
						if (redisTask.getMessageType() == RedisTask.MASTER_MESSAGE_TYPE
								&& redisTask.getMessageType() == RedisTask.SLAVE_MESSAGE_TYPE) {
							RedisProxyClient.executeRedisTask(redisTask);
						}
					}
					entry.getValue().getGroup().shutdownGracefully();
					Channel channel = entry.getValue().getChannel();
					if (channel.isActive()) {
						channel.close();
					}
				}
			}
			if (redisInfo.getHost().equals(Configuration.LOCAL_IP)) {
				ExecuteLinuxCommand.executeLinuxCmd("systemctl restart " + redisInfo.getServiceName());
				initRedisProxyClient(redisInfo, 0);
			} else {
				if (!HeartBeatServer.remoteHARedisIsRun) {
					JSchSSH ssh = new JSchSSH(Configuration.REMOTE_IP, Configuration.REMOTE_SSH_USERNAME,
							Configuration.REMOTE_SSH_PASSWORD);
					try {
						ssh.exec("systemctl restart " + redisInfo.getServiceName());
						initRedisProxyClient(redisInfo, 0);
					} catch (Exception e) {
						HeartBeatServer.reconnectRedisInfoList.add(redisInfo);
						e.printStackTrace();
					}
				} else {
					initRedisProxyClient(redisInfo, 0);
				}
			}
		}
	}

	/**
	 * @Description: 初始化本地0号库Redis代理客户端连接 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月21日 上午11:08:12
	 */
	public static void initLocalRedisProxyClient() {
		initSelectRedisProxyClientList(true, 0);
	}

	/**
	 * @Description: 初始化远程0号库Redis代理客户端连接  
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月21日 上午11:08:51
	 */
	public static void initRemoteRedisProxyClient() {
		initSelectRedisProxyClientList(false, 0);
	}

	/**
	 * @Description: flushall命令实现
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月28日 下午6:37:33
	 */
	public static void flushAll() {
		Set<Entry<RedisInfo, ConcurrentHashMap<Integer, RedisProxyClient>>> entrySet = routingTable.entrySet();
		for (Entry<RedisInfo, ConcurrentHashMap<Integer, RedisProxyClient>> entry : entrySet) {
			if (entry.getKey().getMaster()) {
				RedisProxyClient client = entry.getValue().get(0);
				if (client != null) {
					RedisTask flushAllTask = new RedisTask();
					flushAllTask.setMessageType(RedisTask.FLUSHALL_MESSAGE_TYPE);
					flushAllTask.setRedisMessage(RedisMessageUtils.getRedisMessage(client.channel.alloc(), "FLUSHALL"));
					flushAllTask.setDatabaseNumber(0);
					flushAllTask.setTime(System.currentTimeMillis());
					pipelineWrite(client, flushAllTask);
				}
			}
		}
	}
}
