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
		assertEquals("SX", CodonTranslation.simpleTranslate("AGTCAM"));
		assertEquals("S", CodonTranslation.simpleTranslate("AGTCA"));
	}

	@Test
	public void testTranslateNATriplet() {
		assertEquals("S", CodonTranslation.translateNATriplet("AGT"));
		assertEquals("X", CodonTranslation.translateNATriplet("AG"));
		assertEquals("X", CodonTranslation.translateNATriplet("XXX"));
	}

	@Test
	public void testTranslateToTripletAA() {
		assertEquals("Ser", CodonTranslation.translateToTripletAA("S"));
		assertEquals("ArgSer", CodonTranslation.translateToTripletAA("RS"));
		assertEquals("", CodonTranslation.translateToTripletAA(null));
		assertEquals("", CodonTranslation.translateToTripletAA(""));
		assertEquals("ArgSerXxx", CodonTranslation.translateToTripletAA("RSX"));

	}
}
