package edu.sdust.haredis.visualization.common;

public class ColdBackupFile {

	private String name;
	private String date;

	public ColdBackupFile() {
		super();
	}

	public ColdBackupFile(String name, String date) {
		super();
		this.name = name;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
