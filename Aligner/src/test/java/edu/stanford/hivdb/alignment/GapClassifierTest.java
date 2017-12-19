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

package edu.stanford.hivdb.alignment;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.hivdb.alignment.AlignmentGap;
import edu.stanford.hivdb.alignment.AlignmentGap.AlignmentGapType;
import edu.stanford.hivdb.alignment.GapClassifier;

public class GapClassifierTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testClassifyGaps() {
		String controlLine = "::::::::::::. .::: .. ..---::::::::::::::: ..::: ..:::::::::";
		String alignedNAs = "TTTGCCATAAAAAGAAAAAATGGT    AAATGGAGAAAATTARTAGATCTCAGAGAACTC";
		GapClassifier gapClassifier = new GapClassifier(controlLine, alignedNAs);
		List<AlignmentGap> listAlignmentGaps = gapClassifier.classifyGaps();
		assertEquals(listAlignmentGaps.size(), 1);
		assertEquals(listAlignmentGaps.get(0).getGapType(), AlignmentGapType.DELETION);
		assertEquals(listAlignmentGaps.get(0).getGapPos(), 24);
		assertEquals(listAlignmentGaps.get(0).getGapSize(), 3);
	}

	@Test
	public void testDeletionGapCase1() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 1: gapSize > 3 && gapSize % 3 > 0
		gapClassifier = new GapClassifier(
			":::----:::::", "GGG    CCTTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 3, AlignmentGapType.DELETION));
		expected.add(new AlignmentGap(6, 6, 1, AlignmentGapType.FRAME_SHIFT_DELETION));
		assertTrue(expected.equals(gaps));
	}

	@Test
	public void testDeletionGapCase2() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 2: gapSize > 3 && gapSize % 3 == 0
		gapClassifier = new GapClassifier(
			":::------:::", "GGG      TTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 3, AlignmentGapType.DELETION));
		expected.add(new AlignmentGap(6, 6, 3, AlignmentGapType.DELETION));
		assertTrue(expected.equals(gaps));
	}

	@Test
	public void testDeletionGapCase3() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 3: gapSize == 3
		gapClassifier = new GapClassifier(
			":::---:::", "GGG   TTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 3, AlignmentGapType.DELETION));
		assertTrue(expected.equals(gaps));
	}

	@Test
	public void testDeletionGapCase4() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 4: gapSize < 3
		gapClassifier = new GapClassifier(
			":::--:::", "GGG  TTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 2, AlignmentGapType.FRAME_SHIFT_DELETION));
		assertTrue(expected.equals(gaps));
	}

	@Test
	public void testDeletionGapCase5() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 5: double gaps simple
		gapClassifier = new GapClassifier(
			":::------:::---:::", "GGG      TTT   AAA");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 3, AlignmentGapType.DELETION));
		expected.add(new AlignmentGap(6, 6, 3, AlignmentGapType.DELETION));
		expected.add(new AlignmentGap(12, 12, 3, AlignmentGapType.DELETION));
		assertTrue(expected.equals(gaps));

	}

	// Deprecated: shouldn't happen now
	//@Test
	public void testDeletionGapCase6() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 6: gapSize == 3 but shifted
		gapClassifier = new GapClassifier(
			"::---::::", "GG   TTTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(2, 2, 3, AlignmentGapType.FRAME_SHIFT_DELETION));
		assertTrue(expected.equals(gaps));
	}

	@Test
	public void testDeletionGapCase7() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 7: mixed with insertion
		gapClassifier = new GapClassifier(
			":-::------:::----:-:", "GAGG      TTTAAAAC C");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(1, 1, 1, AlignmentGapType.FRAME_SHIFT_INSERTION));
		expected.add(new AlignmentGap(4, 3, 3, AlignmentGapType.DELETION));
		expected.add(new AlignmentGap(7, 6, 3, AlignmentGapType.DELETION));
		expected.add(new AlignmentGap(13, 12, 3, 1, AlignmentGapType.INSERTION));
		expected.add(new AlignmentGap(16, 12, 1, 2, AlignmentGapType.FRAME_SHIFT_INSERTION));
		expected.add(new AlignmentGap(18, 13, 1, AlignmentGapType.FRAME_SHIFT_DELETION));
		assertTrue(expected.equals(gaps));
	}

	@Test
	public void testInsertionGapCase1() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 1: gapSize > 3 && gapSize % 3 == 0
		gapClassifier = new GapClassifier(
			":::------:::", "GGGAAAAAACCC");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 6, AlignmentGapType.INSERTION));
		assertTrue(expected.equals(gaps));
	}

	@Test
	public void testInsertionGapCase2() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 2: gapSize > 3 && gapSize % 3 > 0
		gapClassifier = new GapClassifier(
			":::-----:::::", "GGGAAAAACCTTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 3, 1, AlignmentGapType.INSERTION));
		expected.add(new AlignmentGap(6, 3, 2, 2, AlignmentGapType.FRAME_SHIFT_INSERTION));
		assertTrue(expected.equals(gaps));

	}

	@Test
	public void testInsertionGapCase3() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 3: gapSize == 3 and at codon position
		gapClassifier = new GapClassifier(
			":::---:::", "GGGAAATTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 3, AlignmentGapType.INSERTION));
		assertTrue(expected.equals(gaps));

	}

	@Test
	public void testInsertionGapCase4() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 4: gapSize < 3
		gapClassifier = new GapClassifier(
			":::-:::", "GGGATTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 1, AlignmentGapType.FRAME_SHIFT_INSERTION));
		assertTrue(expected.equals(gaps));

	}

	@Test
	public void testInsertionGapCase5() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 5: double gaps simple
		gapClassifier = new GapClassifier(
			":::------:::---:::", "GGGAAAAAACCCAAATTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 6, AlignmentGapType.INSERTION));
		expected.add(new AlignmentGap(12, 6, 3, AlignmentGapType.INSERTION));
		assertTrue(expected.equals(gaps));

	}

	// Deprecated: CLapAlign ensure this won't happen
	//@Test
	public void testInsertionGapCase6() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 6: gapSize == 3 but shifted
		gapClassifier = new GapClassifier(
			"::---::::", "GGAAATTTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(2, 2, 3, AlignmentGapType.FRAME_SHIFT_INSERTION));
		assertTrue(expected.equals(gaps));

	}

	@Test
	public void testInsertionGapCase7() {
		GapClassifier gapClassifier;
		List<AlignmentGap> gaps;
        List<AlignmentGap> expected;

		// case 7: multiple gaps
		gapClassifier = new GapClassifier(
			":::-:-:-:---:::---:::", "GGGAAAAAACCCAAATTT");
		gaps = gapClassifier.classifyGaps();
		expected = new ArrayList<AlignmentGap>();
		expected.add(new AlignmentGap(3, 3, 1, AlignmentGapType.FRAME_SHIFT_INSERTION));
		expected.add(new AlignmentGap(5, 4, 1, AlignmentGapType.FRAME_SHIFT_INSERTION));
		expected.add(new AlignmentGap(7, 5, 1, AlignmentGapType.FRAME_SHIFT_INSERTION));
		expected.add(new AlignmentGap(9, 6, 3, AlignmentGapType.INSERTION));
		expected.add(new AlignmentGap(15, 9, 3, AlignmentGapType.INSERTION));
		assertTrue(expected.equals(gaps));
	}
}
