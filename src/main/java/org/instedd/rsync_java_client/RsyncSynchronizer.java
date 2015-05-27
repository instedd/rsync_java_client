package org.instedd.rsync_java_client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.EnumSet;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.instedd.rsync_java_client.SyncMode;
import org.instedd.rsync_java_client.util.Processes;
import org.instedd.rsync_java_client.util.Processes.Exit;

public class RsyncSynchronizer {

  private final Log log = LogFactory.getLog(RsyncSynchronizer.class);

  private RsyncCommandBuilder commandBuilder;
  private EnumSet<SyncMode> mode;
  private Collection<RsyncSynchronizerListener> listeners = new ArrayList<>();

  public RsyncSynchronizer(RsyncCommandBuilder commandBuilder, EnumSet<SyncMode> mode) {
    this.commandBuilder = commandBuilder;
    this.mode = mode;
  }

  public void setUp() {
    makeDirs();
    checkRsyncAvailable();
  }

  public void sync() throws IOException {
    notify(l -> l.transferStarted());

    try {
      List<String> uploadedFiles = new ArrayList<>(), downloadedFiles = new ArrayList<>();

      try {
        if (mode.contains(SyncMode.UPLOAD))
          uploadedFiles.addAll(uploadDocuments());
        if (mode.contains(SyncMode.DOWNLOAD))
          downloadedFiles.addAll(downloadDocuments());
      } catch (InterruptedException e) {
        log.info("Command interrupted");
      }

      notify(l -> l.transferCompleted(uploadedFiles, downloadedFiles));
    } catch (Exception e) {
      notify(l -> l.transferFailed(e.getMessage()));
    }
  }

  public List<String> uploadDocuments() throws InterruptedException, IOException {
    log.info("Will sync files from " + commandBuilder.getLocalOutboxPath() + " to " + commandBuilder.getRemoteInboxPath() + "");
    return sync(commandBuilder.buildUploadCommand());
  }

  public List<String> downloadDocuments() throws InterruptedException, IOException {
    log.info("Will sync files from " + commandBuilder.getRemoteOutboxPath() + " to " + commandBuilder.getLocalInboxPath() + "");
    return sync(commandBuilder.buildDownloadCommand());
  }

  public void addListener(RsyncSynchronizerListener listener) {
    listeners.add(listener);
  }

  protected synchronized List<String> sync(ProcessBuilder command) throws InterruptedException, IOException {
    Exit exit = runCommand(command);
    if (exit.getValue() != 0) {
      throw new IOException(exit.getStderr());
    }
    return parseTransferredFilenames(exit.getStdout());
  }

  private Exit runCommand(ProcessBuilder command) throws IOException, InterruptedException {
    Exit exit = Processes.run(command);
    log.info("Proces exited with value " + exit.getValue());
    log.info("Stderr was " + exit.getStderr());
    log.info("Stdout was " + exit.getStdout());
    return exit;
  }

  protected List<String> parseTransferredFilenames(String outFile) throws FileNotFoundException {
    List<String> transferredFilenames = new ArrayList<>();
    try (Scanner s = new Scanner(outFile)) {
      while (s.hasNextLine()) {
        String line = s.nextLine();
        if (line.startsWith("<") || line.startsWith(">")) {
          transferredFilenames.add(line.split(" ", 2)[1]);
        }
      }
    }
    return transferredFilenames;
  }

  protected void checkRsyncAvailable() {
    try {
      commandBuilder.buildTestCommand().start();
      log.info("Rsync presence test successful");
    } catch (Exception e) {
      log.warn("Could not run test rsync command. Please check that the executable is available");
      throw new IllegalStateException("Could not run test rsync command. Please check that the executable is available.", e);
    }
  }

  protected boolean makeDirs() {
    return new File(commandBuilder.getOutboxLocalDir()).mkdirs();
  }

  private void notify(Consumer<RsyncSynchronizerListener> action) {
    listeners.stream().forEach(action);
  }

}
