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
		<div style="margin-top: 10px">
			<form class="layui-form" action="/haredis/visualization/backups"
				method="post">
				<div class="layui-inline">
					<input id="backups_date" type="text" name="date"
						lay-verify="required" placeholder="请选择您要搜索的日期" autocomplete="off"
						class="layui-input" style="height: 40px">
				</div>
				<button class="layui-btn" lay-submit="search">搜索</button>
				<a class="layui-btn" id="pull_now">拉取当前时间Redis数据备份</a>
			</form>
		</div>
		<table class="layui-table" style="text-align: center;">
			<thead>
				<tr>
					<th style="text-align: center;">序号</th>
					<th style="text-align: center;">备份时间</th>
					<th style="text-align: center;">文件名称</th>
					<th style="text-align: center;">操作</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${!(coldBackupList eq  null)}">
					<c:forEach items="${coldBackupList}" varStatus="status"
						var="backups">
						<tr>
							<td>${status.index + 1}</td>
							<td>${backups.date}</td>
							<td>${backups.name}</td>
							<td>
								<div class="layui-inline">
									<a class="layui-btn layui-btn-sm layui-btn-radius" download
										href="/haredis/visualization/download/${backups.name}"> 下载
									</a>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:if>
			</tbody>
		</table>
	</div>
</body>
<script src="/static/layui/layui.js" type="text/javascript"
	charset="utf-8"></script>
<script>
	layui.use([ 'layer', 'jquery', 'laydate', 'form' ], function() {
		var form = layui.form;
		var layer = layui.layer;
		var laydate = layui.laydate;
		var $ = layui.jquery;
		laydate.render({
			elem : '#backups_date'
		});
		form.on('submit(search)', function(data) {
			return true;
		});
		$("#pull_now").click(function() {
			$.ajax({
				type : "post",
				url : '/haredis/visualization/pull',
				contentType : "application/x-www-form-urlencoded",
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.fileName != null) {
						var loading = layer.load(1, {
							shade : [ 0.1, '#fff' ],
						});
						setTimeout(function() {
							layer.close(loading);
							downloadFileByForm('/haredis/visualization/download/' + data.fileName)
						}, 3000);
					} else {
						layer.alert('拉取失败！', {
							icon : 2,
							skin : 'layer-ext-moon'
						})
					}
				}
			});
		});
		function downloadFileByForm(url) {
			var form = $("<form></form>").attr("action", url).attr("method", "post");
			form.appendTo('body').submit().remove();
		}
	});
</script>
</html>
