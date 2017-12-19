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
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;

/**
 * Applies a set of drug resistance interpretation rules (algorithm) to a list of mutations in a gene.
 * The rules are specified in an XML schema and interpreted using a high-level language
 *   we developed called an Algorithm Specification Interface (ASI)
 * This class is called when we are interested in the HIVDB algorithm. Although the ASI
 *   is meant to be agnostic to the specific set of rules or algorithm, for now we have separate classes
 *   for different algorithms because many aspects of the algorithms differ
 *
 *   TODO: Should the constructor should receive the appropriate version???
 *
 * Data structures:
 *   drugClassDrugMutScores: DrugClass => Drug => Mutation => score
 *   drugClassDrugComboMutScores: DrugClass => Drug => List<Mutation=> score
 *   mutationComments: Mutation => comment
 */
public class AsiHivdb extends AsiBase implements Asi {
	//private final HivdbVersion currentVersion;
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	private final HivdbVersion hivdbVersion;
	private String algorithmName;

	/**
	 * Instantiated by a gene and list of mutations in a sequence.
	 * asiGene is an object created by the XMLAsiTransformer that contains all of the
	 *   specifications and rules in the algorithm for the submitted gene
	 * evaluatedGene is an object that contains all of the results obtained by applying
	 *   the asiGene rules to the submitted mutations
	 *
	 * @param submittedGene, seqMutations
	 */
	public AsiHivdb (Gene submittedGene, MutationSet mutations) {
		this(submittedGene, mutations, HivdbVersion.getLatestVersion());
		algorithmName = "HIVDB";
	}

	public AsiHivdb (Gene submittedGene, MutationSet mutations, HivdbVersion version) {
		super(submittedGene, mutations, version.resourcePath);
		hivdbVersion = version;
	}

	@Override
	public String getAlgorithmName() {
		if (algorithmName == null) {
			algorithmName = String.format("HIVDB_%s", hivdbVersion);
		}
		return algorithmName;
	}

}
