package org.instedd.sync4j.tray;

import static org.instedd.sync4j.util.Exceptions.unchecked;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

public class SystemTrays {

  public static void open(String tooltip, String imageFilename, PopupMenuConfigurer configurer) {
    if (!SystemTray.isSupported()) {
      throw new UnsupportedOperationException("Tray is not available");
    }

    SystemTray tray = SystemTray.getSystemTray();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    PopupMenu menu = new PopupMenu(tooltip);
    configurer.configure(menu);
    if (imageFilename != null)
      unchecked(() -> tray.add(createIcon(tooltip, imageFilename, menu, toolkit)));
  }

  public static TrayIcon createIcon(String tooltip, String imageFilename, PopupMenu menu, Toolkit toolkit) {
    Image image = toolkit.getImage(imageFilename);
    TrayIcon icon = new TrayIcon(image, tooltip, menu);
    icon.setImageAutoSize(true);
    return icon;
  }
}