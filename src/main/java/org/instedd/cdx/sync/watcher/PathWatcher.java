package org.instedd.cdx.sync.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Logger;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.exception.ExceptionUtils;

public class PathWatcher {

  private static final Logger logger = Logger.getLogger(PathWatcher.class.getName());

  public static Runnable asyncWatch(final Path path, final PathWatchListener listener) {
    return new Runnable() {
      public void run() {
        syncWatch(path, listener);
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static void syncWatch(Path path, PathWatchListener listener) {
    try {
      WatchService watcher = path.getFileSystem().newWatchService();
      path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
      listener.onWatchStarted();
      WatchKey key;
      while ((key = watcher.take()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) {
          try {
            listener.onSinglePathChange((Kind<Path>) event.kind(), (Path) event.context());
          } catch (Exception e) {
            logger.warning("Exception thrown " + ExceptionUtils.getStackTrace(e));
          }
        }
        try {
          listener.onGlobalPathChange(path);
        } catch (Exception e) {
          logger.warning("Exception thrown " + ExceptionUtils.getStackTrace(e));
        }
        key.reset();
      }
    } catch (InterruptedException e) {
      // OK. Normal finishing normally
    } catch (Exception e) {
      throw new UnhandledException(e);
    }
  }
}
