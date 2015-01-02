package org.instedd.cdx.sync.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

public interface PathWatchListener {

  void onGlobalPathChange(Path path);

  void onSinglePathChange(Kind<Path> kind, Path path);

  void onWatchStarted();

}
