package org.instedd.cdx.sync;


public interface DocumentSynchronizer {

	public void syncDocuments();
	
	public void queueForSync(String documentName, String content);
}
