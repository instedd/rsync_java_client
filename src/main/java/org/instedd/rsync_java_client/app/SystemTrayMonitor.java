package org.instedd.rsync_java_client.app;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.net.URL;

import org.instedd.rsync_java_client.tray.SystemTrays;

public class SystemTrayMonitor implements RSyncApplicationMonitor {
  private String tooltip;
  private URL imageUrl;
  private TrayIcon trayIcon;

  public SystemTrayMonitor(String tooltip, URL imageUrl) {
    this.tooltip = tooltip;
    this.imageUrl = imageUrl;
  }

  @Override
  public void start(RSyncApplication application) {
    trayIcon = SystemTrays.open(tooltip, imageUrl, menu -> configureMenu(application, menu));
  }

  protected void configureMenu(RSyncApplication application, PopupMenu menu) {
    MenuItem menuItem = new MenuItem("Stop Sync");
    menuItem.addActionListener(e -> {
      application.stop();
    });
    menu.add(menuItem);
  }

  protected TrayIcon getTrayIcon() {
    return trayIcon;
  }

}
