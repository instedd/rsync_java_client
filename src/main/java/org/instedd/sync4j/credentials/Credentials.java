package org.instedd.sync4j.credentials;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;
import org.instedd.sync4j.util.Processes;
import org.instedd.sync4j.util.Processes.Exit;

public class Credentials {

  static Logger logger = Logger.getLogger(Credentials.class.getName());

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
    return FileUtils.readFileToString(getPrivateKeyFile());
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
        logger.info("Generating a new pair of SSH keys [" + privateKeyFile.getAbsolutePath() + "]");
        // windows ignores the argument if it's the empty string instead of
        // passing an empty argument
        String emptyPassphrase = SystemUtils.IS_OS_WINDOWS ? "\"\"" : "";
        ProcessBuilder command = new ProcessBuilder("ssh-keygen", "-t", "rsa", "-N", emptyPassphrase, "-f", privateKeyFile.getPath());
        Exit exit = Processes.run(command);
        logger.info("Exit value: " + exit.getValue() + " Stdout: " + exit.getStdout() + " Stderr: " + exit.getStderr());
      } catch (Exception e) {
        logger.severe("A problem occurred while generating ssh keys: " + e);
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
