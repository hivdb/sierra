package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.genotypes.Genotype.RegionalGenotype;
import edu.stanford.hivdb.hivfacts.HIV;

public class BoundGenotypeTest {

	private final static HIV hiv = HIV.getInstance();
	private final GenotypeReference<HIV> genotype = hiv.getGenotypeReferences().get(0);
	private final BoundGenotype<HIV> boundGenotype = genotype.getBoundGenotype(
			genotype.getSequence(),
			genotype.getFirstNA(),
			genotype.getLastNA(),
			new ArrayList<Integer>());

	@Test
	public void testConstructor() {
		BoundGenotype<HIV> boundGeno = new BoundGenotype<HIV>(
				genotype,
				genotype.getSequence(),
				genotype.getFirstNA(),
				genotype.getLastNA(),
				new ArrayList<Integer>(),
				hiv
				);
		assertNotNull(boundGeno);
	}

	@Test
	public void testGetSequence() {
		assertTrue(boundGenotype.getSequence() instanceof String);
	}

	@Test
	public void testGetFirstNA() {
		assertNotNull(boundGenotype.getFirstNA());
	}

	@Test
	public void testGetLastNA() {
		assertNotNull(boundGenotype.getLastNA());
	}

	@Test
	public void testGetDistance() {
		assertTrue(boundGenotype.getDistance() instanceof Double);
	}

	@Test
	public void testGetReference() {
		assertTrue(boundGenotype.getReference() instanceof GenotypeReference);
		assertEquals(boundGenotype.getReference(), genotype);
	}

	@Test
	public void testGetReferenceAccession() {
		assertTrue(boundGenotype.getReferenceAccession() instanceof String);
	}

	@Test
	public void testGetReferenceCountry() {
		assertNotNull(boundGenotype.getReferenceCountry());
	}

	@Test
	public void testGetReferenceYear() {
		assertNotNull(boundGenotype.getReferenceYear());
	}

	@Test
	public void testGetGenotype() {
		assertTrue(boundGenotype.getGenotype() instanceof Genotype);
	}

	@Test
	public void testGetSubtype() {
		assertTrue(boundGenotype.getSubtype() instanceof Genotype);
		assertEquals(boundGenotype.getGenotype(), boundGenotype.getSubtype());
	}

	@Test
	public void testGetDiscordanceList() {
		assertTrue(boundGenotype.getDiscordanceList() instanceof List);
	}

	@Test
	public void testGetDistancePcnt() {
		BoundGenotype<HIV> boundGenotype1 = genotype.getBoundGenotype(
				genotype.getSequence(),
				genotype.getFirstNA(),
				genotype.getLastNA(),
				new ArrayList<Integer>());
		assertEquals(boundGenotype1.getDistancePcnt(), "0.00%");

		int length = boundGenotype.getLastNA() - boundGenotype.getFirstNA() + 1;

		List<Integer> discordance2 = new ArrayList<Integer>();
		for (int i=0; i < (length * 0.2); i++) {
			discordance2.add(Integer.valueOf(i));
		}

		BoundGenotype<HIV> boundGenotype2 = genotype.getBoundGenotype(
				genotype.getSequence(),
				genotype.getFirstNA(),
				genotype.getLastNA(),
				discordance2);

		assertEquals(boundGenotype2.getDistancePcnt(), "20.0%");


		List<Integer> discordance3 = new ArrayList<Integer>();
		for (int i=0; i < (length + 1); i++) {
			discordance3.add(Integer.valueOf(i));
		}

		BoundGenotype<HIV> boundGenotype3 = genotype.getBoundGenotype(
				genotype.getSequence(),
				genotype.getFirstNA(),
				genotype.getLastNA(),
				discordance3);

		assertEquals(boundGenotype3.getDistancePcnt(), "100%");
	}

	@Test
	public void testGetDisplayGenotypes() {
		// Test in regression test
	}

	@Test
	public void testGetDisplaySubtypes() {
		assertEquals(boundGenotype.getDisplayGenotypes(), boundGenotype.getDisplaySubtypes());
	}

	@Test
	public void testGetDisplay() {
		assertTrue(boundGenotype.getDisplay() instanceof String);

	}

	@Test
	public void testGetDisplayWithoutDistance() {
		assertTrue(boundGenotype.getDisplayWithoutDistance() instanceof String);
	}

	@Test
	public void testGetPrimaryRegionalGenotype() {
		assertTrue(boundGenotype.getPrimaryRegionalGenotype() instanceof RegionalGenotype);
	}

	@Test
	public void testCheckDistance() {
		assertTrue(boundGenotype.checkDistance());
	}

	@Test
	public void testShouldDisplayUnknown() {
		assertFalse(boundGenotype.shouldDisplayUnknown());
	}

	@Test
	public void testGetParentGenotypes() {
		assertTrue(boundGenotype.getParentGenotypes() instanceof List);
	}

	@Test
	public void testShouldFallbackTo() {
		assertFalse(boundGenotype.shouldFallbackTo(boundGenotype));
	}

	@Test
	public void testToString() {
		assertEquals(boundGenotype.toString(), boundGenotype.getDisplay());
	}
}
