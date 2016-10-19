package org.printercounter.service;

import java.io.OutputStream;

public interface FindThreadService extends Runnable {

	String getIp();
	
	void setIp(String ip);

	OutputStream getOutputStream();

	void setOutputStream(OutputStream os);

}
