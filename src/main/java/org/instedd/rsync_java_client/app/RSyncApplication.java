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

  private transient Thread thread;

  public RSyncApplication(Settings settings, EnumSet<SyncMode> syncMode) {
    this.settings = settings;
    this.syncMode = syncMode;
  }

  public void start(RSyncApplicationMonitor... monitors) {
    RsyncSynchronizer synchronizer = newSynchronizer();
    // TODO log sync mode
    PathWatcher watcher = new PathWatcher(Paths.get(settings.localOutboxDir), new RsyncWatchListener(synchronizer));
    thread = new Thread(() -> {
      watcher.watch();
      System.exit(0);
    }, "watcher-thread");

    for(RSyncApplicationMonitor monitor : monitors) {
      monitor.start(this);
      if (monitor instanceof RsyncSynchronizerListener) {
        synchronizer.addListener((RsyncSynchronizerListener) monitor);
      }
    }
    synchronizer.setUp();
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

  protected RsyncSynchronizer newSynchronizer() {
    RsyncCommandBuilder commandBuilder = new RsyncCommandBuilder(settings);
    RsyncSynchronizer synchronizer = new RsyncSynchronizer(commandBuilder, syncMode);
    return synchronizer;
  }

}
