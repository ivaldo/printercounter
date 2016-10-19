package org.printercounter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Printer {
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

	private String serialNumber;
	
	private String printerModel;
	
	private String ip;
	
	private int pageCount;
	
	private String time;

	public Printer() {
	}
	
	public Printer(String serialNumber, String printerModel, String ip, int pageCount, Date time) {
		super();
		this.serialNumber = serialNumber;
		this.printerModel = printerModel;
		this.ip = ip;
		this.pageCount = pageCount;
		this.time = df.format(time);
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getPrinterModel() {
		return printerModel;
	}

	public void setPrinterModel(String printerModel) {
		this.printerModel = printerModel;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
}
