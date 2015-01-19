package org.instedd.sync4j.credentials;

import static org.junit.Assert.*;

import java.io.File;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CredentialsTest {

  @Rule
  public TemporaryFolder root = new TemporaryFolder();

  @Test
  public void initializesCredentialsWhenFileExists() throws Exception {
    File keyFile = root.newFile("id_rsa");
    root.newFile("id_rsa.pub");

    Credentials credentials = new Credentials(keyFile);
    credentials.ensure();

    assertEquals(credentials.getPrivateKeyFile().getAbsolutePath(), keyFile.getAbsolutePath());
    assertThat(credentials.getPublicKeyFile().getAbsolutePath(), CoreMatchers.containsString(".pub"));
  }

  @Test
  public void initializesCredentialsWhenFileDoesNotExists() throws Exception {
    File keyFile = new File(root.getRoot(), "id_rsa");

    Credentials credentials = new Credentials(keyFile);
    credentials.ensure();

    assertEquals(credentials.getPrivateKeyFile().getAbsolutePath(), keyFile.getAbsolutePath());
    assertThat(credentials.getPublicKeyFile().getAbsolutePath(), CoreMatchers.containsString(".pub"));
    assertTrue(keyFile.exists());
  }
}
