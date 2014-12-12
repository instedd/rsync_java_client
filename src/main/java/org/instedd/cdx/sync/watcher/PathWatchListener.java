package org.instedd.cdx.sync.watcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

public interface PathWatchListener {

	void pathChanged(Path path);

	void fileChanged(Kind<File> kind, File context);

}
