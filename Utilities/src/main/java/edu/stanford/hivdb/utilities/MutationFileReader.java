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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class MutationFileReader {
	
	private static final Pattern MUT_PATTERN = Mutation.getPattern();

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
		List<MutationSet> fileMuts = new ArrayList<>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
			String line;
			while ((line = br.readLine()) != null) {
				if (shouldSkip(line)) continue;
				List<Mutation> lineMuts = Stream.of(line.trim().split(","))
					.filter(mutStr -> MUT_PATTERN.matcher(mutStr).find())
					.map(mutStr -> Mutation.parseString(mutStr))
					.collect(Collectors.toList());
				if (!lineMuts.isEmpty()) {
					fileMuts.add(new MutationSet(lineMuts));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileMuts;
	}

	protected static boolean shouldSkip(String line) {
		return line.isEmpty() || line.startsWith("#");
	}
}