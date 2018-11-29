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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.ProgramProperties;

/**
 * Applies a set of drug resistance interpretation rules (algorithm)
 *   to a list of mutations in a gene.
 * The rules are specified in an XML schema and interpreted using a high-level language
 *   we developed called an Algorithm Specification Interface (ASI)
 * This class is called when we are interested in the ANRS algorithm. Although the ASI
 *   is meant to be agnostic to the specific set of rules or algorithm, it is easier to
 *   parse the results of the ASI when we know whether the rules comprise conditions that evaluate
 *   to true or false (the case for ANRS) vs. conditions that evaluate to scores (the case for HIVDB)
 *
 * Data structures:
 *   drugLevels: drugName=>level
 *   drugTriggeredRules: drugName=>rule=>level
 */

public class AsiAnrs extends AsiBase {
	private static final String resourcePath = ProgramProperties.getProperty("ASIFileANRS");
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The class is instantiated by a gene and list of mutation in a sequence.
	 * asiGene is an object created by the XMLAsiTransformer that contains all of the
	 *   specification and rules in the algorithm for the submitted gene
	 * evaluatedGene is an object that contains all of the results obtained by applying
	 *   the asiGene rules to the submitted mutations
	 *
	 * @param submittedGene
	 * @param seqMutations
	 */
	public AsiAnrs (Gene submittedGene, MutationSet seqMutations) {
		super(submittedGene, seqMutations, resourcePath);
	}

	@Override
	public String getAlgorithmName() {
		return "ANRS";
	}
}