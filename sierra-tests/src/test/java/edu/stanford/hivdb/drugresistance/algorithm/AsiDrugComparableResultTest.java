package edu.stanford.hivdb.drugresistance.algorithm;

import static org.junit.Assert.*;

import org.junit.Test;

public class AsiDrugComparableResultTest {
	
	@Test
	public void testConstructor() {
		AsiDrugComparableResult result = new AsiDrugComparableResult(
				"R",
				"Interpretation",
				"Explanation"
				);
		assertNotNull(result);
	}
	
	@Test
	public void testGetInterpretation() {
		AsiDrugComparableResult result = new AsiDrugComparableResult(
				"R",
				"Interpretation",
				"Explanation"
				);
		
		assertEquals(result.getInterpretation(), "Interpretation");
	}
	
	@Test
	public void testGetExplanation() {
		AsiDrugComparableResult result = new AsiDrugComparableResult(
				"R",
				"Interpretation",
				"Explanation"
				);
		
		assertEquals(result.getExplanation(), "Explanation");
	}
	
	@Test
	public void testEquals() {
		AsiDrugComparableResult result = new AsiDrugComparableResult(
				"R",
				"Interpretation",
				"Explanation"
				);
		
		assertTrue(result.equals(result));
		
		assertFalse(result.equals(null));
		assertFalse(result.equals(Integer.valueOf(1)));
		
		AsiDrugComparableResult resultOther = new AsiDrugComparableResult(
				"I", "Interpretation", "Explanation");
		assertFalse(result.equals(resultOther));
		
		resultOther = new AsiDrugComparableResult(
				"R", "InterpretationOther", "Explanation");
		assertFalse(result.equals(resultOther));
		
		resultOther = new AsiDrugComparableResult(
				"R", "Interpretation", "ExplanationOther");
		assertFalse(result.equals(resultOther));

	}
	
	@Test
	public void testHashCode() {
		AsiDrugComparableResult result = new AsiDrugComparableResult(
				"R", "Interpretation", "Explanation");
		
		AsiDrugComparableResult resultOther = new AsiDrugComparableResult(
				"I", "Interpretation", "Explanation");
		
		assertFalse(result.hashCode() == resultOther.hashCode());
	}
	
	@Test
	public void testToString() {
		AsiDrugComparableResult result = new AsiDrugComparableResult(
				"R", "Interpretation", "Explanation");
		
		assertEquals(result.toString(), "AsiDrugComparableResult(\"R\", \"Interpretation\", \"Explanation\")");
	}
	
}