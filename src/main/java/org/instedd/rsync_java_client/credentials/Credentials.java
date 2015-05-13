package org.instedd.rsync_java_client.credentials;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.instedd.rsync_java_client.util.Processes;
import org.instedd.rsync_java_client.util.Processes.Exit;

public class Credentials {

  static Log log = LogFactory.getLog(Credentials.class);

  private File privateKeyFile;

  public Credentials(File privateKeyFile) {
    Validate.notNull(privateKeyFile);

    this.privateKeyFile = privateKeyFile;
  }

  public String getPrivateKeyPath() {
    return privateKeyFile.getAbsolutePath();
  }

  public File getPrivateKeyFile() {
    return privateKeyFile;
  }

  public File getPublicKeyFile() {
    return publicKeyForPrivate(privateKeyFile);
  }

  public String getPublicKey() throws IOException {
    return FileUtils.readFileToString(getPublicKeyFile());
  }

  public void validate() {
    validateKeyFile(getPrivateKeyFile());
    validateKeyFile(getPublicKeyFile());
  }

  public File getParentDirectory() {
    return privateKeyFile.getParentFile();
  }

  public void ensure() throws IOException, InterruptedException {
    if (getParentDirectory() != null) {
      FileUtils.forceMkdir(getParentDirectory());
    }
    if (!privateKeyFile.exists()) {
      try {
        log.info("Generating a new pair of SSH keys [" + privateKeyFile.getAbsolutePath() + "]");
        // windows ignores the argument if it's the empty string instead of
        // passing an empty argument
        String emptyPassphrase = SystemUtils.IS_OS_WINDOWS ? "\"\"" : "";
        ProcessBuilder command = new ProcessBuilder("ssh-keygen", "-t", "rsa", "-N", emptyPassphrase, "-f", privateKeyFile.getPath());
        Exit exit = Processes.run(command);
        log.info("Exit value: " + exit.getValue() + " Stdout: " + exit.getStdout() + " Stderr: " + exit.getStderr());
      } catch (Exception e) {
        log.error("A problem occurred while generating ssh keys: " + e);
        throw e;
      }
    }
    validate();
  }


  protected static void validateKeyFile(File file) {
    Validate.isTrue(file.exists() && file.isFile(), "Invalid key file");
  }

  public static File publicKeyForPrivate(File privateKeyFile) {
    return new File(privateKeyFile.getAbsolutePath() + ".pub");
  }
}
