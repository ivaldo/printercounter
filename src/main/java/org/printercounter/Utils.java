package org.printercounter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Utils {

	public Set<String> getSubnetList(String subnetList) {
		Set<String> set = new HashSet<String>();
		String[] subnetArray = subnetList.split(";");
		for (String subnet : subnetArray) {
			validateSubnet(subnet);
			set.add(subnet);
		}
		return set;
	}

	private void validateSubnet(String subnet) {
		try {
			if (StringUtils.isBlank(subnet)) {
				sendErrorMensage(subnet);
			}
			
			String[] nodes = subnet.split("\\.");
			
			if (nodes.length != 4) {
				sendErrorMensage(subnet);
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	private void sendErrorMensage(String subnet) throws Exception {
		throw new Exception("invalid subnet '" + subnet + "'");
	}

}
