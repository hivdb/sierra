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

package edu.stanford.hivdb.drugresistance.algorithm;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fstrf.stanfordAsiInterpreter.resistance.AsiTransformer;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.Gene;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedCondition;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedDrug;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedDrugClass;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedGene;
import org.fstrf.stanfordAsiInterpreter.resistance.grammar.MutationComparator;
import org.fstrf.stanfordAsiInterpreter.resistance.grammar.StringMutationComparator;
import org.fstrf.stanfordAsiInterpreter.resistance.xml.XmlAsiTransformer;
import org.junit.Test;

/**
 * This tests the fstrf jar file using the code stub provided by Adina Stoica
 * It requires the path to a valid ASI xml file and list of mutations
 * TODO: Use XMLAsiTransformer to validate the xml document
 */

public class FstrfCodeTest {
	private String filePath = "AlgXMLs/HIVDB_8.0.1.xml";
	//private String filePath = "Resources/ANRS.xml";
	private List<String> mutations = new ArrayList<String>();

	@Test
	public void test() throws FileNotFoundException, Exception {

		// AsiTransformer transformer = new XmlAsiTransformer(this.validateXml);
		AsiTransformer transformer = new XmlAsiTransformer(true);

		// The key is the gene name and the value Gene contains all data about the gene
		Map<?, ?> geneMap = transformer.transform(FstrfCodeTest.class.getClassLoader().getResourceAsStream(filePath));

		// Gene has a String name, Set drugClasses, and List geneRules
		Gene asiGene = (Gene) geneMap.get("RT");

		// True means only exact matches are considered equal.
		MutationComparator mutationComparator = new StringMutationComparator(false);
		mutations.add("41L");
		mutations.add("215Y");
		mutations.add("184V");
		mutations.add("103N");
		if (!mutationComparator.areMutationsValid(mutations)){
			throw new RuntimeException("Invalid list of mutations: " + mutations.toString());
		}

		// Class Evaluated Gene has a Gene, a Collection evaluatedDrugClasses,
		//    a Set geneScoredMutations, a Set geneCommentDefinitions, and
		//    parseGeneCommentDefinitions(geneEvaluatedConditions)
		// evaluate Method of Gene Class returns a new EvaluatedGene with
		//    evaluatedDrugClasses and evaluatedConditions (also called drug rules)
		EvaluatedGene evaluatedGene = asiGene.evaluate(mutations, mutationComparator);

		assertEvaluatedGene(evaluatedGene);

		// getAlgorithmInfo returns a map containing information about an algorithm.
		//   one of the keys is ALGNAME_ALGVERSION_ALGDATE
		Map<?, ?> algoInfo = (Map<?, ?>) ((Map<?, ?>) transformer.getAlgorithmInfo(FstrfCodeTest.class.getClassLoader().getResourceAsStream(filePath))).get("ALGNAME_ALGVERSION_ALGDATE");
		assertEquals("HIVDB", algoInfo.get("ALGNAME"));
		assertEquals("8.0.1", algoInfo.get("ALGVERSION"));
		assertEquals("2016-06-08", algoInfo.get("ALGDATE"));
	}

	private void assertEvaluatedGene(EvaluatedGene asiGene) {
		assertEquals("RT", asiGene.getGene().getName());
		for(Object drugClassObj : asiGene.getEvaluatedDrugClasses()) {
			EvaluatedDrugClass drugClass = (EvaluatedDrugClass) drugClassObj;

			for(Object drugObj : drugClass.getEvaluatedDrugs()) {
				EvaluatedDrug drug = (EvaluatedDrug) drugObj;
				Iterator<?> iter = drug.getEvaluatedConditions().iterator();
				EvaluatedCondition condition = (EvaluatedCondition) iter.next();
				switch (drug.getDrug().getDrugName()) {
				case "D4T":
					assertEquals(55.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "TDF":
					assertEquals(15.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "DDI":
					assertEquals(45.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "FTC":
					assertEquals(65.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "3TC":
					assertEquals(65.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "AZT":
					assertEquals(55.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "ABC":
					assertEquals(45.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "NVP":
					assertEquals(60.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "EFV":
					assertEquals(60.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "ETR":
					assertEquals(0.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				case "RPV":
					assertEquals(0.0, (Double) condition.getEvaluator().getResult(), 1e-6);
					break;
				}
			}
		}

	}

}
