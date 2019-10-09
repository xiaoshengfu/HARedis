package edu.sdust.haredis.visualization.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	private static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @Description: 获取当前时间的yyyy-MM-dd_HH-mm-ss格式的字符串
	 * @return String 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月26日 下午8:49:08
	 */
	public static String getStringDate() {
		return format1.format(new Date());
	}

	/**
	 * @Description: 获取指定时间的yyyy-MM-dd_HH-mm-ss格式的字符串
	 * @return String 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月26日 下午8:49:08
	 */
	public static String getStringDate(Date date) {
		return format1.format(date);
	}

	/**
	 * @Description: 将haredis_2019-03-27_16-41-35.zip装成转成yyyy-MM-dd HH:mm:ss
	 * @param stringDate
	 * @return String 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月26日 下午10:02:57
	 */
	public static String formatStringDate(String stringDate) {
		if (stringDate != null) {
			String[] strings1 = stringDate.split(".zip");
			if (strings1.length == 1) {
				String[] strings2 = strings1[0].split("_");
				if (strings2.length == 3) {
					String end = strings2[2].replace("-", ":");
					return strings2[1] + " " + end;
				}
			}
		}
		return null;
	}

	public static String getStringNowDate() {
		return format2.format(new Date());
	}

	/**
	 * @Description: 将haredis_2019-03-27_16-41-35.rdb装成转成yyyy-MM-dd
	 * @param fileName
	 * @return String 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月29日 上午10:14:18
	 */
	public static String getStringNowDate(String fileName) {
		if (fileName != null) {
			String[] strings1 = fileName.split(".rdb");
			if (strings1.length == 1) {
				String[] strings2 = strings1[0].split("_");
				if (strings2.length == 3) {
					return strings2[1];
				}
			}
		}
		return null;
	}
}
