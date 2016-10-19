package org.printercounter.service;

import org.printercounter.bean.Printer;

public interface SnpmService {

	Printer findPrinter(String ip);

}
