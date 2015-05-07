package org.instedd.rsync_java_client;

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
    return process(RSYNC_COMMAND, "-irltz", "--chmod=ug=rwX,o=", "--remove-source-files", "-e", shellCommand(), getLocalOutboxPath(), getRemoteInboxPath());
  }

  public ProcessBuilder buildDownloadCommand() {
    return process(RSYNC_COMMAND, "-irltz", "--chmod=ug=rwX,o=", "--remove-source-files", "-e", shellCommand(), getRemoteOutboxPath(), getLocalInboxPath());
  }

  public ProcessBuilder buildTestCommand() {
    return process(RSYNC_COMMAND, "--help");
  }

  public String getLocalOutboxPath() {
    return localPath(settings.localOutboxDir);
  }

  public String getRemoteInboxPath() {
    return "" + settings.remoteHost + ":" + settings.remoteInboxDir;
  }

  public String getLocalInboxPath() {
    return localPath(settings.localInboxDir);
  }

  public String getRemoteOutboxPath() {
    // trailing slash prevents an 'outbox' directory to be created
    return "" + settings.remoteHost + ":" + settings.remoteOutboxDir + "/";
  }

  public String shellCommand() {
    String userParam = isEmpty(settings.remoteUser) ? "" : "-l " + settings.remoteUser + "";
    String knownHostsParam = isEmpty(settings.knownHostsFilePath) ? "" : "-oUserKnownHostsFile=\"" + cygwinPath(settings.knownHostsFilePath) + "\"";
    return "ssh -p " + settings.remotePort + " " + userParam + " -i \"" + cygwinPath(settings.remoteKey) + "\" " + knownHostsParam
        + " -oBatchMode=yes" + strictCheckingOption();
  }

  protected String localPath(String dir) {
    dir = dir.endsWith("/") ? dir : "" + dir + "/";
    return cygwinPath(dir);
  }

  protected String strictCheckingOption() {
    if ( !settings.strictHostChecking ) {
      return " -oStrictHostKeyChecking=no";
    }
    return "";
  }

  public String cygwinPath(String path) {
    if (SystemUtils.IS_OS_WINDOWS) {
      // replace "C:\some\path" with "/cygdrive/c/some/path" for rsync to
      // understand it
      path = path.replaceFirst("^(.):\\/*", "/cygdrive/$1/");
      path = path.replace("\\", "/");
    }
    return path;
  }

  public String getOutboxLocalDir() {
    return settings.localOutboxDir;
  }
}
