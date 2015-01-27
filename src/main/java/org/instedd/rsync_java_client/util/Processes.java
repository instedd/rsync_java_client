package org.instedd.rsync_java_client.util;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class Processes {

  public static class Exit {
    private final int value;
    private final String out;
    private final String err;

    public Exit(int value, String out, String err) {
      this.value = value;
      this.out = out;
      this.err = err;
    }

    public String getStderr() {
      return err;
    }

    public String getStdout() {
      return out;
    }

    public int getValue() {
      return value;
    }
  }

  public static Exit run(ProcessBuilder command) throws IOException, InterruptedException {
    Process process = command.start();
    process.waitFor();
    return new Exit(process.exitValue(), //
        IOUtils.toString(process.getInputStream()), //
        IOUtils.toString(process.getErrorStream()));
  }
}
