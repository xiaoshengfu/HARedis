<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<title>HARedis管理系统</title>
<link rel="stylesheet" type="text/css"
	href="/static/layui/css/layui.css" media="all" />
<style type="text/css">
body {
	background-color: #F2F2F2;
}

.info-body {
	margin: 40px auto;
	width: 1000px;
}

.first-td {
	width: 160px;
	height: 33px;
}

.first-td2 {
	width: 102px;
	height: 43px;
}

.run {
	width: 14px;
	height: 14px;
	border-radius: 50%;
	background-color: green;
	float: left;
	margin-right: 10px
}

.down {
	width: 14px;
	height: 14px;
	border-radius: 50%;
	background-color: red;
	float: left;
	margin-right: 10px
}

.disconnect {
	width: 14px;
	height: 14px;
	border-radius: 50%;
	background-color: #A1A1A1;
	float: left;
	margin-right: 10px
}

.warning {
	width: 14px;
	height: 14px;
	border-radius: 50%;
	background-color: #FFB90F;
	float: left;
	margin-right: 10px
}

.server-num {
	clear: both;
	float: left;
	margin-top: 8px;
	margin-left: 23px
}

.server-state {
	float: right;
	margin-right: 15px;
	margin-top: 10px;
}

.server-name {
	float: left;
	margin-top: -3px;
}
</style>
</head>
<body>
	<div class="info-body">
		<div class="layui-btn-group">
			<a class="layui-btn layui-btn-normal"
				href="/haredis/visualization/index">服务状态</a> <a
				class="layui-btn layui-btn-normal"
				href="/haredis/visualization/backups">备份管理</a>
		</div>
		<div class="layui-row layui-col-space5" style="margin-top: 10px">
			<div class="layui-col-md3">
				<div class="layui-card" style="height: 408px">
					<div class="layui-card-header">Master主机Redis信息</div>
					<div class="layui-card-body" style="height: 100%">
						<div class="row" id="masterRedisInfo"></div>
					</div>
				</div>
			</div>
			<div class="layui-col-md4">
				<div class="layui-card" style="height: 408px">
					<div class="layui-card-header">Master主机系统信息</div>
					<div class="layui-card-body" style="height: 100%">
						<table style="margin-left: 10px">
							<tr>
								<td class="first-td">IP</td>
								<td id="masterIp">--</td>
							</tr>
							<tr>
								<td class="first-td">VIP</td>
								<td id="masterVip">--</td>
							</tr>
							<tr>
								<td class="first-td">系统类型</td>
								<td id="masterOsType">--</td>
							</tr>
							<tr>
								<td class="first-td">系统架构</td>
								<td id="masterOsArch">--</td>
							</tr>
							<tr>
								<td class="first-td">系统数据模型</td>
								<td id="masterOsDataModel">--</td>
							</tr>
							<tr>
								<td class="first-td">系统描述</td>
								<td id="masterOsDescription">--</td>
							</tr>
							<tr>
								<td class="first-td">CPU存储方式</td>
								<td id="masterCpuEndian">--</td>
							</tr>
							<tr>
								<td class="first-td">供应商</td>
								<td id="masterVendor">--</td>
							</tr>
							<tr>
								<td class="first-td">供应商版本</td>
								<td id="masterVendorName">--</td>
							</tr>
							<tr>
								<td class="first-td">内核版本</td>
								<td id="masterVersion">--</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
			<div class="layui-col-md5">
				<div class="layui-row layui-col-space5">
					<div class="layui-col-md12">
						<div class="layui-card" style="height: 200px">
							<div class="layui-card-header">Master主机内存信息</div>
							<div class="layui-card-body" style="height: 100%">
								<div class="layui-row">
									<div class="layui-col-md6" id="masterMemoryPieChart"
										style="height: 200px;margin-top: -60px;"></div>
									<div class="layui-col-md6" id="masterSwapMemoryPieChart"
										style="height: 200px;margin-top: -60px;"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="layui-col-md12">
						<div class="layui-card" style="height: 200px">
							<div class="layui-card-header">Master主机JVM信息</div>
							<div class="layui-card-body" style="height: 100%">
								<div class="layui-row">
									<div class="layui-col-md7">
										<table>
											<tr>
												<td class="first-td2">Java版本</td>
												<td id="masterJavaVersion">--</td>
											</tr>
											<tr>
												<td class="first-td2">JavaClass版本</td>
												<td id="masterClassVersion">--</td>
											</tr>
											<tr>
												<td class="first-td2">Java提供商</td>
												<td id="masterJavaVendor">--</td>
											</tr>
										</table>
									</div>
									<div id="masterJvmMemoryPieChart" class="layui-col-md5"
										style="height: 200px;margin-top: -60px;"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="layui-row layui-col-space5">
			<div class="layui-col-md3">
				<div class="layui-card" style="height: 408px">
					<div class="layui-card-header">Slave主机Redis信息</div>
					<div class="layui-card-body" style="height: 100%">
						<div class="row" id="slaveRedisInfo"></div>
					</div>
				</div>
			</div>
			<div class="layui-col-md4">
				<div class="layui-card" style="height: 408px">
					<div class="layui-card-header">Slave主机系统信息</div>
					<div class="layui-card-body" style="height: 100%">
						<table style="margin-left: 10px">
							<tr>
								<td class="first-td">IP</td>
								<td id="slaveIp">--</td>
							</tr>
							<tr>
								<td class="first-td">VIP</td>
								<td id="slaveVip">--</td>
							</tr>
							<tr>
								<td class="first-td">系统类型</td>
								<td id="slaveOsType">--</td>
							</tr>
							<tr>
								<td class="first-td">系统架构</td>
								<td id="slaveOsArch">--</td>
							</tr>
							<tr>
								<td class="first-td">系统数据模型</td>
								<td id="slaveOsDataModel">--</td>
							</tr>
							<tr>
								<td class="first-td">系统描述</td>
								<td id="slaveOsDescription">--</td>
							</tr>
							<tr>
								<td class="first-td">CPU存储方式</td>
								<td id="slaveCpuEndian">--</td>
							</tr>
							<tr>
								<td class="first-td">供应商</td>
								<td id="slaveVendor">--</td>
							</tr>
							<tr>
								<td class="first-td">供应商版本</td>
								<td id="slaveVendorName">--</td>
							</tr>
							<tr>
								<td class="first-td">内核版本</td>
								<td id="slaveVersion">--</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
			<div class="layui-col-md5">
				<div class="layui-row layui-col-space5">
					<div class="layui-col-md12">
						<div class="layui-card" style="height: 200px">
							<div class="layui-card-header">Slave主机内存信息</div>
							<div class="layui-card-body" style="height: 100%">
								<div class="layui-row">
									<div class="layui-col-md6" id="slaveMemoryPieChart"
										style="height: 200px;margin-top: -60px;"></div>
									<div class="layui-col-md6" id="slaveSwapMemoryPieChart"
										style="height: 200px;margin-top: -60px;"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="layui-col-md12">
						<div class="layui-card" style="height: 200px">
							<div class="layui-card-header">Slave主机JVM信息</div>
							<div class="layui-card-body" style="height: 100%">
								<div class="layui-row">
									<div class="layui-col-md7">
										<table>
											<tr>
												<td class="first-td2">Java版本</td>
												<td id="slaveJavaVersion">--</td>
											</tr>
											<tr>
												<td class="first-td2">JavaClass版本</td>
												<td id="slaveClassVersion">--</td>
											</tr>
											<tr>
												<td class="first-td2">Java提供商</td>
												<td id="slaveJavaVendor">--</td>
											</tr>
										</table>
									</div>
									<div class="layui-col-md5" id="slaveJvmMemoryPieChart"
										style="height: 200px;margin-top: -60px;"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="layui-row layui-col-space5">
			<div class="layui-col-md7">
				<div class="layui-card" style="height: 300px">
					<div class="layui-card-header">Master主机CPU信息</div>
					<div class="layui-card-body" style="height: 100%">
						<div id="masterCpuHistogram" style="height: 300px;margin-top: -55px;margin-left: -20px"></div>
					</div>
				</div>
			</div>
			<div class="layui-col-md5">
				<div class="layui-card" style="height: 300px">
					<div class="layui-card-header">Master主机文件系统信息</div>
					<div class="layui-card-body" style="height: 100%">
						<div id="masterFileSystemHistogram" style="height: 300px;margin-top: -55px;margin-left: -20px"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="layui-row layui-col-space5">
			<div class="layui-col-md7">
				<div class="layui-card" style="height: 300px">
					<div class="layui-card-header">Slave主机CPU信息</div>
					<div class="layui-card-body" style="height: 100%">
						<div id="slaveCpuHistogram" style="height: 300px;margin-top: -55px;margin-left: -20px"></div>
					</div>
				</div>
			</div>
			<div class="layui-col-md5">
				<div class="layui-card" style="height: 300px">
					<div class="layui-card-header">Slave主机文件系统信息</div>
					<div class="layui-card-body" style="height: 100%">
						<div id="slaveFileSystemHistogram" style="height: 300px;margin-top: -55px;margin-left: -20px"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
<script src="/static/layui/layui.js" type="text/javascript"
	charset="utf-8"></script>
<script src="/static/echarts/echarts.min.js" type="text/javascript"
	charset="utf-8"></script>
<script type="text/javascript">
	var vip = "${vip}";
	layui.use([ 'layer', 'jquery' ], function() {
		var layer = layui.layer;
		var $ = layui.jquery;
		init();
		setInterval(function() {
			init();
		}, 2500);
		function init() {
			$.ajax({
				type : "post",
				url : '/haredis/visualization/state_data',
				contentType : "application/x-www-form-urlencoded",
				dataType : "json",
				async : true,
				success : function(data) {
					initRedisInfo(data);
					initServerMessage(data);
					initChars(data);
				}
			});
		}
		function getRedisInfoHtml(redisInfoList) {
			if (redisInfoList != undefined && redisInfoList != null) {
				var info_html = "";
				for (var i = 0; i < redisInfoList.length; i++) {
					info_html += '<div class="layui-col-md12"><div style="margin-top: 3px"><div class="' + (redisInfoList[i].run ? "run" : "down") + '"></div><div class="server-name">Redis:' + (redisInfoList[i].port) + '</div></div><div><div class="server-num">分片号：' + (redisInfoList[i].sliceNumber) + '</div><div class="server-state">' + (redisInfoList[i].master ? "Master" : "Slave") + '</div><hr /></div></div>'
				}
				return info_html;
			}
			return "";
		}
		function initRedisInfo(data) {
			if (data.master.connect) {
				$("#masterRedisInfo").html(getRedisInfoHtml(data.master.redisInfoList));
			} else {
				$("#masterRedisInfo").html("");
			}
			if (data.slave.connect) {
				$("#slaveRedisInfo").html(getRedisInfoHtml(data.slave.redisInfoList));
			} else {
				$("#slaveRedisInfo").html("");
			}
		}
		function initServerMessage(data) {
			if (data.master.connect) {
				if (data.master.haveVirtualIp) {
					$("#masterVip").html(vip);
				} else {
					$("#masterVip").html("--");
				}
				$("#masterIp").html(data.master.ip);
				$("#masterOsType").html("Linux");
				$("#masterOsArch").html(data.master.os.arch);
				$("#masterOsDataModel").html(data.master.os.dataModel);
				$("#masterOsDescription").html(data.master.os.description);
				$("#masterCpuEndian").html(data.master.os.cpuEndian);
				$("#masterVendor").html(data.master.os.vendor);
				$("#masterVendorName").html(data.master.os.vendorName);
				$("#masterVersion").html(data.master.os.version);
				$("#masterJavaVersion").html(data.master.JavaProperty.javaVersion);
				$("#masterClassVersion").html(data.master.JavaProperty.classVersion);
				$("#masterJavaVendor").html(data.master.JavaProperty.javaVendor);
			} else {
				$("#masterIp").html(data.master.ip);
				$("#masterVip").html("--");
				$("#masterOsType").html("--");
				$("#masterOsArch").html("--");
				$("#masterOsDataModel").html("--");
				$("#masterOsDescription").html("--");
				$("#masterCpuEndian").html("--");
				$("#masterVendor").html("--");
				$("#masterVendorName").html("--");
				$("#masterVersion").html("--");
				$("#masterJavaVersion").html("--");
				$("#masterClassVersion").html("--");
				$("#masterJavaVendor").html("--");
			}
			if (data.slave.connect) {
				if (data.slave.haveVirtualIp) {
					$("#slaveVip").html(vip);
				} else {
					$("#slaveVip").html("--");
				}
				$("#slaveIp").html(data.slave.ip);
				$("#slaveOsType").html("Linux");
				$("#slaveOsArch").html(data.slave.os.arch);
				$("#slaveOsDataModel").html(data.slave.os.dataModel);
				$("#slaveOsDescription").html(data.slave.os.description);
				$("#slaveCpuEndian").html(data.slave.os.cpuEndian);
				$("#slaveVendor").html(data.slave.os.vendor);
				$("#slaveVendorName").html(data.slave.os.vendorName);
				$("#slaveVersion").html(data.slave.os.version);
				$("#slaveJavaVersion").html(data.slave.JavaProperty.javaVersion);
				$("#slaveClassVersion").html(data.slave.JavaProperty.classVersion);
				$("#slaveJavaVendor").html(data.slave.JavaProperty.javaVendor);
			} else {
				$("#slaveIp").html(data.slave.ip);
				$("#slaveVip").html("--");
				$("#slaveOsType").html("--");
				$("#slaveOsArch").html("--");
				$("#slaveOsDataModel").html("--");
				$("#slaveOsDescription").html("--");
				$("#slaveCpuEndian").html("--");
				$("#slaveVendor").html("--");
				$("#slaveVendorName").html("--");
				$("#slaveVersion").html("--");
				$("#slaveJavaVersion").html("--");
				$("#slaveClassVersion").html("--");
				$("#slaveJavaVendor").html("--");
			}
		}
	});
	function initChars(data) {
		if (data != undefined && data != null) {
			var masterMemoryPieChart = echarts.init(document.getElementById('masterMemoryPieChart'));
			var masterSwapMemoryPieChart = echarts.init(document.getElementById('masterSwapMemoryPieChart'));
			var masterJvmMemoryPieChart = echarts.init(document.getElementById('masterJvmMemoryPieChart'));
			var masterCpuHistogram = echarts.init(document.getElementById('masterCpuHistogram'));
			var masterFileSystemHistogram = echarts.init(document.getElementById('masterFileSystemHistogram'));
			var slaveMemoryPieChart = echarts.init(document.getElementById('slaveMemoryPieChart'));
			var slaveSwapMemoryPieChart = echarts.init(document.getElementById('slaveSwapMemoryPieChart'));
			var slaveJvmMemoryPieChart = echarts.init(document.getElementById('slaveJvmMemoryPieChart'));
			var slaveCpuHistogram = echarts.init(document.getElementById('slaveCpuHistogram'));
			var slaveFileSystemHistogram = echarts.init(document.getElementById('slaveFileSystemHistogram'));
			if (data.master.connect) {
				masterMemoryPieChart.setOption(getPieChartOption("物理内存", "16", data.master.memoryPieChart));
				masterSwapMemoryPieChart.setOption(getPieChartOption("交换内存", "16", data.master.swapMemoryPieChart));
				masterJvmMemoryPieChart.setOption(getPieChartOption("JVM内存", "14", data.master.jvmMemoryPieChart));
				masterCpuHistogram.setOption(getCpuHistogramOption(data.master.cpuHistogram));
				masterFileSystemHistogram.setOption(getFileSystemHistogramOption(data.master.fileSystemHistogram));
			} else {
				masterMemoryPieChart.setOption(getPieChartOption("物理内存", "16", []));
				masterSwapMemoryPieChart.setOption(getPieChartOption("交换内存", "16", []));
				masterJvmMemoryPieChart.setOption(getPieChartOption("JVM内存", "16", []));
				masterCpuHistogram.setOption(getCpuHistogramOption([]));
				masterFileSystemHistogram.setOption(getFileSystemHistogramOption([]));
			}
			if (data.slave.connect) {
				slaveMemoryPieChart.setOption(getPieChartOption("物理内存", "16", data.slave.memoryPieChart));
				slaveSwapMemoryPieChart.setOption(getPieChartOption("交换内存", "16", data.slave.swapMemoryPieChart));
				slaveJvmMemoryPieChart.setOption(getPieChartOption("JVM内存", "14", data.slave.jvmMemoryPieChart));
				slaveCpuHistogram.setOption(getCpuHistogramOption(data.slave.cpuHistogram));
				slaveFileSystemHistogram.setOption(getFileSystemHistogramOption(data.slave.fileSystemHistogram));
			} else {
				slaveMemoryPieChart.setOption(getPieChartOption("物理内存", "16", []));
				slaveSwapMemoryPieChart.setOption(getPieChartOption("交换内存", "16", []));
				slaveJvmMemoryPieChart.setOption(getPieChartOption("JVM内存", "16", []));
				slaveCpuHistogram.setOption(getCpuHistogramOption([]));
				slaveFileSystemHistogram.setOption(getFileSystemHistogramOption([]));
			}
		}
	}
	function getPieChartOption(title, fontSize, data) {
		for (var i = 0; i < data.length; i++) {
			data[i].value = (data[i].value / 1024).toFixed(2);
		}
		return {
			tooltip : {
				trigger : 'item',
				formatter : "{a} <br/>{b} : {c}M ({d}%)"
			},
			title : {
				text : title,
				x : 'center',
				y : 'bottom',
				textStyle : {
					fontWeight : 'lighter',
					'fontSize' : fontSize
				}
			},
			series : [
				{
					name : title,
					type : 'pie',
					radius : '55%',
					center : [ '50%', '60%' ],
					'data' : data,
					itemStyle : {
						emphasis : {
							shadowBlur : 10,
							shadowOffsetX : 0,
							shadowColor : 'rgba(0, 0, 0, 0.5)'
						},
						normal : {
							color : function(params) {
								var colorList = [
									'#00CD00', '#98FB98'
								];
								return colorList[params.dataIndex]
							}
						}
					},
					label : {
						normal : {
							show : true,
							position : 'inner',
							textStyle : {
								fontWeight : 300,
								fontSize : 14
							},
							formatter : '{d}%'
						}
					},
				}
			]
		};
	}
	function getCpuHistogramOption(data) {
		var x = [];
		for (var i = 0; i < data.length; i++) {
			x[i] = "CPU" + (i + 1);
		}
		return {
			color : [ '#3398DB' ],
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					type : 'shadow'
				},
				formatter : function(params) {
					var param = params[0];
					var data = param.data;
					return param.marker + param.axisValue + "信息<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;总使用率：" + (data.value * 100).toFixed(2) + "%<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;用户使用率：" + (data.user * 100).toFixed(2) + "%<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;系统使用率：" + (data.sys * 100).toFixed(2) + "%<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;当前等待率：" + (data.wait * 100).toFixed(2) + "%<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;当前错误率：" + (data.nice * 100).toFixed(2) + "%<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;当前空闲率：" + (data.idle * 100).toFixed(2) + "%<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;CPU总量MHz：" + (data.mhz / 1000).toFixed(1) + "GHz<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;CPU生产商：" + data.vendor + "<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;CPU类别：" + data.model + "<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;CPU缓存数量：" + data.cacheSize + "<br>";
				},
			},
			grid : {
				left : '3%',
				right : '4%',
				bottom : '3%',
				containLabel : true
			},
			xAxis : [
				{
					type : 'category',
					data : x,
					axisTick : {
						alignWithLabel : true
					}
				}
			],
			yAxis : [ {
				type : 'value',
				axisLabel : {
					show : true,
					textStyle : {
						color : '#3398DB',
						fontSize : '80%',
					},
					interval : 0,
					showMinLabel : true,
					formatter : function(value) {
						return value * 100 + "%"
					}
				},
				min : 0,
				max : 1,
				splitNumber : 5
			} ],
			series : [
				{
					name : '总使用率',
					type : 'bar',
					barWidth : '60%',
					'data' : data,
					itemStyle : {
						normal : {
							color : function(params) {
								var colorList = [ '#00CD00', '#00CD00', '#00CD00', '#00CD00', '#00CD00', '#00CD00', '#00CD00' ];
								return colorList[params.dataIndex];
							}
						},
					},
				}
			]
		};
	}
	function getFileSystemHistogramOption(data) {
		var x = [];
		for (var i = 0; i < data.length; i++) {
			x[i] = data[i].dirName;
		}
		return {
			color : [ '#3398DB' ],
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					type : 'shadow'
				},
				formatter : function(params) {
					var param = params[0];
					var data = param.data;
					return param.marker + param.axisValue + " 信息<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;总使用率：" + (data.value * 100).toFixed(2) + "%<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;分区的盘符路径：" + data.dirName + "<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;文件系统类型：" + data.sysTypeName + "<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;文件系统总大小：" + (data.total / 1024).toFixed(2) + "M<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;文件系统剩余大小：" + (data.free / 1024).toFixed(2) + "M<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;文件系统可用大小：" + (data.avail / 1024).toFixed(2) + "M<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;文件系统已经使用量：" + (data.used / 1024).toFixed(2) + "M<br>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;文件系统资源的利用率：" + (data.usePercent * 100).toFixed(2) + "%<br>";
				},
			},
			grid : {
				left : '3%',
				right : '4%',
				bottom : '3%',
				containLabel : true
			},
			xAxis : [
				{
					type : 'category',
					data : x,
					axisTick : {
						alignWithLabel : true
					}
				}
			],
			yAxis : [ {
				type : 'value',
				axisLabel : {
					show : true,
					textStyle : {
						color : '#3398DB',
						fontSize : '80%',
					},
					interval : 0,
					showMinLabel : true,
					formatter : function(value) {
						return value * 100 + "%"
					}
				},
				min : 0,
				max : 1,
				splitNumber : 5
			} ],
			series : [
				{
					name : '总使用率',
					type : 'bar',
					barWidth : '60%',
					'data' : data,
					itemStyle : {
						normal : {
							color : function(params) {
								var colorList = [ '#00CD00', '#00CD00', '#00CD00', '#00CD00', '#00CD00', '#00CD00', '#00CD00' ];
								return colorList[params.dataIndex];
							}
						},
					},
				}
			]
		};
	}
</script>
</html>
