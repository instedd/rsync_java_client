package org.instedd.cdx.sync;

import java.util.Arrays;

import org.apache.commons.lang.Validate;

public class Settings {

	public String remoteHost;
	public Integer remotePort;
	public String remoteUser;
	public String remoteKey;
	public String userServerSignature;
	public String serverSignatureLocation;
	public String inboxLocalDir;
	public String outboxLocalDir;
	public String knownHostsFilePath;

	public void validate() {
		for (Object f : Arrays.asList(remoteHost, remotePort, remoteKey)) {
			Validate.notNull(f, "Remote host settings missing (required: host, port, user and path to ssh key");
		}
		for (String f : Arrays.asList(inboxLocalDir, outboxLocalDir)) {
			Validate.notNull(f, "Not all sync paths are configured");
		}
	}
}
