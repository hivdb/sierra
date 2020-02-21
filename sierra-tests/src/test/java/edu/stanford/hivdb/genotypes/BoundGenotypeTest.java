package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.genotypes.Genotype.RegionalGenotype;
import edu.stanford.hivdb.genotypes.GenotypeRegressionTest.SequenceWithExpectedGenotype;
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
	
	
	@Test
	public void testGetDisplayGenotypesRegression() {
		InputStream json = (
			GenotypeRegressionTest.class.getClassLoader()
			.getResourceAsStream("testSequences/lots.json"));
		List<SequenceWithExpectedGenotype> lots = new Gson().fromJson(
				new BufferedReader(new InputStreamReader(json)),
			    new TypeToken<List<SequenceWithExpectedGenotype>>(){}.getType());
		
		
		for (SequenceWithExpectedGenotype eseq : lots) {
			TestSequence seq = eseq.testSequence;
			if (seq.accession.equals("AB356209")) {
				GenotypeResult<HIV> result = hiv.getGenotyper().compareAll(
					seq.sequence, seq.firstNA, seq.lastNA);
				BoundGenotype<HIV> primary = result.getFirstMatch();
				assertEquals("B (3.18%)", primary.getDisplay());
				assertEquals(1005, primary.getSequence().length());
			}
			else if (seq.accession.equals("DQ345008-DQ345037")) {
				GenotypeResult<HIV> result = hiv.getGenotyper().compareAll(
					seq.sequence, seq.firstNA, seq.lastNA);
				BoundGenotype<HIV> primary = result.getFirstMatch();
				assertEquals("A (3.44%)", primary.getDisplay());
				assertEquals(1047, primary.getSequence().length());
			}
		}
	}
	
}
