package org.instedd.cdx.sync;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Arrays;

import org.apache.commons.lang.SystemUtils;

public class RsyncCommandBuilder {

	private static final String RSYNC_COMMAND = "rsync";
	private Settings settings;

	public RsyncCommandBuilder(Settings settings) {
		this.settings = settings;
		this.settings.validate();
	}

	private ProcessBuilder process(String... tokens) {
		return new ProcessBuilder(Arrays.asList(tokens));
	}

	public ProcessBuilder buildUploadCommand() {
		return process(RSYNC_COMMAND, "-iaz", "--remove-source-files", "-e", shellCommand(), getOutboxLocalRoute(), getOutboxRemoteRoute());
	}

	public ProcessBuilder buildDownloadCommand() {
		return process(RSYNC_COMMAND, "-iaz", "--remove-source-files", "-e", shellCommand(), getInboxRemoteRoute(), getInboxLocalRoute());
	}

	public ProcessBuilder buildTestCommand() {
		return process(RSYNC_COMMAND, "--help");
	}

	public String getOutboxLocalRoute() {
		return localRoute(settings.outboxLocalDir);
	}

	public String getOutboxRemoteRoute() {
		return "" + settings.remoteHost + ":/inbox";
	}

	public String getInboxLocalRoute() {
		return localRoute(settings.inboxLocalDir);
	}

	public String getInboxRemoteRoute() {
		// trailing slash prevents an 'outbox' directory to be created
		return "" + settings.remoteHost + ":/outbox/";
	}

	public String shellCommand() {
		String userParam = isEmpty(settings.remoteUser) ? "" : "-l " + settings.remoteUser + "";
		String knownHostsParam = isEmpty(settings.knownHostsFilePath) ? "" : "-oUserKnownHostsFile=\"" + cygwinPath(settings.knownHostsFilePath) + "\"";
		return "ssh -p " + settings.remotePort + " " + userParam + " -i \"" + cygwinPath(settings.remoteKey) + "\" " + knownHostsParam
		    + " -oBatchMode=yes";
	}

	String localRoute(String dir) {
		dir = dir.endsWith("/") ? dir : "" + dir + "/";
		return cygwinPath(dir);
	}

	public String cygwinPath(String path) {
		if (SystemUtils.IS_OS_WINDOWS) {
			// replace "C:/something" with "/cygdrive/c/something" for rsync to
			// understand it
			path = path.replaceFirst("^(.):\\/*", "/cygdrive/$1/");
			path = path.replace("\\", "/");
		}
		return path;
	}

	public String getOutboxLocalDir() {
		return settings.outboxLocalDir;
	}
}