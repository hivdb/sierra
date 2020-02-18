package edu.stanford.hivdb.mutations;

import org.junit.Test;


import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.viruses.Gene;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AAMutationTest {

	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testNormalizeAAChars() {
		Set<Character> chars = new HashSet<>();
		chars.add('#');
		chars.add('i');
		chars.add('~');
		chars.add('d');
		chars.add('Z');
		chars.add('.');
		chars.add('A');
		chars.add('C');
		
		
		Set<Character> chars2 = new HashSet<>();
		chars2.add('A');
		chars2.add('C');
		chars2.add('_');
		chars2.add('-');
		chars2.add('*');
		
		assertEquals(AAMutation.normalizeAAChars(chars), chars2);
	}

	@Test
	public void testConstructor() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		Set<Character> chars = new HashSet<>();
		chars.add('A');
		chars.add('C');
		chars.add('_');
		chars.add('-');
		chars.add('*');
		
		assertNotNull(new AAMutation<HIV>(gene, 10, chars, AAMutation.DEFAULT_MAX_DISPLAY_AAS));
		assertNotNull(new AAMutation<HIV>(gene, 10, chars));
		
		assertNotNull(new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C'}, AAMutation.DEFAULT_MAX_DISPLAY_AAS));
		
		assertNotNull(new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C'}));
		
		assertNotNull(new AAMutation<HIV>(gene, 10, 'A'));
	}
	
	@Test
	public void testGetMaxDisplayAAs() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 'A');
		
		assertEquals(mutation.getMaxDisplayAAs(), AAMutation.DEFAULT_MAX_DISPLAY_AAS);
		
	}
	
	@Test
	public void testGetMainAAPcnts() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 'A');
		assertNotNull(mutation.getMainAAPcnts());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testMergesWith() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(gene, 10, 'A');
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(gene, 10, 'C');
		
		assertEquals(mutation1.mergesWith(mutation2).getAAs(), "AC");
		
	}
	
	@Test
	public void testMergeWith2() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 'A');
		List<Character> chars = new ArrayList<>();
		chars.add('C');
		assertEquals(mutation.mergesWith(chars).getAAs(), "AC");
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSubtractsBy() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D'});
		
		assertEquals(mutation1.subtractsBy(mutation2).getAAs(), "EF");
		
	}
	
	@Test
	public void testSubtractsBy2() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		List<Character> chars = new ArrayList<>();
		chars.add('A');
		chars.add('C');
		chars.add('D');
		
		assertEquals(mutation.subtractsBy(chars).getAAs(), "EF");
		
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIntersectsWith() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(gene, 10,
				new char[] {'C', 'D'});
		
		assertEquals(mutation1.intersectsWith(mutation2).getAAs(), "CD");
		
	}
	
	@Test
	public void testIntersectsWith2() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		List<Character> chars = new ArrayList<>();
		chars.add('C');
		chars.add('D');
		
		assertEquals(mutation.intersectsWith(chars).getAAs(), "CD");
		
	}
	
	@Test
	public void testIsUnsequenced() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isUnsequenced());
	}
	
	@Test
	public void testGetStrain() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertSame(gene.getStrain(), mutation.getStrain());
	}
	
	@Test
	public void testGetGene() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		
		assertSame(gene, mutation.getGene());
		
	}
	
	@Test
	public void testGetAbstractGene() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		
		assertSame(gene.getAbstractGene(), mutation.getAbstractGene());
		
	}
	
	@Test
	public void testGetReference() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getReference(), "L");
	}
	
	@Test
	public void testGetRefChar() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertSame(mutation.getRefChar(), 'L');
	}
	
	@Test
	public void testGetPosition() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10, 
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertSame(mutation.getPosition(), 10);
	}
	
	@Test
	public void testGetDisplayAAS
	
	
//	@Test
//	public void testIsApobecMutation() {
//		Mutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
//		assertTrue(mut.isApobecMutation());
//		Mutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
//		assertFalse(mut2.isApobecMutation());
//		Mutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
//		assertFalse(mut3.isApobecMutation());
//	}
//
//	 @Test
//	 public void testIsApobecDRM() {
//		Mutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
//		assertFalse(mut.isApobecDRM());
//		Mutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
//		assertFalse(mut2.isApobecDRM());
//		Mutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
//		assertTrue(mut3.isApobecDRM());
//		Mutation<HIV> mut4 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'R');
//		assertFalse(mut4.isApobecDRM());
//
//	 }

}