package org.instedd.sync4j.credentials;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.instedd.sync4j.Settings;
import org.instedd.sync4j.settings.MapDBSettingsStore;
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
    store.setSettings(originalSettings);

    Settings settings = store.getSettings();

    assertTrue(reflectionEquals(settings, originalSettings));
  }

}
