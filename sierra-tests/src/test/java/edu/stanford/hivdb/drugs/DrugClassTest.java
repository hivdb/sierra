package edu.stanford.hivdb.drugs;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

public class DrugClassTest {

    final static HIV hiv = HIV.getInstance();

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

}