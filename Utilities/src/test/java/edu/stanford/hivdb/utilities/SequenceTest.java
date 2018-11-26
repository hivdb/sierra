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

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public class SequenceTest {

	@Test 
	public void testConstructionFromGenBank() {
		Sequence seq = Sequence.fromGenbank("186416");
		assertEquals(Integer.valueOf(1966), seq.getLength());
		assertEquals("M13437.1 Human ovarian beta-B inhibin mRNA", seq.getHeader());
		assertEquals("d0b264921eec89f8ab2a57c11bad292c", seq.getMD5());
		assertEquals("35af5703862dd708a8ac1edc6b740e272e2ea85ee77643e5f0" + 
					 "46e283d5eb2823e44f9772ab8f4d9d356f0e8ffc484d916b43" + 
					 "0dec1db960c48125a528f859f97d", seq.getSHA512());
	}
	
	@Test
	public void testConstructionFromGenBank_null() {
		Sequence seq;
		try {
			seq = Sequence.fromGenbank("//://");
		} catch(RuntimeException e) {
			seq = null;
		}
		assertNull(seq);
	}
	
	@Test
	public void testReverseCompliment() {
		Sequence seq = new Sequence("test", "ACG");
		Sequence eSeq = new Sequence("test", "CGT");
		Sequence revCmpSeq = seq.reverseCompliment();
		assertEquals(eSeq, revCmpSeq);
	}
	
	@Test
	public void testRemoveInvalidChars() {
		Sequence seq = new Sequence("test", "AC-GT..RYMWeSKBDHVNn");
		assertEquals("ACGTRYMWSKBDHVNN", seq.getSequence());
		Set<Character> expecteds = new LinkedHashSet<>();
		expecteds.add('-');
		expecteds.add('.');
		expecteds.add('E');
		assertEquals(expecteds, seq.removedInvalidChars());
	}

	@Test
	public void testToString() {
		Sequence seq = new Sequence("test", "ACTGACTAACTTACTC");
		assertEquals(">test\nACTGACTAACTTACTC", seq.toString());
	}

	@Test
	public void testHashCodeAndEquals() {
		Sequence seq1 = new Sequence("test", "ACTGACTAACTTACTC");
		Sequence seq2 = new Sequence("test", "ACTGACTAACTTACTC-");
		Set<Sequence> seqSet = new LinkedHashSet<>();
		seqSet.add(seq1);
		seqSet.add(seq2);
		assertEquals(seq1, seq1);
		assertEquals(seq1, seq2);
		assertEquals(seqSet.size(), 1);
		assertNotEquals(seq1, ">test\nACTGACTAACTTACTC");
		assertNotEquals(seq1, null);
	}
}