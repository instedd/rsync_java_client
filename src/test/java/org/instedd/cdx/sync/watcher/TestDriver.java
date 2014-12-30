package org.instedd.cdx.sync.watcher;

import java.util.Properties;
import java.util.Scanner;

import org.instedd.cdx.sync.Settings;
import org.instedd.cdx.sync.app.RSyncApplication;
import org.instedd.cdx.sync.watcher.RsyncWatchListener.SyncMode;

public class TestDriver {

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(TestDriver.class.getResourceAsStream("/cdxsync.properties"));
		Settings settings = Settings.fromProperties(properties);

		System.out.printf("Settings are %s", settings);

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
