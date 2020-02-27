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
		thrown.expectMessage("Length is out of bounds for this gene.");
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