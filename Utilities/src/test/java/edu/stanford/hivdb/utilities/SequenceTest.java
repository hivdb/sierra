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
