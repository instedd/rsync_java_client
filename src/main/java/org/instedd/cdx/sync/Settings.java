package org.instedd.cdx.sync;

import java.util.Arrays;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;

public class Settings {

	/**
	 * The host to connect to
	 */
	public String remoteHost;

	/**
	 * The port where the host accepts ssh connections
	 *
	 * Defaults to 22
	 */
	public Integer remotePort = 22;

	/**
	 * The username to log into the remote host
	 */
	public String remoteUser;

	/**
	 * The private ssh key used to log into the remote host.
	 *
	 * Defaults to ~/.ssh/id_rsa
	 */
	//TODO where is located on Windows by default?
	public String remoteKey = SystemUtils.USER_HOME + "/.ssh/id_rsa";
	public String serverSignatureLocation;
	public String knownHostsFilePath;

	/**
	 * Client directory where files transferred from server to client will be placed
	 * after download
	 */
	public String inboxLocalDir;

	/**
	 * Client directory where files transferred from client to server must be placed
	 * before upload
	 *
	 * If you want to transfer a file to server, put it here.
	 */
	public String outboxLocalDir;

	/**
	 * Server directory where files transferred from client to server will be placed
	 * after upload
	 */
	public String inboxRemoteDir = "/inbox";

	/**
	 * Server directory where files transferred from server to client must be placed
	 * before download
	 *
	 * If you want to transfer a file to client, put it here
	 */
	public String outboxRemoteDir = "/outbox";

	public void validate() {
		for (Object f : Arrays.asList(remoteHost, remotePort, remoteKey)) {
			Validate.notNull(f, "Remote host settings missing (required: host, port, user and path to ssh key");
		}
		if (inboxLocalDir == null  && outboxLocalDir == null) {
			throw new IllegalArgumentException("either inboxLocalDir or outboxLocalDir must be set");
		}
	}
}
