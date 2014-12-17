package org.instedd.cdx.sync.watcher;

import static org.apache.commons.lang.SystemUtils.USER_HOME;
import static org.apache.commons.lang.SystemUtils.USER_NAME;

import org.instedd.cdx.sync.Settings;
import org.instedd.cdx.sync.app.RSyncApplication;
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

		RSyncApplication app = new RSyncApplication(settings, "cdx-rsync-app" , "", SyncMode.UPLOAD);
		app.start();

		System.out.println("Now go and create or edit some files on ~/tmp/A");

		app.stop();
  }
}
