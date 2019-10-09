package edu.sdust.haredis.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * ClassName: SSH
 * @Description: JSch工具类(远程执行shell命令)
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年4月5日 上午8:14:40
 */
public final class JSchSSH {
	private String userName;
	private String password;
	private String host;

	public JSchSSH(String host, String userName, String password) {
		this.host = host;
		this.userName = userName;
		this.password = password;
	}

	private Session getSession() {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(userName, host, 22);
			session.setPassword(password);
			session.setTimeout(1200);
			Properties props = new Properties();
			props.put("StrictHostKeyChecking", "no");
			session.setConfig(props);
			session.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return session;
	}

	public boolean exec(String[] cmds) {
		StringBuffer cmdBuffer = new StringBuffer();
		for (int i = 0; i < cmds.length; i++) {
			if (i != 0) {
				cmdBuffer.append(" ");
			}
			cmdBuffer.append(cmds[i]);
		}
		return exec(cmdBuffer.toString());
	}

	public boolean exec(String cmd) {
		Session session = getSession();
		Channel channel = null;
		InputStream in = null;
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(cmd);
			((ChannelExec) channel).setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err); // 获取执行错误的信息

			in = channel.getInputStream();
			channel.connect();
			byte[] b = new byte[1024];
			int size = -1;
			while ((size = in.read(b, 0, 1024)) > -1) {
				System.out.print(new String(b, 0, size)); // 打印执行命令的所返回的信息
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			// 关闭连接
			channel.disconnect();
			session.disconnect();
		}
		return false;
	}
}