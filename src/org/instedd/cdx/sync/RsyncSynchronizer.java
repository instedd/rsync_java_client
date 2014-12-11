package org.instedd.cdx.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.ivy.ant.IvyAntSettings.Credentials;

public class RsyncSynchronizer {

	private final Logger logger = Logger.getLogger(RsyncSynchronizer.class
			.getName());

	private DataStore dataStore;
	private RsyncCommandBuilder commandBuilder;
	private Collection<RsyncSynchronizerListener> listeners = new ArrayList<>();

	public RsyncSynchronizer(DataStore dataStore,
			RsyncCommandBuilder commandBuilder) {
		this.dataStore = dataStore;
		this.commandBuilder = commandBuilder;
	}

	public void setUp() {
		new File(commandBuilder.getOutboxLocalDir()).mkdirs();

		checkRsyncAvailable();

		logger.info("Will sync files from ${commandBuilder.outboxLocalRoute()} to ${commandBuilder.outboxRemoteRoute()}");
		logger.info("Will sync files from ${commandBuilder.inboxRemoteRoute()} to ${commandBuilder.inboxLocalRoute()}");
	}

	public synchronized void sync(ProcessBuilder command) {
		logger.debug("Running rsync: {}", command.toString());

		File errFile = File.createTempFile("sync", "err");
		File outFile = File.createTempFile("sync", "out");

		Process process = command.redirectError(errFile)
				.redirectOutput(outFile).start();
		process.waitFor(); // TODO do in background

		// String stdout = stdoutBuffer.toString();
		// if (StringUtils.isEmpty(stderr)) {
		// logger.trace("Standard output for rsync was:\n{}", stdout);
		// } else {
		// logger.warn("Standard output for rsync was:\n{}", stdout);
		// logger.warn("Error output for rsync was:\n{}", stderr);
		// }

		List<String> transferredFilenames = new ArrayList<>();
		Scanner s = new Scanner(outFile);
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.startsWith("<")) {
				transferredFilenames.add(line.split(" ", 2)[1]);
			}
		}
		if (!transferredFilenames.isEmpty()) {
			fireFilesTransfered(transferredFilenames);
		}
	}

	public void uploadDocuments() {
		this.sync(commandBuilder.buildUploadCommand());
	}

	void checkRsyncAvailable() {
		try {
			commandBuilder.buildTestCommand().start();
			logger.info("Rsync presence test successful");
		} catch (Exception e) {
			logger.warn(
					"Could not run test rsync command. Please check that the executable is available.",
					e);
			throw new IllegalStateException(
					"Could not run test rsync command. Please check that the executable is available.",
					e);
		}
	}

	private void fireFilesTransfered(List<String> transferredFilenames) {
		for (RsyncSynchronizerListener listener : listeners)
			listener.onFilesTransfered(transferredFilenames);
	}

}
