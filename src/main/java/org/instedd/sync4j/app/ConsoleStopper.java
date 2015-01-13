package org.instedd.sync4j.app;

import java.util.Scanner;

public class ConsoleStopper implements RSyncApplicationStopper {

  @Override
  public void start(RSyncApplication app) {
    Thread thread = new Thread(() -> {
      System.out.println("Type bye to stop app, or stop it from the system tray");
      Scanner scanner = new Scanner(System.in);
      while (scanner.hasNextLine() && app.isRunning())
        if (scanner.nextLine() == "bye")
          break;
      app.stop();
    });
    thread.setDaemon(true);
    thread.run();
  }

}
