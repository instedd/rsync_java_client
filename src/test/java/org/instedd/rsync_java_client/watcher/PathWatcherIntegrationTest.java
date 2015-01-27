package org.instedd.rsync_java_client.watcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.instedd.rsync_java_client.watcher.PathWatchListener;
import org.instedd.rsync_java_client.watcher.PathWatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PathWatcherIntegrationTest {

  @Rule
  public TemporaryFolder root = new TemporaryFolder();
  private AtomicInteger singleChangeEventsCount = new AtomicInteger(0);
  private AtomicInteger globalChangeEventsCount = new AtomicInteger(0);
  private File rootDir;
  private PathWatcher watch;

  private Object ready = new Object();

  @Before
  public void setup() {
    rootDir = root.getRoot();
    watch = createWatcher(rootDir);
  }

  @Test(timeout = 60000)
  public void notifiesListenerWhenFileSystemChanges() throws Exception {
    Thread thread = new Thread(watch::watch);
    thread.start();

    synchronized (ready) {
      ready.wait();
    }
    thread.interrupt();
    thread.join();

    assertTrue(globalChangeEventsCount.get() >= 1);
    assertEquals(3, singleChangeEventsCount.get());
  }

  private void touch(String name) throws IOException {
    FileUtils.touch(new File(rootDir, name));
  }

  private PathWatcher createWatcher(File rootDir) {
    return new PathWatcher(rootDir.toPath(), new PathWatchListener() {
      public void onSinglePathChange(Kind<Path> kind, Path path) {
        if (singleChangeEventsCount.incrementAndGet() >= 3) {
          synchronized (ready) {
            ready.notify();
          }
        }
      }

      public void onGlobalPathChange(Path path) {
        globalChangeEventsCount.incrementAndGet();
      }

      public void onWatchStarted() throws IOException {
          touch("foo");
          touch("bar");
          touch("baz");
      }
    });
  }

}
