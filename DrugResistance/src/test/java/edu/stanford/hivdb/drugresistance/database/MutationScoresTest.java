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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.MutationScores;
import edu.stanford.hivdb.drugresistance.database.MutationScores.MutScore;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

// TODO How to test mutation that are not scored (e.g. 35T) or that have a score of 0 (103N for ETR)
public class MutationScoresTest {
	
	@Test
	public void testDefaultConstructor() {
		final MutationScores mutScores = new MutationScores();
		assertEquals(MutationScores.class, mutScores.getClass());
	}
	
	@Test 
	public void testMutScoreConstructor() {
		final Gene eGene = Gene.RT; 
		final DrugClass eDrugClass = DrugClass.NRTI;
		final Integer ePos = 41;
		final Character eAA = 'L'; 
		final Drug eDrug = Drug.ABC;
		final Double eScore = 5.0;
		final MutScore mutScore = new MutScore(eGene, eDrugClass, ePos, eAA, eDrug, eScore);
		assertEquals(eGene, mutScore.gene);
		assertEquals(eDrugClass, mutScore.drugClass);
		assertEquals(ePos, mutScore.pos);
		assertEquals(eAA, mutScore.aa);
		assertEquals(eDrug, mutScore.drug);
		assertEquals(eScore, mutScore.score);
	}
	
	@Test
	public void testGetMutScores() {
		final Gene eGene = Gene.RT;
		final Integer ePos = 41;
		final Character eAA = 'L';
		final List<MutScore> mutScoresList = MutationScores.getMutScores();
		for (MutScore ms : mutScoresList) {
			if (ms.pos == ePos) {
				assertEquals(eGene, ms.gene);
				assertEquals(eAA, ms.aa);
			}
		}
	}
	
	@Test
	public void testGetMutScoresForDrugClass() {
		final int ePos = 88;
		final int eNumScoresAtEPos = 13;
		final List<MutScore> mutScoresList = MutationScores.getMutScores(DrugClass.PI);
		int mutScoreCounter = 0;
		for (MutScore ms : mutScoresList) {
			assertNotEquals(DrugClass.NRTI, ms.drugClass);
			assertNotEquals(DrugClass.NNRTI, ms.drugClass);
			assertNotEquals(DrugClass.INSTI, ms.drugClass);
			if (ms.pos == ePos) { mutScoreCounter++; }
		}
		assertEquals(eNumScoresAtEPos, mutScoreCounter);
	}
	
	@Test
	public void testGroupMutationsByPos() {
		final Integer ePos = 67;
		final int eNumABCScoresAtEPos = 7;
		final Map<Integer, List<MutScore>> mutScores 
			= MutationScores.groupMutScoresByPos(Drug.ABC);
		final int numABCScoresAtEPos = mutScores.get(ePos).size();
		assertEquals(eNumABCScoresAtEPos, numABCScoresAtEPos);
	}
	
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

		assertNotEquals(10, drugClassDrugMutScores.get(DrugClass.NRTI).get(Drug.AZT).get(mut1).intValue());
		assertEquals(40, drugClassDrugMutScores.get(DrugClass.NRTI).get(Drug.AZT).get(mut2).intValue());

		//Need to check for existence of a score
		//Assert.assertEquals(0, drugClassDrugMutScores.get(DrugClass.NNRTI).get(Drug.ETR).get(mut3).intValue());

		assertNotEquals(30, drugClassDrugMutScores.get(DrugClass.NNRTI).get(Drug.EFV).get(mut4).intValue());
	}
}
