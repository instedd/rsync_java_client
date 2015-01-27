package org.instedd.rsync_java_client.settings;

import java.io.File;
import java.util.NavigableMap;

import org.instedd.rsync_java_client.Settings;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class MapDBSettingsStore extends AbstractSettingsStore {

  private NavigableMap<String, String> treeMap;
  private DB db;

  public MapDBSettingsStore(DB db, NavigableMap<String, String> treeMap) {
    this.db = db;
    this.treeMap = treeMap;
  }

  @Override
  public Settings getSettings() {
    if (treeMap.isEmpty()) {
      return null;
    }

    return super.getSettings();
  }

  @Override
  public void setSettings(Settings settings) {
    super.setSettings(settings);
    db.commit();
  }

  protected String get(String key) {
    return treeMap.get(key);
  }

  protected void set(String key, String value) {
    if (value != null) {
      treeMap.put(key, value);
    }
  }

  public static MapDBSettingsStore fromMapDB(String databaseFilename) {
    DB db = DBMaker.newFileDB(new File(databaseFilename)).closeOnJvmShutdown().make();
    NavigableMap<String, String> map = db.getTreeMap("settings");
    return new MapDBSettingsStore(db, map);
  }

}
