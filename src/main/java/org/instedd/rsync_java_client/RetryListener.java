package org.instedd.rsync_java_client;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RetryListener implements RsyncSynchronizerListener {
  private RsyncSynchronizer synchronizer;
  private Timer timer;
  private long delay;
  private final Log log = LogFactory.getLog(RetryListener.class);

  public RetryListener(RsyncSynchronizer synchronizer, long delay) {
    this.synchronizer = synchronizer;
    this.delay = delay;
    this.timer = new Timer();
  }

  public void transferStarted() {
    timer.purge();
  }

  public void transferFailed(String errorMessage) {
    timer.schedule(new TimerTask() {
      public void run() {
        log.info("Retrying synchronization");
        synchronizer.sync();
      }
    }, delay);
  }

  public void transferCompleted(List<String> uploadedFiles, List<String> downloadedFiles) {
    timer.purge();
  }

}
