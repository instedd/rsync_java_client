package org.instedd.cdx.sync.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.lang.UnhandledException;

public class PathWatcher {

	public static Runnable asyncWatch(final Path path, final PathWatchListener listener) throws Exception {
		return new Runnable() {
			public void run() {
				try {
					syncWatch(path, listener);
				} catch (IOException e) {
					throw new UnhandledException(e);
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
  public static void syncWatch(Path path, PathWatchListener listener) throws IOException {
		WatchService watcher = path.getFileSystem().newWatchService();
		path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
		try {
			WatchKey key;
			while ((key = watcher.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					listener.fileChanged((Kind<File>) event.kind(), (File) event.context());
				}
				listener.pathChanged(path);
				key.reset();
			}
		} catch (InterruptedException e) {
			// OK
		}
	}
}
