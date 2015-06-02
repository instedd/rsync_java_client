package org.instedd.rsync_java_client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.Properties;

import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.instedd.rsync_java_client.settings.PropertiesSettingsStore;

public class Settings {

  public static class ValidationError extends Exception {
    private String field;

    public ValidationError(String field, String message) {
      super(message);
      this.field = field;
    }

    public String getField() {
      return field;
    }
  }

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

  public void validate() throws ValidationError {
    if (localInboxDir != null) {
      if (localInboxDir.equals(""))
        throw new ValidationError("localInboxDir", "Inbox directory is not set");

      File localInbox = new File(localInboxDir);
      if (!localInbox.exists())
        throw new ValidationError("localInboxDir", "Inbox directory does not exist");
      if (!localInbox.isDirectory())
        throw new ValidationError("localInboxDir", "Inbox directory path doesn't point to a directory");
    }

    if (localOutboxDir != null) {
      if (localOutboxDir.equals(""))
        throw new ValidationError("localOutboxDir", "Outbox directory is not set");

      File localOutbox = new File(localOutboxDir);
      if (!localOutbox.exists())
        throw new ValidationError("localOutboxDir", "Outbox directory does not exist");
      if (!localOutbox.isDirectory())
        throw new ValidationError("localOutboxDir", "Outbox directory path doesn't point to a directory");
    }

    if (remoteHost == null) {
      throw new ValidationError("remoteHost", "Remote host is missing");
    }
  }

  public boolean isValid() {
    try {
      validate();
      return true;
    } catch (ValidationError e) {
      return false;
    }
  }

  public void deactivate() {
    remoteHost = null;
  }

  public boolean isActivated() {
    return remoteHost != null;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  protected int loadProperty(Properties properties, String key, int defaultValue) {
    String value = properties.getProperty(key);
    if (value != null) {
      return Integer.valueOf(value);
    }
    return defaultValue;
  }

  protected boolean loadProperty(Properties properties, String key, boolean defaultValue) {
    String value = properties.getProperty(key);
    if (value != null) {
      return Boolean.valueOf(value);
    }
    return defaultValue;
  }

  protected String loadProperty(Properties properties, String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public void fromProperties(Properties properties) {
    remoteHost = loadProperty(properties, "remote.host", remoteHost);
    remotePort = loadProperty(properties, "remote.port", remotePort);
    remoteUser = loadProperty(properties, "remote.user", remoteUser);
    remoteKey = loadProperty(properties, "remote.key", remoteKey);
    knownHostsFilePath = loadProperty(properties, "known.hosts.file.path", knownHostsFilePath);
    localInboxDir = loadProperty(properties, "local.inbox.dir", localInboxDir);
    localOutboxDir = loadProperty(properties, "local.outbox.dir", localOutboxDir);
    remoteInboxDir = loadProperty(properties, "remote.inbox.dir", remoteInboxDir);
    remoteOutboxDir = loadProperty(properties, "remote.outbox.dir", remoteOutboxDir);
    strictHostChecking = loadProperty(properties, "strict.host.checking", strictHostChecking);
  }

  public void load() throws IOException {
    Path settingsPath = rootPath.resolve("settings.properties");
    if (!settingsPath.toFile().exists()) {
      return;
    }

    Properties properties = new Properties();
    try (InputStream in = Files.newInputStream(settingsPath)) {
      properties.load(in);
    }
    fromProperties(properties);
  }

  protected void saveProperty(Properties properties, String key, String value) {
    if (value != null)
      properties.setProperty(key, value);
  }

  protected void saveProperty(Properties properties, String key, int value) {
    properties.setProperty(key, Integer.toString(value));
  }

  protected void saveProperty(Properties properties, String key, boolean value) {
    properties.setProperty(key, Boolean.toString(value));
  }

  public Properties toProperties() {
    Properties properties = new Properties();
    saveProperty(properties, "remote.host", remoteHost);
    saveProperty(properties, "remote.port", remotePort);
    saveProperty(properties, "remote.user", remoteUser);
    saveProperty(properties, "remote.key", remoteKey);
    saveProperty(properties, "known.hosts.file.path", knownHostsFilePath);
    saveProperty(properties, "local.inbox.dir", localInboxDir);
    saveProperty(properties, "local.outbox.dir", localOutboxDir);
    saveProperty(properties, "remote.inbox.dir", remoteInboxDir);
    saveProperty(properties, "remote.outbox.dir", remoteOutboxDir);
    saveProperty(properties, "strict.host.checking", strictHostChecking);
    return properties;
  }

  public void save() throws IOException {
    Path settingsPath = rootPath.resolve("settings.properties");
    try (OutputStream out = Files.newOutputStream(settingsPath)) {
      toProperties().store(out, null);
    }
  }

  public void copyTo(Settings other) {
    other.remoteHost = this.remoteHost;
    other.remotePort = this.remotePort;
    other.remoteUser = this.remoteUser;
    other.remoteKey = this.remoteKey;
    other.knownHostsFilePath = this.knownHostsFilePath;
    other.localInboxDir = this.localInboxDir;
    other.localOutboxDir = this.localOutboxDir;
    other.remoteInboxDir = this.remoteInboxDir;
    other.remoteOutboxDir = this.remoteOutboxDir;
    other.strictHostChecking = this.strictHostChecking;
  }
}
