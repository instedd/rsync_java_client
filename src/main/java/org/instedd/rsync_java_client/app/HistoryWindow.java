package org.instedd.rsync_java_client.app;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.ocpsoft.prettytime.PrettyTime;

public class HistoryWindow extends JFrame {

  public HistoryWindow(HistoryModel model, JPopupMenu menu) {
    setUndecorated(true);
    setSize(350, 200);
    Container panel = getContentPane();
    panel.setBackground(SystemColor.activeCaption);

    GroupLayout layout = new GroupLayout(panel);
    layout.setAutoCreateGaps(true);
    // layout.setAutoCreateContainerGaps(true);
    panel.setLayout(layout);

    JLabel titleLabel = new JLabel("Sync Client");
    titleLabel.setForeground(SystemColor.activeCaptionText);
    titleLabel.setFont(UIManager.getFont("InternalFrame.titleFont"));
    panel.add(titleLabel);

    JScrollPane historyScroll = new JScrollPane();

    JList<HistoryModel.Entry> historyList = new JList<>(model);
    historyList.setBackground(SystemColor.window);
    historyList.setCellRenderer(new HistoryCellRenderer());
    // historyList.setFixedCellHeight(40);
    panel.add(historyList);

    JLabel settingsLabel = new JLabel(new ImageIcon(HistoryWindow.class.getResource("settings.png")));
    panel.add(settingsLabel);
    settingsLabel.addMouseListener(new MouseAdapter()
    {
        public void mouseClicked(MouseEvent e)
        {
          menu.show(e.getComponent(), e.getX(), e.getY());
        }
    });

    historyScroll.setViewportView(historyList);

    layout.setHorizontalGroup(
      layout.createSequentialGroup()
        .addGap(4)
        .addGroup(layout.createParallelGroup()
          .addGroup(layout.createSequentialGroup()
            .addGap(4)
            .addComponent(titleLabel)
            .addGap(0, 4, Integer.MAX_VALUE)
            .addComponent(settingsLabel))
          .addComponent(historyScroll)
        )
        .addGap(4)
    );

    layout.setVerticalGroup(
      layout.createSequentialGroup()
        .addGap(4)
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
          .addComponent(titleLabel)
          .addComponent(settingsLabel))
        .addComponent(historyScroll)
        .addGap(4)
    );

    addWindowFocusListener(new WindowFocusListener () {
      @Override
      public void windowLostFocus (WindowEvent we ) {
        setVisible(false);
      }
      @Override
      public void windowGainedFocus (WindowEvent we) {}
    });
  }

  public void popup(Point point) {
    if (isVisible()) {
      setVisible(false);
    } else {
      setBestLocationForPopup(point);
      setVisible(true);
    }
  }

  private void setBestLocationForPopup(Point point) {
    Rectangle bounds = getScreenViewableBounds(point);
    int x = point.x;
    int y = point.y;
    if (y < bounds.y) {
      y = bounds.y;
    } else if (y > bounds.y + bounds.height) {
      y = bounds.y + bounds.height;
    }
    if (x < bounds.x) {
      x = bounds.x;
    } else if (x > bounds.x + bounds.width) {
      x = bounds.x + bounds.width;
    }

    if (x + getWidth() > bounds.x + bounds.width) {
      x = (bounds.x + bounds.width) - getWidth();
    }
    if (y + getWidth() > bounds.y + bounds.height) {
      y = (bounds.y + bounds.height) - getHeight();
    }
    setLocation(x, y);
  }

  static class HistoryCellRenderer extends JPanel implements ListCellRenderer<HistoryModel.Entry> {
    private JLabel iconLabel;
    private JLabel textLabel;
    private JLabel dateLabel;
    private static final PrettyTime prettyTime = new PrettyTime();
    private static final Icon downloadIcon = new ImageIcon(HistoryWindow.class.getResource("download.png"));
    private static final Icon uploadIcon = new ImageIcon(HistoryWindow.class.getResource("upload.png"));
    private static final Icon errorIcon = new ImageIcon(HistoryWindow.class.getResource("error.png"));

    public HistoryCellRenderer() {
      GroupLayout layout = new GroupLayout(this);
      layout.setAutoCreateGaps(true);
      setLayout(layout);

      iconLabel = new JLabel();
      add(iconLabel);

      textLabel = new JLabel();
      add(textLabel);

      dateLabel = new JLabel();
      dateLabel.setForeground(SystemColor.textInactiveText);
      add(dateLabel);

      setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow));

      layout.setHorizontalGroup(
        layout.createSequentialGroup()
          .addGap(8)
          .addComponent(iconLabel)
          .addGap(8)
          .addGroup(layout.createParallelGroup()
            .addComponent(textLabel)
            .addComponent(dateLabel))
      );

      layout.setVerticalGroup(
        layout.createParallelGroup()
          .addComponent(iconLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGap(8)
            .addComponent(textLabel)
            .addComponent(dateLabel)
            .addGap(8))
      );
    }

    public Component getListCellRendererComponent(JList<? extends HistoryModel.Entry> list,
      HistoryModel.Entry value, int index, boolean isSelected, boolean cellHasFocus) {

      switch (value.getType()) {
        case ERROR:
          iconLabel.setIcon(errorIcon);
          break;
        case UPLOAD:
          iconLabel.setIcon(uploadIcon);
          break;
        case DOWNLOAD:
          iconLabel.setIcon(downloadIcon);
          break;
      }
      setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
      textLabel.setForeground(isSelected ? SystemColor.textHighlightText : SystemColor.textText);
      textLabel.setText(value.getText());
      dateLabel.setText(prettyTime.format(value.getTimestamp()));

      return this;
    }
  }

  private static GraphicsDevice getGraphicsDeviceAt(Point pos) {
    GraphicsDevice device = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice lstGDs[] = ge.getScreenDevices();

    ArrayList<GraphicsDevice> lstDevices = new ArrayList<GraphicsDevice>(lstGDs.length);
    for (GraphicsDevice gd : lstGDs) {
      GraphicsConfiguration gc = gd.getDefaultConfiguration();
      Rectangle screenBounds = gc.getBounds();

      if (screenBounds.contains(pos)) {
        lstDevices.add(gd);
      }
    }

    if (lstDevices.size() == 1) {
      device = lstDevices.get(0);
    }

    return device;
  }

  private static Rectangle getScreenViewableBounds(Point p) {
    return getScreenViewableBounds(getGraphicsDeviceAt(p));
  }

  private static Rectangle getScreenViewableBounds(GraphicsDevice gd) {
    Rectangle bounds = new Rectangle(0, 0, 0, 0);

    if (gd != null) {
      GraphicsConfiguration gc = gd.getDefaultConfiguration();
      bounds = gc.getBounds();

      Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
      bounds.x += insets.left;
      bounds.y += insets.top;
      bounds.width -= (insets.left + insets.right);
      bounds.height -= (insets.top + insets.bottom);
    }

    return bounds;
  }

}
