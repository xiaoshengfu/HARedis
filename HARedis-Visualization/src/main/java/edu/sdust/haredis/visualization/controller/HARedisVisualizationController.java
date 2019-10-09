package edu.sdust.haredis.visualization.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.sdust.haredis.visualization.common.DateUtils;
import edu.sdust.haredis.visualization.service.HARedisVisualizationDataService;

/**
 * ClassName: HARedisVisualizationController
 * @Description: HARedis可视化系统Controller
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年5月27日 上午8:34:51
 */
@Controller
public class HARedisVisualizationController {

	@Value("${backups.file.location}")
	private String backupsFileLocation;
	@Value("${virtual.ip}")
	private String virtualIp;

	@Autowired
	private HARedisVisualizationDataService hARedisVisualizationDataService;

	/**
	 * @Description: 首页
	 * @param model
	 * @return String 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月27日 上午8:32:03
	 */
	@RequestMapping("/haredis/visualization/index")
	public String showIndexPage(Model model) {
		model.addAttribute("vip", virtualIp);
		return "index";
	}

	/**
	 * @Description: 备份列表页
	 * @param date
	 * @param model
	 * @return String 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月27日 上午8:31:38
	 */
	@RequestMapping("/haredis/visualization/backups")
	public String showColdBackupsPage(String date, Model model) {
		if (date != null) {
			model.addAttribute("coldBackupList", hARedisVisualizationDataService.findColdBackupListByDate(date));
		}
		return "backups";
	}

	/**
	 * @Description: 获取服务状态数据
	 * @return Map<String,Map<String,Object>> 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月27日 上午8:33:52
	 */
	@RequestMapping("/haredis/visualization/state_data")
	@ResponseBody
	public Map<String, Map<String, Object>> getStateData() {
		return hARedisVisualizationDataService.getStateData();
	}

	/**
	 * @Description: 下载冷备份文件
	 * @param fileName
	 * @return ResponseEntity<byte[]> 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月27日 上午8:32:23
	 */
	@RequestMapping("/haredis/visualization/download/{fileName}")
	public ResponseEntity<byte[]> downloadColdBackupFile(@PathVariable String fileName) {
		if (StringUtils.isNotBlank(fileName)) {
			fileName = fileName.split(".zip")[0];
			HttpHeaders headers = new HttpHeaders();
			File file = new File(System.getProperty("evan.webapp") + backupsFileLocation + "/"
					+ DateUtils.getStringNowDate(fileName) + "/" + fileName + ".zip");
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", fileName + ".zip");
			if (file.exists()) {
				try {
					return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
				} catch (IOException e) {
					return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
				}
			} else {
				return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
			}
		} else {
			return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * @Description: 拉取冷备份文件
	 * @return Map<String,Object> 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年5月27日 上午8:33:19
	 */
	@RequestMapping("/haredis/visualization/pull")
	@ResponseBody
	public Map<String, Object> pullColdBackupFile() {
		Map<String, Object> map = new HashMap<String, Object>(1);
		map.put("fileName", hARedisVisualizationDataService.redisColdBackup());
		return map;
	}
}
