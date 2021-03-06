package edu.sdust.haredis.heartbeat;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * ClassName: MarshallingCodeCFactory
 * @Description: Marshalling工厂
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月5日 下午7:34:35
 */
public final class MarshallingCodeCFactory {

	/**
	 * @Description: Marshalling解码器
	 * @return MarshallingDecoder 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月5日 下午7:35:53
	 */
	public static MarshallingDecoder buildMarshallingDecoder() {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
		MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024 * 1024 * 1);
		return decoder;
	}

	/**
	 * @Description: Marshalling编码器
	 * @return MarshallingEncoder 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月5日 下午7:36:27
	 */
	public static MarshallingEncoder buildMarshallingEncoder() {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
		MarshallingEncoder encoder = new MarshallingEncoder(provider);
		return encoder;
	}
}
