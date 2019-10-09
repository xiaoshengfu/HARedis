package edu.sdust.haredis.heartbeat;

import java.util.ArrayList;
import java.util.List;

import edu.sdust.haredis.common.Configuration;
import edu.sdust.haredis.redisproxy.RedisInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * ClassName: Server
 * @Description: 心跳服务端
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月6日 上午11:43:28
 */
public final class HeartBeatServer {

	//本地Redis是否为Master
	public volatile static boolean master = Configuration.STARTUP_TYPE.equals("master") ? true : false;
	//本机是否持有虚拟IP
	public volatile static boolean haveVirtualIp = false;
	//服务是否已经初始化过
	public volatile static boolean init = false;
	//远程haredis服务是否运行
	public volatile static boolean remoteHARedisIsRun = false;
	//远程haredis服务名称
	public static String remoteServiceName = null;
	//远程挂载虚拟ip的网卡名称
	public static String remoteNetworkCardName = null;
	//本地Redis状态集合
	public static List<RedisInfo> localRedisInfoList = Configuration.getRedisInfoList(Configuration.LOCAL_REDIS_INFO,
			Configuration.LOCAL_IP);
	//本地Redis状态集合
	public static List<RedisInfo> remoteRedisInfoList = null;
	//需要重新建立连接的Redis状态集合
	public static List<RedisInfo> reconnectRedisInfoList = new ArrayList<RedisInfo>();

	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
		NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline p = socketChannel.pipeline();
							p.addLast(new IdleStateHandler(5, 5, 5));
							p.addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
							p.addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
							p.addLast(new HeartBeatServerHandler());
						}
					});

			Channel ch = bootstrap.bind(Configuration.LOCAL_HEARTBEAT_PORT).sync().channel();
			ch.closeFuture().sync();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}