package edu.sdust.haredis.redisproxy;

import edu.sdust.haredis.common.Configuration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;

/**
 * ClassName: RedisClient
 * @Description: Redis代理服务端
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月10日 上午9:20:53
 */
public final class RedisProxyService {
	public void run() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
		NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline pipeline = socketChannel.pipeline();
							pipeline.addLast(new RedisDecoder(true));
							pipeline.addLast(new RedisBulkStringAggregator());
							pipeline.addLast(new RedisArrayAggregator());
							pipeline.addLast(new RedisEncoder());
							pipeline.addLast(new RedisProxyServiceHandler());
						}
					});
			Channel ch = bootstrap.bind(Configuration.LOCAL_REDIS_PROXY_PORT).sync().channel();
			ch.closeFuture().sync();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}
