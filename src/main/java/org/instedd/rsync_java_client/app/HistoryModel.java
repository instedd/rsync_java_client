package org.instedd.rsync_java_client.app;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.AbstractListModel;
import org.instedd.rsync_java_client.Settings;

class HistoryModel extends AbstractListModel<HistoryModel.Entry> {
  private static final long serialVersionUID = 1L;
  
  private LinkedList<Entry> entries;
  private Entry error;
  private static final int MAX_SIZE = 10;
  private Path historyPath;

  public HistoryModel(Settings settings) {
    entries = new LinkedList<>();
    historyPath = settings.rootPath.resolve("history.properties");
    load();
  }

  private void load() {
    if (!historyPath.toFile().exists()) {
      return;
    }

    Properties properties = new Properties();
    try (InputStream in = Files.newInputStream(historyPath)) {
      properties.load(in);
    } catch (IOException ex) {
      return;
    }

    String text;
    String ts;
    String type;

    for (int i = 0; ; i++) {
      type = properties.getProperty("history." + i + ".type");
      ts = properties.getProperty("history." + i + ".ts");
      text = properties.getProperty("history." + i + ".text");

      if (type == null || ts == null || text == null) break;

      entries.add(new HistoryModel.Entry(EntryType.valueOf(type), text, new Date(Long.valueOf(ts))));
    }
  }

  private void save() {
    Properties properties = new Properties();
    for (int i = 0; i < entries.size(); i++) {
      Entry entry = entries.get(i);
      properties.setProperty("history." + i + ".type", entry.getType().toString());
      properties.setProperty("history." + i + ".ts", Long.toString(entry.getTimestamp().getTime()));
      properties.setProperty("history." + i + ".text", entry.getText());
    }

    try (OutputStream out = Files.newOutputStream(historyPath)) {
      properties.store(out, null);
    } catch (IOException ex) {
      return;
    }
  }

  @Override
  public HistoryModel.Entry getElementAt(int index) {
    if (error != null) {
      if (index == 0) {
        return error;
      } else {
        index--;
      }
    }

    return entries.get(index);
  }

  @Override
  public int getSize() {
    return entries.size() + (error == null ? 0 : 1);
  }

  public void setError(String message) {
    error = new Entry(EntryType.ERROR, message);
    fireContentsChanged(this, 0, getSize() - 1);
  }

  public void clearError() {
    error = null;
    fireContentsChanged(this, 0, getSize() - 1);
  }

  public void addUpload(String text) {
    add(new Entry(EntryType.UPLOAD, text));
  }

  public void addDownload(String text) {
    add(new Entry(EntryType.DOWNLOAD, text));
  }

  private void add(Entry entry) {
    entries.addFirst(entry);
    while (entries.size() > MAX_SIZE) {
      entries.removeLast();
    }

    save();
    fireContentsChanged(this, 0, getSize() - 1);
  }


  enum EntryType {
    UPLOAD, DOWNLOAD, ERROR
  }

  class Entry {
    private EntryType type;
    private String text;
    private Date ts;

    public Entry(EntryType type, String text) {
      this(type, text, new Date());
    }

    public Entry(EntryType type, String text, Date ts) {
      this.type = type;
      this.text = text;
      this.ts = ts;
    }

    public EntryType getType() {
      return type;
    }

    public String getText() {
      return text;
    }

    public Date getTimestamp() {
      return ts;
    }
  }

}
