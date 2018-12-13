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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class CodonTranslationTest {

	@Test
	public void testConstructor() throws Exception {
		Constructor<CodonTranslation> constructor = CodonTranslation.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testGenerateControlString() {
		assertEquals(
			"  .", CodonTranslation.generateControlString("TTT", "Asn"));

		assertEquals(
			"  .", CodonTranslation.generateControlString("TTTT", "Asn"));

		assertEquals(
			"  .", CodonTranslation.generateControlString("TTTTT", "Asn"));

		assertEquals(
			"  .", CodonTranslation.generateControlString("TTTTTT", "Asn"));

		assertEquals(
			":::", CodonTranslation.generateControlString("ATG", "Met"));

		assertEquals(
			":::  .", CodonTranslation.generateControlString("ATGTTT", "MetAsn"));

		assertEquals(
			"", CodonTranslation.generateControlString("", "MetAsn"));

		assertEquals(
			":::", CodonTranslation.generateControlString("ATG", "MetAsn"));

		assertEquals(
			"", CodonTranslation.generateControlString("ATGTTT", ""));

		assertEquals(
			"", CodonTranslation.generateControlString("", ""));

		assertEquals(
			". .", CodonTranslation.generateControlString("TGA", "Leu"));
	}

	@Test
	public void testSimpleTranslate() {
		assertEquals("", CodonTranslation.simpleTranslate(""));
		assertEquals("S", CodonTranslation.simpleTranslate("AGT"));
		assertEquals("S", CodonTranslation.simpleTranslate("AGTC"));
		assertEquals("S", CodonTranslation.simpleTranslate("AGTCA"));
		assertEquals("X", CodonTranslation.simpleTranslate("SCN"));
		assertEquals("X", CodonTranslation.simpleTranslate("AWK"));
		assertEquals("X", CodonTranslation.simpleTranslate("YWR"));
		assertEquals("SQ", CodonTranslation.simpleTranslate("AGTCAA"));
		assertEquals("SX", CodonTranslation.simpleTranslate("AGTCAM"));
		assertEquals("XS", CodonTranslation.simpleTranslate("CAMAGT"));
		assertEquals("XX", CodonTranslation.simpleTranslate("ABCDEF"));
		assertEquals("HXH", CodonTranslation.simpleTranslate("CATABCCAC"));
	}

	@Test
	public void testSimpleTranslateWithConAA() {
		assertEquals("P", CodonTranslation.simpleTranslate("SCN", 1, "A"));
		assertEquals("A", CodonTranslation.simpleTranslate("SCN", 1, "P"));
	}

	@Test
	public void testTranslateNATriplet() {
		assertEquals("S", CodonTranslation.translateNATriplet("AGT"));
		assertEquals("X", CodonTranslation.translateNATriplet("AG"));
		assertEquals("X", CodonTranslation.translateNATriplet("XXX"));
	}

	@Test
	public void testTranslateToTripletAA() {
		assertEquals("", CodonTranslation.translateToTripletAA(null));
		assertEquals("", CodonTranslation.translateToTripletAA(""));
		assertEquals("Ser", CodonTranslation.translateToTripletAA("S"));
		assertEquals("ArgSer", CodonTranslation.translateToTripletAA("RS"));
		assertEquals("ArgSerXxx", CodonTranslation.translateToTripletAA("RSX"));
	}
}
