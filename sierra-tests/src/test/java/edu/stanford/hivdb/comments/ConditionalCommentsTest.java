package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.comments.ConditionalComments;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.mutations.AAMutation;

import org.fstrf.stanfordAsiInterpreter.resistance.definition.CommentAction;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.CommentDefinition;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.DrugLevelCondition;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.LevelDefinition;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.ResultCommentRule;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedDrugLevelCondition;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedResultCommentRule;


public class ConditionalCommentsTest {
	
	private final static HIV hiv = HIV.getInstance();
	private final static HIV2 hiv2 = HIV2.getInstance();
	private final static ConditionalComments<HIV> comments = hiv.getConditionalComments();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testGetInstance() {
		assertNotNull(comments);
	}
	
	@Test
	public void testGetComments() {
		
		Mutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, 'V');
		
		assertEquals(comments.getComments(mutation).size(), 1);
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 211, 'T');
		
		assertEquals(comments.getComments(mutation).size(), 0);
	}
	
	@Test
	public void testFromAsiMutationCommets() {		
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
				"RT184ACDEFGHKLNPQRSTWY_-",
				"184V/I cause high-level in vitro resistance to 3TC and FTC and low-level resistance to ddI and ABC.");
		defs.add(commentDef);
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184VA");
		
		assertEquals(comments.fromAsiMutationComments(defs, mutations).size(), 2);
		
	}
	
	@Test
	public void testFromAsiMutationCommentsDifferentGenePosition() {
		
		ConditionalComments<HIV> comments = hiv.getConditionalComments();
		
		// Mutation Type conditional comment
		CommentDefinition commentD = new CommentDefinition("RT184VI", "M184V/I cause high-level in vitro resistance to 3TC and FT");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentD);
		
		
		// Mutation RT181V & Comment RT184V
		MutationSet<HIV> muts = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT181V");
		
		assertEquals(comments.fromAsiMutationComments(defs, muts).size(), 0);
	}
	
	@Test
	public void testFromAsiMutationCommetsWithException() {
		List<CommentDefinition> defs = new ArrayList<>();
		CommentDefinition commentDef = new CommentDefinition(
				"DRVHigh",
				"There is evidence for high-level DRV resistance. If DRV is administered it should be used twice daily.");
		defs.add(commentDef);
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Invalid comment name: DRVHigh");
		comments.fromAsiMutationComments(defs, mutations);
	}
	
	@Test
	public void testFromAsiMutationCommetsWithException2() {
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
				"RT184ACDEFGHKLNPQRSTWY_-",
				"184V/I cause high-level in vitro resistance to 3TC and FTC and low-level resistance to ddI and ABC.");
		defs.add(commentDef);
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Mutation M184V is not match with comment definition RT184ACDEFGHKLNPQRSTWY_-.");
		comments.fromAsiMutationComments(defs, mutations);
	}
	
	@Test
	public void testFromAsiMutationCommentsDifferentGenePositionForHIV2() {
		
		ConditionalComments<HIV2> comments = hiv2.getConditionalComments();
		
		// HIV2B RT201A comment
		CommentDefinition commentDef = new CommentDefinition("HIV2BRT210ACDEFGHIKLMPQRSTVWY_",
				"210W is an HIV-1 associated TAM.");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentDef);
		
		// HIV2A RT210A mutation
		MutationSet<HIV2> muts = MutationSet.parseString(hiv2.getGene("HIV2ART"), "RT210A");
		
		assertEquals(comments.fromAsiMutationComments(defs, muts).size(), 0);
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
	public void testFromAsiDrugLevelComments() {
		
		CommentDefinition commentDef = new CommentDefinition(
				"DRVHigh",
				"There is evidence for high-level DRV resistance. If DRV is administered it should be used twice daily.");

		List<EvaluatedResultCommentRule> resultComments = getResultComments("ABC", commentDef, true);
		assertEquals(comments.fromAsiDrugLevelComments(resultComments).size(), 1);
		
		resultComments = getResultComments("ABC", commentDef, false);
		assertEquals(comments.fromAsiDrugLevelComments(resultComments).size(), 0);
	}
}