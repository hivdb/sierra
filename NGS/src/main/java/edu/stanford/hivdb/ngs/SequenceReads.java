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
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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
	
	public static SequenceReads fromCodonReadsTable(
			String name, List<PositionCodonReads> allReads, Double minPrevalence) {
		// TODO: dynamic cutoff
		double finalMinPrevalence = minPrevalence >= 0 ? minPrevalence : (double) 0.05;
		EnumMap<Gene, GeneSequenceReads> geneSequences = allReads.stream()
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
		
		return new SequenceReads(name, geneSequences, finalMinPrevalence);
	}
	
	public SequenceReads(
			final String name,
			final EnumMap<Gene, GeneSequenceReads> allGeneSequenceReads,
			final double minPrevalence) {
		this.name = name;
		this.allGeneSequenceReads = allGeneSequenceReads;
		this.minPrevalence = minPrevalence;
	}
	
	public String getName() { return name; }
	
	public boolean isEmpty() { return allGeneSequenceReads.isEmpty(); }
	
	public double getMinPrevalence() { return minPrevalence; }
	
	public List<GeneSequenceReads> getAllGeneSequenceReads() {
		return new ArrayList<>(allGeneSequenceReads.values());
	}
	
	public GeneSequenceReads getGeneSequenceReads(Gene gene) {
		return allGeneSequenceReads.get(gene);
	}
	
	public List<Gene> getAvailableGenes() {
		return new ArrayList<>(allGeneSequenceReads.keySet());
	}
	
	public List<MutationStats> getMutationStats(List<Double> allMinPrevalence) {
		return allMinPrevalence.stream().map(
			mp -> new MutationStats(mp, getMutations(mp))
		).collect(Collectors.toList());
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
			return null;
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
