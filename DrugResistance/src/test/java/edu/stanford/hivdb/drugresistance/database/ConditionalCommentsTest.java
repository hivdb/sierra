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

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRFast;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments.BoundComment;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments.ConditionType;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments.ConditionalComment;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class ConditionalCommentsTest {

	final private String eName = "name";
	final private String eComment = "comment";
	final private DrugClass eDrugClassPI = DrugClass.PI;
	final private DrugClass eDrugClassNRTI = DrugClass.NRTI;
	final private ConditionType eConTypeMut = ConditionType.MUTATION;
	final private ConditionType eConTypeDL = ConditionType.DRUGLEVEL;
	final private Map<String, Object> eConValue = new HashMap<>();
	
	@Before 
	public void resetEConValue() {
		eConValue.clear();
	}
	
	// ConditionalComment Tests
	
	@Test
	public void testConCmtConstructor() {
		final ConditionalComment cmt = 
			new ConditionalComment(eName, eDrugClassNRTI, eConTypeDL, eConValue, eComment);
		assertEquals(eName, cmt.getName());
		assertEquals(eComment, cmt.getText());
		assertEquals(eDrugClassNRTI, cmt.getDrugClass());
		assertEquals(eConTypeDL, cmt.getConditionType());
		assertEquals(eName, cmt.getName());
		assertEquals(Gene.RT, cmt.getGene());
	}

	@Test
	public void testGetMutationGene() {
		eConValue.put("gene", "PR");
		final ConditionalComment cmt = new ConditionalComment(eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		assertEquals(Gene.PR, cmt.getMutationGene());
	}
	
	@Test
	public void testGetMutationGeneOfDrugLevel() {
		final ConditionalComment cmt = new ConditionalComment(eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationGene());
	}
	
	@Test
	public void testGetMutationPosition() {
		eConValue.put("pos", 20.0);
		final ConditionalComment cmt = new ConditionalComment(eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		assertEquals(Integer.valueOf(20), cmt.getMutationPosition());
	}
	
	@Test
	public void testGetMutationPositionOfDrugLevel() {
		final ConditionalComment cmt = new ConditionalComment(eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
		assertNull(cmt.getMutationPosition());
	}
	
	@Test
	public void testGetMutationAAs() {
		eConValue.put("aas", "N");
		final ConditionalComment cmt = new ConditionalComment(eName, eDrugClassNRTI, eConTypeMut, eConValue, eComment);
		assertEquals("N", cmt.getMutationAAs());
	}
	
	@Test
	public void testGetMutationAAsOfDrugLevel() {
		final ConditionalComment cmt = new ConditionalComment(eName, eDrugClassPI, eConTypeDL, eConValue, eComment);
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
		final ConditionalComment cmt = new ConditionalComment(eName, eDrugClassPI, eConTypeMut, eConValue, eComment);
		final String dlText = cmt.getDrugLevelsText();
		assertEquals("", dlText);
	}

	// BoundComment Tests
	
	@Test
	public void testBndCmtConstructor() {
		final CommentType eCmtType = CommentType.NRTI;
		final List<String> eHighlightText = Arrays.asList("D67P");
		final Mutation eMut = Mutation.parseString("RT67P");
		final BoundComment cmt = 
			new BoundComment(eName, eDrugClassNRTI, eCmtType, eComment, eHighlightText, eMut);
		assertEquals(eName, cmt.getName());
		assertEquals(eDrugClassNRTI, cmt.drugClass());
		assertEquals(eCmtType, cmt.getType());
		assertEquals(eComment, cmt.getText());
		assertEquals(eHighlightText, cmt.getHighlightText());
		assertEquals(eMut, cmt.getBoundMutation());
		assertEquals(Gene.RT, cmt.getGene());
	}
	
	@Test 
	public void testGetCommentsofDrugResistance() {
		final MutationSet mutSet = new MutationSet("RT67P");
		final GeneDR dr = new GeneDRFast(Gene.RT, mutSet);
		final List<BoundComment> cmts = ConditionalComments.getComments(dr);
		final String eTextPrefix = "D67N is a non-polymorphic TAM associated with low-level resistance to AZT and d4T.";
		final String textPrefix = cmts.get(0).getText();
		assertTrue(textPrefix.startsWith(eTextPrefix));
	}
	
	@Test
	public void testGetCommentsFromMutOfInsertion() {
		final Mutation mut = new Mutation(Gene.RT, 69, "_SS");
		final List<BoundComment> result = ConditionalComments.getComments(mut);
		for (BoundComment cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "_");
		}
	}
	
	@Test
	public void testGetCommentsFromMutOfDeletion() {
		final Mutation mut = new Mutation(Gene.RT, 67, "-");
		final List<BoundComment> result = ConditionalComments.getComments(mut);
		for (BoundComment cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "-");
		}
	}
}