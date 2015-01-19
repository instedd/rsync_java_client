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

  private File privateKey;
  private File publicKey;

  public Credentials(File privateKey, File publicKey) {
    Validate.notNull(privateKey);
    Validate.notNull(publicKey);
    validateKeyFile(privateKey);
    validateKeyFile(publicKey);

    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }


  public String getPrivateKeyPath() {
    return privateKey.getAbsolutePath();
  }

  public File getPrivateKey() {
    return privateKey;
  }

  public File getPublicKey() {
    return publicKey;
  }

  protected static void validateKeyFile(File file) {
    Validate.isTrue(file.exists() && file.isFile(), "Invalid key file");
  }

  /**
   * Initializes a new pair of SSH keys if necessary.
   *
   * @throws IOException
   */
  public static Credentials initialize(String remoteKey) throws IOException, InterruptedException {
    File privateKey = new File(remoteKey);
    FileUtils.forceMkdir(privateKey.getParentFile());

    if (!privateKey.exists()) {
      try {
        logger.info("Generating a new pair of SSH keys [" + privateKey.getAbsolutePath() + "]");
        // windows ignores the argument if it's the empty string instead of
        // passing an empty argument
        String emptyPassphrase = SystemUtils.IS_OS_WINDOWS ? "\"\"" : "";
        ProcessBuilder command = new ProcessBuilder("ssh-keygen", "-t", "rsa", "-N", emptyPassphrase, "-f", privateKey.getPath());
        Exit exit = Processes.run(command);
        logger.info("Exit value: " + exit.getValue() + " Stdout: " + exit.getStdout() + " Stderr: " + exit.getStderr());
      } catch (Exception e) {
        logger.severe("A problem occurred while generating ssh keys: " + e);
        throw e;
      }
    }

    return new Credentials(privateKey, new File(remoteKey + ".pub"));
  }

  public static String publicKeyForPrivate(String privateKeyPath) {
    return privateKeyPath + ".pub";
  }
}
