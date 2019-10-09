package edu.sdust.haredis.common;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import edu.sdust.haredis.redisproxy.RedisInfo;

/**
 * ClassName: Configuration
 * @Description: 配置文件读取工具类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月5日 下午12:35:28
 */
public final class Configuration {

	private static String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
	private static ResourceBundle rb = ResourceBundle.getBundle(path.substring(5, path.length()) + "conf");

	/**
	 * @Description: 解析redis.info字符串
	 * @param redisInfo
	 * @param host
	 * @return List<RedisInfo> 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月18日 下午4:49:43
	 */
	public static List<RedisInfo> getRedisInfoList(String redisInfo, String host) {
		String[] infos = redisInfo.split("&");
		List<RedisInfo> list = new ArrayList<RedisInfo>(infos.length);
		for (int i = 0; i < infos.length; i++) {
			String[] ri = infos[i].split("#");
			if (ri.length == 4) {
				list.add(new RedisInfo(host, new Integer(ri[0]), ri[1], ri[2], ri[3], i));
			}
		}
		return list;
	}

	/**
	 * 启动模式master或slave
	 */
	public static final String STARTUP_TYPE = rb.getString("startup.type");
	/**
	 * 虚拟ip地址
	 */
	public static final String VIRTUAL_IP = rb.getString("virtual.ip");
	/**
	 * 挂载虚拟ip的网卡名称
	 */
	public static final String MOUNT_NETWORK_CARD_NAME = rb.getString("mount.network.card.name");
	/**
	 * haredis服务访问密码
	 */
	public static final String HAREDIS_ACCESS_PASSWORD = rb.getString("haredis.access.password");
	/**
	 * 本地Redis信息
	 */
	public static final String LOCAL_REDIS_INFO = rb.getString("local.redis.info");
	/**
	 * 本地主机ip地址
	 */
	public static final String LOCAL_IP = rb.getString("local.ip");
	/**
	 * 本地心跳服务端口
	 */
	public static final int LOCAL_HEARTBEAT_PORT = Integer.parseInt(rb.getString("local.heartbeat.port"));
	/**
	 * 本地redis代理服务端口
	 */
	public static final int LOCAL_REDIS_PROXY_PORT = Integer.parseInt(rb.getString("local.redis.proxy.port"));
	/**
	 * redis代理缓存任务个数
	 */
	public static final int LOCAL_PROXY_CACHE_TASK_NUM = Integer.parseInt(rb.getString("local.proxy.cache.task.num"));
	/**
	 * 本地haredis服务名称
	 */
	public static final String LOCAL_HAREDIS_SERVICE_NAME = rb.getString("local.haredis.service.name");
	/**
	 * 远程主机ip地址
	 */
	public static final String REMOTE_IP = rb.getString("remote.ip");
	/**
	 * 远程心跳服务端口
	 */
	public static final int REMOTE_HAREDIS_HEARTBEAT_PORT = Integer
			.parseInt(rb.getString("remote.haredis.heartbeat.port"));
	/**
	 * 远程ssh连接登陆主机用户名
	 */
	public static final String REMOTE_SSH_USERNAME = rb.getString("remote.ssh.username");
	/**
	 * 远程ssh连接登陆主机密码
	 */
	public static final String REMOTE_SSH_PASSWORD = rb.getString("remote.ssh.password");

}
