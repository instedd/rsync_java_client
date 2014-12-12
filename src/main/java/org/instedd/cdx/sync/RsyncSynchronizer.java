package org.instedd.cdx.sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

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
		logger.info("Will sync files from " + commandBuilder.getInboxRemoteRoute() + " to " + commandBuilder.getInboxLocalRoute() + "");
		this.sync(commandBuilder.buildUploadCommand());
	}

	public void downloadDocuments() throws IOException {
		logger.info("Will sync files from " + commandBuilder.getOutboxLocalRoute() + " to " + commandBuilder.getOutboxRemoteRoute() + "");
		this.sync(commandBuilder.buildDownloadCommand());
	}

	protected synchronized void sync(ProcessBuilder command) throws IOException {
		File errFile = null, outFile = null;
		try {
			errFile = File.createTempFile("sync", "err");
			outFile = File.createTempFile("sync", "out");

			runCommand(command, errFile, outFile);

			List<String> transferredFilenames = parseTransferredFilenames(outFile);
			if (!transferredFilenames.isEmpty()) {
				fireFilesTransfered(transferredFilenames);
			}
		} finally {
			FileUtils.deleteQuietly(errFile);
			FileUtils.deleteQuietly(outFile);
		}
	}

	private void runCommand(ProcessBuilder command, File errFile, File outFile) throws IOException {
	  Process process = command.redirectError(errFile).redirectOutput(outFile).start();
	  try {
	    process.waitFor();
    } catch (InterruptedException e) {
    	logger.info("Command aborted");
    } 
	  // TODO do in background
	  logger.info("Proces exited with value " + process.exitValue());
	  logger.info("Stderr was " + FileUtils.readFileToString(errFile));
  }

	protected List<String> parseTransferredFilenames(File outFile) throws FileNotFoundException {
		List<String> transferredFilenames = new ArrayList<>();
		try (Scanner s = new Scanner(outFile)) {
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if (line.startsWith("<")) {
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
