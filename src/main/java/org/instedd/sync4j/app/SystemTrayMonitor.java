package org.instedd.sync4j.app;

import java.awt.MenuItem;
import java.net.URL;

import org.instedd.sync4j.tray.SystemTrays;

public class SystemTrayMonitor implements RSyncApplicationMonitor {
  private String tooltip;
  private URL imageUrl;

  public SystemTrayMonitor(String tooltip, URL imageUrl) {
    this.tooltip = tooltip;
    this.imageUrl = imageUrl;
  }

  @Override
  public void start(RSyncApplication application) {
    SystemTrays.open(tooltip, imageUrl, menu -> {
      MenuItem menuItem = new MenuItem("Stop Sync");
      menuItem.addActionListener(e -> {
        application.stop();
      });
      menu.add(menuItem);
    });
  }

}
