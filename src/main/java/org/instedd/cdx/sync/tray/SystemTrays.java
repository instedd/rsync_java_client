package org.instedd.cdx.sync.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import org.apache.commons.lang.UnhandledException;

public class SystemTrays {

	public static void open(String tooltip, String imageFilename, PopupMenu menu) {
		if (!SystemTray.isSupported()) {
			throw new UnsupportedOperationException("Tray is not available");
		}

		SystemTray tray = SystemTray.getSystemTray();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
	    tray.add(createIcon(tooltip, imageFilename, menu, toolkit));
    } catch (AWTException e) {
	    throw new UnhandledException(e);
    }
	}

	public static TrayIcon createIcon(String tooltip, String imageFilename, PopupMenu menu, Toolkit toolkit) {
	  Image image = toolkit.getImage(imageFilename);
		TrayIcon icon = new TrayIcon(image, tooltip, menu);
		icon.setImageAutoSize(true);
	  return icon;
  }
}