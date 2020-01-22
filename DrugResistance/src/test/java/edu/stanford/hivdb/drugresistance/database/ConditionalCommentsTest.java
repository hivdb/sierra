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

package edu.stanford.hivdb.drugresistance.database;

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
import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRFast;
import edu.stanford.hivdb.hivfacts.HIVDrugClass;
import edu.stanford.hivdb.hivfacts.HIVGene;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.hivfacts.HIVAbstractGene;
import edu.stanford.hivdb.hivfacts.HIVAAMutation;

public class ConditionalCommentsTest {

	final private String eName = "name";
	final private String eComment = "comment";
	final private HIVDrugClass eDrugClassPI = HIVDrugClass.PI;
	final private HIVDrugClass eDrugClassNRTI = HIVDrugClass.NRTI;
	final private ConditionType eConTypeMut = ConditionType.MUTATION;
	final private ConditionType eConTypeDL = ConditionType.DRUGLEVEL;
	final private Map<String, Object> eConValue = new HashMap<>();

	@Before
	public void resetEConValue() {
		eConValue.clear();
	}

	@Test
	public void testDefaultConstructor() {
		ConditionalComments conCmts = new ConditionalComments();
		assertEquals(ConditionalComments.class, conCmts.getClass());
	}
	
	// ConditionalComment Tests

	@Test
	public void testConCmtConstructor() {
		final ConditionalComment cmt
			= new ConditionalComment(eName, eDrugClassNRTI, eConTypeDL, eConValue, eComment);
		assertEquals(eName, cmt.getName());
		assertEquals(eComment, cmt.getText());
		assertEquals(eDrugClassNRTI, cmt.getDrugClass());
		assertEquals(eConTypeDL, cmt.getConditionType());
		assertEquals(eName, cmt.getName());
		assertEquals(HIVAbstractGene.RT, cmt.getGene());
	}

	@Test
	public void testGetMutationGene() {
		eConValue.put("gene", "PR");
		final ConditionalComment cmt 
			= new ConditionalComment(eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		assertEquals(HIVAbstractGene.PR, cmt.getMutationGene());
	}

	@Test
	public void testGetMutationGeneOfDrugLevel() {
		final ConditionalComment cmt 
			= new ConditionalComment(eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationGene());
	}

	@Test
	public void testGetMutationPosition() {
		eConValue.put("pos", 20.0);
		final ConditionalComment cmt 
			= new ConditionalComment(eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		assertEquals(Integer.valueOf(20), cmt.getMutationPosition());
	}

	@Test
	public void testGetMutationPositionOfDrugLevel() {
		final ConditionalComment cmt 
			= new ConditionalComment(eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationPosition());
	}

	@Test
	public void testGetMutationAAs() {
		eConValue.put("aas", "N");
		final ConditionalComment cmt 
			= new ConditionalComment(eName, eDrugClassNRTI, eConTypeMut, eConValue, eComment);
		assertEquals("N", cmt.getMutationAAs());
	}

	@Test
	public void testGetMutationAAsOfDrugLevel() {
		final ConditionalComment cmt 
			= new ConditionalComment(eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationAAs());
	}

	@Test
	public void testGetDrugLevelsText() {
		final ConditionalComment cmt = ConditionalComments.getAllComments().get(0);
		final String dlText = cmt.getDrugLevelsText();
		assertEquals("DRV: 5", dlText);
	}

	@Test
	public void testGetDrugLevelsTextWithAnd() {
		final ConditionalComment cmt = ConditionalComments.getAllComments().get(1);
		final String dlText = cmt.getDrugLevelsText();
		assertEquals("DRV: 5; TPV: 4", dlText);
	}

	@Test
	public void testGetDrugLevelsTextFromMutation() {
		final ConditionalComment cmt 
			= new ConditionalComment(eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		final String dlText = cmt.getDrugLevelsText();
		assertEquals("", dlText);
	}

	// BoundComment Tests

	@Test
	public void testBndCmtConstructor() {
		final CommentType eCmtType = CommentType.NRTI;
		final List<String> eHighlightText = Arrays.asList("D67P");
		final HIVAAMutation eMut = ConsensusMutation.parseString("RT67P");
		final BoundComment cmt =
			new BoundComment(eName, eDrugClassNRTI, eCmtType, eComment, eHighlightText, eMut);
		assertEquals(eName, cmt.getName());
		assertEquals(eDrugClassNRTI, cmt.drugClass());
		assertEquals(eCmtType, cmt.getType());
		assertEquals(eComment, cmt.getText());
		assertEquals(eHighlightText, cmt.getHighlightText());
		assertEquals(eMut, cmt.getBoundMutation());
		assertEquals(HIVAbstractGene.RT, cmt.getGene());
	}

	@Test
	public void testGetCommentsofDrugResistance() {
		final MutationSet mutSet = new MutationSet("PR84A");
		final GeneDR dr = new GeneDRFast(HIVGene.valueOf("HIV1PR"), mutSet);
		final List<BoundComment> cmts = ConditionalComments.getComments(dr);
		final String eTextPrefix = "There is evidence for intermediate DRV resistance.";
		final String textPrefix = cmts.get(0).getText();
		assertTrue(textPrefix.startsWith(eTextPrefix));
	}

	@Test
	public void testGetCommentsFromMutOfInsertion() {
		final HIVAAMutation mut = new ConsensusMutation(HIVGene.valueOf("HIV1RT"), 69, "_SS");
		final List<BoundComment> result = ConditionalComments.getComments(mut);
		for (BoundComment cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "_");
		}
	}

	@Test
	public void testGetCommentsFromMutOfDeletion() {
		final HIVAAMutation mut = new ConsensusMutation(HIVGene.valueOf("HIV1RT"), 67, "-");
		final List<BoundComment> result = ConditionalComments.getComments(mut);
		for (BoundComment cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "-");
		}
	}
}
