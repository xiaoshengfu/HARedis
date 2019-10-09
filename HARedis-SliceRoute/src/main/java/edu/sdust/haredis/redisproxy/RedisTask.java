package edu.sdust.haredis.redisproxy;

import java.util.Arrays;
import java.util.HashSet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.RedisMessage;

/**
 * ClassName: RedisTask
 * @Description: Redis请求处理工作类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月10日 下午8:56:50
 */
public final class RedisTask {

	public static final int SLAVE_MESSAGE_TYPE = 0;
	public static final int MASTER_MESSAGE_TYPE = 1;
	public static final int AUTH_MESSAGE_TYPE = 2;
	public static final int SELECT_MESSAGE_TYPE = 3;
	public static final int SLAVEOF_MESSAGE_TYPE = 4;
	public static final int BGSAVE_MESSAGE_TYPE = 5;
	public static final int FLUSHALL_MESSAGE_TYPE = 6;
	public static final int PING_MESSAGE_TYPE = 7;
	public static HashSet<String> slaveCommandSet = new HashSet<String>(Arrays.asList("DUMP", "EXISTS", "KEYS", "TYPE",
			"GET", "GETRANGE", "GETBIT", "STRLEN", "HEXISTS", "HGET", "HGETALL", "HKEYS", "HLEN", "HMGET", "HVALS",
			"HSCAN", "LINDEX", "LLEN", "LRANGE", "SCARD", "SISMEMBER", "SMEMBERS", "SRANDMEMBER", "SSCAN", "ZCARD",
			"ZCOUNT", "ZLEXCOUNT", "ZRANGE", "ZRANGEBYLEX", "ZRANGEBYSCORE", "ZRANK", "ZSCAN", "PFCOUNT"));
	public static HashSet<String> masterCommandSet = new HashSet<String>(Arrays.asList("DEL", "EXPIRE", "EXPIREAT",
			"PEXPIRE", "PEXPIREAT", "PERSIST", "PTTL", "TTL", "SET", "GETSET", "SETBIT", "SETEX", "SETNX", "SETRANGE",
			"PSETEX", "INCR", "INCRBY", "INCRBYFLOAT", "DECR", "DECRBY", "APPEND", "HDEL", "HINCRBY", "HINCRBYFLOAT",
			"HMSET", "HSET", "HSETNX", "BLPOP", "BRPOP", "LINSERT", "LPOP", "LPUSH", "LPUSHX", " LREM", "LSET", "LTRIM",
			"RPOP", "RPUSH", "RPUSHX", "SADD", "SPOP", "SREM", "ZADD", "ZINCRBY", "ZREM", "ZREMRANGEBYLEX",
			"ZREMRANGEBYRANK", "ZREMRANGEBYSCORE", "ZREVRANGE", "ZREVRANGEBYSCORE", "ZREVRANK", "ZSCORE", "PFADD"));
	//请求时间
	private long time;
	//请求内容
	private RedisMessage redisMessage;
	//分发到的分片编号
	private Integer sliceNumber;
	//是否分发到Master节点
	private Boolean master;
	//请求库号
	private Integer databaseNumber;
	//请求类型
	private int messageType;
	//当前连接的ChannelHandlerContext
	private ChannelHandlerContext channelHandlerContext;

	public RedisTask() {
		super();
	}

	public RedisTask(RedisMessage redisMessage) {
		super();
		this.redisMessage = redisMessage;
	}

	public RedisTask(Integer databaseNumber, ChannelHandlerContext channelHandlerContext) {
		super();
		this.databaseNumber = databaseNumber;
		this.channelHandlerContext = channelHandlerContext;
	}

	public RedisTask(long time, RedisMessage redisMessage, Integer sliceNumber, Boolean master, Integer databaseNumber,
			int messageType) {
		super();
		this.time = time;
		this.redisMessage = redisMessage;
		this.sliceNumber = sliceNumber;
		this.master = master;
		this.databaseNumber = databaseNumber;
		this.messageType = messageType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public RedisMessage getRedisMessage() {
		return redisMessage;
	}

	public void setRedisMessage(RedisMessage redisMessage) {
		this.redisMessage = redisMessage;
	}

	public Integer getSliceNumber() {
		return sliceNumber;
	}

	public void setSliceNumber(Integer sliceNumber) {
		this.sliceNumber = sliceNumber;
	}

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public Integer getDatabaseNumber() {
		return databaseNumber;
	}

	public void setDatabaseNumber(Integer databaseNumber) {
		this.databaseNumber = databaseNumber;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public ChannelHandlerContext getChannelHandlerContext() {
		return channelHandlerContext;
	}

	public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
		this.channelHandlerContext = channelHandlerContext;
	}

	@Override
	public String toString() {
		return "RedisTask [time=" + time + ", redisMessage=" + redisMessage + ", sliceNumber=" + sliceNumber
				+ ", master=" + master + ", databaseNumber=" + databaseNumber + ", messageType=" + messageType
				+ ", channelHandlerContext=" + channelHandlerContext + "]";
	}

}
