package org.instedd.sync4j;

import org.instedd.sync4j.Settings;
import org.junit.Test;

public class SettingsUnitTest {

  @Test(expected = IllegalArgumentException.class)
  public void validatesMissingSettings() {
    new Settings().validate();
  }

}
