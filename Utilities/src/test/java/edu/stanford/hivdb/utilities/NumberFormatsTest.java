/*
    
    Copyright (C) 2017 Stanford HIVDB team
    
    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.utilities;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;

import edu.stanford.hivdb.utilities.NumberFormats;

public class NumberFormatsTest {

	@Test
	public void testPrettyDecimal() {
		Assert.assertEquals("37", NumberFormats.prettyDecimalAsString(37.0));
		Assert.assertEquals(37.0, NumberFormats.prettyDecimal(37.0), 1e-10);
		Assert.assertEquals("3.8", NumberFormats.prettyDecimalAsString(3.7777));
		Assert.assertEquals(3.8, NumberFormats.prettyDecimal(3.7777), 1e-10);
		Assert.assertEquals("1.1", NumberFormats.prettyDecimalAsString(1.05));
		Assert.assertEquals(1.1, NumberFormats.prettyDecimal(1.05), 1e-10);
		Assert.assertEquals("1.0", NumberFormats.prettyDecimalAsString(1.04));
		Assert.assertEquals(1.0, NumberFormats.prettyDecimal(1.04), 1e-10);
		Assert.assertEquals("0.79", NumberFormats.prettyDecimalAsString(0.79));
		Assert.assertEquals(0.79, NumberFormats.prettyDecimal(0.79), 1e-10);
		Assert.assertEquals("0.003", NumberFormats.prettyDecimalAsString(0.003));
		Assert.assertEquals(0.003, NumberFormats.prettyDecimal(0.003), 1e-10);
		Assert.assertEquals("0.0003", NumberFormats.prettyDecimalAsString(0.0003));
		Assert.assertEquals(0.0003, NumberFormats.prettyDecimal(0.0003), 1e-10);
		Assert.assertEquals("3e-05", NumberFormats.prettyDecimalAsString(0.00003));
		Assert.assertEquals(3e-05, NumberFormats.prettyDecimal(0.00003), 1e-10);

		Assert.assertEquals("-0.06", NumberFormats.prettyDecimalAsString(-0.059));
		Assert.assertEquals(-0.06, NumberFormats.prettyDecimal(-0.059), 1e-10);
		Assert.assertEquals("-0.05", NumberFormats.prettyDecimalAsString(-0.051));
		Assert.assertEquals(-0.05, NumberFormats.prettyDecimal(-0.051), 1e-10);
	}

	@Test
	public void testConstructor() throws NoSuchMethodException, SecurityException {
		Constructor<NumberFormats> c = NumberFormats.class.getDeclaredConstructor();
		Assert.assertFalse(c.isAccessible());
	}
}