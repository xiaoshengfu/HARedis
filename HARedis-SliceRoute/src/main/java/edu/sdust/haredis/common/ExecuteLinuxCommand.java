package edu.sdust.haredis.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ExecuteLinuxCommand
 * @Description: java执行linux命令工具类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月6日 上午11:42:33
 */
public final class ExecuteLinuxCommand {

	/**
	 * @Description: 执行本地shell命令
	 * @param cmd 命令 如：ls -l
	 * @return List<String> 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午4:47:51
	 */
	public static List<String> executeLinuxCmd(String cmd) {
		Runtime run = Runtime.getRuntime();
		try {
			Process process = run.exec(new String[] { "/bin/sh", "-c", cmd });
			InputStream in = process.getInputStream();
			BufferedReader bs = new BufferedReader(new InputStreamReader(in));
			List<String> list = new ArrayList<String>();
			String result = null;
			while ((result = bs.readLine()) != null) {
				list.add(result);
			}
			in.close();
			process.destroy();
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Description: 执行本地shell命令
	 * @param commands 命令集合
	 * @return List<String> 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年4月11日 下午4:49:17
	 */
	public static List<String> executeNewFlow(List<String> commands) {
		List<String> rspList = new ArrayList<String>();
		Runtime run = Runtime.getRuntime();
		try {
			Process proc = run.exec("/bin/bash", null, null);
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
			for (String line : commands) {
				out.println(line);
			}
			out.println("exit");
			String rspLine = "";
			while ((rspLine = in.readLine()) != null) {
				rspList.add(rspLine);
			}
			proc.waitFor();
			in.close();
			out.close();
			proc.destroy();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return rspList;
	}
}
