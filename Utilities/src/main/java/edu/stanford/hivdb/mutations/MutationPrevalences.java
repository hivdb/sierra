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

package edu.stanford.hivdb.mutations;

import java.net.URL;
import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.stanford.hivdb.utilities.Cachable;

public class MutationPrevalences {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	private static final String DB_URL_TPL =
		"http://hivdb.stanford.edu/download/treatmentProfilesBySubtype/%s." +
		"MixturesIncludedYes.UnpublishedIncludedNo.MoreThan0.1Perc." +
		"MoreThan1Occurrence.PatientCount.txt";

	private static final String PR_PREVALENCE_DB_URL = String.format(DB_URL_TPL, "PI");
	private static final String IN_PREVALENCE_DB_URL = String.format(DB_URL_TPL, "INI");
	private static final String RT_PREVALENCE_DB_URL = String.format(DB_URL_TPL, "RTI");

	/*
	 * Class representing the frequency of mutations for treated and naive individuals
	 * for one particular mutation and subtype.
	 */
	public static class MutationPrevalence {
		public final Mutation mutation;
		public final String subtype;
		public final Integer totalNaive;
		public final Integer totalTreated;
		public final Integer frequencyNaive;
		public final Integer frequencyTreated;
		public final Double percentageNaive;
		public final Double percentageTreated;

		public MutationPrevalence(
				Mutation mutation, String subtype,
				int totalNaive, int freqNaive, double pcntNaive,
				int totalTreated, int freqTreated, double pcntTreated) {
			this.mutation = mutation;
			this.subtype = subtype;
			this.totalNaive = totalNaive;
			this.totalTreated = totalTreated;
			this.frequencyNaive = freqNaive;
			this.frequencyTreated = freqTreated;
			this.percentageNaive = pcntNaive;
			this.percentageTreated = pcntTreated;
		}

		public MutationPrevalence(
				Mutation mutation, String subtype, int totalNaive, int totalTreated) {
			this(mutation, subtype, totalNaive, 0, 0.0, totalTreated, 0, 0.0);
		}
		
		public String getAA() {
			return mutation.getAAs();
		}

		@Override
		public String toString() {
			return String.format(
				"%s %s %d %d %d %d %f %f",
				mutation, subtype, totalNaive, totalTreated, frequencyNaive,
				frequencyTreated, percentageNaive, percentageTreated);
		}

		public boolean isRare() {
			return this.percentageNaive < 0.1 && this.percentageTreated < 0.1;
		}
	}

	// map from mutationId -> mutationPrevalence
	@Cachable.CachableField
	private static List<MutationPrevalence> mutationPrevalences;

	private static transient
		Map<GenePosition, List<MutationPrevalence>> mutationPrevalenceByGenePositions;
	private static transient
		Map<Gene, Map<String, Integer[]>> numPatients;
	private static transient List<String> allTypes;

	static {
		Cachable.setup(MutationPrevalences.class, () -> {
			try {
				populateMutationPrevalenceStore();
			} catch (IOException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
		// This map is an index of mutationPrevalences
		mutationPrevalenceByGenePositions = mutationPrevalences
			.stream()
			.filter(mp -> !mp.isRare())
			.collect(Collectors.groupingBy(
				mp -> mp.mutation.getGenePosition()));
		numPatients = mutationPrevalences
			.stream()
			.collect(Collectors.groupingBy(
				mp -> mp.mutation.getGene(),
				Collectors.toMap(
					mp -> mp.subtype,
					mp -> (new Integer[] {mp.totalNaive, mp.totalTreated}),
					(left, right) -> (new Integer[] {
						Math.max(left[0], right[0]),
						Math.max(left[1], right[1])
					})
				)
			));
		allTypes = mutationPrevalences
			.stream()
			.map(mp -> mp.subtype)
			.distinct()
			.collect(Collectors.toList());
	}

	public static Map<Gene, Map<String, Integer[]>> getNumPatients() {
		return numPatients;
	}
	
	public static List<String> getAllTypes() {
		return allTypes;
	}

	/* Returns all MutationPrevalences at the same position of given mutation.
	 */
	public static List<MutationPrevalence> getPrevalenceAtSamePosition(Mutation mutation) {
		return mutationPrevalenceByGenePositions
			.getOrDefault(mutation.getGenePosition(), new ArrayList<>());
	}
	
	public static Map<Mutation, List<MutationPrevalence>>
			groupPrevalenceByPositions(MutationSet mutations) {
		return mutations
			.stream()
			.collect(Collectors.toMap(
				mut -> mut,
				mut -> getPrevalenceAtSamePosition(mut),
				(m1, m2) -> m1, // MutationSet filters duplicates. So I don't believe this function will ever be called. 
				TreeMap::new
			));
	}

	protected static void readPrevalenceFile(URL prevalenceDb, Gene gene) throws IOException {
		Scanner scanner = new Scanner(prevalenceDb.openStream());
		String[] colHeaders;
		String firstLine = scanner.nextLine();
		colHeaders = firstLine.split("\t");
		int treatedOffset = (colHeaders.length - 3) / 2;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] columns = line.split("\t");
			int pos = Integer.parseInt(columns[0]);
			String mut = columns[2];
			Mutation mutation = new AAMutation(gene, pos, mut.toCharArray(), 0xff);

			for (int col = 3; col < 3 + treatedOffset; col += 3) {
				String[] colParts = colHeaders[col].split(":");
				String subtype = colParts[0]; // e.g. A or All

				int i = col;
				int totalNaive = Integer.parseInt("0" + columns[i]);
				int freqNaive = Integer.parseInt("0" + columns[++ i]);
				double pcntNaive = Double.parseDouble("0" + columns[++ i]);

				i = col + treatedOffset;
				int totalTreated = Integer.parseInt("0" + columns[i]);
				int freqTreated = Integer.parseInt("0" + columns[++ i]);
				double pcntTreated = Double.parseDouble("0" + columns[++ i]);

				MutationPrevalence mp = new MutationPrevalence(
					mutation, subtype, totalNaive, freqNaive,
					pcntNaive, totalTreated, freqTreated, pcntTreated);
				mutationPrevalences.add(mp);
			}
		}
		scanner.close();
	}

	/* update the mutation prevalence store. */
	protected static void populateMutationPrevalenceStore() throws IOException {
		mutationPrevalences = new ArrayList<>();
		readPrevalenceFile(new URL(PR_PREVALENCE_DB_URL), Gene.PR);
		readPrevalenceFile(new URL(IN_PREVALENCE_DB_URL), Gene.IN);
		readPrevalenceFile(new URL(RT_PREVALENCE_DB_URL), Gene.RT);
	}
}