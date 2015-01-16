package org.instedd.sync4j;

import static org.apache.commons.lang.SystemUtils.USER_HOME;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.instedd.sync4j.settings.PropertiesSettingsStore;

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
  // TODO where is located on Windows by default?
  public String remoteKey = USER_HOME + "/.ssh/id_rsa";
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

  public static Settings fromProperties(final Properties props) {
    return new PropertiesSettingsStore(props).getSettings();
  }


}
