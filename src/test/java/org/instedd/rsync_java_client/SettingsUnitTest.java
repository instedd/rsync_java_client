package org.instedd.rsync_java_client;

import org.instedd.rsync_java_client.Settings;
import org.junit.Test;

public class SettingsUnitTest {

  @Test(expected = IllegalArgumentException.class)
  public void validatesMissingSettings() {
    new Settings().validate();
  }

}
