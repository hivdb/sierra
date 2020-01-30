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
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.drugs.Drug;


public class HIVTest {

	@Test
	public void testGetInstance() {
		HIV hiv1 = HIV.getInstance();
		HIV hiv2 = HIV.getInstance();
		assertSame(hiv1, hiv2);
    }

    @Test
    public void testgetDrugClasses() {
        HIV hiv = HIV.getInstance();
        assertNotNull(hiv.getDrugClasses());
    }
    
    @Test
    public void testGetStrain() {
    	HIV hiv = HIV.getInstance();
    	assertNotNull(hiv.getStrain("HIV1"));
    }
    
    @Test
    public void testGetDrugClass() {
    	HIV hiv = HIV.getInstance();
    	assertEquals(hiv.getDrugClass("PI").getAbstractGene(), "PR");
    	assertEquals(hiv.getDrugClass("NRTI").getAbstractGene(), "RT");
    	assertEquals(hiv.getDrugClass("NNRTI").getAbstractGene(), "RT");
    	assertEquals(hiv.getDrugClass("INSTI").getAbstractGene(), "IN");
    	
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
    public void testGetDrugClassSynonymMap() {
    	HIV hiv = HIV.getInstance();
    	assertEquals(hiv.getDrugClassSynonymMap().get("PI").getName(), "PI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("NRTI").getName(), "NRTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("NNRTI").getName(), "NNRTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("INSTI").getName(), "INSTI");
    	assertEquals(hiv.getDrugClassSynonymMap().get("INI").getName(), "INSTI");

    }
    
    @Test
    public void testGetDrugs() {
    	HIV hiv = HIV.getInstance();
    	
    	List<Drug<HIV>> piExpecteds = new ArrayList<>();
    	piExpecteds.add(hiv.getDrugSynonymMap().get("ATV"));
    	piExpecteds.add(hiv.getDrugSynonymMap().get("DRV"));
    	piExpecteds.add(hiv.getDrugSynonymMap().get("FPV"));
    	piExpecteds.add(hiv.getDrugSynonymMap().get("IDV"));
    	piExpecteds.add(hiv.getDrugSynonymMap().get("LPV"));
    	piExpecteds.add(hiv.getDrugSynonymMap().get("NFV"));
    	piExpecteds.add(hiv.getDrugSynonymMap().get("SQV"));
    	piExpecteds.add(hiv.getDrugSynonymMap().get("TPV"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("PI").getDrugs()), piExpecteds);
    	
    	List<Drug<HIV>> nrtiExpecteds = new ArrayList<>();
    	nrtiExpecteds.add(hiv.getDrugSynonymMap().get("ABC"));
    	nrtiExpecteds.add(hiv.getDrugSynonymMap().get("AZT"));
    	nrtiExpecteds.add(hiv.getDrugSynonymMap().get("D4T"));
    	nrtiExpecteds.add(hiv.getDrugSynonymMap().get("DDI"));
    	nrtiExpecteds.add(hiv.getDrugSynonymMap().get("FTC"));
    	nrtiExpecteds.add(hiv.getDrugSynonymMap().get("LMV"));
    	nrtiExpecteds.add(hiv.getDrugSynonymMap().get("TDF"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("NRTI").getDrugs()), nrtiExpecteds);
    	
    	List<Drug<HIV>> nnrtiExpecteds = new ArrayList<>();
    	nnrtiExpecteds.add(hiv.getDrugSynonymMap().get("DOR"));
    	nnrtiExpecteds.add(hiv.getDrugSynonymMap().get("EFV"));
    	nnrtiExpecteds.add(hiv.getDrugSynonymMap().get("ETR"));
    	nnrtiExpecteds.add(hiv.getDrugSynonymMap().get("NVP"));
    	nnrtiExpecteds.add(hiv.getDrugSynonymMap().get("RPV"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("NNRTI").getDrugs()), nnrtiExpecteds);
    	
    	List<Drug<HIV>> instiExpecteds = new ArrayList<>();

    	instiExpecteds.add(hiv.getDrugSynonymMap().get("BIC"));
    	instiExpecteds.add(hiv.getDrugSynonymMap().get("DTG"));
    	instiExpecteds.add(hiv.getDrugSynonymMap().get("EVG"));
    	instiExpecteds.add(hiv.getDrugSynonymMap().get("RAL"));
    	assertEquals(new ArrayList<Drug<HIV>>(hiv.getDrugClass("INSTI").getDrugs()), instiExpecteds);
    }
}