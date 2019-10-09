package edu.sdust.haredis.visualization.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.sdust.haredis.heartbeat.HeartBeatData;
import edu.sdust.haredis.visualization.common.CPUHistogramData;
import edu.sdust.haredis.visualization.common.ColdBackupFile;
import edu.sdust.haredis.visualization.common.DateUtils;
import edu.sdust.haredis.visualization.common.FileSystemHistogramData;
import edu.sdust.haredis.visualization.common.PieChartData;
import edu.sdust.haredis.visualization.heartbeat.HeartBeatVisualizationClient;
import edu.sdust.haredis.visualization.service.HARedisVisualizationDataService;

@Service
public class HARedisVisualizationDataServiceImpl implements HARedisVisualizationDataService {

	@Value("${backups.file.location}")
	private String backupsFileLocation;

	@Resource(name = "masterClient")
	private HeartBeatVisualizationClient masterClient;
	@Resource(name = "slaveClient")
	private HeartBeatVisualizationClient slaveClient;

	private Map<String, Object> getStateData(HeartBeatVisualizationClient client) {
		Map<String, Object> stateData = new HashMap<String, Object>(11);
		if (client.getConnect()) {
			client.getHandler().getLock().lock();
			try {
				List<PieChartData> memoryPieChartList = new ArrayList<PieChartData>(2);
				List<PieChartData> swapMemoryPieChartList = new ArrayList<PieChartData>(2);
				List<PieChartData> jvmMemoryPieChartList = new ArrayList<PieChartData>(2);
				HeartBeatData data = client.getHeartBeatData();
				memoryPieChartList.add(new PieChartData("内存使用量", data.getMemoryMap().get("mem.used")));
				memoryPieChartList.add(new PieChartData("内存剩余量", data.getMemoryMap().get("mem.free")));
				swapMemoryPieChartList.add(new PieChartData("交换区使用量", data.getMemoryMap().get("swap.used")));
				swapMemoryPieChartList.add(new PieChartData("交换区剩余量", data.getMemoryMap().get("swap.free")));
				jvmMemoryPieChartList.add(new PieChartData("内存使用量",
						Double.toString(Double.parseDouble(data.getJavaProperty().get("jvm.totalMemory"))
								- Double.parseDouble(data.getJavaProperty().get("jvm.freeMemory")))));
				jvmMemoryPieChartList.add(new PieChartData("内存剩余量", data.getJavaProperty().get("jvm.freeMemory")));
				List<HashMap<String, String>> cpuInfoList = data.getCpuInfoList();
				List<HashMap<String, String>> fileSystemList = data.getFileSystemList();
				List<CPUHistogramData> cpuHistogramList = new ArrayList<CPUHistogramData>(data.getCpuInfoList().size());
				List<FileSystemHistogramData> fileSystemHistogramList = new ArrayList<FileSystemHistogramData>(
						data.getFileSystemList().size());
				for (int i = 0; i < cpuInfoList.size(); i++) {
					HashMap<String, String> map = cpuInfoList.get(i);
					cpuHistogramList.add(new CPUHistogramData(map.get("cpuinfo.user"), map.get("cpuinfo.sys"),
							map.get("cpuinfo.wait"), map.get("cpuinfo.nice"), map.get("cpuinfo.idle"),
							map.get("cpuinfo.mhz"), map.get("cpuinfo.vendor"), map.get("cpuinfo.model"),
							map.get("cpuinfo.cacheSize")));
				}
				for (int i = 0; i < fileSystemList.size(); i++) {
					HashMap<String, String> map = fileSystemList.get(i);
					fileSystemHistogramList.add(new FileSystemHistogramData(map.get("fs.dirName"),
							map.get("fs.sysTypeName"), map.get("fs.total"), map.get("fs.free"), map.get("fs.avail"),
							map.get("fs.used"), map.get("fs.usePercent")));
				}
				stateData.put("connect", true);
				stateData.put("ip", client.getIp());
				stateData.put("haveVirtualIp", data.getHaveVirtualIp());
				stateData.put("redisInfoList", data.getRedisInfoList());
				stateData.put("JavaProperty", data.getJavaProperty());
				stateData.put("os", data.getOs());
				stateData.put("memoryPieChart", memoryPieChartList);
				stateData.put("swapMemoryPieChart", swapMemoryPieChartList);
				stateData.put("jvmMemoryPieChart", jvmMemoryPieChartList);
				stateData.put("cpuHistogram", cpuHistogramList);
				stateData.put("fileSystemHistogram", fileSystemHistogramList);
			} finally {
				client.getHandler().getLock().unlock();
			}
		} else {
			stateData.put("connect", false);
			stateData.put("ip", client.getIp());
		}
		return stateData;
	}

	@Scheduled(cron = "0 0 1 * * ?")
	@Override
	public Map<String, Map<String, Object>> getStateData() {
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>(2);
		map.put("master", getStateData(masterClient));
		map.put("slave", getStateData(slaveClient));
		return map;
	}

	@Override
	public String redisColdBackup() {
		Date date = new Date();
		if (masterClient.getConnect()) {
			masterClient.sendFileMsg(date);
			return "haredis_" + DateUtils.getStringDate(date) + ".zip";
		} else if (slaveClient.getConnect()) {
			slaveClient.sendFileMsg(date);
			return "haredis_" + DateUtils.getStringDate(date) + ".zip";
		}
		return null;
	}

	@Override
	public List<ColdBackupFile> findColdBackupListByDate(String date) {
		File folder = new File(System.getProperty("evan.webapp") + backupsFileLocation + "/" + date);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			List<ColdBackupFile> coldBackupList = new ArrayList<ColdBackupFile>(files.length);
			for (File file : files) {
				if (file.isFile()) {
					coldBackupList.add(new ColdBackupFile(file.getName(), DateUtils.formatStringDate(file.getName())));
				}
			}
			return coldBackupList;
		}
		return null;
	}

}
