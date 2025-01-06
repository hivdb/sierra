package edu.stanford.hivdb.drugs;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class DrugClassTest {

	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testLoadJSON() {
		assertEquals(5, hiv.getDrugClasses().size());
	}
	
	@Test
	public void testDrugClass() {
		
		// WARNING: DrugClass Private Constructor is for GSON
	}
	
	@Test
	public void testGetSynonyms() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.getSynonyms().size(), 0);
	}

	@Test
	public void testGetDrugs() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.getDrugs().size(), 6);
	}
	
	@Test
	public void testGetName() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.getName(), "NNRTI");
	}
	
	@Test
	public void testName() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.name(), drugClass.getName());
	}
	
	@Test
	public void testGetFullName() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.getFullName(), "Non-nucleoside Reverse Transcriptase Inhibitor");
	}
	
	@Test
	public void testGetAbstractGene() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.getAbstractGene(), "RT");
	}
	
	@Test
	public void testGetStrains() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertTrue(drugClass.getStrains().contains(hiv.getStrain("HIV1")));
		
		// INFO: strainObjs is cached
		assertTrue(drugClass.getStrains().contains(hiv.getStrain("HIV1")));
	}
	
	@Test
	public void testSupportStrain() {
		// INFO: Using NRTI instead of NNRTI to test strainObj is not cached
		DrugClass<HIV> drugClass = hiv.getDrugClass("NRTI");
		assertTrue(drugClass.supportStrain(hiv.getStrain("HIV1")));
		
		// INFO: strainObjs is cached
		assertTrue(drugClass.supportStrain(hiv.getStrain("HIV1")));
	}

	@Test
	public void testGetMutationTypes() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.getMutationTypes().size(), 2);
		
		// INFO: mutationTypeObjects is cached
		assertEquals(drugClass.getMutationTypes().size(), 2);
	}
	
	@Test
	public void testToString() {
		DrugClass<HIV> drugClass = hiv.getDrugClass("NNRTI");
		assertEquals(drugClass.toString(), drugClass.getName());

	}
	
	@Test
	public void testEquals() {
		assertTrue(hiv.getDrugClass("NNRTI").equals(hiv.getDrugClass("NNRTI")));
		
		assertFalse(hiv.getDrugClass("NNRTI").equals(hiv.getDrugClass("NRTI")));
		
		assertFalse(hiv.getDrugClass("NNRTI").equals(null));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(hiv.getDrugClass("NNRTI").hashCode(), hiv.getDrugClass("NNRTI").hashCode());
		assertFalse(hiv.getDrugClass("NNRTI").hashCode() == hiv.getDrugClass("NRTI").hashCode());
		assertFalse(hiv.getDrugClass("NNRTI").hashCode() == hiv.getDrugClass("NRTI").hashCode());
	}
 
	@Test
	public void testCompareTo() {
		assertEquals(hiv.getDrugClass("NNRTI").compareTo(hiv.getDrugClass("NRTI")), 1);
		assertEquals(hiv.getDrugClass("NNRTI").compareTo(hiv.getDrugClass("NNRTI")), 0);
	}
	
	// INFO: Start of Full DrugClass Test
	@Test
	public void testGetAbstractGeneFullDrugClass() {
		assertEquals(hiv.getDrugClass("PI").getAbstractGene(), "PR");
		assertEquals(hiv.getDrugClass("NRTI").getAbstractGene(), "RT");
		assertEquals(hiv.getDrugClass("NNRTI").getAbstractGene(), "RT");
		assertEquals(hiv.getDrugClass("INSTI").getAbstractGene(), "IN");
	}

	@Test
	public void testGetFullNameFullDrugClass() {
		assertEquals(
			"Protease Inhibitor",
			hiv.getDrugClass("PI").getFullName()
		);
		assertEquals(
			"Nucleoside Reverse Transcriptase Inhibitor",
			hiv.getDrugClass("NRTI").getFullName()
		);
		assertEquals(
			"Non-nucleoside Reverse Transcriptase Inhibitor",
			hiv.getDrugClass("NNRTI").getFullName()
		);
		assertEquals(
			"Integrase Strand Transfer Inhibitor",
			hiv.getDrugClass("INSTI").getFullName()
		);
	}
   
	@Test
	public void testGetDrugsFullDrugClass() {

		List<Drug<HIV>> PIDrugs = new ArrayList<>();
		PIDrugs.add(hiv.getDrug("ATV"));
		PIDrugs.add(hiv.getDrug("DRV"));
		PIDrugs.add(hiv.getDrug("FPV"));
		PIDrugs.add(hiv.getDrug("IDV"));
		PIDrugs.add(hiv.getDrug("LPV"));
		PIDrugs.add(hiv.getDrug("NFV"));
		PIDrugs.add(hiv.getDrug("SQV"));
		PIDrugs.add(hiv.getDrug("TPV"));

		final DrugClass<HIV> PI = hiv.getDrugClass("PI");
		assertEquals(PIDrugs, new ArrayList<Drug<HIV>>(PI.getDrugs()));

		List<Drug<HIV>> NRTIDrugs = new ArrayList<>();
		NRTIDrugs.add(hiv.getDrug("ABC"));
		NRTIDrugs.add(hiv.getDrug("AZT"));
		NRTIDrugs.add(hiv.getDrug("D4T"));
		NRTIDrugs.add(hiv.getDrug("DDI"));
		NRTIDrugs.add(hiv.getDrug("FTC"));
		NRTIDrugs.add(hiv.getDrug("LMV"));
		NRTIDrugs.add(hiv.getDrug("TDF"));

		final DrugClass<HIV> NRTI = hiv.getDrugClass("NRTI");
		assertEquals(NRTIDrugs, new ArrayList<Drug<HIV>>(NRTI.getDrugs()));

		List<Drug<HIV>> NNRTIDrugs = new ArrayList<>();
		NNRTIDrugs.add(hiv.getDrug("DOR"));
		NNRTIDrugs.add(hiv.getDrug("DPV"));
		NNRTIDrugs.add(hiv.getDrug("EFV"));
		NNRTIDrugs.add(hiv.getDrug("ETR"));
		NNRTIDrugs.add(hiv.getDrug("NVP"));
		NNRTIDrugs.add(hiv.getDrug("RPV"));
	
		final DrugClass<HIV> NNRTI = hiv.getDrugClass("NNRTI");

		assertEquals(NNRTIDrugs, new ArrayList<Drug<HIV>>(NNRTI.getDrugs()));

		List<Drug<HIV>> INSTIDrugs = new ArrayList<>();

		INSTIDrugs.add(hiv.getDrug("BIC"));
		INSTIDrugs.add(hiv.getDrug("CAB"));
		INSTIDrugs.add(hiv.getDrug("DTG"));
		INSTIDrugs.add(hiv.getDrug("EVG"));
		INSTIDrugs.add(hiv.getDrug("RAL"));

		final DrugClass<HIV> INSTI = hiv.getDrugClass("INSTI");
		assertEquals(INSTIDrugs, new ArrayList<Drug<HIV>>(INSTI.getDrugs()));
	}

}