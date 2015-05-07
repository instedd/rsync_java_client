package org.instedd.rsync_java_client;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.instedd.rsync_java_client.settings.PropertiesSettingsStore;

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
  public String remoteKey = combine(rootPath(), ".ssh/id_rsa");

  public String knownHostsFilePath = combine(rootPath(), "known_hosts");

  /**
   * The configuration path, the place where all the settings and keys are stored.
   *
   * Defaults to ~
   */
  public String rootPath = System.getProperty("user.home");

  /**
   * Client directory where files transferred from server to client will be
   * placed after download
   */
  public String localInboxDir = combine(rootPath(), "cdx/inbox");

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

  public void validate() {
    for (Object f : Arrays.asList(remoteHost, remotePort, remoteKey)) {
      Validate.notNull(f, "Remote host settings missing (required: host, port, user and path to ssh key");
    }// TODO this validation depends on sync mode
    if (localInboxDir == null && localOutboxDir == null) {
      throw new IllegalArgumentException("either inboxLocalDir or outboxLocalDir must be set");
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  private String combine(String path1, String path2)
  {
    File file1 = new File(path1);
    File file2 = new File(file1, path2);
    return file2.getPath();
  }

  public static Settings fromProperties(final Properties props) {
    return new PropertiesSettingsStore(props).getSettings();
  }
}
