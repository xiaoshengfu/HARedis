package edu.sdust.haredis.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

/**
 * ClassName: SigarUtils
 * @Description: Sigar工具类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月8日 上午9:19:26
 */
public final class SigarUtils {

	public static HashMap<String, String> javaProperty() {
		Runtime r = Runtime.getRuntime();
		Properties props = System.getProperties();
		HashMap<String, String> property = new HashMap<String, String>();
		property.put("jvm.totalMemory", Double.toString(r.totalMemory() / 1024L));// JVM可以使用的总内存
		property.put("jvm.freeMemory", Double.toString(r.freeMemory() / 1024L));// JVM可以使用的剩余内存
		property.put("jvm.availableProcessors", Integer.toString(r.availableProcessors()));// JVM可以使用的处理器个数
		property.put("jvmVendor", props.getProperty("java.vm.vendor"));// Java的虚拟机实现供应商
		property.put("name", props.getProperty("java.vm.name"));// Java的虚拟机实现名称
		property.put("javaVersion", props.getProperty("java.version"));// Java的运行环境版本
		property.put("javaVendor", props.getProperty("java.vendor"));// Java的运行环境供应商
		property.put("classVersion", props.getProperty("java.class.version"));// Java的类格式版本号
		return property;
	}

	public static HashMap<String, String> memory() throws SigarException {
		Sigar sigar = new Sigar();
		HashMap<String, String> memoryMap = new HashMap<String, String>();
		Mem mem = sigar.getMem();
		Swap swap = sigar.getSwap();
		memoryMap.put("mem.total", Double.toString(mem.getTotal() / 1024L));// 内存总量
		memoryMap.put("mem.used", Double.toString(mem.getUsed() / 1024L));// 当前内存使用量
		memoryMap.put("mem.free", Double.toString(mem.getFree() / 1024L));// 当前内存剩余量
		memoryMap.put("swap.total", Double.toString(swap.getTotal() / 1024L));// 交换区总量
		memoryMap.put("swap.used", Double.toString(swap.getUsed() / 1024L));// 当前交换区使用量
		memoryMap.put("swap.free", Double.toString(swap.getFree() / 1024L));// 当前交换区剩余量
		return memoryMap;
	}

	public static List<HashMap<String, String>> cpuInfoList() throws SigarException {
		Sigar sigar = new Sigar();
		CpuInfo infos[] = sigar.getCpuInfoList();
		List<HashMap<String, String>> cpuInfoList = new ArrayList<HashMap<String, String>>(infos.length);
		HashMap<String, String> map = null;
		CpuPerc cpuList[] = sigar.getCpuPercList();
		for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
			map = new HashMap<String, String>();
			map.put("cpuinfo.mhz", Integer.toString(infos[i].getMhz()));// CPU的总量MHz
			map.put("cpuinfo.vendor", infos[i].getVendor());// 获得CPU的卖主，如：Intel
			map.put("cpuinfo.model", infos[i].getModel());// 获得CPU的类别，如：Celeron
			map.put("cpuinfo.cacheSize", Long.toString(infos[i].getCacheSize()));// 缓冲存储器数量
			map.put("cpuinfo.combined", Double.toString(cpuList[i].getCombined()));// 总的使用率
			map.put("cpuinfo.user", Double.toString(cpuList[i].getUser()));// 用户使用率
			map.put("cpuinfo.sys", Double.toString(cpuList[i].getSys()));// 系统使用率
			map.put("cpuinfo.nice", Double.toString(cpuList[i].getNice()));// 当前错误率
			map.put("cpuinfo.wait", Double.toString(cpuList[i].getWait()));// 当前等待率
			map.put("cpuinfo.idle", Double.toString(cpuList[i].getIdle()));// 当前空闲率
			cpuInfoList.add(map);
		}
		return cpuInfoList;
	}

	public static HashMap<String, String> os() {
		HashMap<String, String> os = new HashMap<String, String>();
		OperatingSystem opeSys = OperatingSystem.getInstance();
		os.put("cpuEndian", opeSys.getCpuEndian());//CPU存储方式
		os.put("dataModel", opeSys.getDataModel());//系统数据模型
		os.put("arch", opeSys.getArch());// 操作系统内核类型如： 386、486、586等x86
		os.put("description", opeSys.getDescription());// 系统描述
		os.put("vendorName", opeSys.getVendorName());// 操作系统名称
		os.put("version", opeSys.getVersion());// 操作系统的版本号
		os.put("vendor", opeSys.getVendor());//操作系统的卖主
		return os;
	}

	public static List<HashMap<String, String>> fileSystemList() throws Exception {
		Sigar sigar = new Sigar();
		HashMap<String, String> file = null;
		FileSystem fslist[] = sigar.getFileSystemList();
		List<HashMap<String, String>> fileSystemList = new ArrayList<HashMap<String, String>>(fslist.length);
		for (int i = 0; i < fslist.length; i++) {
			file = new HashMap<String, String>();
			FileSystem fs = fslist[i];
			file.put("fs.devName", fs.getDevName());// 分区的盘符名称
			file.put("fs.dirName", fs.getDirName());// 分区的盘符路径
			file.put("fs.flags", Long.toString(fs.getFlags()));// 分区的盘符标志
			file.put("fs.sysTypeName", fs.getSysTypeName());// 文件系统类型，比如 FAT32、NTFS
			file.put("fs.typeName", fs.getTypeName());// 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
			file.put("fs.type", Integer.toString(fs.getType()));// 文件系统类型
			FileSystemUsage usage = null;
			usage = sigar.getFileSystemUsage(fs.getDirName());
			file.put("fs.diskReads", Long.toString(usage.getDiskReads()));//读出
			file.put("fs.diskWrites", Long.toString(usage.getDiskWrites()));//写入
			switch (fs.getType()) {
			case 2:
				file.put("fs.total", Double.toString(usage.getTotal() / 1024L));// 文件系统总大小
				file.put("fs.free", Double.toString(usage.getFree() / 1024L));// 文件系统剩余大小
				file.put("fs.avail", Double.toString(usage.getAvail() / 1024L));// 文件系统可用大小
				file.put("fs.used", Double.toString(usage.getUsed() / 1024L));// 文件系统已经使用量
				file.put("fs.usePercent", Double.toString(usage.getUsePercent()));// 文件系统资源的利用率
				fileSystemList.add(file);
				break;
			}
		}
		return fileSystemList;
	}

	public static List<HashMap<String, String>> netList() throws Exception {
		Sigar sigar = new Sigar();
		String ifNames[] = sigar.getNetInterfaceList();
		HashMap<String, String> net = null;
		List<HashMap<String, String>> netInterfaceList = new ArrayList<HashMap<String, String>>(ifNames.length);
		for (int i = 0; i < ifNames.length; i++) {
			String name = ifNames[i];
			NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
			net = new HashMap<String, String>();
			net.put("net.name", name);// 网络设备名
			net.put("net.address", ifconfig.getAddress());// IP地址
			net.put("net.netmask", ifconfig.getNetmask());// 子网掩码
			if ((ifconfig.getFlags() & 1L) <= 0L) {
				continue;
			}
			NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
			net.put("net.rxPackets", Long.toString(ifstat.getRxPackets()));// 接收的总包裹数
			net.put("net.txPackets", Long.toString(ifstat.getTxPackets()));// 发送的总包裹数
			net.put("net.rxBytes", Long.toString(ifstat.getRxBytes()));// 接收到的总字节数
			net.put("net.txBytes", Long.toString(ifstat.getTxBytes()));// 发送的总字节数
			net.put("net.rxErrors", Long.toString(ifstat.getRxErrors()));// 接收到的错误包数
			net.put("net.txErrors", Long.toString(ifstat.getTxErrors()));// 发送数据包时的错误数
			net.put("net.rxDropped", Long.toString(ifstat.getRxDropped()));// 接收时丢弃的包数
			net.put("net.txDropped", Long.toString(ifstat.getTxDropped()));// 发送时丢弃的包数
			netInterfaceList.add(net);
		}
		return netInterfaceList;
	}

	public static List<HashMap<String, String>> ethernetList() throws SigarException {
		Sigar sigar = new Sigar();
		String[] ifaces = sigar.getNetInterfaceList();
		List<HashMap<String, String>> netInterfaceList = new ArrayList<HashMap<String, String>>(ifaces.length);
		HashMap<String, String> ethernet = null;
		for (int i = 0; i < ifaces.length; i++) {
			NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
			if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
					|| NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
				continue;
			}
			ethernet = new HashMap<String, String>();
			ethernet.put("ethernet.name", cfg.getName());// 名称
			ethernet.put("ethernet.address", cfg.getAddress());// IP地址
			ethernet.put("ethernet.broadcast", cfg.getBroadcast());// 网关广播地址
			ethernet.put("ethernet.hwaddr", cfg.getHwaddr());// 网卡MAC地址
			ethernet.put("ethernet.netmask", cfg.getNetmask());// 子网掩码
			ethernet.put("ethernet.description", cfg.getDescription());// 网卡描述信息
			ethernet.put("ethernet.type", cfg.getType());// 网卡类型
			netInterfaceList.add(ethernet);
		}
		return netInterfaceList;
	}
}
