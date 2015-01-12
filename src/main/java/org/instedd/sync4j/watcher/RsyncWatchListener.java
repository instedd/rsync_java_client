package org.instedd.sync4j.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.logging.Logger;

import org.instedd.sync4j.RsyncSynchronizer;

public class RsyncWatchListener implements PathWatchListener {

  private static final Logger logger = Logger.getLogger(RsyncWatchListener.class.getName());
  private RsyncSynchronizer synchronizer;
  private SyncMode mode;

  public RsyncWatchListener(RsyncSynchronizer synchronizer, SyncMode mode) {
    this.synchronizer = synchronizer;
    this.mode = mode;
  }

  @Override
  public void onWatchStarted() throws IOException {
    logger.info("Watch started. Doing initial sync");
    mode.doSync(synchronizer);
  }

  @Override
  public void onSinglePathChange(Kind<Path> kind, Path context) {
    logger.info("File change event " + kind + " for file " + context);
  }

  public void onGlobalPathChange(Path path) throws IOException {
    logger.info("Path changed. Doing sync");
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
