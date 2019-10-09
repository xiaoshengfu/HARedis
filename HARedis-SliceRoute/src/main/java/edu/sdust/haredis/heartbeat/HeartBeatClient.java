package edu.sdust.haredis.heartbeat;

import java.util.concurrent.TimeUnit;

import edu.sdust.haredis.common.Configuration;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * ClassName: Client
 * @Description: 心跳客户端
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月6日 上午11:28:18
 */
public final class HeartBeatClient {

	private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
	public static Channel channel;
	private Bootstrap bootstrap;

	public void start() {
		try {
			bootstrap = new Bootstrap();
			bootstrap.group(workGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline p = socketChannel.pipeline();
					p.addLast(new IdleStateHandler(5, 5, 5));
					p.addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
					p.addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
					p.addLast(new HeartBeatClientHandler(HeartBeatClient.this));
				}
			});
			doConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description: 发送心跳数据
	 * @param channel 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午9:56:37
	 */
	private void sendHeartBeat(Channel channel) {
		if (channel != null && channel.isActive()) {
			HeartBeatData data = new HeartBeatData();
			data.setIp(Configuration.LOCAL_IP);
			data.setRequestType(CustomHeartbeatHandler.CUSTOM_MSG);
			channel.writeAndFlush(data);
			//System.out.println(data);
		}
	}

	/**
	 * @Description: 与远端心跳服务连接
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午9:59:56
	 */
	protected void doConnect() {
		if (channel != null && channel.isActive()) {
			return;
		}
		ChannelFuture future = bootstrap.connect(Configuration.REMOTE_IP, Configuration.REMOTE_HAREDIS_HEARTBEAT_PORT);
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture futureListener) throws Exception {
				if (futureListener.isSuccess()) {
					System.out.println("心跳连接成功！");
					channel = futureListener.channel();
					futureListener.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
						@Override
						public void run() {
							sendHeartBeat(channel);
						}
					}, 1, 2, TimeUnit.SECONDS);
				} else {
					System.out.println("心跳连接失败！");
					futureListener.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							doConnect();
						}
					}, 5, TimeUnit.SECONDS);
				}
			}
		});
	}
}
