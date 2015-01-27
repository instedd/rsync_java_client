package org.instedd.rsync_java_client.credentials;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.instedd.rsync_java_client.Settings;
import org.instedd.rsync_java_client.settings.MapDBSettingsStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MapDBSettingsStoreTest {

  @Rule
  public TemporaryFolder root = new TemporaryFolder();
  private MapDBSettingsStore store;

  @Before
  public void setUp() throws IOException {
    File dbFile = root.newFile("foobar");
    store = MapDBSettingsStore.fromMapDB(dbFile.getAbsolutePath());
  }

  @Test
  public void canSetAndGetSettings() throws IOException {
    Settings originalSettings = new Settings();
    originalSettings.remoteHost = "192.64.34.2";
    originalSettings.strictHostChecking = false;
    store.setSettings(originalSettings);

    Settings settings = store.getSettings();

    assertTrue(reflectionEquals(settings, originalSettings));
  }

  @Test
  public void thereAreNoSettingsOnAnEmptyDB() throws Exception {
    assertNull(store.getSettings());
  }

}
