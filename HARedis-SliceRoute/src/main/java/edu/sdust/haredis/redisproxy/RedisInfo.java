package edu.sdust.haredis.redisproxy;

import java.io.Serializable;

/**
 * ClassName: RedisInfo
 * @Description: Redis基本信息、状态信息和Redis连接存储类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年5月15日 下午3:46:23
 */
public final class RedisInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String host; //ip
	private Integer port; //端口
	private String password; //密码
	private Boolean master = false; //是否为Master
	private Boolean run = false; //是否运行
	private String serviceName; //服务名称
	private String backupsLocation; //持久化文件位置
	private Integer sliceNumber; //编号
	private transient RedisInfo sliceRedisInfo; //同一分片的Redis信息

	public RedisInfo() {
		super();
	}

	public RedisInfo(Boolean master, Integer sliceNumber) {
		super();
		this.master = master;
		this.sliceNumber = sliceNumber;
	}


	public RedisInfo(String host, Integer port, String password, String serviceName, String backupsLocation,
			Integer sliceNumber) {
		super();
		this.host = host;
		this.port = port;
		this.password = password;
		this.serviceName = serviceName;
		this.backupsLocation = backupsLocation;
		this.sliceNumber = sliceNumber;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public Boolean getRun() {
		return run;
	}

	public void setRun(Boolean run) {
		this.run = run;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getBackupsLocation() {
		return backupsLocation;
	}

	public void setBackupsLocation(String backupsLocation) {
		this.backupsLocation = backupsLocation;
	}

	public Integer getSliceNumber() {
		return sliceNumber;
	}

	public void setSliceNumber(Integer sliceNumber) {
		this.sliceNumber = sliceNumber;
	}

	public RedisInfo getSliceRedisInfo() {
		return sliceRedisInfo;
	}

	public void setSliceRedisInfo(RedisInfo sliceRedisInfo) {
		this.sliceRedisInfo = sliceRedisInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		result = prime * result + ((sliceNumber == null) ? 0 : sliceNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RedisInfo other = (RedisInfo) obj;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		if (sliceNumber == null) {
			if (other.sliceNumber != null)
				return false;
		} else if (!sliceNumber.equals(other.sliceNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RedisInfo [host=" + host + ", port=" + port + ", password=" + password + ", master=" + master + ", run="
				+ run + ", serviceName=" + serviceName + ", backupsLocation=" + backupsLocation + ", sliceNumber="
				+ sliceNumber + "]";
	}
}
