package org.instedd.cdx.sync;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RsyncSynchronizerIntegrationTest {

	@Rule
	public TemporaryFolder root = new TemporaryFolder();
	private Settings settings;
	private RsyncSynchronizer synchronizer;

	@Before
	public void setUp() throws IOException {
		File inbox = root.newFolder();
		File anInboxFile = new File(inbox, "foo");

		File outbox = root.newFolder();
		settings = new Settings() {
			{
				remoteHost = "localhost";
				remotePort = 22;
				remoteUser = "user";
				remoteKey = "todo";
			}
		};

		settings.inboxLocalDir = inbox.getAbsolutePath();
		settings.outboxLocalDir = outbox.getAbsolutePath();

		synchronizer = new RsyncSynchronizer(new RsyncCommandBuilder(settings));
	}

	@Test
	public void canUpload() throws Exception {
		synchronizer.setUp();
		synchronizer.uploadDocuments();

		fail("unimplemented");
	}

	@Test
	public void canDownload() throws Exception {
		synchronizer.setUp();
		synchronizer.downloadDocuments();

		fail("unimplemented");
	}

	@Test
	public void firesListeners() throws Exception {
		fail("unimplemented");
	}

}
