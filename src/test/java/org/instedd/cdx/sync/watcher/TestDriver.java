package org.instedd.cdx.sync.watcher;

import org.instedd.cdx.sync.app.Main;

public class TestDriver {

	public static void main(String[] args) throws Exception {
		Main.main(new String[] { "src/test/resources/cdxsync.properties" });
	}
}
