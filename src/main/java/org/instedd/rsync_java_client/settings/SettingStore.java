package org.instedd.rsync_java_client.settings;

import org.instedd.rsync_java_client.Settings;

public interface SettingStore {

  /**
   * Answers the stored settings
   *
   * @return the stored settings, or null, if no settings have being saved to
   *         this store yet
   */
  Settings getSettings();

  void setSettings(Settings settings);

}
