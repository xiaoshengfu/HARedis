package edu.sdust.haredis.common;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;

/**
 * ClassName: RedisMessageUtils
 * @Description: 操作RedisMessage工具类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年4月11日 下午5:20:01
 */
public final class RedisMessageUtils {

	/**
	 * @Description: 打印RedisMessage信息
	 * @param msg void 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午5:20:26
	 */
	public static void printAggregatedRedisResponse(RedisMessage msg) {
		if (msg instanceof SimpleStringRedisMessage) {
			System.out.println(((SimpleStringRedisMessage) msg).content());
		} else if (msg instanceof ErrorRedisMessage) {
			System.out.println(((ErrorRedisMessage) msg).content());
		} else if (msg instanceof IntegerRedisMessage) {
			System.out.println(((IntegerRedisMessage) msg).value());
		} else if (msg instanceof FullBulkStringRedisMessage) {
			System.out.println("FullBulkStringRedisMessage");
			System.out.println(getString((FullBulkStringRedisMessage) msg));
		} else if (msg instanceof ArrayRedisMessage) {
			for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
				printAggregatedRedisResponse(child);
			}
		} else {
			throw new CodecException("unknown message type: " + msg);
		}
	}

	/**
	 * @Description: 获取FullBulkStringRedisMessage的字符串内容
	 * @param msg
	 * @return String 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午5:21:25
	 */
	public static String getString(FullBulkStringRedisMessage msg) {
		if (msg.isNull()) {
			return "(nil)";
		}
		return msg.content().toString(CharsetUtil.UTF_8);
	}

	/**
	 * @Description: 获取ArrayRedisMessage的字符串数组
	 * @param arrayRedisMessage
	 * @return String[] 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午5:22:19
	 */
	public static String[] getStringMessage(ArrayRedisMessage arrayRedisMessage) {
		List<RedisMessage> list = arrayRedisMessage.children();
		String[] message = new String[list.size()];
		RedisMessage redisMessage = null;
		for (int i = 0; i < list.size(); i++) {
			redisMessage = list.get(i);
			if (redisMessage instanceof FullBulkStringRedisMessage) {
				message[i] = getString((FullBulkStringRedisMessage) redisMessage);
			}
		}
		return message;
	}

	/**
	 * @Description: 根据字符串数组生成对应ArrayRedisMessage
	 * @param byteBufAllocator
	 * @param commands
	 * @return ArrayRedisMessage 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午5:24:18
	 */
	public static ArrayRedisMessage getRedisMessage(ByteBufAllocator byteBufAllocator, String[] commands) {
		List<RedisMessage> children = new ArrayList<>(commands.length);
		for (String cmdString : commands) {
			children.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(byteBufAllocator, cmdString)));
		}
		ArrayRedisMessage redisMessage = new ArrayRedisMessage(children);
		return redisMessage;
	}

	/**
	 * @Description: 根据字符串生成对应ArrayRedisMessage
	 * @param byteBufAllocator
	 * @param msg
	 * @return ArrayRedisMessage 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午5:26:24
	 */
	public static ArrayRedisMessage getRedisMessage(ByteBufAllocator byteBufAllocator, String msg) {
		String[] commands = msg.split("\\s+");
		List<RedisMessage> children = new ArrayList<>(commands.length);
		for (String cmdString : commands) {
			children.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(byteBufAllocator, cmdString)));
		}
		ArrayRedisMessage redisMessage = new ArrayRedisMessage(children);
		return redisMessage;
	}

	/**
	 * @Description: FullBulkStringRedisMessage->duplicate
	 * @param redisMessage
	 * @return RedisMessage 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月12日 上午11:31:30
	 */
	public static RedisMessage duplicateRedisMessage(RedisMessage redisMessage) {
		if (redisMessage instanceof ArrayRedisMessage) {
			ArrayRedisMessage arm = (ArrayRedisMessage) redisMessage;
			List<RedisMessage> rmlist = arm.children();
			for (int i = 0; i < rmlist.size(); i++) {
				rmlist.set(i, duplicateRedisMessage(rmlist.get(i)));
			}
			return arm;
		} else if (redisMessage instanceof FullBulkStringRedisMessage) {
			FullBulkStringRedisMessage fbsrm = (FullBulkStringRedisMessage) redisMessage;
			return fbsrm.duplicate();
		} else {
			return redisMessage;
		}
	}
}
