package org.printercounter.service.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.printercounter.Printer;
import org.printercounter.service.FindThreadService;
import org.printercounter.service.SnpmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class FindThreadServiceImpl implements FindThreadService {

	private static final Logger logger = Logger.getLogger(FindThreadServiceImpl.class);
	
	@Autowired
	private SnpmService snpmService;
	
	private String ip;
	
	private OutputStream outputStream;
	
	@Override
	public String getIp() {
		return ip;
	}
	
	@Override
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void setOutputStream(OutputStream os) {
		this.outputStream = os;
	}

	@Override
	public void run() {
		Printer printer = snpmService.findPrinter(ip);
		if (printer != null) {
			String data = printer.toString() + ",\n";
			try {
				outputStream.write(data.getBytes(), 0, data.length());
			} catch (IOException e) {
				logger.warn(ExceptionUtils.getStackFrames(e));
			}
		}
	}


}
