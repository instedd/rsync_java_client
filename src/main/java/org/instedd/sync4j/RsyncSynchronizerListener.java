package org.instedd.sync4j;

import java.util.List;

public interface RsyncSynchronizerListener {

  void onFilesTransfered(List<String> transferredFilenames);

}
