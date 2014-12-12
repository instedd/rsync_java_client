package org.instedd.cdx.sync.watcher;

import static org.apache.commons.lang.SystemUtils.USER_HOME;
import static org.apache.commons.lang.SystemUtils.USER_NAME;

import java.nio.file.Paths;

import org.instedd.cdx.sync.RsyncCommandBuilder;
import org.instedd.cdx.sync.RsyncSynchronizer;
import org.instedd.cdx.sync.Settings;
import org.instedd.cdx.sync.watcher.RsyncUploadWatchListener.SyncMode;

public class TestDriver {

  public static void main(String[] args) throws Exception {
		Settings settings = new Settings() {
			{
				remoteHost = "localhost";
				remoteUser = USER_NAME;

				localOutboxDir = USER_HOME + "/tmp/A";
				remoteInboxDir = USER_HOME + "/tmp/B";
			}
		};


		RsyncCommandBuilder commandBuilder = new RsyncCommandBuilder(settings );
		RsyncSynchronizer synchronizer = new RsyncSynchronizer(commandBuilder);
		synchronizer.setUp();

		Runnable asyncWatch = PathWatcher.asyncWatch(Paths.get(settings.localOutboxDir), new RsyncUploadWatchListener(synchronizer, SyncMode.UPLOAD));

		Thread thread = new Thread(asyncWatch);
		thread.start();
		thread.join();


  }

}
