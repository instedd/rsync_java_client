package org.instedd.cdx.sync;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RsyncSynchronizerIntegrationTest {

  @Rule
  public TemporaryFolder root = new TemporaryFolder();
  private Settings settings;
  private RsyncSynchronizer synchronizer;

  private File remoteInbox;
  private File remoteOutbox;

  private File localInbox;
  private File localOutbox;

  @Before
  public void setUp() throws IOException {
    localInbox = root.newFolder();
    localOutbox = root.newFolder();

    remoteInbox = root.newFolder();
    remoteOutbox = root.newFolder();

    settings = new Settings() {
      {
        remoteHost = "localhost";
        remoteUser = SystemUtils.USER_NAME;

        localInboxDir = localInbox.getAbsolutePath();
        localOutboxDir = localOutbox.getAbsolutePath();

        remoteOutboxDir = remoteOutbox.getAbsolutePath();
        remoteInboxDir = remoteInbox.getAbsolutePath();
      }
    };

    synchronizer = new RsyncSynchronizer(new RsyncCommandBuilder(settings));
  }

  @Test
  public void canUpload() throws Exception {
    File a = createTestFileInto(localOutbox);
    File remoteCopy = createTestFileInto(remoteInbox);

    a.createNewFile();

    assertTrue(a.exists());
    assertFalse(remoteCopy.exists());

    synchronizer.setUp();
    synchronizer.uploadDocuments();

    assertFalse(a.exists());
    assertTrue(remoteCopy.exists());
  }

  @Test
  public void canDownload() throws Exception {
    File a = createTestFileInto(remoteOutbox);
    File localCopy = createTestFileInto(localInbox);

    a.createNewFile();

    assertTrue(a.exists());
    assertFalse(localCopy.exists());

    synchronizer.setUp();
    synchronizer.downloadDocuments();

    assertFalse(a.exists());
    assertTrue(localCopy.exists());
  }

  @Test
  public void firesListenersOnDownload() throws Exception {
    File a = createTestFileInto(remoteOutbox);
    a.createNewFile();

    AtomicBoolean eventProperlyFired = setupSynchronizerWithListener();
    synchronizer.downloadDocuments();

    assertTrue(eventProperlyFired.get());
  }

  @Test
  public void firesListenersOnUpload() throws Exception {
    File a = createTestFileInto(localOutbox);
    a.createNewFile();

    AtomicBoolean eventProperlyFired = setupSynchronizerWithListener();
    synchronizer.uploadDocuments();

    assertTrue(eventProperlyFired.get());
  }

  private AtomicBoolean setupSynchronizerWithListener() {
    synchronizer.setUp();
    final AtomicBoolean eventProperlyFired = new AtomicBoolean(false);
    synchronizer.addListener(transferredFilenames -> //
        eventProperlyFired.set(transferredFilenames.equals(Arrays.asList("sample"))));
    return eventProperlyFired;
  }

  private File createTestFileInto(File directory) {
    return new File(directory, "sample");
  }
}
