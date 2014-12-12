package org.instedd.cdx.sync.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchEvent.Kind;
import java.util.logging.Logger;

import org.apache.commons.lang.UnhandledException;
import org.instedd.cdx.sync.RsyncSynchronizer;

public class RsyncUploadWatchListener implements PathWatchListener {

	private static final Logger logger = Logger.getLogger(PathWatcher.class.getName());
	private RsyncSynchronizer syncronizer;

	public RsyncUploadWatchListener(RsyncSynchronizer synchronizer) {
		this.syncronizer = synchronizer;
	}

	@Override
	public void fileChanged(Kind<File> kind, File context) {
		logger.info("File change event " + kind + " for file " + context);
	}

	public void pathChanged(java.nio.file.Path path) {
		try {
			syncronizer.downloadDocuments();
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
	}

}
