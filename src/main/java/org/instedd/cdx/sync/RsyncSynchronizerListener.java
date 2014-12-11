package org.instedd.cdx.sync;

import java.util.List;

public interface RsyncSynchronizerListener {

	void onFilesTransfered(List<String> transferredFilenames);

}
