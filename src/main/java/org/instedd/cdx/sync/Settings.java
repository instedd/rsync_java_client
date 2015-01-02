package org.instedd.cdx.sync;

import static org.apache.commons.lang.SystemUtils.USER_HOME;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;

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
	public String remoteKey = USER_HOME + "/.ssh/id_rsa";
	public String serverSignatureLocation;
	public String knownHostsFilePath;

	/**
	 * Client directory where files transferred from server to client will be placed
	 * after download
	 */
	public String localInboxDir;

	/**
	 * Client directory where files transferred from client to server must be placed
	 * before upload
	 *
	 * If you want to transfer a file to server, put it here.
	 */
	public String localOutboxDir;

	/**
	 * Server directory where files transferred from client to server will be placed
	 * after upload
	 */
	public String remoteInboxDir = "/inbox";

	/**
	 * Server directory where files transferred from server to client must be placed
	 * before download
	 *
	 * If you want to transfer a file to client, put it here
	 */
	public String remoteOutboxDir = "/outbox";

	public void validate() {
		for (Object f : Arrays.asList(remoteHost, remotePort, remoteKey)) {
			Validate.notNull(f, "Remote host settings missing (required: host, port, user and path to ssh key");
		}//TODO this validation depends on sync mode
		if (localInboxDir == null  && localOutboxDir == null) {
			throw new IllegalArgumentException("either inboxLocalDir or outboxLocalDir must be set");
		}
	}

	@Override
	public String toString() {
	  return ToStringBuilder.reflectionToString(this);
	}

	public static Settings fromProperties(final Properties props) {
		return new Settings() {
			{
				remoteHost = extract(props, "remote.host", remoteHost);
				remotePort = Integer.valueOf(extract(props, "remote.port", remotePort));
				remoteUser = extract(props, "remote.user", remoteUser);
				remoteKey = extract(props, "remote.key", remoteKey);
				serverSignatureLocation = extract(props, "server.signature.location", serverSignatureLocation);
				knownHostsFilePath = extract(props, "known.hosts.file.path", knownHostsFilePath);
				localInboxDir = extract(props, "local.inbox.dir", localInboxDir);
				localOutboxDir = extract(props, "local.outbox.dir", localOutboxDir);
				remoteInboxDir = extract(props, "remote.inbox.dir", remoteInboxDir);
				remoteOutboxDir = extract(props, "remote.outbox.dir", remoteOutboxDir);
			}
		};
	}


	private static String extract(Properties properties, String key, Object defaultValue) {
		return properties.getProperty("sync." + key, ObjectUtils.toString(defaultValue, null));
	}

}
