package org.instedd.rsync_java_client.app;

import java.nio.file.Paths;
import java.util.EnumSet;

import org.instedd.rsync_java_client.RsyncCommandBuilder;
import org.instedd.rsync_java_client.RsyncSynchronizer;
import org.instedd.rsync_java_client.RsyncSynchronizerListener;
import org.instedd.rsync_java_client.Settings;
import org.instedd.rsync_java_client.SyncMode;
import org.instedd.rsync_java_client.watcher.PathWatcher;
import org.instedd.rsync_java_client.watcher.RsyncWatchListener;

public class RSyncApplication {

  private final Settings settings;
  private final EnumSet<SyncMode> syncMode;
  private final RsyncSynchronizer synchronizer;

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
    return synchronizer;
  }

}
