package org.instedd.rsync_java_client.app;

import java.nio.file.Paths;
import java.util.EnumSet;

import org.instedd.rsync_java_client.*;
import org.instedd.rsync_java_client.watcher.*;

public class RSyncApplication {

  private final Settings settings;
  private final EnumSet<SyncMode> syncMode;
  private final RsyncSynchronizer synchronizer;
  private static final long retryInterval = 5 * 60 * 1000; // 5 minutes

  private transient Thread thread;

  public RSyncApplication(Settings settings, EnumSet<SyncMode> syncMode) {
    this.settings = settings;
    this.syncMode = syncMode;
    this.synchronizer = newSynchronizer();
  }

  public void addMonitor(RSyncApplicationMonitor monitor) {
    monitor.start(this);
    if (monitor instanceof RsyncSynchronizerListener) {
      synchronizer.addListener((RsyncSynchronizerListener) monitor);
    }
  }

  public Settings getSettings() {
    return settings;
  }

  public void start() {
    PathWatcher watcher = new PathWatcher(Paths.get(settings.localOutboxDir), new RsyncWatchListener(synchronizer));
    thread = new Thread(() -> { watcher.watch(); }, "watcher-thread");
    thread.start();
  }

  public boolean isRunning() {
    return thread != null && thread.isAlive();
  }

  public void stop() {
    if (thread != null) {
      thread.interrupt();
    }
  }

  public void restart() {
    stop();
    start();
  }

  protected RsyncSynchronizer newSynchronizer() {
    RsyncCommandBuilder commandBuilder = new RsyncCommandBuilder(settings);
    RsyncSynchronizer synchronizer = new RsyncSynchronizer(commandBuilder, syncMode);
    synchronizer.setUp();
    synchronizer.addListener(new RetryListener(synchronizer, retryInterval));
    return synchronizer;
  }

}
