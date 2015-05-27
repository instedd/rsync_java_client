package org.instedd.rsync_java_client.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.instedd.rsync_java_client.RsyncSynchronizer;

public class RsyncWatchListener implements PathWatchListener {

  private static final Log log = LogFactory.getLog(RsyncWatchListener.class);
  private RsyncSynchronizer synchronizer;

  public RsyncWatchListener(RsyncSynchronizer synchronizer) {
    this.synchronizer = synchronizer;
  }

  @Override
  public void onWatchStarted() throws IOException {
    log.info("Watch started. Doing initial sync");
    synchronizer.sync();
  }

  @Override
  public void onSinglePathChange(Kind<Path> kind, Path context) {
    log.info("File change event " + kind + " for file " + context);
  }

  public void onGlobalPathChange(Path path) throws IOException {
    log.info("Path changed. Doing sync");
    synchronizer.sync();
  }
}
