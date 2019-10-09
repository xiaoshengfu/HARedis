package edu.sdust.haredis.heartbeat;

import java.util.List;

import edu.sdust.haredis.common.Configuration;
import edu.sdust.haredis.common.ExecuteLinuxCommand;
import edu.sdust.haredis.redisproxy.RedisInfo;
import edu.sdust.haredis.redisproxy.RedisProxyClient;
import edu.sdust.haredis.redisproxy.RedisProxyService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * ClassName: ClientHandler
 * @Description: 心跳客户端处理Handler
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月6日 上午11:31:47
 */
public final class HeartBeatClientHandler extends CustomHeartbeatHandler {

	private HeartBeatClient client;

	public HeartBeatClientHandler(HeartBeatClient client) {
		this.client = client;
	}

	/**
	 * 数据处理
	 */
	@Override
	protected void handleData(ChannelHandlerContext ctx, HeartBeatData data) {
		if (data.getRequestType() == AUTH_SUCCESS_MSG) {
			HeartBeatServer.remoteHARedisIsRun = true;
			if (!HeartBeatServer.init) {
				HeartBeatServer.remoteServiceName = data.getServiceName();
				HeartBeatServer.remoteNetworkCardName = data.getNetworkCardName();
				List<RedisInfo> remoteRedisInfoList = data.getRedisInfoList();
				List<RedisInfo> localRedisInfoList = HeartBeatServer.localRedisInfoList;
				if (remoteRedisInfoList != null && remoteRedisInfoList.size() == localRedisInfoList.size()) {
					HeartBeatServer.remoteRedisInfoList = remoteRedisInfoList;
					for (int i = 0; i < remoteRedisInfoList.size(); i++) {
						remoteRedisInfoList.get(i).setSliceRedisInfo(localRedisInfoList.get(i));
						localRedisInfoList.get(i).setSliceRedisInfo(remoteRedisInfoList.get(i));
					}
					if (data.getHaveVirtualIp()) {
						for (int i = 0; i < remoteRedisInfoList.size(); i++) {
							localRedisInfoList.get(i).setMaster(!remoteRedisInfoList.get(i).getMaster());
						}
					} else {
						if (HeartBeatServer.master) {
							ExecuteLinuxCommand.executeLinuxCmd("ip addr add " + Configuration.VIRTUAL_IP + "/25 dev "
									+ Configuration.MOUNT_NETWORK_CARD_NAME);
							HeartBeatServer.haveVirtualIp = true;
							for (int i = 0; i < localRedisInfoList.size(); i++) {
								localRedisInfoList.get(i).setMaster(true);
							}
						} else {
							for (int i = 0; i < remoteRedisInfoList.size(); i++) {
								remoteRedisInfoList.get(i).setMaster(true);
							}
						}
					}
				}
				RedisProxyClient.initLocalRedisProxyClient();
				RedisProxyClient.initRemoteRedisProxyClient();
				new Thread(() -> new RedisProxyService().run()).start();
				HeartBeatServer.init = true;
			} else {
				List<RedisInfo> reconnectList = HeartBeatServer.reconnectRedisInfoList;
				for (int i = 0; i < reconnectList.size(); i++) {
					RedisProxyClient.initRedisProxyClient(reconnectList.get(i), 0);
				}
				reconnectList.clear();
			}
		}
	}

	/**
	 * 通道建立成功操作
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(AUTH_MSG);
		data.setIp(Configuration.LOCAL_IP);
		data.setAccessPassword(Configuration.HAREDIS_ACCESS_PASSWORD);
		ctx.writeAndFlush(data);
	}

	/**
	 * PING检测通道状态
	 */
	@Override
	protected void handleAllIdle(ChannelHandlerContext ctx) {
		sendPingMsg(ctx);
	}

	/**
	 * 通道断开
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		client.doConnect();
	}

	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.close();
		}
	}
}