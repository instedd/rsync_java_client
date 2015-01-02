package org.instedd.cdx.sync.app;

import static org.instedd.cdx.sync.util.Exceptions.interruptable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import org.instedd.cdx.sync.Settings;
import org.instedd.cdx.sync.watcher.RsyncWatchListener.SyncMode;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length != 1) {
      System.out.println("Usage: cdxsync <properties filename>");
      System.exit(1);
    }
    String propertiesFilename = args[0];
    Properties properties = properties(propertiesFilename);

    Settings settings = Settings.fromProperties(properties);
    System.out.printf("Settings are %s\n", settings);

    String appName = properties.getProperty("app.name");
    String appIcon = properties.getProperty("app.icon");
    SyncMode appMode = SyncMode.valueOf(properties.getProperty("app.mode").toUpperCase());

    final RSyncApplication app = new RSyncApplication(settings, appName, appIcon, appMode);
    stopOnExit(app);
    app.start();

    System.out.printf("Now go and create or edit some files on %s\n", settings.localOutboxDir);
    loop(app);

  }

  protected static void stopOnExit(final RSyncApplication app) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        interruptable(app::stop);
      } finally {
        System.out.println("bye!");
      }
    }));
  }

  protected static void loop(RSyncApplication app) {
    System.out.println("Type bye to stop app, or stop it from the system tray");
    @SuppressWarnings("resource")
    Scanner in = new Scanner(System.in);
    while (in.hasNextLine() && app.isRunning()) {
      if (in.nextLine().equals("bye"))
        break;
    }
    System.exit(0);
  }

  protected static Properties properties(String propertiesFilename) throws IOException {
    try (InputStream fileIs = new FileInputStream(propertiesFilename)) {
      Properties properties = new Properties();
      properties.load(fileIs);
      return properties;
    }
  }

}
