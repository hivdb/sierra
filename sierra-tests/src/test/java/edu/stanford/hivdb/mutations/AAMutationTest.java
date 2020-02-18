package edu.stanford.hivdb.mutations;

import org.junit.Test;


import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.viruses.Gene;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	public void testGetDisplayAAS() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getDisplayAAs(), "ACDEF");
	}

	@Test
	public void testGetDisplayAAChars() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		Set<Character> chars = new TreeSet<>();
		chars.add('A');
		chars.add('C');
		chars.add('D');
		chars.add('E');
		chars.add('F');
		assertEquals(mutation.getDisplayAAChars(), chars);
	}

	@Test
	public void testGetAAs() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getAAs(), "ACDEF");
	}

	@Test
	public void testGetAAChars() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		Set<Character> chars = new TreeSet<>();
		chars.add('A');
		chars.add('C');
		chars.add('D');
		chars.add('E');
		chars.add('F');
		assertEquals(mutation.getAAChars(), chars);
	}

	@Test
	public void testSplit() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		Set<AAMutation<HIV>> mutations = new TreeSet<>();
		mutations.add(new AAMutation<HIV>(gene, 10, 'A'));
		mutations.add(new AAMutation<HIV>(gene, 10, 'C'));
		mutations.add(new AAMutation<HIV>(gene, 10, 'D'));
		mutations.add(new AAMutation<HIV>(gene, 10, 'E'));
		mutations.add(new AAMutation<HIV>(gene, 10, 'F'));
		assertEquals(mutation.split(), mutations);
	}


	@Test
	public void testGetTriplet() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getTriplet(), "");
	}

	@Test
	public void testGetInsertedNAs() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getInsertedNAs(), "");
	}

	@Test
	public void testIsInsertion() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isInsertion());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '_'});

		assertTrue(mutation.isInsertion());
	}

	@Test
	public void testIsDeleteion() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isDeletion());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '-'});

		assertTrue(mutation.isDeletion());
	}

	@Test
	public void testIsIndel() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isIndel());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '-'});

		assertTrue(mutation.isIndel());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '_'});

		assertTrue(mutation.isIndel());
	}

	@Test
	public void testIsMixture() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A'});
		assertFalse(mutation.isMixture());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertTrue(mutation.isMixture());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'X'});
		assertTrue(mutation.isMixture());
	}

	@Test
	public void testHasReference() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation.hasReference());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'L'});
		assertTrue(mutation.hasReference());
	}

	@Test
	public void testHasStop() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation.hasStop());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', '*'});

		assertTrue(mutation.hasStop());
	}

	@Test
	public void testHasBDHVN() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.hasBDHVN());
	}

	@Test
	public void testIsAmbiguous() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isAmbiguous());

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'X'});

		assertTrue(mutation.isAmbiguous());
	}

	@Test
	public void testGetAAsWithRefFirst() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getAAsWithRefFirst(), "ACDEF");

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'L'});

		assertEquals(mutation.getAAsWithRefFirst(), "LACDE");
	}

	@Test
	public void testGetPrimaryType() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getPrimaryType(), hiv.getMutationType("Accessory"));
	}

	@Test
	public void testGetAAWithoutReference() {

		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getAAsWithoutReference(), "ACDEF");

		mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L'});

		assertEquals(mutation.getAAsWithoutReference(), "ACDEF");
	}


	@Test
	public void testEquals() {
		Gene<HIV> prGene = hiv.getGene("HIV1PR");
		Gene<HIV> rtGene = hiv.getGene("HIV1RT");

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		AAMutation<HIV> mutation2 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertTrue(mutation1.equals(mutation2));

		mutation2 = new AAMutation<HIV>(rtGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation1.equals(mutation2));

		mutation2 = new AAMutation<HIV>(prGene, 11,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation1.equals(mutation2));

		mutation2 = new AAMutation<HIV>(prGene, 11,
				new char[] {'A', 'C', 'D', 'E'});

		assertFalse(mutation1.equals(mutation2));
	}

	@Test
	public void testHashCode() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.hashCode(), 1766230873);
	}

	@Test
	public void testToString() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.toString(), "L10ACDEF");
	}

	@Test
	public void testGetShortText() {

		Gene<HIV> gene = hiv.getGene("HIV1PR");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getShortText(), "L10ACDEF");
	}

	@Test
	public void testCompareTo() {
		Gene<HIV> prGene = hiv.getGene("HIV1PR");
		Gene<HIV> rtGene = hiv.getGene("HIV1RT");

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		AAMutation<HIV> mutation2 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation1.compareTo(mutation2), 0);

		mutation2 = new AAMutation<HIV>(rtGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation1.compareTo(mutation2), -1);

		mutation2 = new AAMutation<HIV>(prGene, 11,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation1.compareTo(mutation2), -1);

		mutation2 = new AAMutation<HIV>(prGene, 11,
				new char[] {'A', 'C', 'D', 'E'});

		assertEquals(mutation1.compareTo(mutation2), -1);
	}

	@Test
	public void testContainsSharedA() {
		Gene<HIV> prGene = hiv.getGene("HIV1PR");
		Gene<HIV> rtGene = hiv.getGene("HIV1RT");

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		AAMutation<HIV> mutation2 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertTrue(mutation1.containsSharedAA(mutation2));

		mutation2 = new AAMutation<HIV>(rtGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation1.containsSharedAA(mutation2));

		mutation2 = new AAMutation<HIV>(prGene, 11,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation1.containsSharedAA(mutation2));

		mutation2 = new AAMutation<HIV>(prGene, 11,
				new char[] {'A', 'C', 'D', 'E'});

		assertFalse(mutation1.containsSharedAA(mutation2));
	}

	@Test
	public void testContainsSharedAA() {
		Gene<HIV> prGene = hiv.getGene("HIV1PR");

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		AAMutation<HIV> mutation2 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), true));

		mutation2 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '*'});

		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), true));


		mutation1 = new AAMutation<HIV>(prGene, 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L'});

		mutation2 = new AAMutation<HIV>(prGene, 10,
				new char[] {'L'});

		assertFalse(mutation1.containsSharedAA(mutation2.getAAChars(), true));

		mutation2 = new AAMutation<HIV>(prGene, 10,
				new char[] {'L'});

		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), false));
	}

	@Test
	public void testIsAtDrugResistancePosition() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertTrue(mutation.isAtDrugResistancePosition());
	}

	@Test
	public void testIsDRM() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertTrue(mutation.isDRM());
	}

	@Test
	public void testGetDRMDrugClass() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertEquals(mutation.getDRMDrugClass(), hiv.getDrugClass("NRTI"));

		mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertNull(mutation.getDRMDrugClass());
	}

	@Test
	public void testIsTSM() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertTrue(mutation.isTSM());
	}

	@Test
	public void testGetTSMDrugclass() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertEquals(mutation.getTSMDrugClass(), hiv.getDrugClass("NRTI"));
	}

	@Test
	public void testGetGenePosition() {

		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		GenePosition<HIV> position = new GenePosition<HIV>(gene, 184);

		assertEquals(mutation.getGenePosition(), position);
	}

	@Test
	public void testIsUnusual() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertTrue(mutation.isUnusual());

		mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'X'});

		assertTrue(mutation.isUnusual());
	}

	@Test
	public void testIsSDRM() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertTrue(mutation.isSDRM());

		mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation.isSDRM());
	}

	@Test
	public void testGeSDRMDrugclass() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertEquals(mutation.getSDRMDrugClass(), hiv.getDrugClass("NRTI"));
	}

	@Test
	public void testIsApobecMutation() {
		Mutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
		assertTrue(mut.isApobecMutation());
		Mutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
		assertFalse(mut2.isApobecMutation());
		Mutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
		assertFalse(mut3.isApobecMutation());
	}

	 @Test
	 public void testIsApobecDRM() {
		Mutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
		assertFalse(mut.isApobecDRM());
		Mutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
		assertFalse(mut2.isApobecDRM());
		Mutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
		assertTrue(mut3.isApobecDRM());
		Mutation<HIV> mut4 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'R');
		assertFalse(mut4.isApobecDRM());

	 }

	 @Test
	 public void testGetHighestMutPrevalence() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		 assertEquals(19.8080, mutation.getHighestMutPrevalence(), 0.01);
	 }

	 @Test
	 public void testGetPrevalences() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		 List<MutationPrevalence<HIV>> prevalence = mutation.getPrevalences();
		 
		 assertTrue(prevalence.size() > 0);
	 }
	 
	 @Test
	 public void testGetTypes() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		 assertTrue(mutation.getTypes().size() > 0);
	 }
	 
	 @Test
	 public void testGetASIFormat() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V', '_', '*', '-'});
		 
		 assertEquals(mutation.getASIFormat(), "M184ZdACDEFVi");
	 }

	 @Test
	 public void testHIVDBFormat() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V', '_', '*', '-'});
		 
		 assertEquals(mutation.getHIVDBFormat(), "184*~ACDEFV#");
	 }
	 
	 @Test
	 public void testGetHumanFormat() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V', '_', '*', '-'});
		 
		 assertEquals(mutation.getHumanFormat(), "M184X");
	 }

	 @Test
	 public void testGetShortHumanFormat() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V', '_', '*', '-'});
		 
		 assertEquals(mutation.getShortHumanFormat(), "M184X");
	 }
	 
	 @Test
	 public void testGetHumanFormatWithoutLeadingRef() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'M'});
		 
		 assertEquals(mutation.getHumanFormatWithoutLeadingRef(), "184MACDEF");
	 }
	 
	 @Test
	 public void testGetComments() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'M'});
		 
		 
		 assertTrue(mutation.getComments().size() > 0);
		 
	 }
}