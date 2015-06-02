package org.instedd.rsync_java_client;

import org.instedd.rsync_java_client.Settings;
import org.junit.Test;

public class SettingsUnitTest {

  @Test(expected = Settings.ValidationError.class)
  public void validatesMissingSettings() throws Settings.ValidationError {
    new Settings().validate();
  }

}
