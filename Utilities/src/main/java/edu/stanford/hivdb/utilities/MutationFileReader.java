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

package edu.stanford.hivdb.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class MutationFileReader {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Pattern mutPatWithGene = Pattern.compile("([\\D]*)([\\d]*)([\\D]*)");

	/**
	 * Reads lists of mutations, one line at a time, from a comma-delimited file
	 *   and returns a list of mutation objects.
	 * Each mutation consists of a gene, position, and one or more amino acids.
	 *   There is no consensus amino acid preceding the position
	 *
	 * @param fileInputStream
	 * @return List<Mutation>
	 */
	public static List<MutationSet> readMutationLists(InputStream fileInputStream) {
		LOGGER.debug("In readMutationListsAllGenes");
		List<MutationSet> mutationLists = new ArrayList<>();

		try {	
			BufferedReader br = new BufferedReader (new InputStreamReader(fileInputStream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0 || line.substring(0,1).equals("#")){
					continue;
				}
				line = StringUtils.deleteWhitespace(line);
				LOGGER.debug("  line:" + line);

				List<Mutation> seqMutList = new ArrayList<>();
				String [] seqMutStrings = line.split(",");

				for (String mutStr : seqMutStrings) { // per mutation
					//System.out.println(mutStr);
					Matcher m = mutPatWithGene.matcher(mutStr);
					if (m.find()) {

						Gene gene = Gene.valueOf(m.group(1));
						int pos = Integer.valueOf(m.group(2));
						String mutAA = m.group(3);
						mutAA = mutAA.replaceAll("#","_");
						mutAA = mutAA.replaceAll("~", "-");
						Mutation mut = new Mutation(gene,pos,mutAA);
						seqMutList.add(mut);
					}
				}
				mutationLists.add(new MutationSet(seqMutList));
			}
			br.close();

		} catch (FileNotFoundException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.debug("\n");
		return mutationLists;
	}


	/**
	 * Reads in a list of mutations on a single line. This is used primarily for testing purposes
	 * The expected mutation format for this reader an optional consensus followed by Pos . AAs separated by commas
	 * The gene is known because the drugClass is passed in as a parameter
	 * @param filePath Path to the file to read in
	 * @return List of a List of mutation sequences
	 */
	public static List<MutationSet> readMutationListsForDrugClass(DrugClass drugClass, InputStream fileInputStream) {
		LOGGER.debug("In readMutationLists");
		Gene gene = drugClass.gene();
		List<MutationSet> mutationLists = new ArrayList<>();

		try {
			BufferedReader br = new BufferedReader (new InputStreamReader(fileInputStream));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0 || line.substring(0,1).equals("#")){
					continue;
				}
				// For now these files have headers beginning with ">" which will be skipped
				if (line.substring(0,1).equals(">")){
					continue;
				}
				line = StringUtils.strip(line);
				LOGGER.debug("  line:" + line);

				MutationSet seqMutList = new MutationSet(gene, line);
				mutationLists.add(seqMutList);
			}
			br.close();

		} catch (FileNotFoundException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.debug("\n");
		return mutationLists;
	}



}
