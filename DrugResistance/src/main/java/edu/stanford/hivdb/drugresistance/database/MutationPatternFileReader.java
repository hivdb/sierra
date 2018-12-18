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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.MutationFileReader;

public class MutationPatternFileReader extends MutationFileReader {
	
	/**
	 * Reads in a list of mutations on a single line. This is used primarily for testing purposes
	 * The expected mutation format for this reader an optional consensus followed by Pos . AAs separated by commas
	 * The gene is known because the drugClass is passed in as a parameter
	 * @param filePath Path to the file to read in
	 * @return List of MutationSets
	 */
	public static List<MutationSet> readMutationListsForDrugClass(DrugClass drugClass, InputStream fileInputStream) {
		Gene gene = drugClass.gene();
		List<MutationSet> mutationLists = new ArrayList<>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(">") || shouldSkip(line)) continue;
				mutationLists.add(new MutationSet(gene, line.trim()));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mutationLists;
	}
}
