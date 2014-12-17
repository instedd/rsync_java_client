package org.instedd.cdx.sync.watcher;

import static org.apache.commons.lang.SystemUtils.USER_HOME;
import static org.apache.commons.lang.SystemUtils.USER_NAME;

import java.util.Scanner;

import org.instedd.cdx.sync.Settings;
import org.instedd.cdx.sync.app.RSyncApplication;
import org.instedd.cdx.sync.watcher.RsyncWatchListener.SyncMode;

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

		RSyncApplication app = new RSyncApplication(settings, "cdx-rsync-app", null, SyncMode.FULL);
		app.start();

		System.out.println("Now go and create or edit some files on ~/tmp/A");
		System.out.println("Type bye to stop app");

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			if (in.nextLine().equals("bye"))
				break;
		}

		app.stop();
	}
}
