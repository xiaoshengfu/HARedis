package edu.sdust.haredis.visualization.heartbeat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import edu.sdust.haredis.heartbeat.CustomHeartbeatHandler;
import edu.sdust.haredis.heartbeat.HeartBeatData;
import edu.sdust.haredis.heartbeat.MarshallingCodeCFactory;
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

public final class HeartBeatVisualizationClient {

	private String ip;
	private Integer port;
	private String accessPassword;
	private Boolean connect = false;
	private HeartBeatVisualizationClientHandler handler;
	private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
	private Channel channel;
	private Bootstrap bootstrap;

	public HeartBeatVisualizationClient(String ip, Integer port, String accessPassword) {
		super();
		this.ip = ip;
		this.port = port;
		this.accessPassword = accessPassword;
	}

	public HeartBeatData getHeartBeatData() {
		return handler.getHeartBeatData();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getAccessPassword() {
		return accessPassword;
	}

	public void setAccessPassword(String accessPassword) {
		this.accessPassword = accessPassword;
	}

	public HeartBeatVisualizationClientHandler getHandler() {
		return handler;
	}

	public void setHandler(HeartBeatVisualizationClientHandler handler) {
		this.handler = handler;
	}

	public Boolean getConnect() {
		return connect;
	}

	public void setConnect(Boolean connect) {
		this.connect = connect;
	}

	private void getStateHeartBeat(Channel channel) {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(CustomHeartbeatHandler.STATE_MSG);
		channel.writeAndFlush(data);
	}

	public void sendFileMsg(Date date) {
		if (channel != null && channel.isActive()) {
			HeartBeatData data = new HeartBeatData();
			data.setIp(ip);
			data.setRequestType(CustomHeartbeatHandler.FILE_MSG);
			data.setBackupsTime(date);
			data.setAccessPassword(accessPassword);
			channel.writeAndFlush(data);
		}
	}

	public void start() {
		try {
			bootstrap = new Bootstrap();
			bootstrap.group(workGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline p = socketChannel.pipeline();
					p.addLast(new IdleStateHandler(7, 7, 7));
					p.addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
					p.addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
					p.addLast(handler);
				}
			});
			doConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doConnect() {
		if (channel != null && channel.isActive()) {
			return;
		}
		ChannelFuture future = bootstrap.connect(ip, port);
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture futureListener) throws Exception {
				if (futureListener.isSuccess()) {
					System.out.println("连接" + ip + "成功！");
					channel = futureListener.channel();
					futureListener.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
						@Override
						public void run() {
							if (channel != null && channel.isActive()) {
								getStateHeartBeat(channel);
							}
						}
					}, 1, 2, TimeUnit.SECONDS);
				} else {
					System.out.println("连接" + ip + "失败！");
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
