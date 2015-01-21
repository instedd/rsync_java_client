package org.instedd.sync4j.settings;

import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;

public class PropertiesSettingsStore extends AbstractSettingsStore {

  private Properties props;

  public PropertiesSettingsStore(Properties props) {
    this.props = props;
  }

  protected String get(String key, String defaultValue) {
    return props.getProperty("sync." + key, ObjectUtils.toString(defaultValue, null));
  }

  @Override
  protected void set(String string, String remoteOutboxDir) {
    throw new UnsupportedOperationException();
  }

}
