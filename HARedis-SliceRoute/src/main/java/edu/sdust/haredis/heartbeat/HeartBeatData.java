package edu.sdust.haredis.heartbeat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.sdust.haredis.redisproxy.RedisInfo;

/**
 * ClassName: RequestInfo
 * @Description: 心跳请求数据包
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月5日 上午11:56:22
 */
public final class HeartBeatData implements Serializable {

	private static final long serialVersionUID = 1L;
	//请求类型
	private Integer requestType;
	//haredis访问密码
	private String accessPassword;
	//本机IP
	private String ip;
	//是否持有虚拟ip
	private Boolean haveVirtualIp;
	//是否初始化
	private Boolean init;
	//是否为Master
	private Boolean master;
	//服务名称
	private String serviceName;
	//挂载虚拟ip网卡名称
	private String networkCardName;
	//Redis信息集合
	private List<RedisInfo> redisInfoList;
	//持久化文件备份时间
	private Date backupsTime;
	//Redis持久化文件
	private byte[] backupsFile;
	//Java环境状态
	private HashMap<String, String> javaProperty;
	//cup状态
	private List<HashMap<String, String>> cpuInfoList;
	//内存状态
	private HashMap<String, String> memoryMap;
	//系统信息
	private HashMap<String, String> os;
	//文件系统状态
	private List<HashMap<String, String>> fileSystemList;
	//网络状态
	private List<HashMap<String, String>> netList;
	//网卡状态
	private List<HashMap<String, String>> ethernetList;

	public HeartBeatData() {
		super();
	}

	public Integer getRequestType() {
		return requestType;
	}

	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}

	public String getAccessPassword() {
		return accessPassword;
	}

	public void setAccessPassword(String accessPassword) {
		this.accessPassword = accessPassword;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Boolean getHaveVirtualIp() {
		return haveVirtualIp;
	}

	public void setHaveVirtualIp(Boolean haveVirtualIp) {
		this.haveVirtualIp = haveVirtualIp;
	}

	public Boolean getInit() {
		return init;
	}

	public void setInit(Boolean init) {
		this.init = init;
	}

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getNetworkCardName() {
		return networkCardName;
	}

	public void setNetworkCardName(String networkCardName) {
		this.networkCardName = networkCardName;
	}

	public List<RedisInfo> getRedisInfoList() {
		return redisInfoList;
	}

	public void setRedisInfoList(List<RedisInfo> redisInfoList) {
		this.redisInfoList = redisInfoList;
	}

	public Date getBackupsTime() {
		return backupsTime;
	}

	public void setBackupsTime(Date backupsTime) {
		this.backupsTime = backupsTime;
	}

	public byte[] getBackupsFile() {
		return backupsFile;
	}

	public void setBackupsFile(byte[] backupsFile) {
		this.backupsFile = backupsFile;
	}

	public HashMap<String, String> getJavaProperty() {
		return javaProperty;
	}

	public void setJavaProperty(HashMap<String, String> javaProperty) {
		this.javaProperty = javaProperty;
	}

	public List<HashMap<String, String>> getCpuInfoList() {
		return cpuInfoList;
	}

	public void setCpuInfoList(List<HashMap<String, String>> cpuInfoList) {
		this.cpuInfoList = cpuInfoList;
	}

	public HashMap<String, String> getMemoryMap() {
		return memoryMap;
	}

	public void setMemoryMap(HashMap<String, String> memoryMap) {
		this.memoryMap = memoryMap;
	}

	public HashMap<String, String> getOs() {
		return os;
	}

	public void setOs(HashMap<String, String> os) {
		this.os = os;
	}

	public List<HashMap<String, String>> getFileSystemList() {
		return fileSystemList;
	}

	public void setFileSystemList(List<HashMap<String, String>> fileSystemList) {
		this.fileSystemList = fileSystemList;
	}

	public List<HashMap<String, String>> getNetList() {
		return netList;
	}

	public void setNetList(List<HashMap<String, String>> netList) {
		this.netList = netList;
	}

	public List<HashMap<String, String>> getEthernetList() {
		return ethernetList;
	}

	public void setEthernetList(List<HashMap<String, String>> ethernetList) {
		this.ethernetList = ethernetList;
	}

	@Override
	public String toString() {
		return "HeartBeatData [requestType=" + requestType + ", accessPassword=" + accessPassword + ", ip=" + ip
				+ ", haveVirtualIp=" + haveVirtualIp + ", init=" + init + ", master=" + master + ", serviceName="
				+ serviceName + ", networkCardName=" + networkCardName + ", netList=" + netList + ", ethernetList="
				+ ethernetList + "]";
	}
}
