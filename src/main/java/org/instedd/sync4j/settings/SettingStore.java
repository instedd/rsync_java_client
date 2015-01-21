package org.instedd.sync4j.settings;

import org.instedd.sync4j.Settings;

public interface SettingStore {

  Settings getSettings();

  void setSettings(Settings settings);

}