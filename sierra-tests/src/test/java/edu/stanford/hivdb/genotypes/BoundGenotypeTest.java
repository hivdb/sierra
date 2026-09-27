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

	/** Build a BoundGenotype for the given subtype reference at (approximately)
	 *  the requested distance, by seeding a discordance list of the right size.
	 *  The distance is discordanceList.size() / seqLen, so the values in the
	 *  list are irrelevant.
	 */
	private BoundGenotype<HIV> boundGenotypeAtDistance(String indexName, double targetDistance) {
		GenotypeReference<HIV> ref = hiv.getGenotypeReferences().stream()
			.filter(r -> r.getGenotype().getIndexName().equals(indexName))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("No reference for " + indexName));
		int length = ref.getLastNA() - ref.getFirstNA() + 1;
		int numDiscordance = (int) Math.round(length * targetDistance);
		List<Integer> discordance = new ArrayList<>();
		for (int i = 0; i < numDiscordance; i++) {
			discordance.add(Integer.valueOf(i));
		}
		return ref.getBoundGenotype(
			ref.getSequence(), ref.getFirstNA(), ref.getLastNA(), discordance);
	}

	@Test
	public void testIsPureSubtypeAboveDistanceUpperLimit() {
		// K's distance upper-limit is 6% and the "unknown" threshold is 11%.

		// Below the upper-limit: reported normally, no warning ("point 2").
		assertFalse(
			boundGenotypeAtDistance("K", 0.03).isPureSubtypeAboveDistanceUpperLimit());

		// Above the upper-limit but below "unknown": the "point 5" scenario.
		BoundGenotype<HIV> point5 = boundGenotypeAtDistance("K", 0.09);
		assertTrue(point5.isPureSubtypeAboveDistanceUpperLimit());
		assertEquals("K", point5.getDisplayWithoutDistance());

		// Above the "unknown" threshold: reported as Unknown, no warning ("point 1").
		assertFalse(
			boundGenotypeAtDistance("K", 0.15).isPureSubtypeAboveDistanceUpperLimit());
	}

	@Test
	public void testIsPureSubtypeAboveDistanceUpperLimitNotPureSubtype() {
		// A recombinant (CRF) above its upper-limit is handled by "point 4",
		// not "point 5", so it must not trigger the pure-subtype warning even
		// though the distance is in the same band.
		GenotypeReference<HIV> crfRef = hiv.getGenotypeReferences().stream()
			.filter(r -> r.getGenotype().getClassificationLevel() == GenotypeClassificationLevel.CRF)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No CRF reference found"));
		int length = crfRef.getLastNA() - crfRef.getFirstNA() + 1;
		List<Integer> discordance = new ArrayList<>();
		for (int i = 0; i < Math.round(length * 0.09); i++) {
			discordance.add(Integer.valueOf(i));
		}
		BoundGenotype<HIV> crf = crfRef.getBoundGenotype(
			crfRef.getSequence(), crfRef.getFirstNA(), crfRef.getLastNA(), discordance);
		assertFalse(crf.shouldDisplayUnknown());
		assertFalse(crf.checkDistance());
		assertFalse(crf.isPureSubtypeAboveDistanceUpperLimit());
	}

	@Test
	public void testGetParentGenotypes() {
		assertTrue(boundGenotype.getParentGenotypes() instanceof List);
	}

	@Test
	public void testToString() {
		assertEquals(boundGenotype.toString(), boundGenotype.getDisplay());
	}
}
