package org.instedd.cdx.sync.watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.instedd.cdx.sync.RsyncSynchronizer;

public class RsyncWatchListener implements PathWatchListener {

	private static final Logger logger = Logger.getLogger(PathWatcher.class.getName());
	private RsyncSynchronizer synchronizer;
	private SyncMode mode;

	public RsyncWatchListener(RsyncSynchronizer synchronizer, SyncMode mode) {
		this.synchronizer = synchronizer;
		this.mode = mode;
	}

	@Override
	public void onSinglePathChange(Kind<Path> kind, Path context) {
		logger.info("File change event " + kind + " for file " + context);
	}

	public void onGlobalPathChange(Path path) {
		try {
			//TODO exception handling should be done by PathWatch component, in order
			//to avoid boilerplate here and avoid crashing in case programmer forgets try-catches
			mode.doSync(synchronizer);
		} catch (Exception e) {
			logger.warning("Exception thrown " + ExceptionUtils.getStackTrace(e));
		}
	}
//TODO move to general sync code
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
