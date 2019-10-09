package edu.sdust.haredis.visualization.common;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ClassName: FileUtils
 * @Description: 文件操作工具类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月17日 下午1:59:59
 */
public final class FileUtils {

	/**
	 * @Description: 根据文件路径生成文件byte数组
	 * @param filePath
	 * @return byte[] 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月17日 下午2:00:27
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				return null;
			}
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * @Description: 根据文件生成文件byte数组
	 * @param file
	 * @return byte[] 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月17日 下午2:00:27
	 */
	public static byte[] getBytes(File file) {
		byte[] buffer = null;
		try {
			if (!file.exists()) {
				return null;
			}
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * @Description: 根据byte数组生成文件
	 * @param bfile
	 * @param filePath
	 * @param fileName void 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月17日 下午2:01:31
	 */
	public static void getFile(byte[] bfile, String filePath, String fileName) {
		if (bfile == null) {
			return;
		}
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.isDirectory() && !dir.exists()) {
				dir.mkdirs();
			}
			file = new File(filePath + "/" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * @Description: 将多个文件压缩为zip文件
	 * @param srcFile
	 * @param zipFile 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月26日 下午8:19:40
	 */
	public static void zipFiles(List<File> srcFile, File zipFile) {
		byte[] buf = new byte[1024];
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
			for (int i = 0; i < srcFile.size(); i++) {
				FileInputStream in = new FileInputStream(srcFile.get(i));
				out.putNextEntry(new ZipEntry(srcFile.get(i).getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
