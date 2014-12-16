package org.instedd.cdx.sync.app;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

import org.instedd.cdx.sync.RsyncCommandBuilder;
import org.instedd.cdx.sync.RsyncSynchronizer;
import org.instedd.cdx.sync.Settings;
import org.instedd.cdx.sync.tray.PopupMenuConfigurer;
import org.instedd.cdx.sync.tray.SystemTrays;
import org.instedd.cdx.sync.watcher.PathWatcher;
import org.instedd.cdx.sync.watcher.RsyncUploadWatchListener;
import org.instedd.cdx.sync.watcher.RsyncUploadWatchListener.SyncMode;

public class RSyncApplication {

	private Settings settings;
	private String tooltip;
	private String imageFilename;

	public RSyncApplication(Settings settings, String tooltip, String imageFilename) {
		this.settings = settings;
		this.tooltip = tooltip;
		this.imageFilename = imageFilename;
	}

	public void start() {
		RsyncCommandBuilder commandBuilder = new RsyncCommandBuilder(settings);
		RsyncSynchronizer synchronizer = new RsyncSynchronizer(commandBuilder);

		Runnable asyncWatch = PathWatcher.asyncWatch(Paths.get(settings.localOutboxDir), new RsyncUploadWatchListener(synchronizer, SyncMode.UPLOAD));
		final Thread thread = new Thread(asyncWatch);

		SystemTrays.open(tooltip, imageFilename, new PopupMenuConfigurer() {
			public void configure(PopupMenu menu) {
				MenuItem menuItem = new MenuItem("Stop Sync");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						thread.interrupt();
					}
				});
				menu.add(menuItem);
			}
		});
		synchronizer.setUp();
		thread.start();
	}

}
