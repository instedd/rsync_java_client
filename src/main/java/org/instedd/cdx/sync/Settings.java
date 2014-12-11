package org.instedd.cdx.sync;

import java.util.Arrays;

public class Settings {

	public String remoteHost;
	public String remotePort;
	public String remoteUser;
	public String remoteKey;
	public String userServerSignature;
	public String serverSignatureLocation;
	public String inboxLocalDir;
	public String outboxLocalDir;
	public String knownHostsFilePath;

	public void validate() {
		for (String f : Arrays.asList(remoteHost, remotePort, remoteKey)) {
			checkNotNull(f, "Remote host settings missing (required: host, port, user and path to ssh key");
		}
		for (String f : Arrays.asList(inboxLocalDir, outboxLocalDir)) {
			checkNotNull(f, "Not all sync paths are configured");
		}
	}

	private void checkNotNull(Object o, String message) {
		if (o == null)
			throw new IllegalArgumentException(message);
	}

}
