package org.instedd.cdx.sync;

import org.junit.Test;

public class SettingsUnitTest {

	@Test(expected = IllegalArgumentException.class)
	public void validatesMissingSettings() {
		new Settings().validate();
	}

}
