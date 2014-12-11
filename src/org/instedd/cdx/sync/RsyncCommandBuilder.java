package org.instedd.cdx.sync;

import java.util.Arrays;

public class RsyncCommandBuilder {

	private Settings settings;
	
	public RsyncCommandBuilder(Settings settings) {
		this.settings = settings;
		this.settings.validate();
	}

	private ProcessBuilder process(String...tokens) {
		return new ProcessBuilder(Arrays.asList(tokens));
	}
	
	public ProcessBuilder buildUploadCommand() {
		return process("rsync", "-iaz", "--remove-source-files", "-e", shellCommand(), getOutboxLocalRoute(), getOutboxRemoteRoute());
	}
	
	public ProcessBuilder buildDownloadCommand() {
		return process("rsync", "-iaz", "--remove-source-files", "-e", shellCommand(), getInboxRemoteRoute(), getInboxLocalRoute());
	}

	public ProcessBuilder buildTestCommand() {
		return process("rsync --help");
	}
	
	public String getOutboxLocalRoute() { 
		return localRoute(settings.outboxLocalDir);
	}
	
	public String getOutboxRemoteRoute() { 
		return "${remoteHost}:/inbox";
	}
	
	public String getInboxLocalRoute() { 
		return localRoute(settings.inboxLocalDir);
	}
	public String getInboxRemoteRoute() { 
		return "${remoteHost}:/outbox/" ; //trailing slash prevents an 'outbox' directory to be created
	}
	
	public String shellCommand() {
		String userParam = isEmpty(settings.remoteUser) ? "" : "-l ${remoteUser}";
		String knownHostsParam = isEmpty(settings.knownHostsFilePath) ? "" : "-oUserKnownHostsFile=\"${cygwinPath(knownHostsFilePath)}\"";
		return "ssh -p ${remotePort} ${userParam} -i \"${cygwinPath(remoteKey)}\" ${knownHostsParam} -oBatchMode=yes";
	}

	private boolean isEmpty(String text) {
		return text == null || text.isEmpty();
	}

	String localRoute(String dir) {
		dir = dir.endsWith("/") ? dir : "${dir}/";
		return cygwinPath(dir);
	}
	
	public String cygwinPath(String path) {
		if (SystemUtils.IS_OS_WINDOWS) {
			path = path.replaceFirst("^(.):\\/*", "/cygdrive/$1/"); // replace "C:/something" with "/cygdrive/c/something" for rsync to understand it
			path = path.replace("\\", "/");
		}
		return path;
	}
	
	

	public String getOutboxLocalDir() {
		return settings.outboxLocalDir;
	}
}