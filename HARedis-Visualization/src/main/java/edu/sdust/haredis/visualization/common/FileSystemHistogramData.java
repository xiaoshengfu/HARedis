package edu.sdust.haredis.visualization.common;

public class FileSystemHistogramData {

	private String value;// 总使用率
	private String dirName;// 分区的盘符路径
	private String sysTypeName;// 文件系统类型，比如 FAT32、NTFS
	private String total;// 文件系统总大小
	private String free;// 文件系统剩余大小
	private String avail;// 文件系统可用大小
	private String used;// 文件系统已经使用量
	private String usePercent;// 文件系统资源的利用率

	public FileSystemHistogramData() {
		super();
	}

	public FileSystemHistogramData(String dirName, String sysTypeName, String total, String free, String avail,
			String used, String usePercent) {
		super();
		this.value = Double.toString(Double.parseDouble(used) / Double.parseDouble(total));
		this.dirName = dirName;
		this.sysTypeName = sysTypeName;
		this.total = total;
		this.free = free;
		this.avail = avail;
		this.used = used;
		this.usePercent = usePercent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public String getSysTypeName() {
		return sysTypeName;
	}

	public void setSysTypeName(String sysTypeName) {
		this.sysTypeName = sysTypeName;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public String getAvail() {
		return avail;
	}

	public void setAvail(String avail) {
		this.avail = avail;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getUsePercent() {
		return usePercent;
	}

	public void setUsePercent(String usePercent) {
		this.usePercent = usePercent;
	}

}
