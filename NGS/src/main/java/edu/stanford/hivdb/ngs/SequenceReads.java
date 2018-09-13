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

package edu.stanford.hivdb.ngs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeReference;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.SeqUtils;

public class SequenceReads {

	private final static int HXB2_PR_FIRST_NA = 2253;
	private final EnumMap<Gene, GeneSequenceReads> allGeneSequenceReads;
	private String name;
	private HIVGenotypeResult subtypeResult;
	private MutationSet mutations;
	private String concatenatedSeq;
	private Double mixturePcnt;
	private Double minPrevalence;
	private Double medianReadDepth;
	private Long minReadDepth;
	
	public static SequenceReads fromCodonReadsTable(
			String name, List<PositionCodonReads> allReads,
			Double minPrevalence, Long minReadDepth) {
		// TODO: dynamic cutoff
		double finalMinPrevalence = minPrevalence >= 0 ? minPrevalence : (double) 0.05;
		long finalMinReadDepth = minReadDepth > 0 ? minReadDepth : (long) 1000;
		EnumMap<Gene, GeneSequenceReads> geneSequences = allReads.stream()
			// remove all codons with their read depth < minReadDepth
			.filter(read -> read.getTotalReads() >= finalMinReadDepth)
			.collect(
				Collectors.groupingBy(
					PositionCodonReads::getGene,
					() -> new EnumMap<>(Gene.class),
					Collectors.collectingAndThen(
						Collectors.toList(),
						list -> new GeneSequenceReads(list, finalMinPrevalence)
					)
				)
			);

		Median median = new Median();
		double[] ReadDepths = (
			allReads.stream()
			.filter(read -> read.getTotalReads() >= finalMinReadDepth)
			.mapToDouble(read -> read.getTotalReads())
			.toArray()
		);
		double medianReadDepth = -1;
		if (ReadDepths.length > 0) {
			medianReadDepth = median.evaluate(ReadDepths);
		}
		
		return new SequenceReads(
				name, geneSequences,
				finalMinPrevalence, finalMinReadDepth,
				medianReadDepth);
	}
	
	public SequenceReads(
			final String name,
			final EnumMap<Gene, GeneSequenceReads> allGeneSequenceReads,
			final double minPrevalence, final long minReadDepth,
			final double medianReadDepth) {
		this.name = name;
		this.allGeneSequenceReads = allGeneSequenceReads;
		this.minPrevalence = minPrevalence;
		this.minReadDepth = minReadDepth;
		this.medianReadDepth = medianReadDepth;
	}
	
	public String getName() { return name; }
	
	public boolean isEmpty() { return allGeneSequenceReads.isEmpty(); }
	
	public double getMinPrevalence() { return minPrevalence; }
	
	public long getMinReadDepth() { return minReadDepth; }

	public Double getMedianReadDepth() { return medianReadDepth; }
	
	public List<GeneSequenceReads> getAllGeneSequenceReads() {
		return new ArrayList<>(allGeneSequenceReads.values());
	}
	
	public GeneSequenceReads getGeneSequenceReads(Gene gene) {
		return allGeneSequenceReads.get(gene);
	}
	
	public List<Gene> getAvailableGenes() {
		return new ArrayList<>(allGeneSequenceReads.keySet());
	}
	
	public List<MutationStats> getMutationStats(Collection<Double> allMinPrevalence) {
		return allMinPrevalence.stream().map(
			mp -> new MutationStats(mp, getMutations(mp))
		).collect(Collectors.toList());
	}
	
	public List<MutationStats> getAllMutationStats() {
		Set<Double> allMinPrevalence = allGeneSequenceReads.values().stream()
			.map(gs -> gs.getPrevalencePoints())
			.reduce(new TreeSet<>(), (s1, s2) -> {
				s1.addAll(s2); return s1;
			});
		return getMutationStats(allMinPrevalence);
	}
	
	public String getConcatenatedSeq() {
		if (concatenatedSeq == null) {
			StringBuilder concatSeq = new StringBuilder();
			for (Gene gene : Gene.values()) {
				GeneSequenceReads geneSeq = allGeneSequenceReads.get(gene);
				if (geneSeq == null) {
					concatSeq.append(StringUtils.repeat("...", gene.getLength()));
				} else {
					concatSeq.append(geneSeq.getAlignedNAs(true));
				}
			}
			concatenatedSeq = concatSeq.toString();
		}
		return concatenatedSeq;
	}
	
	public MutationSet getMutations(final double minPrevalence) {
		if (!isEmpty()) {
			return allGeneSequenceReads.values().stream()
				.map(gs -> gs.getMutations(minPrevalence))
				.reduce((m1, m2) -> m1.mergesWith(m2))
				.get();
		} else {
			return new MutationSet();
		}
	}

	public MutationSet getMutations() {
		if (!isEmpty() && mutations == null) {
			mutations = getMutations(this.minPrevalence);
		}
		return mutations;
	}

	public HIVGenotypeResult getSubtypeResult() {
		if (!isEmpty() && subtypeResult == null) {
			subtypeResult = HIVGenotypeReference.compareAll(
				getConcatenatedSeq(), HXB2_PR_FIRST_NA);
		}
		return subtypeResult;
	}

	public BoundGenotype getBestMatchingSubtype() {
		if (isEmpty()) {
			return null;
		}
		return getSubtypeResult().getBestMatch();
	}

	public String getSubtypeText() {
		if (isEmpty()) {
			return "NA";
		}
		return getBestMatchingSubtype().getDisplay();
	}

	public double getMixturePcnt() {
		if (mixturePcnt == null) {
			StringBuilder concatSeq = new StringBuilder();
			for (GeneSequenceReads geneSeqReads : allGeneSequenceReads.values()) {
				concatSeq.append(geneSeqReads.getAlignedNAs(false));
			}
			mixturePcnt = SeqUtils.mixturePcnt(concatSeq.toString());
		}
		return mixturePcnt;
	}

}
