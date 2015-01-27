package org.instedd.rsync_java_client.tray;

import static org.instedd.rsync_java_client.util.Exceptions.unchecked;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.net.URL;

public class SystemTrays {

  public static void open(String tooltip, URL imageUrl, PopupMenuConfigurer configurer) {
    if (!SystemTray.isSupported()) {
      throw new UnsupportedOperationException("Tray is not available");
    }

    SystemTray tray = SystemTray.getSystemTray();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    PopupMenu menu = new PopupMenu(tooltip);
    configurer.configure(menu);
    if (imageUrl != null)
      unchecked(() -> tray.add(createIcon(tooltip, imageUrl, menu, toolkit)));
  }

  public static TrayIcon createIcon(String tooltip, URL imageUrl, PopupMenu menu, Toolkit toolkit) {
    Image image = toolkit.getImage(imageUrl);
    TrayIcon icon = new TrayIcon(image, tooltip, menu);
    icon.setImageAutoSize(true);
    return icon;
  }
}
