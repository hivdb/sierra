package edu.stanford.hivdb.ngs;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Strain;

public class OneCodonReadsCoverage {
	private final Gene gene;
	private final long position;
	private final long totalReads;
	private final boolean isTrimmed;
	
	public OneCodonReadsCoverage(
		final Gene gene,
		final long position,
		final long totalReads,
		final boolean isTrimmed
	) {
		this.gene = gene;
		this.position = position;
		this.totalReads = totalReads;
		this.isTrimmed = isTrimmed;
	}
	
	public Gene getGene() {
		return gene;
	}
	
	public Long getPosition() {
		return position;
	}
	
	public Long getTotalReads() {
		return totalReads;
	}
	
	public Boolean isTrimmed() {
		return isTrimmed;
	}
	
	public Long getPolPosition() {
		// internal function, don't expose to GraphQL
		long absPos;
		Strain strain = gene.getStrain();
		switch(gene.getGeneEnum()) {
			case PR:
				absPos = position;
				break;
			case RT:
				absPos = Gene.valueOf(strain, "PR").getLength() + position;
				break;
			default:  // case IN
				absPos = (
					Gene.valueOf(strain, "PR").getLength() +
					Gene.valueOf(strain, "RT").getLength() +
					position);
				break;
		}
		return absPos;
	}
	
}