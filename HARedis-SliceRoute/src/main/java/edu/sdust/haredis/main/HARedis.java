package edu.sdust.haredis.main;

import edu.sdust.haredis.heartbeat.HeartBeatClient;
import edu.sdust.haredis.heartbeat.HeartBeatServer;

/**
 * ClassName: HARedis
 * @Description: HARedis启动类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月6日 下午5:31:34
 */
public final class HARedis {
	public static void main(String[] args) {
		new HeartBeatClient().start();
		new HeartBeatServer().run();
	}
}
