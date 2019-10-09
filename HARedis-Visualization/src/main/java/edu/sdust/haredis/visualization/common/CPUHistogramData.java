package edu.sdust.haredis.visualization.common;

public class CPUHistogramData {

	private String value;// 总使用率
	private String user;// 用户使用率
	private String sys;// 系统使用率
	private String wait;// 当前等待率
	private String nice;// 当前错误率
	private String idle;// 当前空闲率
	private String mhz;// CPU总量MHz
	private String vendor;// CPU生产商
	private String model;// CPU类别
	private String cacheSize;// CPU缓存数量

	public CPUHistogramData() {
		super();
	}

	public CPUHistogramData(String user, String sys, String wait, String nice, String idle, String mhz, String vendor,
			String model, String cacheSize) {
		super();
		this.value = Double.toString(Double.parseDouble(user) + Double.parseDouble(sys));
		this.user = user;
		this.sys = sys;
		this.wait = wait;
		this.nice = nice;
		this.idle = idle;
		this.mhz = mhz;
		this.vendor = vendor;
		this.model = model;
		this.cacheSize = cacheSize;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}

	public String getWait() {
		return wait;
	}

	public void setWait(String wait) {
		this.wait = wait;
	}

	public String getNice() {
		return nice;
	}

	public void setNice(String nice) {
		this.nice = nice;
	}

	public String getIdle() {
		return idle;
	}

	public void setIdle(String idle) {
		this.idle = idle;
	}

	public String getMhz() {
		return mhz;
	}

	public void setMhz(String mhz) {
		this.mhz = mhz;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(String cacheSize) {
		this.cacheSize = cacheSize;
	}

}
