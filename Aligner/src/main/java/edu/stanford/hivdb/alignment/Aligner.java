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

package edu.stanford.hivdb.alignment;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.utilities.Sequence;

/**
 * This is the entry point for the Aligner project.
 *
 *
 */
public class Aligner
{

	/**
	 * Receives a nucleotide sequence and aligns to the submitted reference gene sequence:
	 * PR, RT, or IN (found in the Gene enum class). Returns null if the gene is not present.
	 * @param gene
	 * @param sequence
	 * @return AlignedGeneSeq object
	 */
	@Deprecated
	public static AlignedGeneSeq alignGeneToSequence (Gene gene, Sequence sequence) {
		return NucAminoAligner.align(sequence).getAlignedGeneSequence(gene);
	}

	/**
	 * Receives a sequence and aligns it to each HIV gene. Creates a map of each
	 * gene to an alignedGeneSeq object if the gene is present.
	 * @param sequence
	 * @return map of genes to alignedGeneSeqs
	 */
	@Deprecated
	public static Map<Gene, AlignedGeneSeq> alignGenesToSequence (Sequence sequence) {
		return NucAminoAligner.align(sequence).getAlignedGeneSequenceMap();
	}

	/**
	 * Receives set of sequences and aligns them to each HIV gene in parallel.
	 *
	 * Note: this method doesn't guarantee the input order of the sequences (if
	 * you used LinkedHashSet).
	 *
	 * @Param sequences
	 * @return map of sequences to map of genes to alignedGeneSeqs
	 */
	@Deprecated
	public static Map<Sequence, Map<Gene, AlignedGeneSeq>>
			parallelAlign(Set<Sequence> sequences) {
		return NucAminoAligner
			.parallelAlign(sequences)
			.stream()
			.collect(Collectors.toMap(
				as -> as.getInputSequence(),
				as -> as.getAlignedGeneSequenceMap()));
	}

	/**
	 * Receives set of sequences and aligns them to each HIV gene in parallel.
	 *
	 * @Param sequences
	 * @return list of AlignedSequence objects
	 */
	public static List<AlignedSequence> parallelAlign(List<Sequence> sequences) {
		return NucAminoAligner.parallelAlign(sequences);
	}


}
