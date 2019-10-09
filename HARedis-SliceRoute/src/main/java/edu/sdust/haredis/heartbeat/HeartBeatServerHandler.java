package edu.sdust.haredis.heartbeat;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.sdust.haredis.common.Configuration;
import edu.sdust.haredis.common.ExecuteLinuxCommand;
import edu.sdust.haredis.common.FileUtils;
import edu.sdust.haredis.common.JSchSSH;
import edu.sdust.haredis.common.SigarUtils;
import edu.sdust.haredis.redisproxy.RedisInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * ClassName: ServerHandler
 * @Description: 心跳服务端处理Handler
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月6日 上午11:43:35
 */
public final class HeartBeatServerHandler extends CustomHeartbeatHandler {

	//连接客户端信息存储Map
	public HashMap<String, Integer> channelMap = new HashMap<String, Integer>();
	//心跳信息
	public static final int HEARTBEAT_MSG = 1;
	//可视化信息
	public static final int VISUALIZATION_MSG = 2;

	/**
	 * @Description: 首次获取服务状态数据
	 * @return HeartBeatData
	 * @throws Exception  
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:31:55
	 */
	private HeartBeatData getFirstStateData() throws Exception {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(AUTH_SUCCESS_MSG);
		data.setIp(Configuration.LOCAL_IP);
		data.setHaveVirtualIp(HeartBeatServer.haveVirtualIp);
		data.setInit(HeartBeatServer.init);
		data.setMaster(HeartBeatServer.master);
		data.setRedisInfoList(HeartBeatServer.localRedisInfoList);
		data.setJavaProperty(SigarUtils.javaProperty());
		data.setCpuInfoList(SigarUtils.cpuInfoList());
		data.setMemoryMap(SigarUtils.memory());
		data.setOs(SigarUtils.os());
		data.setFileSystemList(SigarUtils.fileSystemList());
		return data;
	}

	/**
	 * @Description: 获取服务状态数据
	 * @return HeartBeatData
	 * @throws Exception  
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:31:55
	 */
	private HeartBeatData getStateData() throws Exception {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(STATE_MSG);
		data.setIp(Configuration.LOCAL_IP);
		data.setHaveVirtualIp(HeartBeatServer.haveVirtualIp);
		data.setInit(HeartBeatServer.init);
		data.setMaster(HeartBeatServer.master);
		data.setRedisInfoList(HeartBeatServer.localRedisInfoList);
		data.setJavaProperty(SigarUtils.javaProperty());
		data.setCpuInfoList(SigarUtils.cpuInfoList());
		data.setMemoryMap(SigarUtils.memory());
		data.setOs(SigarUtils.os());
		data.setFileSystemList(SigarUtils.fileSystemList());
		return data;
	}

	/**
	 * @Description: 获取持久化文件数据
	 * @return HeartBeatData
	 * @throws Exception  
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:36:48
	 */
	private HeartBeatData getBackupsFileData(Date date) {
		HeartBeatData data = new HeartBeatData();
		data.setIp(Configuration.LOCAL_IP);
		data.setRequestType(FILE_MSG);
		data.setBackupsTime(date);
		List<RedisInfo> localRedisInfoList = HeartBeatServer.localRedisInfoList;
		List<File> fileList = new ArrayList<File>(localRedisInfoList.size());
		for (int i = 0; i < localRedisInfoList.size(); i++) {
			String backupsLocation = localRedisInfoList.get(i).getBackupsLocation();
			if (backupsLocation != null) {
				File file = new File(backupsLocation);
				if (file.exists()) {
					fileList.add(file);
				}
			}
		}
		File zipFile = new File("/backups.zip");
		FileUtils.zipFiles(fileList, zipFile);
		data.setBackupsFile(FileUtils.getBytes(zipFile));
		return data;
	}

	/**
	 * @Description: 获取授权成功数据
	 * @return HeartBeatData 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:52:25
	 */
	private HeartBeatData getAuthSuccessData() {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(AUTH_SUCCESS_MSG);
		data.setIp(Configuration.LOCAL_IP);
		data.setMaster(HeartBeatServer.master);
		data.setHaveVirtualIp(HeartBeatServer.haveVirtualIp);
		data.setNetworkCardName(Configuration.MOUNT_NETWORK_CARD_NAME);
		data.setServiceName(Configuration.LOCAL_HAREDIS_SERVICE_NAME);
		data.setRedisInfoList(HeartBeatServer.localRedisInfoList);
		return data;
	}

	/**
	 * @Description: 获取授权失败数据
	 * @return HeartBeatData 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:52:49
	 */
	private HeartBeatData getAuthFailData() {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(AUTH_FAIL_MSG);
		data.setIp(Configuration.LOCAL_IP);
		return data;
	}

	/**
	 * @Description: 获取没有授权数据
	 * @return HeartBeatData 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:53:08
	 */
	private HeartBeatData getNoAuthData() {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(NOT_AUTH_MSG);
		data.setIp(Configuration.LOCAL_IP);
		return data;
	}

	/**
	 * 连接数据请求处理(心跳)
	 */
	@Override
	protected void handleData(ChannelHandlerContext ctx, HeartBeatData data) {
		String id = ctx.channel().id().asShortText();
		Integer type = channelMap.get(id);
		if (type != null) {
			if (data.getRequestType() == CUSTOM_MSG) {
				//TODO
			} else if (data.getRequestType() == STATE_MSG) {
				try {
					ctx.writeAndFlush(getStateData());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (data.getRequestType() == FILE_MSG) {
				ctx.writeAndFlush(getBackupsFileData(data.getBackupsTime()));
			}
		} else {
			if (data.getRequestType() == AUTH_MSG) {
				if (Configuration.REMOTE_IP.equals(data.getIp())
						&& Configuration.HAREDIS_ACCESS_PASSWORD.equals(data.getAccessPassword())) {
					channelMap.put(id, HEARTBEAT_MSG);
					ctx.writeAndFlush(getAuthSuccessData());
				} else {
					ctx.writeAndFlush(getAuthFailData()).addListener(ChannelFutureListener.CLOSE);
				}
			} else if (data.getRequestType() == AUTH_VISUALIZATION_MSG) {
				if (Configuration.HAREDIS_ACCESS_PASSWORD.equals(data.getAccessPassword())) {
					channelMap.put(id, VISUALIZATION_MSG);
					try {
						ctx.writeAndFlush(getFirstStateData());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					ctx.writeAndFlush(getAuthFailData()).addListener(ChannelFutureListener.CLOSE);
				}
			} else {
				ctx.writeAndFlush(getNoAuthData()).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	/**
	 * 心跳超时处理
	 */
	@Override
	protected void handleReaderIdle(ChannelHandlerContext ctx) {
		String id = ctx.channel().id().asShortText();
		Integer type = channelMap.remove(id);
		if (type != null) {
			channelMap.remove(id);
			if (type == HEARTBEAT_MSG) {
				remoteHARedisDown();
				Channel clientChannel = HeartBeatClient.channel;
				if (clientChannel.isActive()) {
					clientChannel.close();
				}
			} else if (type == VISUALIZATION_MSG) {
				//TODO
			}
		}
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.close();
		}
	}

	/**
	 * @Description: 远程HARedis挂掉处理
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午5:20:50
	 */
	public static void remoteHARedisDown() {
		System.out.println("remoteHARedisDown");
		HeartBeatServer.remoteHARedisIsRun = false;
		JSchSSH ssh = new JSchSSH(Configuration.REMOTE_IP, Configuration.REMOTE_SSH_USERNAME,
				Configuration.REMOTE_SSH_PASSWORD);
		if (!HeartBeatServer.haveVirtualIp) {
			try {
				HeartBeatServer.haveVirtualIp = true;
				ssh.exec(
						"ip addr del " + Configuration.VIRTUAL_IP + "/25 dev " + HeartBeatServer.remoteNetworkCardName);
				ExecuteLinuxCommand.executeLinuxCmd(
						"ip addr add " + Configuration.VIRTUAL_IP + "/25 dev " + Configuration.MOUNT_NETWORK_CARD_NAME);
				ssh.exec("systemctl restart " + HeartBeatServer.remoteServiceName);
			} catch (Exception e) {
				e.printStackTrace();
				ExecuteLinuxCommand.executeLinuxCmd(
						"ip addr add " + Configuration.VIRTUAL_IP + "/25 dev " + Configuration.MOUNT_NETWORK_CARD_NAME);
			}
		} else {
			try {
				ssh.exec("systemctl restart " + HeartBeatServer.remoteServiceName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通道断开
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String id = ctx.channel().id().asShortText();
		Integer type = channelMap.remove(id);
		if (type != null) {
			channelMap.remove(id);
			if (type == HEARTBEAT_MSG) {
				remoteHARedisDown();
			} else if (type == VISUALIZATION_MSG) {
				//TODO
			}
		}
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.close();
		}
	}

	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
}