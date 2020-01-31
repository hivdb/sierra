package edu.stanford.hivdb.drugs;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class DrugClassTest {

    final static HIV hiv = HIV.getInstance();
    
    @Test
    public void testGetDrugClass() {
    	assertNotNull(hiv.getDrugClass("PI"));
    }

    @Test
    public void testGetAbstractGene() {
    	assertEquals(hiv.getDrugClass("PI").getAbstractGene(), "PR");
    	assertEquals(hiv.getDrugClass("NRTI").getAbstractGene(), "RT");
    	assertEquals(hiv.getDrugClass("NNRTI").getAbstractGene(), "RT");
    	assertEquals(hiv.getDrugClass("INSTI").getAbstractGene(), "IN");
    }

    @Test
    public void testGetFullName() {
    	assertEquals(
    			hiv.getDrugClass("PI").getFullName(),
    			"Protease Inhibitor"
    			);
    	assertEquals(
    			hiv.getDrugClass("NRTI").getFullName(),
    			"Nucleoside Reverse Transcriptase Inhibitor"
    			);
    	assertEquals(
    			hiv.getDrugClass("NNRTI").getFullName(),
    			"Non-nucleoside Reverse Transcriptase Inhibitor"
    			);
    	assertEquals(
    			hiv.getDrugClass("INSTI").getFullName(),
    			"Integrase Strand Transfer Inhibitor"
    			);

    }
   
    @Test
    public void testGetDrugs() {

    	List<Drug<HIV>> piExpecteds = new ArrayList<>();
    	piExpecteds.add(hiv.getDrug("ATV"));
    	piExpecteds.add(hiv.getDrug("DRV"));
    	piExpecteds.add(hiv.getDrug("FPV"));
    	piExpecteds.add(hiv.getDrug("IDV"));
    	piExpecteds.add(hiv.getDrug("LPV"));
    	piExpecteds.add(hiv.getDrug("NFV"));
    	piExpecteds.add(hiv.getDrug("SQV"));
    	piExpecteds.add(hiv.getDrug("TPV"));

    	DrugClass<HIV> PI = hiv.getDrugClass("PI");
    	assertEquals(new ArrayList<Drug<HIV>>(PI.getDrugs()), piExpecteds);

    	List<Drug<HIV>> nrtiExpecteds = new ArrayList<>();
    	nrtiExpecteds.add(hiv.getDrug("ABC"));
    	nrtiExpecteds.add(hiv.getDrug("AZT"));
    	nrtiExpecteds.add(hiv.getDrug("D4T"));
    	nrtiExpecteds.add(hiv.getDrug("DDI"));
    	nrtiExpecteds.add(hiv.getDrug("FTC"));
    	nrtiExpecteds.add(hiv.getDrug("LMV"));
    	nrtiExpecteds.add(hiv.getDrug("TDF"));

    	DrugClass<HIV> NRTI = hiv.getDrugClass("NRTI");
    	assertEquals(new ArrayList<Drug<HIV>>(NRTI.getDrugs()), nrtiExpecteds);

    	List<Drug<HIV>> nnrtiExpecteds = new ArrayList<>();
    	nnrtiExpecteds.add(hiv.getDrug("DOR"));
    	nnrtiExpecteds.add(hiv.getDrug("EFV"));
    	nnrtiExpecteds.add(hiv.getDrug("ETR"));
    	nnrtiExpecteds.add(hiv.getDrug("NVP"));
    	nnrtiExpecteds.add(hiv.getDrug("RPV"));
    
    	final DrugClass<HIV> NNRTI = hiv.getDrugClass("NNRTI");

    	assertEquals(new ArrayList<Drug<HIV>>(NNRTI.getDrugs()), nnrtiExpecteds);

    	List<Drug<HIV>> instiExpecteds = new ArrayList<>();

    	instiExpecteds.add(hiv.getDrug("BIC"));
    	instiExpecteds.add(hiv.getDrug("DTG"));
    	instiExpecteds.add(hiv.getDrug("EVG"));
    	instiExpecteds.add(hiv.getDrug("RAL"));

    	final DrugClass<HIV> INSTI = hiv.getDrugClass("INSTI");
    	assertEquals(new ArrayList<Drug<HIV>>(INSTI.getDrugs()), instiExpecteds);
    }

}