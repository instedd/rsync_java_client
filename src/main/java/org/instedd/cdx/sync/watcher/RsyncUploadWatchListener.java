package org.instedd.cdx.sync.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.logging.Logger;

import org.apache.commons.lang.UnhandledException;
import org.instedd.cdx.sync.RsyncSynchronizer;

public class RsyncUploadWatchListener implements PathWatchListener {

	private static final Logger logger = Logger.getLogger(PathWatcher.class.getName());
	private RsyncSynchronizer synchronizer;
	private SyncMode mode;

	public RsyncUploadWatchListener(RsyncSynchronizer synchronizer, SyncMode mode) {
		this.synchronizer = synchronizer;
		this.mode = mode;
	}

	@Override
	public void onSinglePathChange(Kind<Path> kind, Path context) {
		logger.info("File change event " + kind + " for file " + context);
	}

	public void onGlobalPathChange(java.nio.file.Path path) {
		try {
			mode.doSync(synchronizer);
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
	}

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
