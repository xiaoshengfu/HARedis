package edu.sdust.haredis.visualization.heartbeat;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Value;

import edu.sdust.haredis.heartbeat.CustomHeartbeatHandler;
import edu.sdust.haredis.heartbeat.HeartBeatData;
import edu.sdust.haredis.visualization.common.DateUtils;
import edu.sdust.haredis.visualization.common.FileUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public final class HeartBeatVisualizationClientHandler extends CustomHeartbeatHandler {

	@Value("${backups.file.location}")
	private String backupsFileLocation;

	private HeartBeatVisualizationClient client;
	private HeartBeatData heartBeatData;
	private Lock lock = new ReentrantLock();

	public HeartBeatData getHeartBeatData() {
		return heartBeatData;
	}

	public void setHeartBeatData(HeartBeatData heartBeatData) {
		this.heartBeatData = heartBeatData;
	}

	public HeartBeatVisualizationClient getClient() {
		return client;
	}

	public void setClient(HeartBeatVisualizationClient client) {
		this.client = client;
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	@Override
	protected void handleData(ChannelHandlerContext channelHandlerContext, HeartBeatData data) {
		lock.lock();
		try {
			if (data.getRequestType() == AUTH_SUCCESS_MSG) {
				heartBeatData = data;
				client.setConnect(true);
			} else if (data.getRequestType() == STATE_MSG) {
				heartBeatData = data;
				System.out.println(data);
			} else if (data.getRequestType() == FILE_MSG) {
				if (data.getBackupsFile() != null && data.getBackupsTime() != null) {
					FileUtils.getFile(data.getBackupsFile(),
							System.getProperty("evan.webapp") + backupsFileLocation + "/"
									+ DateUtils.getStringNowDate(),
							"haredis_" + DateUtils.getStringDate(data.getBackupsTime()) + ".zip");
				}
			} else if (data.getRequestType() == AUTH_FAIL_MSG) {
				//TODO
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(AUTH_VISUALIZATION_MSG);
		data.setIp(client.getIp());
		data.setAccessPassword(client.getAccessPassword());
		ctx.writeAndFlush(data);
	}

	private void doException(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.close();
		}
	}

	@Override
	protected void handleReaderIdle(ChannelHandlerContext ctx) {
		doException(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		doException(ctx);
		client.setConnect(false);
		client.doConnect();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		doException(ctx);
	}
}