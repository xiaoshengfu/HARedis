package edu.sdust.haredis.redisproxy;

import java.util.concurrent.ConcurrentHashMap;

import edu.sdust.haredis.common.Configuration;
import edu.sdust.haredis.common.RedisMessageUtils;
import edu.sdust.haredis.heartbeat.HeartBeatServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

/**
 * ClassName: RedisProxyServiceHandler
 * @Description: Redis代理服务端Handler
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月10日 下午6:29:38
 */
public final class RedisProxyServiceHandler extends ChannelInboundHandlerAdapter {

	//客户端连接信息Map key客户端标识 value连接信息
	public static ConcurrentHashMap<String, ChannelInfo> channelMessages = new ConcurrentHashMap<String, ChannelInfo>();

	@Override
	public void channelRead(ChannelHandlerContext context, Object data) throws Exception {
		RedisMessage redisMessage = (RedisMessage) data;
		//RedisMessageUtils.printAggregatedRedisResponse(redisMessage);
		String id = context.channel().id().asShortText();
		ChannelInfo info = channelMessages.get(id);
		if (info != null) {
			if (redisMessage instanceof ArrayRedisMessage) {
				ArrayRedisMessage arm = (ArrayRedisMessage) redisMessage;
				RedisMessage message = arm.children().get(0);
				String command = RedisMessageUtils.getString((FullBulkStringRedisMessage) message).toUpperCase();
				if (command.equals("AUTH")) {
					if (arm.children().size() == 2) {
						if (Configuration.HAREDIS_ACCESS_PASSWORD.equals("")) {
							context.writeAndFlush(
									new ErrorRedisMessage("ERR Client sent AUTH, but no password is set"));
						} else {
							RedisMessage pwdMsg = arm.children().get(1);
							if (pwdMsg instanceof FullBulkStringRedisMessage) {
								String pwd = RedisMessageUtils.getString((FullBulkStringRedisMessage) pwdMsg);
								if (Configuration.HAREDIS_ACCESS_PASSWORD.equals(pwd)) {
									info.setAuth(true);
									context.writeAndFlush(new SimpleStringRedisMessage("OK"));
								} else {
									info.setAuth(false);
									context.writeAndFlush(new ErrorRedisMessage("ERR Invalid password"));
								}
							} else {
								context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
							}
						}
					} else {
						context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
					}
				} else {
					if (info.getAuth()) {
						if (command.equals("PING")) {
							if (arm.children().size() == 1) {
								context.writeAndFlush(new SimpleStringRedisMessage("PONG"));
							} else {
								context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
							}
						} else if (command.equals("SELECT")) {
							if (arm.children().size() == 2) {
								RedisMessage numMsg = arm.children().get(1);
								if (numMsg instanceof FullBulkStringRedisMessage) {
									String num = RedisMessageUtils.getString((FullBulkStringRedisMessage) numMsg);
									try {
										if (Integer.parseInt(num) <= 16) {
											info.setDatabaseNumber(Integer.parseInt(num));
										} else {
											context.writeAndFlush(
													new ErrorRedisMessage("ERR DB index is out of range"));
											return;
										}
									} catch (Exception e) {
										context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
										return;
									}
									RedisProxyClient.initDatabase(info.getDatabaseNumber());
									context.writeAndFlush(new SimpleStringRedisMessage("OK"));
								} else {
									context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
								}
							} else {
								context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
							}
						} else if (command.equals("ECHO")) {
							if (arm.children().size() == 2) {
								RedisMessage msg = arm.children().get(1);
								if (msg instanceof FullBulkStringRedisMessage) {
									context.writeAndFlush(new SimpleStringRedisMessage(
											RedisMessageUtils.getString((FullBulkStringRedisMessage) msg)));
								} else {
									context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
								}
							} else {
								context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
							}
						} else if (command.equals("COMMAND")) {
							context.writeAndFlush(new SimpleStringRedisMessage("OK"));
						} else if (command.equals("INFO")) {
							context.writeAndFlush(
									new SimpleStringRedisMessage("local:" + HeartBeatServer.localRedisInfoList
											+ "\nremote:" + HeartBeatServer.remoteRedisInfoList));
						} else if (command.equals("FLUSHALL")) {
							RedisProxyClient.flushAll();
							context.writeAndFlush(new SimpleStringRedisMessage("OK"));
						} else if (command.equals("QUIT")) {
							context.channel().close();
						} else {
							RedisTask redisTask = new RedisTask(info.getDatabaseNumber(), context);
							if (RedisTask.slaveCommandSet.contains(command)) {
								redisTask.setMessageType(RedisTask.SLAVE_MESSAGE_TYPE);
							} else if (RedisTask.masterCommandSet.contains(command)) {
								redisTask.setMessageType(RedisTask.MASTER_MESSAGE_TYPE);
							} else {
								context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
								return;
							}
							if (arm.children().size() > 1) {
								RedisMessage keyMsg = arm.children().get(1);
								if (keyMsg instanceof FullBulkStringRedisMessage) {
									redisTask.setSliceNumber(
											RedisMessageUtils.getString((FullBulkStringRedisMessage) keyMsg).hashCode()
													% HeartBeatServer.localRedisInfoList.size());
									redisTask.setRedisMessage(RedisMessageUtils.duplicateRedisMessage(redisMessage));
									RedisProxyClient.executeRedisTask(redisTask);
								} else {
									context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
								}
							} else {
								context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
							}
						}
					} else {
						context.writeAndFlush(new ErrorRedisMessage("NOAUTH Authentication required"));
					}
				}
			} else {
				context.writeAndFlush(new ErrorRedisMessage("ERR Unsupported type"));
			}
		} else {
			context.writeAndFlush(new ErrorRedisMessage("ERR Channel is unregister"))
					.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String id = ctx.channel().id().asShortText();
		ChannelInfo info = new ChannelInfo();
		if ("".equals(Configuration.HAREDIS_ACCESS_PASSWORD)) {
			info.setAuth(true);
		}
		channelMessages.put(id, info);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String id = ctx.channel().id().asShortText();
		channelMessages.remove(id);
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		String id = ctx.channel().id().asShortText();
		channelMessages.remove(id);
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			ctx.close();
		}
	}
}