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

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PositionCodonReads {

	private final Gene gene;
	private final int position;
	private final long totalReads;
	private final Map<String, Long> allCodonReads;

	public PositionCodonReads(
		final Gene gene,
		final int position,
		final long totalReads,
		final Map<String, Long> allCodonReads) {
		this.gene = gene;
		this.position = position;
		this.totalReads = totalReads;
		this.allCodonReads = allCodonReads.entrySet().stream()
			.sorted(Comparator.comparingLong(Map.Entry<String, Long>::getValue).reversed())
			.collect(Collectors.toMap(
				e -> e.getKey(),
				e -> e.getValue(),
				(e1, e2) -> e1,
				LinkedHashMap::new));
	}

	public Gene getGene() { return gene; }
	public long getPosition() { return position; }
	public GenePosition getGenePositon() { return new GenePosition(gene, position); }
	public long getTotalReads() { return totalReads; }
	public List<CodonReads> getCodonReads() {
		return allCodonReads.entrySet().stream()
			.map(e -> new CodonReads(gene, position, e.getKey(), e.getValue(), totalReads))
			.collect(Collectors.toList());
	}

	public Map<String, Double> getCodonWithPrevalence(double minPrevalence) {
		long minReads = Math.round(totalReads * minPrevalence + 0.5);
		return allCodonReads.entrySet().stream()
			.filter(e -> e.getValue() > minReads)
			.collect(Collectors.toMap(
				e -> e.getKey(),
				e -> e.getValue() * 100.0 / totalReads,
				(e1, e2) -> e1,
				LinkedHashMap::new));
	}

	public String getCodonConsensus(double minPrevalence) {
		List<String> codons = (
			getCodonWithPrevalence(minPrevalence).keySet().stream()
			.map(cd -> cd.substring(0, 3))
			.collect(Collectors.toList()));
		if (codons.isEmpty()) {
			// do not return null
			return "NNN";
		}
		return CodonTranslation.getMergedCodon(codons);
	}
}
