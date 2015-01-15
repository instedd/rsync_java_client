package org.instedd.sync4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.instedd.sync4j.util.Processes;
import org.instedd.sync4j.util.Processes.Exit;

public class RsyncSynchronizer {

  private final Logger logger = Logger.getLogger(RsyncSynchronizer.class.getName());

  private RsyncCommandBuilder commandBuilder;
  private Collection<RsyncSynchronizerListener> listeners = new ArrayList<>();

  public RsyncSynchronizer(RsyncCommandBuilder commandBuilder) {
    this.commandBuilder = commandBuilder;
  }

  public void setUp() {
    makeDirs();
    checkRsyncAvailable();
  }

  public void uploadDocuments() throws IOException {
    logger.info("Will sync files from " + commandBuilder.getLocalOutboxPath() + " to " + commandBuilder.getRemoteInboxPath() + "");
    this.sync(commandBuilder.buildUploadCommand());
  }

  public void downloadDocuments() throws IOException {
    logger.info("Will sync files from " + commandBuilder.getRemoteOutboxPath() + " to " + commandBuilder.getLocalInboxPath() + "");
    this.sync(commandBuilder.buildDownloadCommand());
  }

  public void addListener(RsyncSynchronizerListener listener) {
    listeners.add(listener);
  }

  protected synchronized void sync(ProcessBuilder command) throws IOException {
    Exit exit;
    try {
      exit = runCommand(command);
    } catch (InterruptedException e) {
      logger.info("Command interrupted");
      return;
    }

    List<String> transferredFilenames = parseTransferredFilenames(exit.getStdout());
    if (!transferredFilenames.isEmpty()) {
      fireFilesTransfered(transferredFilenames);
    }
  }

  private Exit runCommand(ProcessBuilder command) throws IOException, InterruptedException {
    Exit exit = Processes.run(command);
    logger.info("Proces exited with value " + exit.getValue());
    logger.info("Stderr was " + exit.getStderr());
    logger.info("Stdout was " + exit.getStdout());
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
      logger.info("Rsync presence test successful");
    } catch (Exception e) {
      logger.warning("Could not run test rsync command. Please check that the executable is available");
      throw new IllegalStateException("Could not run test rsync command. Please check that the executable is available.", e);
    }
  }

  protected boolean makeDirs() {
    return new File(commandBuilder.getOutboxLocalDir()).mkdirs();
  }

  protected void fireFilesTransfered(List<String> transferredFilenames) {
    for (RsyncSynchronizerListener listener : listeners)
      listener.onFilesTransfered(transferredFilenames);
  }

}
