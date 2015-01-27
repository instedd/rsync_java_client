package org.instedd.rsync_java_client;

import java.util.List;

public interface RsyncSynchronizerListener {

  void onFilesTransfered(List<String> transferredFilenames);

}
