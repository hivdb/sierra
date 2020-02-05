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

package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.hivdb.comments.BoundComment;
import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.comments.ConditionType;
import edu.stanford.hivdb.comments.ConditionalComment;
import edu.stanford.hivdb.comments.ConditionalComments;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AAMutation;

public class ConditionalCommentsTest {
	
	private static final HIV hiv = HIV.getInstance();

	final private String eName = "name";
	final private String eComment = "comment";
	final private DrugClass<HIV> eDrugClassPI = hiv.getDrugClass("PI");
	final private DrugClass<HIV> eDrugClassNRTI = hiv.getDrugClass("NRTI");
	final private ConditionType eConTypeMut = ConditionType.MUTATION;
	final private ConditionType eConTypeDL = ConditionType.DRUGLEVEL;
	final private Map<String, Object> eConValue = new HashMap<>();

	@Before
	public void resetEConValue() {
		eConValue.clear();
	}

	@Test
	public void testDefaultConstructor() {
		ConditionalComments<HIV> conCmts = hiv.getConditionalComments();
		assertEquals(ConditionalComments.class, conCmts.getClass());
	}
	
	// ConditionalComment Tests

	@Test
	public void testConCmtConstructor() {
		final ConditionalComment<HIV> cmt
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassNRTI, eConTypeDL, eConValue, eComment);
		assertEquals(eName, cmt.getName());
		assertEquals(eComment, cmt.getText());
		assertEquals(eDrugClassNRTI, cmt.getDrugClass());
		assertEquals(eConTypeDL, cmt.getConditionType());
		assertEquals(eName, cmt.getName());
		assertEquals(hiv.getGene("HIV1RT"), cmt.getGene());
	}

	@Test
	public void testGetMutationGene() {
		eConValue.put("gene", "PR");
		final ConditionalComment<HIV> cmt 
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		assertEquals(hiv.getGene("HIV1PR"), cmt.getMutationGene());
	}

	@Test
	public void testGetMutationGeneOfDrugLevel() {
		final ConditionalComment<HIV> cmt 
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationGene());
	}

	@Test
	public void testGetMutationPosition() {
		eConValue.put("pos", 20.0);
		final ConditionalComment<HIV> cmt 
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		assertEquals(Integer.valueOf(20), cmt.getMutationPosition());
	}

	@Test
	public void testGetMutationPositionOfDrugLevel() {
		final ConditionalComment<HIV> cmt 
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationPosition());
	}

	@Test
	public void testGetMutationAAs() {
		eConValue.put("aas", "N");
		final ConditionalComment<HIV> cmt 
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassNRTI, eConTypeMut, eConValue, eComment);
		assertEquals("N", cmt.getMutationAAs());
	}

	@Test
	public void testGetMutationAAsOfDrugLevel() {
		final ConditionalComment<HIV> cmt 
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationAAs());
	}

//	@Test
//	public void testGetDrugLevelsText() {
//		hiv.getConditionalComments()
//		final ConditionalComment<HIV> cmt = ConditionalComments.getAllComments().get(0);
//		final String dlText = cmt.getDrugLevelsText();
//		assertEquals("DRV: 5", dlText);
//	}
//
//	@Test
//	public void testGetDrugLevelsTextWithAnd() {
//		hiv.com
//		final ConditionalComment<HIV> cmt = ConditionalComments.getAllComments().get(1);
//		final String dlText = cmt.getDrugLevelsText();
//		assertEquals("DRV: 5; TPV: 4", dlText);
//	}

	@Test
	public void testGetDrugLevelsTextFromMutation() {
		final ConditionalComment<HIV> cmt 
			= new ConditionalComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		final String dlText = cmt.getDrugLevelsText();
		assertEquals("", dlText);
	}

	// BoundComment Tests

	@Test
	public void testBndCmtConstructor() {
		final CommentType eCmtType = CommentType.NRTI;
		final List<String> eHighlightText = Arrays.asList("D67P");
		final AAMutation<HIV> eMut = new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "P");
		final BoundComment<HIV> cmt =
			new BoundComment<HIV>(hiv.getStrain("HIV1"), eName, eDrugClassNRTI, eCmtType, eComment, eHighlightText, eMut);
		assertEquals(eName, cmt.getName());
		assertEquals(eDrugClassNRTI, cmt.drugClass());
		assertEquals(eCmtType, cmt.getType());
		assertEquals(eComment, cmt.getText());
		assertEquals(eHighlightText, cmt.getHighlightText());
		assertEquals(eMut, cmt.getBoundMutation());
		assertEquals(hiv.getGene("HIV1RT"), cmt.getGene());
	}

	@Test
	public void testGetCommentsFromMutOfInsertion() {
		final AAMutation<HIV> mut = new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 69, "_SS");
		final List<BoundComment<HIV>> result = hiv.getConditionalComments().getComments(mut);
		for (BoundComment<HIV> cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "_");
		}
	}

	@Test
	public void testGetCommentsFromMutOfDeletion() {
		final AAMutation<HIV> mut = new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "-");
		final List<BoundComment<HIV>> result = hiv.getConditionalComments().getComments(mut);
		for (BoundComment<HIV> cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "-");
		}
	}
}
