package edu.stanford.hivdb.drugresistance.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.fstrf.stanfordAsiInterpreter.resistance.definition.CommentAction;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.CommentDefinition;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.DrugLevelCondition;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.LevelDefinition;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.ResultCommentRule;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedDrugLevelCondition;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedResultCommentRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.comments.BoundComment;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.mutations.MutationSet;

public class ASIResultHandlerTest {
	
	private final static HIV hiv = HIV.getInstance();
	private final static HIV2 hiv2 = HIV2.getInstance();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testExtractMutationCommets() {		
		// Mutation Type conditional comment
		List<CommentDefinition> defs = new ArrayList<>();
		CommentDefinition commentDef = new CommentDefinition(
				"RT184VI",
				"184V/I cause high-level in vitro resistance to 3TC and FTC and low-level resistance to ddI and ABC.");
		defs.add(commentDef);
		commentDef = new CommentDefinition(
				"RT181IV",
				"Y181I/V are 2-base pair non-polymorphic mutations selected by NVP and ETR.");
		defs.add(commentDef);
		commentDef = new CommentDefinition(
				"RT184ACDEFGHKLNPQRSTWYid",
				"184V/I cause high-level in vitro resistance to 3TC and FTC and low-level resistance to ddI and ABC.");
		defs.add(commentDef);
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184VA");
		
		assertEquals(ASIResultHandler.extractMutationComments(hiv, defs, mutations).size(), 2);
		
	}
	
	@Test
	public void testExtractMutationCommentsDifferentGenePosition() {
		
		// Mutation Type conditional comment
		CommentDefinition commentD = new CommentDefinition("RT184VI", "M184V/I cause high-level in vitro resistance to 3TC and FT");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentD);
		
		
		// Mutation RT181V & Comment RT184V
		MutationSet<HIV> muts = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT181V");
		
		assertEquals(ASIResultHandler.extractMutationComments(hiv, defs, muts).size(), 0);
	}
	
	@Test
	public void testExtractMutationCommentsWithException() {
		List<CommentDefinition> defs = new ArrayList<>();
		CommentDefinition commentDef = new CommentDefinition(
				"DRVHigh",
				"There is evidence for high-level DRV resistance. If DRV is administered it should be used twice daily.");
		defs.add(commentDef);
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Invalid comment name: DRVHigh");
		ASIResultHandler.extractMutationComments(hiv, defs, mutations);
	}
	
	@Test
	public void testExtractMutationCommentsForRangeDels() {
		List<CommentDefinition> defs = new ArrayList<>();
		CommentDefinition commentDef = new CommentDefinition(
			"RT67-70d",
			"Amino acid deletions between codons 67 and 70 are rare and usually occur in combination with multiple TAMs, K65R, or the Q151M mutation complex. Deletions at position 67 are more often associated with multiple TAMs. Deletions at positions 69 and 70 are more often associated with K65R or the Q151M mutation complex. Deletions at codon 68 are extremely rare and less well characterized.");
		defs.add(commentDef);
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT68d");
		
		List<BoundComment<HIV>> cmts = ASIResultHandler.extractMutationComments(hiv, defs, mutations);
		assertEquals(1, cmts.size());
	}
	
	@Test
	public void testExtractMutationCommentsDifferentGenePositionForHIV2() {
		
		// HIV2B RT201A comment
		CommentDefinition commentDef = new CommentDefinition("HIV2ART210ACDEFGHIKLMPQRSTVWY_-",
				"210W is an HIV-1 associated TAM.");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentDef);
		
		// HIV2B RT210A mutation
		MutationSet<HIV2> muts = MutationSet.parseString(hiv2.getGene("HIV2BRT"), "RT210A");
		
		List<BoundComment<HIV2>> cmts = ASIResultHandler.extractMutationComments(hiv2, defs, muts);
		assertEquals(1, cmts.size());
	}
	
	private List<EvaluatedResultCommentRule> getResultComments(
			String drugName, CommentDefinition commmentDefinition, boolean evaluationResult) {
		
		List<DrugLevelCondition> conditions = new ArrayList<>();
		DrugLevelCondition condition = new DrugLevelCondition(drugName);
		conditions.add(condition);
		
		List<CommentAction> actions = new ArrayList<>();
		
		CommentAction action = new CommentAction(commmentDefinition);
		actions.add(action);
		ResultCommentRule commentRule = new ResultCommentRule(conditions, actions);
		
		LevelDefinition level = new LevelDefinition(1, "Resistance", "I");

		List<EvaluatedDrugLevelCondition> evalConditions = new ArrayList<>();
		EvaluatedDrugLevelCondition evalDrugLevelCondition = new EvaluatedDrugLevelCondition(
				condition, true, level, drugName);
		evalConditions.add(evalDrugLevelCondition);
		
		
		List<EvaluatedResultCommentRule> resultComments = new ArrayList<>();	
		EvaluatedResultCommentRule evalCommentRule = new EvaluatedResultCommentRule(
				commentRule, evaluationResult, evalConditions);
		evalCommentRule.addDefinition(commmentDefinition);
		resultComments.add(evalCommentRule);		
		
		return resultComments;
	}
	
	@Test
	public void testExtractDrugLevelComments() {
		
		CommentDefinition commentDef = new CommentDefinition(
				"DRVHigh",
				"There is evidence for high-level DRV resistance. If DRV is administered it should be used twice daily.");

		List<EvaluatedResultCommentRule> resultComments = getResultComments("ABC", commentDef, true);
		assertEquals(1, ASIResultHandler.extractDrugLevelComments(hiv, resultComments).size());
		
		resultComments = getResultComments("ABC", commentDef, false);
		assertEquals(0, ASIResultHandler.extractDrugLevelComments(hiv, resultComments).size());
	}
}
