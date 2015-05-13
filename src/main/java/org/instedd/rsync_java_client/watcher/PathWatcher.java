package org.instedd.rsync_java_client.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.instedd.rsync_java_client.util.Exceptions.interruptable;
import static org.instedd.rsync_java_client.util.Exceptions.unchecked;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.instedd.rsync_java_client.util.Exceptions.CheckedRunnable;

public class PathWatcher {

  private static final Log log = LogFactory.getLog(PathWatcher.class);
  private Path path;
  private PathWatchListener listener;

  public PathWatcher(Path path, PathWatchListener listener) {
    this.path = path;
    this.listener = listener;
  }

  public void watch() {
    unchecked(() -> {
      WatchService watcher = path.getFileSystem().newWatchService();
      path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
      listener.onWatchStarted();
      interruptable(() -> pollEvents(watcher));
    });
  }

  @SuppressWarnings("unchecked")
  protected void pollEvents(WatchService watcher) throws InterruptedException {
    WatchKey key;
    while ((key = watcher.take()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        warningOnException(() -> listener.onSinglePathChange((Kind<Path>) event.kind(), (Path) event.context()));
      }
      warningOnException(() -> listener.onGlobalPathChange(path));
      key.reset();
    }
  }

  private static void warningOnException(CheckedRunnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      log.warn("Exception thrown " + ExceptionUtils.getStackTrace(e));
    }
  }
}
