/*

    Copyright (C) 2020 Stanford HIVDB team

    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.hivfacts.hiv2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationType;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Strain;


public class HIV2Test {

	final static HIV2 hiv2 = HIV2.getInstance();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testGetInstance() {
		assertSame(HIV2.getInstance(), HIV2.getInstance());
    }

	@Test
	public void testGetName() {
		assertEquals(hiv2.getName(), "HIV2");
	}

	@Test
	public void testGetStrains() {
		Collection<Strain<HIV2>> strains = hiv2.getStrains();
		assertEquals(strains.size(), 2);
		Strain<HIV2> strain = strains.iterator().next();
		assertEquals(strain.getName(), "HIV2A");
		
	}

    @Test
    public void testGetStrain() {
    	assertNotNull(hiv2.getStrain("HIV2A"));
    	
    }

    @Test
    public void testGetStrainWithException() {
    	thrown.expect(IllegalArgumentException.class);
    	thrown.expectMessage("Strain \"null\" not found");
    	hiv2.getStrain(null);
    }
    
    @Test
    public void testGetStrainWithException2() {
    	thrown.expect(IllegalArgumentException.class);
    	thrown.expectMessage("Strain \"HIV1\" not found");
    	hiv2.getStrain("HIV1");
    }
    
    @Test
    public void testGetStrainWithException3() {
    	thrown.expect(IllegalArgumentException.class);
    	thrown.expectMessage("Strain \"HIV2\" not found");
    	hiv2.getStrain("HIV2");
    }
    
    @Test
    public void testGetStrainWithException4() {
    	thrown.expect(IllegalArgumentException.class);
    	thrown.expectMessage("Strain \"HIV-2A\" not found");
    	hiv2.getStrain("HIV-2A");
    }

    @Test
    public void testGetGenes() {
    	Strain<HIV2> strain = hiv2.getStrain("HIV2A");
    	Collection<Gene<HIV2>> genes = hiv2.getGenes(strain);

    	assertNotNull(genes);
    	assertEquals(genes.size(), 3);

		assertArrayEquals(hiv2.getGenes(hiv2.getStrain("HIV2A")).toArray(), new Gene[] {
				hiv2.getGene("HIV2APR"),
				hiv2.getGene("HIV2ART"),
				hiv2.getGene("HIV2AIN")
			});

    }

    @Test
    public void testGetGene() {
    	assertNotNull(hiv2.getGene("HIV2APR"));
    	assertEquals(hiv2.getGene("HIV2APR").getName(), "HIV2APR");
    	assertEquals(hiv2.getGene("HIV2APR").getAbstractGene(), "PR");

    	assertNotNull(hiv2.getGene("HIV2ART"));
    	assertNotNull(hiv2.getGene("HIV2AIN"));
    }

    @Test
    public void testGetDrugClasses() {
        assertNotNull(hiv2.getDrugClasses());
        assertEquals(hiv2.getDrugClasses().size(), 3);

        assertEquals(hiv2.getDrugClasses().iterator().next().getName(), "PI");

		assertArrayEquals(
				new DrugClass[] {hiv2.getDrugClass("PI")},
				hiv2.getGene("HIV2APR").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv2.getDrugClass("NRTI")},
				hiv2.getGene("HIV2ART").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv2.getDrugClass("INSTI")},
				hiv2.getGene("HIV2AIN").getDrugClasses().toArray()
				);
    }


    @Test
    public void testGetDrugClassSynonymMap() {
    	assertEquals(hiv2.getDrugClassSynonymMap().get("PI").getName(), "PI");
    	assertEquals(hiv2.getDrugClassSynonymMap().get("NRTI").getName(), "NRTI");
    	assertEquals(hiv2.getDrugClassSynonymMap().get("INSTI").getName(), "INSTI");
    	assertEquals(hiv2.getDrugClassSynonymMap().get("INI").getName(), "INSTI");

    }

    @Test
    public void testGetDrugClass() {
    	assertNotNull(hiv2.getDrugClass("PI"));
    	assertNotNull(hiv2.getDrugClass("NRTI"));
    	assertNull(hiv2.getDrugClass("NNRTI"));
    	assertNotNull(hiv2.getDrugClass("INSTI"));
    }

    @Test
    public void testGetDrugs() {
    	assertNotNull(hiv2.getDrugs());
    	assertEquals(hiv2.getDrugs().size(), 24);
    }

	@Test
	public void testGetDrugSynonymMap() {
		assertEquals(hiv2.getDrugSynonymMap().get("ABC"),   hiv2.getDrug("ABC"));
		assertEquals(hiv2.getDrugSynonymMap().get("AZT"),   hiv2.getDrug("AZT"));
		assertEquals(hiv2.getDrugSynonymMap().get("D4T"),   hiv2.getDrug("D4T"));
		assertEquals(hiv2.getDrugSynonymMap().get("DDI"),   hiv2.getDrug("DDI"));
		assertEquals(hiv2.getDrugSynonymMap().get("FTC"),   hiv2.getDrug("FTC"));
		assertEquals(hiv2.getDrugSynonymMap().get("3TC"),   hiv2.getDrug("LMV"));
		assertEquals(hiv2.getDrugSynonymMap().get("LMV"),   hiv2.getDrug("LMV"));
		assertEquals(hiv2.getDrugSynonymMap().get("TDF"),   hiv2.getDrug("TDF"));
		assertEquals(hiv2.getDrugSynonymMap().get("ATV/r"), hiv2.getDrug("ATV"));
		assertEquals(hiv2.getDrugSynonymMap().get("DRV/r"), hiv2.getDrug("DRV"));
		assertEquals(hiv2.getDrugSynonymMap().get("FPV/r"), hiv2.getDrug("FPV"));
		assertEquals(hiv2.getDrugSynonymMap().get("IDV/r"), hiv2.getDrug("IDV"));
		assertEquals(hiv2.getDrugSynonymMap().get("LPV/r"), hiv2.getDrug("LPV"));
		assertEquals(hiv2.getDrugSynonymMap().get("ATV"),   hiv2.getDrug("ATV"));
		assertEquals(hiv2.getDrugSynonymMap().get("DRV"),   hiv2.getDrug("DRV"));
		assertEquals(hiv2.getDrugSynonymMap().get("FPV"),   hiv2.getDrug("FPV"));
		assertEquals(hiv2.getDrugSynonymMap().get("IDV"),   hiv2.getDrug("IDV"));
		assertEquals(hiv2.getDrugSynonymMap().get("LPV"),   hiv2.getDrug("LPV"));
		assertEquals(hiv2.getDrugSynonymMap().get("NFV"),   hiv2.getDrug("NFV"));
		assertEquals(hiv2.getDrugSynonymMap().get("SQV/r"), hiv2.getDrug("SQV"));
		assertEquals(hiv2.getDrugSynonymMap().get("TPV/r"), hiv2.getDrug("TPV"));
		assertEquals(hiv2.getDrugSynonymMap().get("SQV"),   hiv2.getDrug("SQV"));
		assertEquals(hiv2.getDrugSynonymMap().get("TPV"),   hiv2.getDrug("TPV"));
		assertEquals(hiv2.getDrugSynonymMap().get("EFV"),   hiv2.getDrug("EFV"));
		assertEquals(hiv2.getDrugSynonymMap().get("ETR"),   hiv2.getDrug("ETR"));
		assertEquals(hiv2.getDrugSynonymMap().get("NVP"),   hiv2.getDrug("NVP"));
		assertEquals(hiv2.getDrugSynonymMap().get("RPV"),   hiv2.getDrug("RPV"));
		assertEquals(hiv2.getDrugSynonymMap().get("DTG"),   hiv2.getDrug("DTG"));
		assertEquals(hiv2.getDrugSynonymMap().get("EVG"),   hiv2.getDrug("EVG"));
		assertEquals(hiv2.getDrugSynonymMap().get("RAL"),   hiv2.getDrug("RAL"));
	}

	@Test
	public void testGetDrugSynonymMapWithException() {

		assertNull(hiv2.getDrugSynonymMap().get(""));
		assertNull(hiv2.getDrugSynonymMap().get("EVH"));
	}

	@Test
	public void testGetDrugResistAlgorithms() {
		assertNotNull(hiv2.getDrugResistAlgorithms());
		// 20200214, 28 algorithms
		assertEquals(hiv2.getDrugResistAlgorithms().size(), 1);
	}

	@Test
	public void testGetDrugResistAlgorithms2() {
		List<String> algoNames = new ArrayList<>();
		algoNames.add("HIVDB_8.9");
		algoNames.add("Rega_10.0");
		algoNames.add("ANRS_30");

		assertEquals(hiv2.getDrugResistAlgorithms(algoNames).size(), 3);

		// 20200214 no HIVDB_9.0
		List<String> algoNames2 = new ArrayList<>();
		algoNames2.add("HIVDB_9.0");
		assertEquals(hiv2.getDrugResistAlgorithms(algoNames2).size(), 1);
		assertNull(hiv2.getDrugResistAlgorithms(algoNames2).iterator().next());
	}

	@Test
	public void testGetDrugResistAlgorithm() {
		DrugResistanceAlgorithm<HIV2> algo = hiv2.getDrugResistAlgorithm("HIVDB_9.0a2");
		assertNotNull(algo);
		assertEquals(algo.getFamily(), "HIVDB");
	}
	
	@Test
	public void testGetDrugResistAlgorithmWithException() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Unable to locate algorithm HIVDB_1.0");
		hiv2.getDrugResistAlgorithm("HIVDB_1.0");
	}

	@Test
	public void testGetDrugResistAlgorithm2() {
		DrugResistanceAlgorithm<HIV2> algo = hiv2.getDrugResistAlgorithm("HIVDB", "9.0a2");
		assertNotNull(algo);
		assertEquals(algo.getVersion(), "9.0a2");
	}


	@Test
	public void testExtractMutationGene() {
		assertNotNull(hiv2.extractMutationGene("RT:M184V"));
		assertNull(hiv2.extractMutationGene("RRRRR"));

		assertNull(hiv2.extractMutationGene("RR:M184V"));
	}

	@Test(expected=Mutation.InvalidMutationException.class)
	public void testExtractMutationGeneWithException() {
		hiv2.extractMutationGene(":123V");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testParseMutationString() {
		hiv2.getGene("HIV2RT");
	}

	@Test(expected=Mutation.InvalidMutationException.class)
	public void testParseMutationStringeWithException() {
		Gene<HIV2> gene = hiv2.getGene("HIV2ART");
		assertNotNull(hiv2.parseMutationString(gene, "RR:M184V"));
	}

	@Test
	public void testParseMutationString2() {
		assertNotNull(hiv2.parseMutationString("RT:M184V"));
	}


	@Test
	public void testNewMutationSet() {
		assertNotNull(hiv2.newMutationSet("RT:M184V"));

		assertEquals(hiv2.newMutationSet(hiv2.getGene("HIV2ART"), "M184V, E44A").size(), 2);

		List<String> formattedMuts = new ArrayList<>();
		formattedMuts.add("RT:M184V");
		assertNotNull(hiv2.newMutationSet(formattedMuts));

	}

	@Test
	public void testNewMutationSet2() {
		List<String> formattedMuts = new ArrayList<>();
		formattedMuts.add("M184V");
		formattedMuts.add("E44A");

		assertEquals(hiv2.newMutationSet(hiv2.getGene("HIV2ART"), formattedMuts).size(), 2);

		String nullString = null;
		assertEquals(hiv2.newMutationSet(hiv2.getGene("HIV2ART"), nullString).size(), 0);
	}

	@Test(expected=Mutation.InvalidMutationException.class)
	public void testNewMutationSet3() {
		assertEquals(hiv2.newMutationSet("M184V, E44A").size(), 2);
	}

	@Test
	public void testGetDrugResistMutations() {
		assertNotNull(hiv2.getDrugResistMutations());
		assertEquals(hiv2.getDrugResistMutations().size(), 3);
	}

	@Test
	public void testGetSurveilDrugResistMutations() {
		assertNotNull(hiv2.getSurveilDrugResistMutations());
		assertEquals(hiv2.getSurveilDrugResistMutations().size(), 3);
	}


	@Test
	public void testGetRxSelectedMutations() {
		assertNotNull(hiv2.getRxSelectedMutations());
		assertEquals(hiv2.getRxSelectedMutations().size(), 3);
	}

	@Test
	public void testGetApobecMutations() {
		assertNotNull(hiv2.getApobecMutations());
	}


	@Test
	public void testGetApobecDRMs() {
		assertNotNull(hiv2.getApobecDRMs());
	}

	@Test
	public void testGetMutationTypes() {
		assertNotNull(hiv2.getMutationTypes());
		assertEquals(hiv2.getMutationTypes().size(), 5);

		assertArrayEquals(
				new MutationType[] {
					hiv2.getMutationType("Major"),
					hiv2.getMutationType("Accessory"),
					hiv2.getMutationType("Other")
				},
				hiv2.getGene("HIV2APR").getMutationTypes().toArray());

		assertArrayEquals(
				new MutationType[] {
					hiv2.getMutationType("Major"),
					hiv2.getMutationType("Accessory"),
					hiv2.getMutationType("Other")
				},
				hiv2.getGene("HIV2ART").getMutationTypes().toArray());
		assertArrayEquals(
				new MutationType[] {
					hiv2.getMutationType("Major"),
					hiv2.getMutationType("Accessory"),
					hiv2.getMutationType("Other")
				},
				hiv2.getGene("HIV2AIN").getMutationTypes().toArray());

	}

	@Test
	public void testGetMutationType() {
		assertNotNull(hiv2.getMutationType("Major"));
		assertNull(hiv2.getMutationType("Error"));
	}

	@Test
	public void testGetMutationTypePairs() {
		assertNotNull(hiv2.getMutationTypePairs());
		// 20200214, 260 mutation type pairs
		assertEquals(hiv2.getMutationTypePairs().size(), 124);
	}

	@Test
	public void testGetAminoAcidPercents() {
		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
		assertNotNull(hiv2.getAminoAcidPercents(strain, "all", "A").get());
	}

	@Test
	public void testGetCodonPercents() {
		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
		assertNotNull(hiv2.getCodonPercents(strain, "all", "A").get());
	}

	@Test
	public void testGetMutationPrevalence() {
//		Gene<HIV2> gene = hiv2.getGene("HIV2ART");
//		GenePosition<HIV2> pos = new GenePosition<HIV2>(gene, 1);
//		assertNotNull(hiv2.getMutationPrevalence(pos));
	}

	@Test
	public void testGetConditionalComments() {
		assertNotNull(hiv2.getConditionalComments());
	}

	@Test
	public void testGetMainSubtypes() {
		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
		assertEquals(hiv2.getMainSubtypes(strain).size(), 1);
	}

	@Test
	public void testGetNumPatientsForAAPercents() {
//		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
//		assertEquals(hiv2.getNumPatientsForAAPercents(strain).size(), 3);
	}

	@Test
	public void testGetGenotypes() {
		assertNotNull(hiv2.getGenotypes());
	}

	@Test
	public void testGetGenotype() {
		assertNotNull(hiv2.getGenotype("HIV2A"));
	}

	@Test
	public void testGetGenotypeUnknown() {
		assertNotNull(hiv2.getGenotypeUnknown());
		assertEquals(hiv2.getGenotypeUnknown().getDisplayName(), "Unknown");
	}

	@Test
	public void testGetGenotypeReferences() {
		assertNotNull(hiv2.getGenotypeReferences());
	}

	@Test
	public void testGetGenotyper() {
		assertNotNull(hiv2.getGenotyper());
	}


	@Test
	public void testGetMainStrain() {
		assertSame(hiv2.getStrain("HIV2A"), hiv2.getMainStrain());
	}

	@Test
	public void testEquals() {
		assertTrue(hiv2.equals(HIV2.getInstance()));
		assertFalse(hiv2.equals(null));
	}
}