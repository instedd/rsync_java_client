package org.instedd.sync4j.settings;

import org.instedd.sync4j.Settings;

public abstract class AbstractSettingsStore implements SettingStore {

  @Override
  public Settings getSettings() {
    return new Settings() {
      {
        remoteHost = get("remote.host", remoteHost);
        remotePort = Integer.valueOf(get("remote.port", remotePort.toString()));
        remoteUser = get("remote.user", remoteUser);
        remoteKey = get("remote.key", remoteKey);
        knownHostsFilePath = get("known.hosts.file.path", knownHostsFilePath);
        localInboxDir = get("local.inbox.dir", localInboxDir);
        localOutboxDir = get("local.outbox.dir", localOutboxDir);
        remoteInboxDir = get("remote.inbox.dir", remoteInboxDir);
        remoteOutboxDir = get("remote.outbox.dir", remoteOutboxDir);
      }
    };
  }

  protected abstract String get(String string, String value);

  @Override
  public void setSettings(Settings settings) {
    set("remote.host", settings.remoteHost);
    set("remote.port", settings.remotePort.toString());
    set("remote.user", settings.remoteUser);
    set("remote.key", settings.remoteKey);
    set("known.hosts.file.path", settings.knownHostsFilePath);
    set("local.inbox.dir", settings.localInboxDir);
    set("local.outbox.dir", settings.localOutboxDir);
    set("remote.inbox.dir", settings.remoteInboxDir);
    set("remote.outbox.dir", settings.remoteOutboxDir);

  }

  protected abstract void set(String string, String remoteOutboxDir);
}
