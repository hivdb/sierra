/*

	Copyright (C) 2019 Stanford HIVDB team

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

public class CodonReads {
	private final String codon;
	private final long reads;
	private final long totalReads;
	private final Gene gene;
	private final int position;
	private final double proportion;
	private transient Mutation mutation;
	private transient Character aminoAcid;
	
	public static String normalizeCodon(String codon) {
		// Tolerant spaces, commas, colons, semicolons and dashes
		return codon.replaceAll("[ ,:;-]", "");
	}

	public CodonReads(
		final Gene gene, final int position,
		final String codon, final long reads, final long totalReads
	) {
		this.gene = gene;
		this.position = position;
		this.codon = normalizeCodon(codon);
		this.reads = reads;
		this.totalReads = totalReads;
		this.proportion = (double) reads / totalReads;
	}
	
	public String getCodon() { return codon; }
	public Long getReads() { return reads; }
	public Long getTotalReads() { return totalReads; }
	
	public Character getAminoAcid() {
		if (aminoAcid == null) {
			if (!codon.matches("^[ACGT]*$")) {
				// do not allow ambiguous codes
				aminoAcid = 'X';
			}
			if (codon.length() > 5) {
				aminoAcid = '_';  // insertion
			}
			else if (codon.length() < 3) {
				aminoAcid = '-';  // deletion
			}
			else {
				String aminoAcids = CodonTranslation.translateNATriplet(codon.substring(0, 3));
				if (aminoAcids.length() > 1) {
					// Ambiguous codon should not happen in NGS codons
					aminoAcid = 'X';
				}
				else {
					aminoAcid = aminoAcids.charAt(0);
				}
			}
		}
		return aminoAcid;
	}

	private Mutation getMutation() {
		if (mutation == null) {
			mutation = new AAMutation(gene, position, getAminoAcid());
		}
		return mutation;
	}
	
	public Double getProportion() {
		return this.proportion;
	}
	
	public Double getPrevalence() {
		return AAMutation.getPrevalence(gene, position, getAminoAcid()) * 100;
	}
	
	public boolean isReference() {
		return getAminoAcid() == gene.getRefChar(position);
	}
	
	public boolean isApobecMutation() {
		return isReference() ? false : getMutation().isApobecMutation();
	}
	
	public boolean isApobecDRM() {
		return isReference() ? false : getMutation().isApobecDRM();
	}
	
	public boolean isUnusual() {
		return isReference() ? false : getMutation().isUnusual();
	}
	
	public boolean isDRM() {
		return isReference() ? false : getMutation().isDRM();
	}

}