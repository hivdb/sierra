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

package edu.stanford.hivdb.drugresistance.database;

import java.sql.SQLException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.MutationScores;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

// TODO How to test mutation that are not scored (e.g. 35T) or that have a score of 0 (103N for ETR)
public class MutationScoresTest {

	@Test
	public void testMutScoreHash() throws SQLException {
		Gene gene = Gene.RT;
		Mutation mut1 = new Mutation(gene, 41, "L");
		Mutation mut2 = new Mutation(gene, 215, "SY");
		Mutation mut3 = new Mutation(gene, 35, "T");
		Mutation mut4 = new Mutation(gene, 103, "N");
		MutationSet mutSet = new MutationSet(mut1, mut2, mut3, mut4);

		Map<DrugClass, Map<Drug, Map<Mutation, Double>>> drugClassDrugMutScores =
				MutationScores.getDrugClassMutScoresForMutSet(gene, mutSet);

		Assert.assertNotEquals(10, drugClassDrugMutScores.get(DrugClass.NRTI).get(Drug.AZT).get(mut1).intValue());
		Assert.assertEquals(40, drugClassDrugMutScores.get(DrugClass.NRTI).get(Drug.AZT).get(mut2).intValue());

		//Need to check for existence of a score
		//Assert.assertEquals(0, drugClassDrugMutScores.get(DrugClass.NNRTI).get(Drug.ETR).get(mut3).intValue());

		Assert.assertNotEquals(30, drugClassDrugMutScores.get(DrugClass.NNRTI).get(Drug.EFV).get(mut4).intValue());
	}

}
