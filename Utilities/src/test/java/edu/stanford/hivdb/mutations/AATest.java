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

package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import org.junit.Test;

public class AATest {

	@Test
	public void testToHIVDBFormat() {
		assertEquals("K", AA.toHIVDBFormat("K"));
		assertEquals("D", AA.toHIVDBFormat("D"));
		assertEquals("I", AA.toHIVDBFormat("I"));
		assertEquals("#", AA.toHIVDBFormat("Insertion"));
		assertEquals("#", AA.toHIVDBFormat("ins"));
		assertEquals("#", AA.toHIVDBFormat("i"));
		assertEquals("#", AA.toHIVDBFormat("_"));
		assertEquals("#", AA.toHIVDBFormat("#"));
		assertEquals("~", AA.toHIVDBFormat("Deletion"));
		assertEquals("~", AA.toHIVDBFormat("del"));
		assertEquals("~", AA.toHIVDBFormat("d"));
		assertEquals("~", AA.toHIVDBFormat("-"));
		assertEquals("~", AA.toHIVDBFormat("~"));
	}

	@Test
	public void testToInternalFormat() {
		assertEquals("K", AA.toInternalFormat("K"));
		assertEquals("D", AA.toInternalFormat("D"));
		assertEquals("I", AA.toInternalFormat("I"));
		assertEquals("_", AA.toInternalFormat("Insertion"));
		assertEquals("_", AA.toInternalFormat("ins"));
		assertEquals("_", AA.toInternalFormat("i"));
		assertEquals("_", AA.toInternalFormat("#"));
		assertEquals("_", AA.toInternalFormat("_"));
		assertEquals("-", AA.toInternalFormat("Deletion"));
		assertEquals("-", AA.toInternalFormat("del"));
		assertEquals("-", AA.toInternalFormat("d"));
		assertEquals("-", AA.toInternalFormat("~"));
		assertEquals("-", AA.toInternalFormat("-"));
	}

	@Test
	public void testToASIFormat() {
		assertEquals("K", AA.toASIFormat("K"));
		assertEquals("D", AA.toASIFormat("D"));
		assertEquals("I", AA.toASIFormat("I"));
		assertEquals("i", AA.toASIFormat("Insertion"));
		assertEquals("i", AA.toASIFormat("ins"));
		assertEquals("i", AA.toASIFormat("_"));
		assertEquals("i", AA.toASIFormat("#"));
		assertEquals("i", AA.toASIFormat("i"));
		assertEquals("d", AA.toASIFormat("Deletion"));
		assertEquals("d", AA.toASIFormat("del"));
		assertEquals("d", AA.toASIFormat("-"));
		assertEquals("d", AA.toASIFormat("~"));
		assertEquals("d", AA.toASIFormat("d"));
	}

}
