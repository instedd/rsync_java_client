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
  private SyncMode mode;

  public RsyncWatchListener(RsyncSynchronizer synchronizer, SyncMode mode) {
    this.synchronizer = synchronizer;
    this.mode = mode;
  }

  @Override
  public void onWatchStarted() throws IOException {
    log.info("Watch started. Doing initial sync");
    mode.doSync(synchronizer);
  }

  @Override
  public void onSinglePathChange(Kind<Path> kind, Path context) {
    log.info("File change event " + kind + " for file " + context);
  }

  public void onGlobalPathChange(Path path) throws IOException {
    log.info("Path changed. Doing sync");
    mode.doSync(synchronizer);
  }

  // TODO move to general sync code
  public enum SyncMode {
    DOWNLOAD {
      public void doSync(RsyncSynchronizer synchronizer) throws IOException {
        synchronizer.downloadDocuments();
      }
    },
    UPLOAD {
      public void doSync(RsyncSynchronizer synchronizer) throws IOException {
        synchronizer.uploadDocuments();
      }
    },
    FULL {
      public void doSync(RsyncSynchronizer synchronizer) throws IOException {
        DOWNLOAD.doSync(synchronizer);
        UPLOAD.doSync(synchronizer);
      }
    };
    public abstract void doSync(RsyncSynchronizer synchronizer) throws IOException;

  }

}
