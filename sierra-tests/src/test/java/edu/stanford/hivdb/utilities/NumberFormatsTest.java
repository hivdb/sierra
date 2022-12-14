package edu.stanford.hivdb.utilities;


import org.junit.Test;
import static org.junit.Assert.*;

public class NumberFormatsTest {
	
	@Test
	public void prettyDecimalAsStringTest() {
		assertEquals(NumberFormats.prettyDecimalAsString(11.11), "11");
		assertEquals(NumberFormats.prettyDecimalAsString(5.11), "5.1");
		assertEquals(NumberFormats.prettyDecimalAsString(0.123), "0.12");
		assertEquals(NumberFormats.prettyDecimalAsString(0.01234), "0.012");
		assertEquals(NumberFormats.prettyDecimalAsString(0.0000123), "1e-05");
	}
	
	@Test
	public void prettyDecimalTest() {
		assertEquals(NumberFormats.prettyDecimal(11.11), Double.valueOf(11.0));
		assertEquals(NumberFormats.prettyDecimal(5.11), Double.valueOf(5.1));
		assertEquals(NumberFormats.prettyDecimal(0.123), Double.valueOf(0.12));
		assertEquals(NumberFormats.prettyDecimal(0.01234), Double.valueOf(0.012));
		assertEquals(NumberFormats.prettyDecimal(0.0000123), Double.valueOf(1e-05));
		
	}
}