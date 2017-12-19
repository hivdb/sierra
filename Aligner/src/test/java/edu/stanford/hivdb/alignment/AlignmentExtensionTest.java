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

import org.junit.Test;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.utilities.Sequence;

public class AlignmentExtensionTest {

	@Test
	public void testProcess() {

		Sequence seq = new Sequence(
			"PR76-99RT1-15",
		//	"LeuValGlyProThrProValAsnIleIleGlyArgAsnLeuLeuThrGlnIleGlyCysThr"
		//	"::: ..:::::::::::::::::::::::::::::::::::: ..::::::::::::::::::"
			"TTAATAGGACCTACACCTGTCAACATAATTGGAAGAAATCTGATGACTCAGATTGGCTGCACT" +
		//	"LeuAsnPhe"
		//	":::::::::"
			"TTAAATTTTCCCATTAGTCCTATTGATACTGTACCAGTAAAATTAAAGCCAGGA");

		AlignmentExtension ext1 = new AlignmentExtension(
			seq, Gene.PR,
			/*firstAA*/80,
			/*lastAA*/93,
			/*firstNA*/13,
			/*lastNA*/54,
			"ACACCTGTCAACATAATTGGAAGAAATCTGATGACTCAGATT",
			":::::::::::::::::::::::::::::: ..:::::::::",
			"ThrProValAsnIleIleGlyArgAsnLeuLeuThrGlnIle");
		assertEquals(
			"TTAATAGGACCTACACCTGTCAACATAATTGGAAGAAATCTGATGACTCAGATTGGCTGCACT" +
			"TTAAATTTT", ext1.getAlignedNAs());
		assertEquals(
			"::: ..:::::::::::::::::::::::::::::::::::: ..::::::::::::::::::" +
			":::::::::", ext1.getControlLine());
		assertEquals(
			"LeuValGlyProThrProValAsnIleIleGlyArgAsnLeuLeuThrGlnIleGlyCysThr" +
			"LeuAsnPhe", ext1.getAATripletLine());
		assertEquals(76, ext1.getFirstAA());
		assertEquals(1, ext1.getFirstNA());
		assertEquals(99, ext1.getLastAA());
		assertEquals(72, ext1.getLastNA());

		AlignmentExtension ext2 = new AlignmentExtension(
			seq, Gene.RT,
			/*firstAA*/1,
			/*lastAA*/15,
			/*firstNA*/73,
			/*lastNA*/117,
			"CCCATTAGTCCTATTGATACTGTACCAGTAAAATTAAAGCCAGGA",
			"FAKECONTROL", "FAKEAATRIPLET");
		assertEquals(
			"CCCATTAGTCCTATTGATACTGTACCAGTAAAATTAAAGCCAGGA",
			ext2.getAlignedNAs());
		assertEquals(
			"FAKECONTROL", ext2.getControlLine());
		assertEquals(
			"FAKEAATRIPLET", ext2.getAATripletLine());
		assertEquals(1, ext2.getFirstAA());
		assertEquals(73, ext2.getFirstNA());
		assertEquals(15, ext2.getLastAA());
		assertEquals(117, ext2.getLastNA());
	}

	@Test
	public void testGetUpstream() {

		// case 1: maxExtAA > len(leftRemains) / 3
		assertEquals(
			"Failed to extend firstAA by 2.",
			"BCDEFG",
			AlignmentExtension.getUpstream(
				"ABCDEFGHIJKLMNOPQ", 3, 8));

		// case 2.1:
		// maxExtAA == len(leftRemains) / 3
		// leftRemains % 3 > 0
		assertEquals(
			"Failed to extend firstAA by 2.",
			"BCDEFG",
			AlignmentExtension.getUpstream(
				"ABCDEFGHIJKLMNOPQ", 2, 8));

		// case 2.2:
		// maxExtAA == len(leftRemains) / 3
		// leftRemains % 3 == 0
		assertEquals(
			"Failed to extend firstAA by 2.",
			"ABCDEF",
			AlignmentExtension.getUpstream(
				"ABCDEFGHIJKLMNOPQ", 2, 7));

		// case 3: maxExtAA < len(leftRemains) / 3
		assertEquals(
			"Failed to extend firstAA by 1.",
			"EFG",
			AlignmentExtension.getUpstream(
				"ABCDEFGHIJKLMNOPQ", 1, 8));
	}

	@Test
	public void testGetDownstream() {

		// case 1: maxExtAA > len(rightRemains) / 3
		assertEquals(
			"Failed to extend lastAA by 2.",
			"JKLMNO",
			AlignmentExtension.getDownstream(
				"ABCDEFGHIJKLMNOPQ", 3, 9));

		// case 2.1:
		// maxExtAA == len(rightRemains) / 3
		// rightRemains % 3 > 0
		assertEquals(
			"Failed to extend lastAA by 2.",
			"JKLMNO",
			AlignmentExtension.getDownstream(
				"ABCDEFGHIJKLMNOPQ", 2, 9));

		// case 2.2:
		// maxExtAA == len(rightRemains) / 3
		// rightRemains % 3 == 0
		assertEquals(
			"Failed to extend lastAA by 2.",
			"LMNOPQ",
			AlignmentExtension.getDownstream(
				"ABCDEFGHIJKLMNOPQ", 2, 11));

		// case 3: maxExtAA < len(rightRemains) / 3
		assertEquals(
			"Failed to extend lastAA by 1.",
			"JKL",
			AlignmentExtension.getDownstream(
				"ABCDEFGHIJKLMNOPQ", 1, 9));
	}

	@Test
	public void testRemoveUnusuals() {

		// case 1.1: RT 6-13
		assertEquals(
			"GATACTGTACCAGTAAAATTAAAG",
			AlignmentExtension.removeUnusuals(
				Gene.RT, 13, /* direction */-1,
				"GATACTGTACCAGTAAAATTAAAG"));

		// case 1.2: RT 6-13; the 9 is an unusual one
		assertEquals(
			"GTAAAATTAAAG",
			AlignmentExtension.removeUnusuals(
				Gene.RT, 13, /* direction */-1,
				"GATACTGTAAAAGTAAAATTAAAG"));

		// case 2.1: RT 207-215
		assertEquals(
			"GAACATCTGCTTAAGTGGGGATTTTAT",
			AlignmentExtension.removeUnusuals(
				Gene.RT, 207, /* direction */1,
				"GAACATCTGCTTAAGTGGGGATTTTAT"));

		// case 2.2: RT 207-215, the 210 is an unusual one
		assertEquals(
			"GAACATCTG",
			AlignmentExtension.removeUnusuals(
				Gene.RT, 207, /* direction */1,
				"GAACATCTGCCCAAGTGGGGATTTTAT"));

		// case 3.1: PR 1-5; the 1 is an unusual one
		assertEquals(
			"CAAATCACTCTT",
			AlignmentExtension.removeUnusuals(
				Gene.PR, 5, -1, "CAGCAAATCACTCTT"));

		// case 3.2: PR 1-5; the 5 is an unusual one
		assertEquals(
			"",
			AlignmentExtension.removeUnusuals(
				Gene.PR, 5, -1, "CCTCAAATCACTCAA"));

		// case 4.1: PR 94-99; the 99 is an unusual one
		assertEquals(
			"GGCTGCACTTTAAAT",
			AlignmentExtension.removeUnusuals(
				Gene.PR, 94, 1, "GGCTGCACTTTAAATCCC"));

		// case 4.2: PR 94-99; the 94 is an unusual one
		assertEquals(
			"",
			AlignmentExtension.removeUnusuals(
				Gene.PR, 94, 1, "AACTGCACTTTAAATTTT"));

		// case 4.3: PR 93-99;
		// the 93 is a common one, 97 is an unusual one
		assertEquals(
			"CTTGGCTGCACT",
			AlignmentExtension.removeUnusuals(
				Gene.PR, 93, 1, "CTTGGCTGCACTATAAATTTT"));
	}

}
