package org.instedd.rsync_java_client.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

public interface PathWatchListener {

  /**
   * Fired whenever a path changes, because of any amount of single path
   * changes.
   *
   * For each time a global change event is fired, at least a single path change
   * event is fired too.
   *
   * @param path
   *          the path that changed
   * @throws Exception
   *           if any exceptions occurs. Exceptions here will be ignored by the
   *           {@link PathWatcher}
   */
  void onGlobalPathChange(Path path) throws Exception;

  /**
   *
   * @param kind
   *          the kind of change
   * @param path
   *          the path of the file that changed
   * @throws Exception
   *           if any exceptions occurs. Exceptions here will be ignored by the
   *           {@link PathWatcher}
   */
  void onSinglePathChange(Kind<Path> kind, Path path) throws Exception;

  /**
   * Fired when the {@link PathWatcher} starts its watch.
   *
   * @throws Exception
   *           if any exception occurs. Exceptions here will
   *           <strong>not</strong> be ignored, and will cause the watcher to
   *           end its watchF
   */
  void onWatchStarted() throws Exception;

}
