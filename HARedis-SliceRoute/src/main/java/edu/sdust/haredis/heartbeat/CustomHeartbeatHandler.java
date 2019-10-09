package edu.sdust.haredis.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * ClassName: CustomHeartbeatHandler
 * @Description: 自定义心跳抽象类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月6日 上午11:27:26
 */
public abstract class CustomHeartbeatHandler extends SimpleChannelInboundHandler<HeartBeatData> {

	/**
	 * 验证合法心跳客户端消息类型
	 */
	public static final int AUTH_MSG = 1;
	/**
	 * 验证成功消息类型
	 */
	public static final int AUTH_SUCCESS_MSG = 2;
	/**
	 * 状态检测消息类型
	 */
	public static final int PING_MSG = 3;
	/**
	 * 状态响应消息类型
	 */
	public static final int PONG_MSG = 4;
	/**
	 * 正常心跳消息类型
	 */
	public static final int CUSTOM_MSG = 5;
	/**
	 * 验证合法可视化客户端消息类型
	 */
	public static final int AUTH_VISUALIZATION_MSG = 6;
	/**
	 * 状态信息消息类型
	 */
	public static final int STATE_MSG = 7;
	/**
	 * 验证合法冷备份客户端消息类型
	 */
	public static final int AUTH_FAIL_MSG = 8;
	/**
	 * 没有通过验证消息类型
	 */
	public static final int NOT_AUTH_MSG = 9;
	/**
	 * 文件消息消息类型
	 */
	public static final int FILE_MSG = 10;

	@Override
	protected void channelRead0(ChannelHandlerContext context, HeartBeatData data) throws Exception {
		if (data.getRequestType() == PING_MSG) {
			sendPongMsg(context);
		} else if (data.getRequestType() == PONG_MSG) {
			//TODO
		} else {
			handleData(context, data);
		}
	}

	/**
	 * @Description: 发送PING消息
	 * @param context 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午4:58:05
	 */
	protected void sendPingMsg(ChannelHandlerContext context) {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(PING_MSG);
		context.channel().writeAndFlush(data);
	}

	/**
	 * @Description: 发送PONG消息
	 * @param context 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午4:58:05
	 */
	private void sendPongMsg(ChannelHandlerContext context) {
		HeartBeatData data = new HeartBeatData();
		data.setRequestType(PONG_MSG);
		context.channel().writeAndFlush(data);
	}

	/**
	 * @Description: 消息处理
	 * @param channelHandlerContext
	 * @param data void 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午4:59:01
	 */
	protected abstract void handleData(ChannelHandlerContext channelHandlerContext, HeartBeatData data);

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			case READER_IDLE:
				handleReaderIdle(ctx);
				break;
			case WRITER_IDLE:
				handleWriterIdle(ctx);
				break;
			case ALL_IDLE:
				handleAllIdle(ctx);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * @Description: Channel在规定时间内没有读操作触发时间的回调函数
	 * @param ctx 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午5:00:32
	 */
	protected void handleReaderIdle(ChannelHandlerContext ctx) {
	}

	/**
	 * @Description: Channel在规定时间内没有写操作触发时间的回调函数
	 * @param ctx  
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午5:00:40
	 */
	protected void handleWriterIdle(ChannelHandlerContext ctx) {
	}

	/**
	 * @Description: Channel在规定时间内没有读和写操作触发时间的回调函数
	 * @param ctx 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月2日 下午6:01:54
	 */
	protected void handleAllIdle(ChannelHandlerContext ctx) {
	}
}