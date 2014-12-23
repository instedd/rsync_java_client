package org.instedd.cdx.sync.watcher;

import static org.apache.commons.lang.SystemUtils.USER_HOME;
import java.util.Scanner;

import org.instedd.cdx.sync.Settings;
import org.instedd.cdx.sync.app.RSyncApplication;
import org.instedd.cdx.sync.watcher.RsyncWatchListener.SyncMode;

public class TestDriver {

	public static void main(String[] args) throws Exception {
		Settings settings = new Settings() {
			{
				remoteHost = "localhost";
				remoteUser = "cdx-sync";
				remotePort = 2222;

				localOutboxDir = USER_HOME + "/tmp/A";
				remoteInboxDir = "sync/"+"2f480eb6-0130-7b72-374c-d689b42ff541"+"/inbox";
			}
		};

		RSyncApplication app = new RSyncApplication(settings, "cdx-rsync-app", null, SyncMode.UPLOAD);
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
