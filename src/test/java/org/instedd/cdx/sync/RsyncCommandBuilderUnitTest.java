package org.instedd.cdx.sync;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

public class RsyncCommandBuilderUnitTest {

	private Settings settings;
	private RsyncCommandBuilder builder;

	@Before
	public void setUp() throws IOException {
		settings = new Settings() {
			{
				remoteHost = "localhost";
				remotePort = 22;
				remoteUser = "user";
				remoteKey = "todo";
			}
		};

		settings.inboxLocalDir = "sampleIns";
		settings.outboxLocalDir = "sampleOut";

		builder = new RsyncCommandBuilder(settings);
	}

	@Test
	public void canBuildDownload() {
		assertCommandLike("rsync -iaz --remove-source-files -e ssh -p 22 -l user -i \"todo\"  -oBatchMode=yes localhost:/outbox/",
		    builder.buildDownloadCommand());
	}

	@Test
	public void canBuildTest() throws Exception {
		assertCommandLike("rsync --help", builder.buildTestCommand());
	}

	@Test
	public void canBuildUpload() throws Exception {
		assertCommandLike("rsync -iaz --remove-source-files -e ssh -p 22 -l user -i \"todo\"  -oBatchMode=yes", builder.buildUploadCommand());
	}

	private void assertCommandLike(String string, ProcessBuilder buildUploadCommand) {
		assertThat(StringUtils.join(buildUploadCommand.command(), " "), CoreMatchers.containsString(string));
	}
}
