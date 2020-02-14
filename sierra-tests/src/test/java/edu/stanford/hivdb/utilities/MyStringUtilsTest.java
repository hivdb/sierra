package edu.stanford.hivdb.utilities;


import org.junit.Test;
import static org.junit.Assert.*;


public class MyStringUtilsTest {
	
	@Test
	public void hasSharedCharTest() {
		assertTrue(MyStringUtils.hasSharedChar("AAA", "ABC"));
		assertFalse(MyStringUtils.hasSharedChar("AAA", "BCD"));
	}
	
	@Test
	public void sortAlphabeticallyTest() {
		assertEquals(MyStringUtils.sortAlphabetically("gfedcba"), "abcdefg");
	}
}