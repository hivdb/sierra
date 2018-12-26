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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import edu.stanford.hivdb.drugresistance.algorithm.Asi.AsiDrugComparableResult;
import edu.stanford.hivdb.drugresistance.algorithm.Asi.SIREnum;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;

public class AlgorithmComparison {

	public static class ComparableDrugScore {
		public Drug drug;
		public String algorithm;
		public SIREnum SIR;
		public String interpretation;
		public String explanation;

		public ComparableDrugScore(
				Drug drug, String alg, SIREnum SIR,
				String interpretation, String explanation) {
			this.drug = drug;
			this.algorithm = alg;
			this.SIR = SIR;
			this.interpretation = interpretation;
			this.explanation = explanation;
		}

		@Override
		public String toString() {
			return String.format("%s (%s): %s", drug, algorithm, SIR);
		}

		@Override
			public boolean equals(Object o) {
				if (o == this) { return true; }
				if (o == null) { return false; }
				if (!(o instanceof ComparableDrugScore)) { return false;}
				ComparableDrugScore ds = (ComparableDrugScore) o;

				// isDeletion and isInsertion is related to aas
				return new EqualsBuilder()
					.append(drug, ds.drug)
					.append(algorithm, ds.algorithm)
					.append(SIR, ds.SIR)
					.append(interpretation, ds.interpretation)
					.append(explanation, ds.explanation)
					.isEquals();
			}

			@Override
			public int hashCode() {
				return new HashCodeBuilder(24757, 43569)
					.append(drug)
					.append(algorithm)
					.append(SIR)
					.append(interpretation)
					.append(explanation)
					.toHashCode();
			}
	}

	@SuppressWarnings("unused")
	private String[] algorithms;
	private List<ComparableDrugScore> comparisonResults = new ArrayList<>();
	private final transient Map<Gene, List<Asi>> asiListMap;
	private static Map<Algorithm, Class<? extends Asi>> algorithmAsiClass = new EnumMap<>(Algorithm.class);

	static {
		algorithmAsiClass.put(Algorithm.HIVDB, AsiHivdb.class);
		algorithmAsiClass.put(Algorithm.ANRS, AsiAnrs.class);
		algorithmAsiClass.put(Algorithm.REGA, AsiRega.class);

	}

	public List<ComparableDrugScore> getComparisonResults() { return comparisonResults; }

	public AlgorithmComparison (Map<Gene, MutationSet> allMutations, Collection<Algorithm> algorithms) {
		this.algorithms = algorithms.stream().map(Algorithm::name).toArray(String[]::new);
		// System.out.println(allMutations);
		this.asiListMap = new LinkedHashMap<>();
		for (Gene gene : allMutations.keySet()) {
			final MutationSet mutations = allMutations.get(gene);
			asiListMap.put(gene, calcAsiListFromAlgorithms(gene, mutations, algorithms));
			compareResults(gene);
		}
	}

	public AlgorithmComparison (Map<Gene, List<Asi>> asiListMap) {
		this.asiListMap = asiListMap;
		List<String> algorithmList = new ArrayList<>();
		for (Gene gene : this.asiListMap.keySet()) {
			compareResults(gene);
			if (algorithmList.isEmpty()) {
				algorithmList = asiListMap.get(gene)
					.stream()
					.map(Asi::getAlgorithmName)
					.collect(Collectors.toList());
			}
		}
		this.algorithms = algorithmList.toArray(new String[0]);
	}

	public AlgorithmComparison (
			Map<Gene, MutationSet> allMutations,
			HivdbVersion... versions) {
		this.algorithms = Arrays.stream(versions)
			.map(HivdbVersion::getFullName).toArray(String[]::new);
		this.asiListMap = new LinkedHashMap<>();
		for (Gene gene : allMutations.keySet()) {
			final MutationSet mutations = allMutations.get(gene);
			this.asiListMap.put(
				gene,
				calcAsiHivdbListFromVersions(
					gene, mutations, Arrays.asList(versions)));
			compareResults(gene);
		}
	}

	public List<Asi> getAsiList(Gene gene) {
		return this.asiListMap.get(gene);
	}

	public static List<Asi> calcAsiListFromAlgorithms(
			Gene gene, MutationSet mutations, Collection<Algorithm> algorithms) {
		return algorithms
			.stream()
			.map(alg -> {
				try {
					Class<?> klass = algorithmAsiClass.get(alg);
					return (Asi) klass
						.getDeclaredConstructor(Gene.class, MutationSet.class)
						.newInstance(gene, mutations);
				}
				catch (InvocationTargetException e) {
					System.out.println(e.getTargetException());
					throw new RuntimeException(e.getTargetException());
				}
				catch (IllegalAccessException | InstantiationException |
						NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toList());
	}

	public static List<Asi> calcAsiListFromCustomAlgorithms(
			Gene gene, MutationSet mutations, Map<String, String> customAlgorithms) {
		return customAlgorithms
			.entrySet()
			.stream()
			.map(e -> new AsiCustom(gene, mutations, e.getKey(), e.getValue()))
			.collect(Collectors.toList());
	}

	public static List<Asi> calcAsiHivdbListFromVersions(
			Gene gene, MutationSet mutations, Collection<HivdbVersion> versions) {
		return versions
			.stream()
			.map(ver -> new AsiHivdb(gene, mutations, ver))
			.collect(Collectors.toList());
	}

	private void compareResults (Gene gene) {

		for (Asi asiObj : this.asiListMap.get(gene)) {
			String algName = asiObj.getAlgorithmName();

			for (DrugClass drugClass : gene.getDrugClasses()) {

				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					AsiDrugComparableResult result = asiObj.getDrugComparableResult(drug);
					comparisonResults.add(new ComparableDrugScore(
						drug, algName, result.SIR,
						result.Interpretation, result.Explanation));
				}
			}
		}
	}
}
