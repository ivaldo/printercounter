package org.printercounter.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.printercounter.Printer;
import org.printercounter.service.SnpmService;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class SnpmServiceImpl implements SnpmService {

	private static final Logger logger = Logger.getLogger(SnpmServiceImpl.class);
	
	@Value("${printercounter.snmp.port}")
	private int snmpPort;

	@Value("${printercounter.snmp.timeout}")
	private int snmpTimeout;

	@Value("${printercounter.snmp.community}")
	private String snmpCommunity;

	@Value("${printercounter.snmp.version}")
	private int snmpVersion;

	@Value("${printercounter.snmp.oid_serialnumber}")
	private String oidSerialNumber;
	
	@Value("${printercounter.snmp.oid_model}")
	private String oidModel;

	@Value("${printercounter.snmp.oid_pagecount}")
	private String oidPageCount;

	@Override
	public Printer findPrinter(String ip) {

		Printer printer = null;

		CommunityTarget comtarget = new CommunityTarget();
		comtarget.setCommunity(new OctetString(snmpCommunity));
		comtarget.setVersion(snmpVersion);
		comtarget.setAddress(new UdpAddress(ip + "/" + snmpPort));
		comtarget.setRetries(1);
		comtarget.setTimeout(snmpTimeout);

		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oidSerialNumber)));
		pdu.add(new VariableBinding(new OID(oidModel)));
		pdu.add(new VariableBinding(new OID(oidPageCount)));
		pdu.setType(PDU.GET);
		pdu.setRequestID(new Integer32(1));

		TransportMapping transport = null;
		Snmp snmp = null;

		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();

			snmp = new Snmp(transport);

			ResponseEvent response = snmp.get(pdu, comtarget);

			if (response != null) {

				PDU responsePDU = response.getResponse();

				if (responsePDU != null) {
					int errorStatus = responsePDU.getErrorStatus();

					if (errorStatus == PDU.noError) {

						Vector<?> v = responsePDU.getVariableBindings();

						if (v != null && v.size() > 2) {

							String serialNumber = responsePDU.get(0).toValueString();
							String printerModel = responsePDU.get(1).toValueString();
							String counter = responsePDU.get(2).toValueString();

							if (StringUtils.isNotBlank(serialNumber) && StringUtils.isNotBlank(printerModel)
									&& StringUtils.isNotBlank(counter) && StringUtils.isNumeric(counter)) {

								printer = new Printer(serialNumber.trim(), printerModel.trim(), ip,
										Integer.parseInt(counter), new Date());
							}
						}
					}
				}
			}

		} catch (IOException e) {
			logger.error(e);

		} finally {
			close(snmp);
			close(transport);
		}

		return printer;
	}

	private void close(TransportMapping transport) {
		try {
			if (transport != null) {
				transport.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void close(Snmp snmp) {
		try {
			if (snmp != null) {
				snmp.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
