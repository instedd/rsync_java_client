package org.instedd.rsync_java_client;

import java.util.List;

public interface RsyncSynchronizerListener {

  public void transferStarted();
  public void transferFailed(String errorMessage);
  public void transferCompleted(List<String> uploadedFiles, List<String> downloadedFiles);

}
