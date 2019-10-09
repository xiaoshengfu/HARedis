package edu.sdust.haredis.redisproxy;

/**
 * ClassName: ChannelInfo
 * @Description: 用户客户端连接信息
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月10日 下午8:57:50
 */
public final class ChannelInfo {

	private Boolean auth = false;
	private Integer databaseNumber = 0;

	public ChannelInfo() {
		super();
	}

	public Boolean getAuth() {
		return auth;
	}

	public void setAuth(Boolean auth) {
		this.auth = auth;
	}

	public Integer getDatabaseNumber() {
		return databaseNumber;
	}

	public void setDatabaseNumber(Integer databaseNumber) {
		this.databaseNumber = databaseNumber;
	}

	@Override
	public String toString() {
		return "ChannelInfo [auth=" + auth + ", databaseNumber=" + databaseNumber + "]";
	}
}
