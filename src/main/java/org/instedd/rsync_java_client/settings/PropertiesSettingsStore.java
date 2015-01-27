package org.instedd.rsync_java_client.settings;

import java.util.Properties;

public class PropertiesSettingsStore extends AbstractSettingsStore {

  private Properties props;

  public PropertiesSettingsStore(Properties props) {
    this.props = props;
  }

  protected String get(String key) {
    return props.getProperty("sync." + key);
  }

  @Override
  protected void set(String string, String remoteOutboxDir) {
    throw new UnsupportedOperationException();
  }

}
