package org.instedd.sync4j.settings;

import org.instedd.sync4j.Settings;

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