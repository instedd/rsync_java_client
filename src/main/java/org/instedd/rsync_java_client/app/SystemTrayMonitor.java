package org.instedd.rsync_java_client.app;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.util.List;
import org.instedd.rsync_java_client.RsyncSynchronizerListener;

import org.instedd.rsync_java_client.tray.SystemTrays;

public class SystemTrayMonitor implements RSyncApplicationMonitor, RsyncSynchronizerListener {
  private String tooltip;
  private URL imageUrl;
  private TrayIcon trayIcon;
  private HistoryModel history;

  public SystemTrayMonitor(String tooltip, URL imageUrl) {
    this.tooltip = tooltip;
    this.imageUrl = imageUrl;
  }

  @Override
  public void start(RSyncApplication application) {
    JPopupMenu menu = new JPopupMenu();
    configureMenu(application, menu);

    history = new HistoryModel(application.getSettings());
    HistoryWindow historyWindow = new HistoryWindow(history, menu);

    Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);
    trayIcon = new TrayIcon(image, tooltip);
    trayIcon.setImageAutoSize(true);

    try {
      SystemTray.getSystemTray().add(trayIcon);
    } catch (AWTException e) {
    }

    trayIcon.addMouseListener(new MouseAdapter()
    {
        public void mouseClicked(MouseEvent e)
        {
          historyWindow.popup(e.getPoint());
        }
    });
  }

  @Override
  public void transferStarted() {
    history.clearError();
  }

  @Override
  public void transferFailed(String errorMessage) {
    history.setError(errorMessage);
  }

  @Override
  public void transferCompleted(List<String> uploadedFiles, List<String> downloadedFiles) {
    if (uploadedFiles != null) {
      for (String file : uploadedFiles) {
        history.addUpload(file);
      }
    }

    if (downloadedFiles != null) {
      for (String file : downloadedFiles) {
        history.addDownload(file);
      }
    }
  }

  protected void configureMenu(RSyncApplication application, JPopupMenu menu) {
    JMenuItem menuItem = new JMenuItem("Exit");
    menuItem.addActionListener(e -> {
      application.stop();
      System.exit(0);
    });
    menu.add(menuItem);
  }

  protected TrayIcon getTrayIcon() {
    return trayIcon;
  }

}
