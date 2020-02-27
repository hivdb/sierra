package edu.stanford.hivdb.hivfacts;

import org.junit.Test;

public class HIVDataLoaderTest {

	@Test(expected=ExceptionInInitializerError.class)
	public void testLoadResourceError() {
		HIVDataLoader.loadResource("hiv12345.json");
	}


}
