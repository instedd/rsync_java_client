package org.instedd.rsync_java_client.app;

import java.util.Scanner;

public class ConsoleMonitor implements RSyncApplicationMonitor {

  @Override
  public void start(RSyncApplication app) {
    Thread thread = new Thread(() -> {
      System.out.println("Type bye to stop app, or stop it from the system tray");
      Scanner scanner = new Scanner(System.in);
      while (scanner.hasNextLine() && app.isRunning())
        if (scanner.nextLine().equals("bye"))
          break;
      scanner.close();
      app.stop();
    });
    thread.start();
  }

}
