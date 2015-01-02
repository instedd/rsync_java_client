package org.instedd.cdx.sync.util;

import java.util.concurrent.Callable;

import org.apache.commons.lang.UnhandledException;

public class Exceptions {

  public interface CheckedRunnable {
    void run() throws Exception;
  }

  public interface InterruptableRunnable {
    void run() throws InterruptedException;
  }

  public static <A> A unchecked(Callable<A> c) {
    try {
      return c.call();
    } catch (Exception e) {
      throw new UnhandledException(e);
    }
  }

  public static void unchecked(CheckedRunnable c) {
    unchecked(() -> {
      c.run();
      return null;
    });
  }

  public static void interruptable(InterruptableRunnable c) {
    try {
      c.run();
    } catch (InterruptedException e) {
    }
  }

}
