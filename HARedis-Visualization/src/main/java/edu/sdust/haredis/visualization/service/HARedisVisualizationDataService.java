package edu.sdust.haredis.visualization.service;

import java.util.List;
import java.util.Map;

import edu.sdust.haredis.visualization.common.ColdBackupFile;

/**
 * ClassName: HARedisVisualizationDataService
 * @Description: TODO
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年5月26日 下午7:22:24
 */
public interface HARedisVisualizationDataService {

	/**
	 * @Description: 获取HARedis状态信息
	 * @return Map<String, Map<String, Object>>
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月26日 下午7:30:13
	 */
	public Map<String, Map<String, Object>> getStateData();

	/**
	 * @Description: Redis冷备份
	 * @return String Redis冷备份文件名称
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月26日 下午7:30:23
	 */
	public String redisColdBackup();

	/**
	 * @Description: 通过日期查询Redis备份文件列表
	 * @param date
	 * @return List<ColdBackupFile> 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月20日 下午5:16:38
	 */
	public List<ColdBackupFile> findColdBackupListByDate(String date);
}
