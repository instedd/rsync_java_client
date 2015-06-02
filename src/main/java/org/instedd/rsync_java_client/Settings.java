package org.instedd.rsync_java_client;

import java.io.File;

import java.util.Arrays;
import java.util.Properties;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.instedd.rsync_java_client.settings.PropertiesSettingsStore;

public class Settings {
  public String appName;

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
   * The configuration path, the place where all the settings and keys are stored.
   *
   * Defaults to ~
   */
  public Path rootPath;

  public String remoteKey;

  /**
   * The private ssh key used to log into the remote host.
   *
   * Defaults to ~/.ssh/id_rsa
   */
  public String getRemoteKeyPath() {
    if (remoteKey != null) {
      return remoteKey;
    } else {
      return rootPath.resolve("remote_key").toString();
    }
  }

  public String knownHostsFilePath;


  /**
   * Client directory where files transferred from server to client will be
   * placed after download
   */
  public String localInboxDir;

  /**
   * Client directory where files transferred from client to server must be
   * placed before upload
   *
   * If you want to transfer a file to server, put it here.
   */
  public String localOutboxDir;

  /**
   * Server directory where files transferred from client to server will be
   * placed after upload
   */
  public String remoteInboxDir = "/inbox";

  /**
   * Server directory where files transferred from server to client must be
   * placed before download
   *
   * If you want to transfer a file to client, put it here
   */
  public String remoteOutboxDir = "/outbox";

  public boolean strictHostChecking = true;

  public Settings(String appName, String rootPath) {
    this.appName = appName;
    if (rootPath == null) {
      if (System.getProperty("os.name").contains("Windows")) {
        this.rootPath = FileSystems.getDefault().getPath(System.getenv("LOCALAPPDATA"), appName);
      } else {
        this.rootPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), "." + appName);
      }
    } else {
      this.rootPath = FileSystems.getDefault().getPath(rootPath);
    }

    File rootPathDir = new File(this.rootPath.toString());
    if (!rootPathDir.exists()) {
      rootPathDir.mkdirs();
    }

    knownHostsFilePath = this.rootPath.resolve("known_hosts").toString();
  }

  public Settings() {
    this("rsync_java_client", null);
  }

  public void validate() {
    for (Object f : Arrays.asList(remoteHost, remotePort)) {
      Validate.notNull(f, "Remote host settings missing (required: host, port)");
    }// TODO this validation depends on sync mode
    if (localInboxDir == null && localOutboxDir == null) {
      throw new IllegalArgumentException("either inboxLocalDir or outboxLocalDir must be set");
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public static Settings fromProperties(final Properties props) {
    return new PropertiesSettingsStore(props).getSettings();
  }
}
