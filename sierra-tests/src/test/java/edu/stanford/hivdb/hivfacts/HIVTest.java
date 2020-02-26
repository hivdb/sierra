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

package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.mutations.GenePosition;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationType;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Strain;


public class HIVTest {

	final static HIV hiv = HIV.getInstance();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test(expected=ExceptionInInitializerError.class)
	public void testLoadResourceError() {
		HIV.loadResource("hiv12345.json");
	}

	@Test
	public void testGetInstance() {
		HIV hiv1 = HIV.getInstance();
		HIV hiv2 = HIV.getInstance();
		assertSame(hiv1, hiv2);
    }

	@Test
	public void testGetName() {
		assertEquals(hiv.getName(), "HIV");
	}

	@Test
	public void testGetStrains() {
		Collection<Strain<HIV>> strains = hiv.getStrains();
		assertEquals(strains.size(), 1);
		Strain<HIV> strain = strains.iterator().next();
		assertEquals(strain.getName(), "HIV1");
	}

    @Test
    public void testGetStrain() {
    	
    	assertNotNull(hiv.getStrain("HIV1"));
    }
    
    @Test
    public void testGetStrainWithException() {
    	thrown.expect(IllegalArgumentException.class);
    	thrown.expectMessage("Strain \"null\" not found");
    	assertNull(hiv.getStrain(null));
    }

    @Test
    public void testGetStrainWithException2() {
    	
    	thrown.expect(IllegalArgumentException.class);
    	thrown.expectMessage("Strain \"HIV2\" not found");
    	
    	hiv.getStrain("HIV2");
    }
    
    @Test
    public void testGetStrainWithException3() {
    	
    	thrown.expect(IllegalArgumentException.class);
    	thrown.expectMessage("Strain \"HIV-1\" not found");
    	
    	hiv.getStrain("HIV-1");
    }

    @Test
    public void testGetGenes() {
    	Strain<HIV> strain = hiv.getStrain("HIV1");
    	Collection<Gene<HIV>> genes = hiv.getGenes(strain);

    	assertNotNull(genes);
    	assertEquals(genes.size(), 3);

		assertArrayEquals(hiv.getGenes(hiv.getStrain("HIV1")).toArray(), new Gene[] {
				hiv.getGene("HIV1PR"),
				hiv.getGene("HIV1RT"),
				hiv.getGene("HIV1IN")
			});
    }
    


    @Test
    public void testGetGene() {
    	assertNotNull(hiv.getGene("HIV1PR"));
    	assertEquals(hiv.getGene("HIV1PR").getName(), "HIV1PR");
    	assertEquals(hiv.getGene("HIV1PR").getAbstractGene(), "PR");

    	assertNotNull(hiv.getGene("HIV1RT"));
    	assertNotNull(hiv.getGene("HIV1IN"));
    }

    @Test
    public void testGetDrugClasses() {
        assertNotNull(hiv.getDrugClasses());
        assertEquals(hiv.getDrugClasses().size(), 4);

        assertEquals(hiv.getDrugClasses().iterator().next().getName(), "PI");

		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("PI")},
				hiv.getGene("HIV1PR").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("NRTI"), hiv.getDrugClass("NNRTI")},
				hiv.getGene("HIV1RT").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("INSTI")},
				hiv.getGene("HIV1IN").getDrugClasses().toArray()
				);
    }


    @Test
    public void testGetDrugClassSynonymMap() {
    	assertEquals(hiv.getDrugClassSynonymMap().get("PI").getName(), "PI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("NRTI").getName(), "NRTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("NNRTI").getName(), "NNRTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("INSTI").getName(), "INSTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("INI").getName(), "INSTI");

    }

    @Test
    public void testGetDrugClass() {
    	assertNotNull(hiv.getDrugClass("PI"));
    	assertNotNull(hiv.getDrugClass("NRTI"));
    	assertNotNull(hiv.getDrugClass("NNRTI"));
    	assertNotNull(hiv.getDrugClass("INSTI"));
    }

    @Test
    public void testGetDrugs() {
    	assertNotNull(hiv.getDrugs());
    	assertEquals(hiv.getDrugs().size(), 24);
    }

	@Test
	public void testGetDrugSynonymMap() {
		assertEquals(hiv.getDrugSynonymMap().get("ABC"),   hiv.getDrug("ABC"));
		assertEquals(hiv.getDrugSynonymMap().get("AZT"),   hiv.getDrug("AZT"));
		assertEquals(hiv.getDrugSynonymMap().get("D4T"),   hiv.getDrug("D4T"));
		assertEquals(hiv.getDrugSynonymMap().get("DDI"),   hiv.getDrug("DDI"));
		assertEquals(hiv.getDrugSynonymMap().get("FTC"),   hiv.getDrug("FTC"));
		assertEquals(hiv.getDrugSynonymMap().get("3TC"),   hiv.getDrug("LMV"));
		assertEquals(hiv.getDrugSynonymMap().get("LMV"),   hiv.getDrug("LMV"));
		assertEquals(hiv.getDrugSynonymMap().get("TDF"),   hiv.getDrug("TDF"));
		assertEquals(hiv.getDrugSynonymMap().get("ATV/r"), hiv.getDrug("ATV"));
		assertEquals(hiv.getDrugSynonymMap().get("DRV/r"), hiv.getDrug("DRV"));
		assertEquals(hiv.getDrugSynonymMap().get("FPV/r"), hiv.getDrug("FPV"));
		assertEquals(hiv.getDrugSynonymMap().get("IDV/r"), hiv.getDrug("IDV"));
		assertEquals(hiv.getDrugSynonymMap().get("LPV/r"), hiv.getDrug("LPV"));
		assertEquals(hiv.getDrugSynonymMap().get("ATV"),   hiv.getDrug("ATV"));
		assertEquals(hiv.getDrugSynonymMap().get("DRV"),   hiv.getDrug("DRV"));
		assertEquals(hiv.getDrugSynonymMap().get("FPV"),   hiv.getDrug("FPV"));
		assertEquals(hiv.getDrugSynonymMap().get("IDV"),   hiv.getDrug("IDV"));
		assertEquals(hiv.getDrugSynonymMap().get("LPV"),   hiv.getDrug("LPV"));
		assertEquals(hiv.getDrugSynonymMap().get("NFV"),   hiv.getDrug("NFV"));
		assertEquals(hiv.getDrugSynonymMap().get("SQV/r"), hiv.getDrug("SQV"));
		assertEquals(hiv.getDrugSynonymMap().get("TPV/r"), hiv.getDrug("TPV"));
		assertEquals(hiv.getDrugSynonymMap().get("SQV"),   hiv.getDrug("SQV"));
		assertEquals(hiv.getDrugSynonymMap().get("TPV"),   hiv.getDrug("TPV"));
		assertEquals(hiv.getDrugSynonymMap().get("EFV"),   hiv.getDrug("EFV"));
		assertEquals(hiv.getDrugSynonymMap().get("ETR"),   hiv.getDrug("ETR"));
		assertEquals(hiv.getDrugSynonymMap().get("NVP"),   hiv.getDrug("NVP"));
		assertEquals(hiv.getDrugSynonymMap().get("RPV"),   hiv.getDrug("RPV"));
		assertEquals(hiv.getDrugSynonymMap().get("DTG"),   hiv.getDrug("DTG"));
		assertEquals(hiv.getDrugSynonymMap().get("EVG"),   hiv.getDrug("EVG"));
		assertEquals(hiv.getDrugSynonymMap().get("RAL"),   hiv.getDrug("RAL"));
	}

	@Test
	public void testGetDrugSynonymMapWithException() {

		assertNull(hiv.getDrugSynonymMap().get(""));
		assertNull(hiv.getDrugSynonymMap().get("EVH"));
	}

	@Test
	public void testGetDrugResistAlgorithms() {
		assertNotNull(hiv.getDrugResistAlgorithms());
		// 20200214, 28 algorithms
		assertEquals(hiv.getDrugResistAlgorithms().size(), 28);
	}

	@Test
	public void testGetDrugResistAlgorithms2() {
		List<String> algoNames = new ArrayList<>();
		algoNames.add("HIVDB_8.9");
		algoNames.add("Rega_10.0");
		algoNames.add("ANRS_30");

		assertEquals(hiv.getDrugResistAlgorithms(algoNames).size(), 3);

		// 20200214 no HIVDB_9.0
		List<String> algoNames2 = new ArrayList<>();
		algoNames2.add("HIVDB_9.0");
		assertEquals(hiv.getDrugResistAlgorithms(algoNames2).size(), 1);
		assertNull(hiv.getDrugResistAlgorithms(algoNames2).iterator().next());
	}

	@Test
	public void testGetDrugResistAlgorithm() {
		DrugResistanceAlgorithm<HIV> algo = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		assertNotNull(algo);
		assertEquals(algo.getFamily(), "HIVDB");
	}

	@Test
	public void testGetDrugResistAlgorithm2() {
		DrugResistanceAlgorithm<HIV> algo = hiv.getDrugResistAlgorithm("HIVDB", "8.9");
		assertNotNull(algo);
		assertEquals(algo.getVersion(), "8.9");
	}


	@Test
	public void testExtractMutationGene() {
		assertNotNull(hiv.extractMutationGene("RT:M184V"));
		assertNull(hiv.extractMutationGene("RRRRR"));
		
		assertNull(hiv.extractMutationGene("RR:M184V"));
	}
	
	@Test(expected=Mutation.InvalidMutationException.class)
	public void testExtractMutationGeneWithException() {
		hiv.extractMutationGene(":123V");
	}

	@Test
	public void testParseMutationString() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		assertNotNull(hiv.parseMutationString(gene, "RT:M184V"));
	}

	@Test(expected=Mutation.InvalidMutationException.class)
	public void testParseMutationStringeWithException() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		assertNotNull(hiv.parseMutationString(gene, "RR:M184V"));
	}

	@Test
	public void testParseMutationString2() {
		assertNotNull(hiv.parseMutationString("RT:M184V"));
	}


	@Test
	public void testNewMutationSet() {
		assertNotNull(hiv.newMutationSet("RT:M184V"));
		
		assertEquals(hiv.newMutationSet(hiv.getGene("HIV1RT"), "M184V, E44A").size(), 2);
		
		List<String> formattedMuts = new ArrayList<>();
		formattedMuts.add("RT:M184V");
		assertNotNull(hiv.newMutationSet(formattedMuts));
		
	}

	@Test
	public void testNewMutationSet2() {
		List<String> formattedMuts = new ArrayList<>();
		formattedMuts.add("M184V");
		formattedMuts.add("E44A");

		assertEquals(hiv.newMutationSet(hiv.getGene("HIV1RT"), formattedMuts).size(), 2);
		
		String nullString = null;
		assertEquals(hiv.newMutationSet(hiv.getGene("HIV1RT"), nullString).size(), 0);
	}

	@Test(expected=Mutation.InvalidMutationException.class)
	public void testNewMutationSet3() {
		assertEquals(hiv.newMutationSet("M184V, E44A").size(), 2);
	}

	@Test
	public void testGetDrugResistMutations() {
		assertNotNull(hiv.getDrugResistMutations());
		assertEquals(hiv.getDrugResistMutations().size(), 4);
	}

	@Test
	public void testGetSurveilDrugResistMutations() {
		assertNotNull(hiv.getSurveilDrugResistMutations());
		assertEquals(hiv.getSurveilDrugResistMutations().size(), 4);
	}


	@Test
	public void testGetRxSelectedMutations() {
		assertNotNull(hiv.getRxSelectedMutations());
		assertEquals(hiv.getRxSelectedMutations().size(), 4);
	}

	@Test
	public void testGetApobecMutations() {
		assertNotNull(hiv.getApobecMutations());
	}


	@Test
	public void testGetApobecDRMs() {
		assertNotNull(hiv.getApobecDRMs());
	}

	@Test
	public void testGetMutationTypes() {
		assertNotNull(hiv.getMutationTypes());
		assertEquals(hiv.getMutationTypes().size(), 5);

		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("Major"),
					hiv.getMutationType("Accessory"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV1PR").getMutationTypes().toArray());
		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("NRTI"),
					hiv.getMutationType("NNRTI"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV1RT").getMutationTypes().toArray());
		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("Major"),
					hiv.getMutationType("Accessory"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV1IN").getMutationTypes().toArray());

	}

	@Test
	public void testGetMutationType() {
		assertNotNull(hiv.getMutationType("Major"));
		assertNull(hiv.getMutationType("Error"));
	}

	@Test
	public void testGetMutationTypePairs() {
		assertNotNull(hiv.getMutationTypePairs());
		// 20200214, 260 mutation type pairs
		assertEquals(hiv.getMutationTypePairs().size(), 260);
	}

	@Test
	public void testGetAminoAcidPercents() {
		Strain<HIV> strain = hiv.getStrain("HIV1");
		assertNotNull(hiv.getAminoAcidPercents(strain, "all", "A").get());
	}

	@Test
	public void testGetCodonPercents() {
		Strain<HIV> strain = hiv.getStrain("HIV1");
		assertNotNull(hiv.getCodonPercents(strain, "all", "A").get());
	}

	@Test
	public void testGetMutationPrevalence() {
		Gene<HIV> gene = hiv.getGene("HIV1RT");
		GenePosition<HIV> pos = new GenePosition<HIV>(gene, 184);
		assertNotNull(hiv.getMutationPrevalence(pos));
	}

	@Test
	public void testGetConditionalComments() {
		assertNotNull(hiv.getConditionalComments());
	}

	@Test
	public void testGetMainSubtypes() {
		Strain<HIV> strain = hiv.getStrain("HIV1");
		assertEquals(hiv.getMainSubtypes(strain).size(), 8);
	}

	@Test
	public void testGetNumPatientsForAAPercents() {
		Strain<HIV> strain = hiv.getStrain("HIV1");
		assertEquals(hiv.getNumPatientsForAAPercents(strain).size(), 3);
	}

	@Test
	public void testGetGenotypes() {
		assertNotNull(hiv.getGenotypes());
	}

	@Test
	public void testGetGenotype() {
		assertNotNull(hiv.getGenotype("B"));
	}

	@Test
	public void testGetGenotypeUnknown() {
		assertNotNull(hiv.getGenotypeUnknown());
		assertEquals(hiv.getGenotypeUnknown().getDisplayName(), "Unknown");
	}

	@Test
	public void testGetGenotypeReferences() {
		assertNotNull(hiv.getGenotypeReferences());
	}

	@Test
	public void testGetGenotyper() {
		assertNotNull(hiv.getGenotyper());
	}


	@Test
	public void testGetMainStrain() {
		assertSame(hiv.getStrain("HIV1"), hiv.getMainStrain());
	}

	@Test
	public void testEquals() {
		assertTrue(hiv.equals(HIV.getInstance()));
		assertFalse(hiv.equals(null));
	}
}