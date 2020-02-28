package edu.stanford.hivdb.mutations;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.viruses.Gene;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AAMutationTest {

	private final static HIV hiv = HIV.getInstance();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testNormalizeAAChars() {
		assertNull(AAMutation.normalizeAAChars(null));
		
		Set<Character> chars = new HashSet<>();
		Set<Character> charsExpected = new HashSet<>();
		
		chars.add('#');
		chars.add('i');
		charsExpected.add('_');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
		
		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('#');
		charsExpected.add('_');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
		
		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('i');
		charsExpected.add('_');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
		
		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('~');
		chars.add('d');
		charsExpected.add('-');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
		
		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('~');
		charsExpected.add('-');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
		
		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('d');
		charsExpected.add('-');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
		
		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('Z');
		chars.add('.');
		charsExpected.add('*');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
	
		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('.');
		charsExpected.add('*');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);

		chars = new HashSet<>();
		charsExpected = new HashSet<>();
		chars.add('Z');
		charsExpected.add('*');
		assertEquals(AAMutation.normalizeAAChars(chars), charsExpected);
	}
	
	@Test
	public void testAAMutation() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'H');
		assertNotNull(mutation);
	}
	
	@Test
	public void testAAMutation2() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, new char[] {'H'});
		assertNotNull(mutation);
	}
	
	@Test
	public void testAAMutation3() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, new char[] {'H'}, 6);
		assertNotNull(mutation);
	}

	@Test
	public void testAAMutation4() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		int position = 1;
		Set<Character> aaChars = new HashSet<>();
		aaChars.add('G');
		aaChars.add('H');
		aaChars.add('_');
		aaChars.add('-');
		aaChars.add('*');
		
		assertNotNull(new AAMutation<HIV>(gene, position, aaChars));
	}
	
	@Test
	public void testAAMutation5() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		int position = 1;
		Set<Character> aaChars = new HashSet<>();
		aaChars.add('G');
		aaChars.add('H');
		aaChars.add('_');
		aaChars.add('-');
		aaChars.add('*');
		assertNotNull(new AAMutation<HIV>(gene, position, aaChars, 6));

	}
	
	@Test
	public void testAAMutaionWithException() {
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		int position = 100;
		Set<Character> aaChars = new HashSet<>();
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Position is out of bounds for HIV1PR: 100 (1-99).");
		new AAMutation<HIV>(gene, position, aaChars, 6);
	}

	@Test
	public void testGetMaxDisplayAAs() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'H');

		assertEquals(mutation.getMaxDisplayAAs(), AAMutation.DEFAULT_MAX_DISPLAY_AAS);

	}

	@Test
	public void testGetMainAAPcnts() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'H');
		assertNotNull(mutation.getMainAAPcnts());
		
		// INFO: mainAAcnts is cached.
		assertNotNull(mutation.getMainAAPcnts());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testMergesWithDeprecated() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'C');

		assertEquals(mutation1.mergesWith(mutation2).getAAs(), "AC");

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testMergesWithDeprecatedWithException() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
		Mutation<HIV> another = null;
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 10 (HIV1PR)");
		mutation.mergesWith(another);

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testMergesWithDeprecatedWithException2() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'H');
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 1, 'H');
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 1 (HIV1PR)");
		mutation1.mergesWith(mutation2);

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testMergesWithDeprecatedWithException3() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'H');
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 2, 'R');
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 1 (HIV1PR)");
		mutation1.mergesWith(mutation2);

	}

	@Test
	public void testMergeWith() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'A');
		List<Character> chars = new ArrayList<>();
		chars.add('C');
		assertEquals(mutation.mergesWith(chars).getAAs(), "AC");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSubtractsByDeprecated() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D'});

		assertEquals(mutation1.subtractsBy(mutation2).getAAs(), "EF");

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSubtractsByDeprecatedWithException() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = null;

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 10 (HIV1PR)");
		mutation1.subtractsBy(mutation2);

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSubtractsByDeprecatedWithException2() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 10 (HIV1PR)");
		mutation1.subtractsBy(mutation2);

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSubtractsByDeprecatedWithException3() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 11,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 10 (HIV1PR)");
		mutation1.subtractsBy(mutation2);

	}
	
	

	@Test
	public void testSubtractsBy() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		List<Character> chars = new ArrayList<>();
		chars.add('A');
		chars.add('C');
		chars.add('D');

		assertEquals(mutation.subtractsBy(chars).getAAs(), "EF");
		
		chars = new ArrayList<>();
		chars.add('A');
		chars.add('C');
		chars.add('D');
		chars.add('E');
		chars.add('F');
		assertNull(mutation.subtractsBy(chars));

	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIntersectsWithDeprecated() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'C', 'D'});

		
		assertEquals(mutation1.intersectsWith(mutation2).getAAs(), "CD");

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIntersectsWithDeprecatedWithException() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = null;

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 10 (HIV1PR)");
		mutation1.intersectsWith(mutation2);

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIntersectsWithDeprecatedWithException2() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10,
				new char[] {'C', 'D'});

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 10 (HIV1PR)");
		mutation1.intersectsWith(mutation2);

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIntersectsWithDeprecatedWithException3() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 11,
				new char[] {'C', 'D'});

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The other mutation must be at this position: 10 (HIV1PR)");
		mutation1.intersectsWith(mutation2);

	}

	@Test
	public void testIntersectsWith() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		List<Character> chars = new ArrayList<>();
		chars.add('C');
		chars.add('D');

		assertEquals(mutation.intersectsWith(chars).getAAs(), "CD");
		
		chars = new ArrayList<>();
		chars.add('H');
		assertNull(mutation.intersectsWith(chars));

	}

	@Test
	public void testIsUnsequenced() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isUnsequenced());
	}

	@Test
	public void testGetStrain() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertSame(hiv.getStrain("HIV1"), mutation.getStrain());
	}

	@Test
	public void testGetGene() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertSame(hiv.getGene("HIV1PR"), mutation.getGene());

	}

	@Test
	public void testGetAbstractGene() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertSame(hiv.getGene("HIV1PR").getAbstractGene(), mutation.getAbstractGene());

	}

	@Test
	public void testGetReference() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getReference(), "L");
	}

	@Test
	public void testGetRefChar() {

		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertSame(mutation.getRefChar(), 'L');
		
		// INFO: ref is cached.
		assertSame(mutation.getRefChar(), 'L');
	}

	@Test
	public void testGetPosition() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertSame(mutation.getPosition(), 10);
	}

	@Test
	public void testGetDisplayAAS() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getDisplayAAs(), "ACDEF");
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'G', 'H'});
		
		assertEquals(mutation.getDisplayAAs(), "X");
	}

	@Test
	public void testGetDisplayAAChars() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		Character aaChars[] = {'A', 'C', 'D', 'E', 'F'};
		Set<Character> chars = new HashSet<Character>(Arrays.asList(aaChars));
		assertEquals(mutation.getDisplayAAChars(), chars);
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'G', 'H'});
		chars = new HashSet<Character>(Arrays.asList(new Character[] {'X'}));
		
		
		assertEquals(mutation.getDisplayAAChars(), chars);
	}

	@Test
	public void testGetAAs() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getAAs(), "ACDEF");
	}

	@Test
	public void testGetAAChars() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});	
		
		Character aaChars[] = {'A', 'C', 'D', 'E', 'F'};
		Set<Character> chars = new HashSet<Character>(Arrays.asList(aaChars));
		
		assertEquals(mutation.getAAChars(), chars);
	}

	@Test
	public void testSplit() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L'});
		Set<AAMutation<HIV>> mutations = new TreeSet<>();
		mutations.add(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A'));
		mutations.add(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'C'));
		mutations.add(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'D'));
		mutations.add(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'E'));
		mutations.add(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'F'));
		
		assertEquals(mutation.split(), mutations);
	}


	@Test
	public void testGetTriplet() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getTriplet(), "");
	}

	@Test
	public void testGetInsertedNAs() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation.getInsertedNAs(), "");
	}

	@Test
	public void testIsInsertion() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isInsertion());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '_'});

		assertTrue(mutation.isInsertion());
	}

	@Test
	public void testIsDeleteion() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isDeletion());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '-'});

		assertTrue(mutation.isDeletion());
	}

	@Test
	public void testIsIndel() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isIndel());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '-'});

		assertTrue(mutation.isIndel());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', '_'});

		assertTrue(mutation.isIndel());
	}

	@Test
	public void testIsMixture() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A'});
		assertFalse(mutation.isMixture());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertTrue(mutation.isMixture());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'X'});
		assertTrue(mutation.isMixture());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'X'});
		assertTrue(mutation.isMixture());
	}

	@Test
	public void testHasReference() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation.hasReference());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'L'});
		assertTrue(mutation.hasReference());
	}

	@Test
	public void testHasStop() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertFalse(mutation.hasStop());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', '*'});

		assertTrue(mutation.hasStop());
	}

	@Test
	public void testHasBDHVN() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.hasBDHVN());
	}

	@Test
	public void testIsAmbiguous() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isAmbiguous());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'G', 'H'});

		assertTrue(mutation.isAmbiguous());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'X'});

		assertTrue(mutation.isAmbiguous());
		
		// WARNING: because hasBDHVN is always false, one branch can't be reached.
	}

	@Test
	public void testGetAAsWithRefFirst() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getAAsWithRefFirst(), "ACDEF");

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'L'});

		assertEquals(mutation.getAAsWithRefFirst(), "LACDE");
	}

	@Test
	public void testGetPrimaryType() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getPrimaryType(), hiv.getMutationType("Accessory"));
	}

	@Test
	public void testGetAAWithoutReference() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getAAsWithoutReference(), "ACDEF");

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L'});

		assertEquals(mutation.getAAsWithoutReference(), "ACDEF");
	}


	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertTrue(mutation1.equals(mutation1));

		AAMutation<HIV> mutation2 = null;
		assertFalse(mutation1.equals(mutation2));

		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation1.equals(mutation2));
		
		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 11,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation1.equals(mutation2));
		
		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E'});
		assertFalse(mutation1.equals(mutation2));
		
		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E'});
		assertFalse(mutation1.equals(mutation2));
		
		assertFalse(mutation1.equals("A"));
	}

	@Test
	public void testHashCode() {
		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E'});

		assertEquals(mutation1.hashCode(), mutation1.hashCode());
		assertEquals(mutation1.hashCode(), mutation1.hashCode());
		
		assertFalse(mutation1.hashCode() == mutation2.hashCode());
		assertFalse(mutation1.hashCode() == mutation2.hashCode());
	}

	@Test
	public void testToString() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.toString(), "L10ACDEF");
	}

	@Test
	public void testGetShortText() {

		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation.getShortText(), "L10ACDEF");
	}

	@Test
	public void testCompareTo() {

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertEquals(mutation1.compareTo(mutation2), 0);

		mutation2 =new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation1.compareTo(mutation2), -1);

		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 11,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertEquals(mutation1.compareTo(mutation2), -1);

		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E'});
		assertEquals(mutation1.compareTo(mutation2), 1);
	}

	@Test
	public void testContainsSharedA() {

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertTrue(mutation1.containsSharedAA(mutation2));

		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation1.containsSharedAA(mutation2));

		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 11,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation1.containsSharedAA(mutation2));

		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 11,
				new char[] {'A', 'C', 'D', 'E'});

		assertFalse(mutation1.containsSharedAA(mutation2));
	}

	@Test
	public void testContainsSharedAA() {

		AAMutation<HIV> mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L', '*'});
		AAMutation<HIV> mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L', '*'});
		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), true));
		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), false));

		mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L', '*'});
		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L'});
		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), true));
		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), false));
		
		mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F', 'L', '*'});
		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'*'});
		assertFalse(mutation1.containsSharedAA(mutation2.getAAChars(), true));
		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), false));

		mutation1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'D',});
		mutation2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'A', 'C'});
		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), true));
		assertTrue(mutation1.containsSharedAA(mutation2.getAAChars(), false));
	}

	@Test
	public void testIsAtDrugResistancePosition() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertTrue(mutation.isAtDrugResistancePosition());
		
		// INFO: isAtDrugRessitancePosition is cached
		assertTrue(mutation.isAtDrugResistancePosition());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isAtDrugResistancePosition());
	}

	@Test
	public void testIsDRM() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertTrue(mutation.isDRM());
		assertTrue(mutation.isDRM());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		
		assertFalse(mutation.isDRM());
	}

	@Test
	public void testGetDRMDrugClass() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertEquals(mutation.getDRMDrugClass(), hiv.getDrugClass("NRTI"));
		assertEquals(mutation.getDRMDrugClass(), hiv.getDrugClass("NRTI"));

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});

		assertNull(mutation.getDRMDrugClass());
		
		// INFO: lookupDrugClass last line won't be reached
		//       because getDRMDrugClass has a if statement to tell if isDRM
	}

	@Test
	public void testIsTSM() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertTrue(mutation.isTSM());
		assertTrue(mutation.isTSM());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'L'});
		assertFalse(mutation.isTSM());
	}

	@Test
	public void testGetTSMDrugclass() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertEquals(mutation.getTSMDrugClass(), hiv.getDrugClass("NRTI"));
		assertEquals(mutation.getTSMDrugClass(), hiv.getDrugClass("NRTI"));
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10,
				new char[] {'L'});
		assertNull(mutation.getTSMDrugClass());
	}

	@Test
	public void testGetGenePosition() {

		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		GenePosition<HIV> position = new GenePosition<HIV>(hiv.getGene("HIV1RT"), 184);

		assertEquals(mutation.getGenePosition(), position);
	}

	@Test
	public void testIsUnusual() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertTrue(mutation.isUnusual());
		assertTrue(mutation.isUnusual());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'X'});

		assertTrue(mutation.isUnusual());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'M'});
		
		assertFalse(mutation.isUnusual());
	}

	@Test
	public void testIsSDRM() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});
		assertTrue(mutation.isSDRM());
		assertTrue(mutation.isSDRM());

		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertFalse(mutation.isSDRM());
	}

	@Test
	public void testGeSDRMDrugclass() {
		AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		assertEquals(mutation.getSDRMDrugClass(), hiv.getDrugClass("NRTI"));
		assertEquals(mutation.getSDRMDrugClass(), hiv.getDrugClass("NRTI"));
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				new char[] {'A', 'C', 'D', 'E', 'F'});
		assertNull(mutation.getSDRMDrugClass());
	}

	@Test
	public void testIsApobecMutation() {
		Mutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
		assertTrue(mutation.isApobecMutation());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
		assertFalse(mutation.isApobecMutation());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
		assertFalse(mutation.isApobecMutation());
		assertFalse(mutation.isApobecMutation());
	}

	 @Test
	 public void testIsApobecDRM() {
		Mutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
		assertFalse(mutation.isApobecDRM());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
		assertFalse(mutation.isApobecDRM());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
		assertTrue(mutation.isApobecDRM());
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'R');
		assertFalse(mutation.isApobecDRM());
		assertFalse(mutation.isApobecDRM());

	 }

	 @Test
	 public void testGetHighestMutPrevalence() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V', 'M', 'X'});

		 assertTrue(mutation.getHighestMutPrevalence() > 0.0);
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
					new char[] {'M', 'X'});
		 assertEquals(0.0, mutation.getHighestMutPrevalence(), 0.0);
		 assertEquals(0.0, mutation.getHighestMutPrevalence(), 0.0);
	 }

	 @Test
	 public void testGetPrevalences() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V'});

		 List<MutationPrevalence<HIV>> prevalence = mutation.getPrevalences();
		 
		 assertEquals(prevalence.size(), 26);
	 }
	 
	 @Test
	 public void testGetTypes() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V'});
		 assertEquals(mutation.getTypes().size(), 2);
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
					new char[] {'M'});
		 
		 assertEquals(mutation.getTypes().size(), 1);
	 }
	 
	 @Test
	 public void testGetASIFormat() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'V', 'X', '_', '*', '-'});
		 
		 assertEquals(mutation.getASIFormat(), "M184ZdACDEFVZi");
	 }

	 @Test
	 public void testHIVDBFormat() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'A', 'C', 'D', 'E', 'F', 'V', 'X', '_', '*', '-'});
		 
		 assertEquals(mutation.getHIVDBFormat(), "184*~ACDEFVX#");
	 }
	 
	 @Test
	 public void testGetHumanFormat() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'A', 'C', 'D', 'E', 'F', 'V', 'X', '_', '*', '-'});
		 assertEquals(mutation.getHumanFormat(), "M184X");
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'A', 'X', '_', '*', '-'});
		 assertEquals(mutation.getHumanFormat(), "M184*-AX_");
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'_'});
		 assertEquals(mutation.getHumanFormat(), "M184Insertion");
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'-'});
		 assertEquals(mutation.getHumanFormat(), "M184Deletion");
	 }

	 @Test
	 public void testGetShortHumanFormat() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'A', 'C', 'D', 'E', 'F', 'V', 'X', '_', '*', '-'});
		 assertEquals(mutation.getShortHumanFormat(), "M184X");
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'A', 'X', '_', '*', '-'});
		 assertEquals(mutation.getShortHumanFormat(), "M184*-AX_");
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'_'});
		 assertEquals(mutation.getShortHumanFormat(), "M184i");
		 
		 mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184,
				 	new char[] {'-'});
		 assertEquals(mutation.getShortHumanFormat(), "M184d");
	 }
	 
	 @Test
	 public void testGetHumanFormatWithoutLeadingRef() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>( hiv.getGene("HIV1RT"), 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'M'});
		 assertEquals(mutation.getHumanFormatWithoutLeadingRef(), "184MACDEF");
		 
		 mutation = new AAMutation<HIV>( hiv.getGene("HIV1RT"), 184,
				 	new char[] {'A', 'C', 'D', 'E', 'F', 'G', 'H'});
		 assertEquals(mutation.getHumanFormatWithoutLeadingRef(), "184X");
		 
		 mutation = new AAMutation<HIV>( hiv.getGene("HIV1RT"), 184,
				 	new char[] {'A', 'C', 'D', 'E', 'F', 'V', 'X', '_', '*', '-'});
		 assertEquals(mutation.getHumanFormatWithoutLeadingRef(), "184X");
	 }
	 
	 @Test
	 public void testGetHumanFormatWithGene() {
		 AAMutation<HIV> mutation = new AAMutation<HIV>( hiv.getGene("HIV1RT"), 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'M'});
		 assertEquals(mutation.getHumanFormatWithGene(), "HIV1RT_M184MACDEF");
	 }
	 
	 @Test
	 public void testGetComments() {
		 Gene<HIV> gene = hiv.getGene("HIV1RT");
		 AAMutation<HIV> mutation = new AAMutation<HIV>(gene, 184,
					new char[] {'A', 'C', 'D', 'E', 'F', 'M'});
		 
		 assertEquals(mutation.getComments().size(), 1);
		 
	 }
}