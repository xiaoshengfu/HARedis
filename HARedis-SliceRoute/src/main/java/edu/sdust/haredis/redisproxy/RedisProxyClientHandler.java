package edu.sdust.haredis.redisproxy;

import java.util.concurrent.LinkedBlockingQueue;

import edu.sdust.haredis.common.RedisMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

/**
 * ClassName: RedisClientHandler
 * @Description: Redis代理客户端Handler
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月10日 下午8:56:13
 */
public final class RedisProxyClientHandler extends ChannelInboundHandlerAdapter {

	//对应的Redis代理客户端
	private RedisProxyClient redisProxyClient;

	//Redis任务队列
	private LinkedBlockingQueue<RedisTask> taskQueue = new LinkedBlockingQueue<RedisTask>();

	public RedisProxyClientHandler(RedisProxyClient redisProxyClient) {
		super();
		this.redisProxyClient = redisProxyClient;
	}

	public LinkedBlockingQueue<RedisTask> getTaskQueue() {
		return taskQueue;
	}

	/**
	 * Redis响应结果处理
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		RedisMessage redisMessage = (RedisMessage) msg;
		//RedisMessageUtils.printAggregatedRedisResponse(redisMessage);
		RedisTask task = taskQueue.poll();
		if (task != null) {
			if (task.getMessageType() == RedisTask.SLAVEOF_MESSAGE_TYPE && redisMessage instanceof ErrorRedisMessage) {
				RedisProxyClient.executeRedisTask(task);
			}
			ChannelHandlerContext chc = task.getChannelHandlerContext();
			if (chc != null && !chc.isRemoved()) {
				chc.writeAndFlush(RedisMessageUtils.duplicateRedisMessage(redisMessage));
			}
		}
	}

	/**
	 * channel断开事件
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (redisProxyClient.getDatabaseNumber() == 0 && redisProxyClient.getConnected()) {
			redisProxyClient.setConnected(false);
			RedisProxyClient.redisDown(redisProxyClient.getRedisInfo(), ctx.alloc());
		}
	}

	/**
	 * 发生异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}
}
