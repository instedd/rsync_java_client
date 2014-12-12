package org.instedd.cdx.sync;

import java.util.Arrays;

import org.apache.commons.lang.Validate;

public class Settings {

	/**
	 * The host to connect to
	 */
	public String remoteHost;
	/**
	 * The port where the host accepts ssh connections
	 */
	public Integer remotePort;
	/**
	 * The username to log into the remote host
	 */
	public String remoteUser;
	/**
	 * The private ssh key used to log into the remote host
	 */
	public String remoteKey;
	public String serverSignatureLocation;
	public String knownHostsFilePath;

	public String inboxLocalDir;
	public String outboxLocalDir;

	public void validate() {
		for (Object f : Arrays.asList(remoteHost, remotePort, remoteKey)) {
			Validate.notNull(f, "Remote host settings missing (required: host, port, user and path to ssh key");
		}
		if (inboxLocalDir == null  && outboxLocalDir == null) {
			throw new IllegalArgumentException("either inboxLocalDir or outboxLocalDir must be set");
		}
	}
}
