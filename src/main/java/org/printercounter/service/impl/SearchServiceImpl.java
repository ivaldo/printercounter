package org.printercounter.service.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;
import org.printercounter.service.FindThreadService;
import org.printercounter.service.SearchService;
import org.printercounter.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

	private static final Logger logger = Logger.getLogger(SearchServiceImpl.class);
	
	@Autowired
	private Utils utils;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor; 

	@Value("${printercounter.subnet.cidr_list}")
	private String subnetListStr;

	@Value("${printercounter.file_out.path}")
	private String filepath;
	
	@Value("${printercounter.file_out.name}")
	private String filename;
	
	@Override
	public void run() throws IOException {
		long di = System.currentTimeMillis();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		String filenameWithDate = dateFormat.format(new Date()) + "-" + filename;
		Path p = Paths.get(filepath, filenameWithDate);
    	OutputStream os = new BufferedOutputStream(Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND));		
		
    	write(os, "[");
    	
		for (String subnet : utils.getSubnetList(subnetListStr)) {
			SubnetUtils subnetUtils = new SubnetUtils(subnet);
			for (String ip : subnetUtils.getInfo().getAllAddresses()) {
				FindThreadService findThread = context.getBean(FindThreadService.class);
				findThread.setIp(ip);
				findThread.setOutputStream(os);
				taskExecutor.execute(findThread);
			}
		}
		waitForShutdown();
		write(os, "{}]");
		os.close();
		
		long df = System.currentTimeMillis();
		System.out.println("finalizado em " + (df-di) + "ms");
	}

	private void write(OutputStream outputStream, String data) {
		try {
			outputStream.write(data.getBytes(), 0, data.length());
		} catch (IOException e) {
			logger.warn(ExceptionUtils.getStackFrames(e));
		}
		
	}

	private void waitForShutdown() {
		while (true) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (taskExecutor.getActiveCount() == 0) {
				taskExecutor.shutdown();
				break;
			}
		}
	}

}
