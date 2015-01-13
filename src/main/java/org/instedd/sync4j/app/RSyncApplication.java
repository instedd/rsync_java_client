package org.instedd.sync4j.app;

import java.nio.file.Paths;

import org.instedd.sync4j.RsyncCommandBuilder;
import org.instedd.sync4j.RsyncSynchronizer;
import org.instedd.sync4j.Settings;
import org.instedd.sync4j.watcher.PathWatcher;
import org.instedd.sync4j.watcher.RsyncWatchListener;
import org.instedd.sync4j.watcher.RsyncWatchListener.SyncMode;

public class RSyncApplication {

  private final Settings settings;
  private final SyncMode syncMode;

  private transient Thread thread;

  public RSyncApplication(Settings settings, SyncMode syncMode) {
    this.settings = settings;
    this.syncMode = syncMode;
  }

  public void start(RSyncApplicationStopper... stoppers) {
    RsyncSynchronizer synchronizer = newSynchronizer();
    // TODO log sync mode
    PathWatcher watcher = new PathWatcher(Paths.get(settings.localOutboxDir), new RsyncWatchListener(synchronizer, syncMode));
    thread = new Thread(watcher::watch, "watcher-thread");
    for(RSyncApplicationStopper stopper : stoppers) {
      stopper.start(this);
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

  public void waitStop() throws InterruptedException {
    if (thread != null) {
      thread.join();
    }
  }

  protected RsyncSynchronizer newSynchronizer() {
    RsyncCommandBuilder commandBuilder = new RsyncCommandBuilder(settings);
    RsyncSynchronizer synchronizer = new RsyncSynchronizer(commandBuilder);
    return synchronizer;
  }

}
