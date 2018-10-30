/*
    
    Copyright (C) 2017 Stanford HIVDB team
    
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

package edu.stanford.hivdb.drugs;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.mutations.Gene;

public class DrugClassTest {

	@Test
	public void testGetGene() {
		assertEquals(DrugClass.PI.gene(), Gene.PR);
		assertEquals(DrugClass.NRTI.gene(), Gene.RT);
		assertEquals(DrugClass.NNRTI.gene(), Gene.RT);
		assertEquals(DrugClass.INSTI.gene(), Gene.IN);
	}

	@Test
	public void testGetSynonym() {
		assertEquals(DrugClass.getSynonym("PI"), DrugClass.PI);
		assertEquals(DrugClass.getSynonym("NRTI"), DrugClass.NRTI);
		assertEquals(DrugClass.getSynonym("NNRTI"), DrugClass.NNRTI);
		assertEquals(DrugClass.getSynonym("INSTI"), DrugClass.INSTI);
		assertEquals(DrugClass.getSynonym("INI"), DrugClass.INSTI);
	}

	@Test
	public void testGetDrugsForHivdbTesting() {
		List<Drug> piExpecteds = new ArrayList<>();
		piExpecteds.add(Drug.ATV);
		piExpecteds.add(Drug.DRV);
		piExpecteds.add(Drug.FPV);
		piExpecteds.add(Drug.IDV);
		piExpecteds.add(Drug.LPV);
		piExpecteds.add(Drug.NFV);
		piExpecteds.add(Drug.SQV);
		piExpecteds.add(Drug.TPV);
		assertEquals(
			DrugClass.PI.getDrugsForHivdbTesting(), piExpecteds);

		List<Drug> nrtiExpecteds = new ArrayList<>();
		nrtiExpecteds.add(Drug.ABC);
		nrtiExpecteds.add(Drug.AZT);
		nrtiExpecteds.add(Drug.D4T);
		nrtiExpecteds.add(Drug.DDI);
		nrtiExpecteds.add(Drug.FTC);
		nrtiExpecteds.add(Drug.LMV);
		nrtiExpecteds.add(Drug.TDF);
		assertEquals(
			DrugClass.NRTI.getDrugsForHivdbTesting(), nrtiExpecteds);

		List<Drug> nnrtiExpecteds = new ArrayList<>();
		nnrtiExpecteds.add(Drug.DOR);
		nnrtiExpecteds.add(Drug.EFV);
		nnrtiExpecteds.add(Drug.ETR);
		nnrtiExpecteds.add(Drug.NVP);
		nnrtiExpecteds.add(Drug.RPV);
		assertEquals(
			DrugClass.NNRTI.getDrugsForHivdbTesting(), nnrtiExpecteds);

		List<Drug> instiExpecteds = new ArrayList<>();
		instiExpecteds.add(Drug.BIC);
		instiExpecteds.add(Drug.DTG);
		instiExpecteds.add(Drug.EVG);
		instiExpecteds.add(Drug.RAL);
		assertEquals(
			DrugClass.INSTI.getDrugsForHivdbTesting(), instiExpecteds);
	}

	@Test
	public void testGetAllDrugs() {
		List<Drug> piExpecteds = new ArrayList<>();
		piExpecteds.add(Drug.ATV);
		piExpecteds.add(Drug.DRV);
		piExpecteds.add(Drug.FPV);
		piExpecteds.add(Drug.IDV);
		piExpecteds.add(Drug.LPV);
		piExpecteds.add(Drug.NFV);
		piExpecteds.add(Drug.SQV);
		piExpecteds.add(Drug.TPV);
		assertEquals(
			DrugClass.PI.getAllDrugs(), piExpecteds);

		List<Drug> nrtiExpecteds = new ArrayList<>();
		nrtiExpecteds.add(Drug.ABC);
		nrtiExpecteds.add(Drug.AZT);
		nrtiExpecteds.add(Drug.D4T);
		nrtiExpecteds.add(Drug.DDI);
		nrtiExpecteds.add(Drug.FTC);
		nrtiExpecteds.add(Drug.LMV);
		nrtiExpecteds.add(Drug.TDF);
		assertEquals(
			DrugClass.NRTI.getAllDrugs(), nrtiExpecteds);

		List<Drug> nnrtiExpecteds = new ArrayList<>();
		nnrtiExpecteds.add(Drug.DOR);
		nnrtiExpecteds.add(Drug.EFV);
		nnrtiExpecteds.add(Drug.ETR);
		nnrtiExpecteds.add(Drug.NVP);
		nnrtiExpecteds.add(Drug.RPV);
		assertEquals(
			DrugClass.NNRTI.getAllDrugs(), nnrtiExpecteds);

		List<Drug> instiExpecteds = new ArrayList<>();
		instiExpecteds.add(Drug.BIC);
		instiExpecteds.add(Drug.DTG);
		instiExpecteds.add(Drug.EVG);
		instiExpecteds.add(Drug.RAL);
		assertEquals(
			DrugClass.INSTI.getAllDrugs(), instiExpecteds);
	}

	@Test
	public void testGetFullName() {
		assertEquals(
			"Nucleoside Reverse Transcriptase Inhibitor",
			DrugClass.NRTI.getFullName());
		assertEquals(
			"Non-nucleoside Reverse Transcriptase Inhibitor",
			DrugClass.NNRTI.getFullName());
		assertEquals(
			"Protease Inhibitor", DrugClass.PI.getFullName());
		assertEquals(
			"Integrase Strand Transfer Inhibitor",
			DrugClass.INSTI.getFullName());
	}

}
