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

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import org.junit.Test;

public class MyStringUtilsTest {

	@Test
	public void testHasSharedChar() {
		assertTrue(
			MyStringUtils.hasSharedChar("ARN", "ARN"));
		assertTrue(
			MyStringUtils.hasSharedChar("AGN", "ARN"));
		assertTrue(
			MyStringUtils.hasSharedChar("AG", "ARNDCE"));
		assertFalse(
			MyStringUtils.hasSharedChar("ARNQGH", "DCE"));
	}

	@Test
	public void testSortAlphabetically() {
		assertEquals(
			MyStringUtils.sortAlphabetically("gfdcba"), "abcdfg");
		assertEquals(
			MyStringUtils.sortAlphabetically("GfdCba"), "CGabdf");
	}

	@Test
	public void testConstructor() throws NoSuchMethodException, SecurityException {
		Constructor<MyStringUtils> c = MyStringUtils.class.getDeclaredConstructor();
		assertFalse(c.isAccessible());
	}
}