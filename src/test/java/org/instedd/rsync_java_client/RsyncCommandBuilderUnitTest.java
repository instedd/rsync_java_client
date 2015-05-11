package org.instedd.rsync_java_client;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.instedd.rsync_java_client.RsyncCommandBuilder;
import org.instedd.rsync_java_client.Settings;
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
        knownHostsFilePath = "";
      }
    };

    settings.localInboxDir = "sampleIn";
    settings.localOutboxDir = "sampleOut";

    builder = new RsyncCommandBuilder(settings);
  }

  @Test
  public void canBuildDownload() {
    assertCommandLike("rsync -irltz --chmod=ug=rwX,o= --remove-source-files -e ssh -p 22 -l user -i \"todo\"  -oBatchMode=yes localhost:/outbox/ sampleIn/",
        builder.buildDownloadCommand());
  }

  @Test
  public void canBuildTest() throws Exception {
    assertCommandLike("rsync --help", builder.buildTestCommand());
  }

  @Test
  public void canBuildUpload() throws Exception {
    assertCommandLike("rsync -irltz --chmod=ug=rwX,o= --remove-source-files -e ssh -p 22 -l user -i \"todo\"  -oBatchMode=yes sampleOut/ localhost:/inbox",
        builder.buildUploadCommand());
  }

  private void assertCommandLike(String string, ProcessBuilder command) {
    assertEquals(string, StringUtils.join(command.command(), " "));
  }
}
