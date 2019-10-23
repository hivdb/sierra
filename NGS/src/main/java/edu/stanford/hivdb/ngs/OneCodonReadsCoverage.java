package edu.stanford.hivdb.ngs;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.GenePosition;

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
	
	public GenePosition getGenePosition() {
		return new GenePosition(gene, (int) position);
	}
	
	public Long getTotalReads() {
		return totalReads;
	}
	
	public Boolean isTrimmed() {
		return isTrimmed;
	}
	
	public Integer getPolPosition() {
		return getGenePosition().getPolPosition();
	}
	
}