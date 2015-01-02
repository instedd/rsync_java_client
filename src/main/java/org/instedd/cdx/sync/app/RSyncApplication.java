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
import org.instedd.cdx.sync.watcher.RsyncWatchListener;
import org.instedd.cdx.sync.watcher.RsyncWatchListener.SyncMode;

public class RSyncApplication {

	private final Settings settings;
	private final String tooltip;
	private final String imageFilename;
	private final SyncMode syncMode;

	private transient Thread thread;

	//TODO extract parameter object
	public RSyncApplication(Settings settings, String tooltip, String imageFilename, SyncMode syncMode) {
		this.settings = settings;
		this.tooltip = tooltip;
		this.imageFilename = imageFilename;
		this.syncMode = syncMode;
	}

	public void start() {
		RsyncSynchronizer synchronizer = newSynchronizer();
		//TODO log sync mode
		Runnable asyncWatch = PathWatcher.asyncWatch(Paths.get(settings.localOutboxDir), new RsyncWatchListener(synchronizer, syncMode));
		thread = new Thread(asyncWatch, "watcher-thread");

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

	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

	public void stop() throws InterruptedException {
		if (thread != null) {
			thread.interrupt();
			thread.join();
		}
	}

	protected RsyncSynchronizer newSynchronizer() {
		RsyncCommandBuilder commandBuilder = new RsyncCommandBuilder(settings);
		RsyncSynchronizer synchronizer = new RsyncSynchronizer(commandBuilder);
		return synchronizer;
	}


}
