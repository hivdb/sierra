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

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Helper class mainly used to build mutation search index.
 *
 * Many mutation-related indices use gene and position as their index
 * key. This class instantiates hashable and comparable objects using
 * value gene and pos.
 */
public class GenePosition implements Comparable<GenePosition> {
	public final Gene gene;
	public final Integer position;
	
	public static Set<GenePosition> getGenePositionsBetween(
		final GenePosition start, final GenePosition end
	) {
		if (start.gene.getStrain() != end.gene.getStrain()) {
			throw new IllegalArgumentException(
				"Virus strain of `start` and `end` positions must be the same."
			);
		}
		Strain strain = start.gene.getStrain();
		Set<GenePosition> genePositions = new LinkedHashSet<>();
		for (Gene gene : strain.getGenes()) {
			int startPos = 1;
			int endPos = gene.getLength();
			if (gene.compareTo(start.gene) == 0) {
				startPos = start.position;
			}
			if (gene.compareTo(end.gene) == 0) {
				endPos = end.position;
			}
			if (gene.compareTo(start.gene) >= 0 &&
				gene.compareTo(end.gene) <= 0) {
				genePositions.addAll(
					gene.getGenePositionsBetween(startPos, endPos));
			}
		}
		return genePositions;
		
	}

	public GenePosition(final Gene gene, final int pos) {
		this.gene = gene;
		this.position = pos;
	}

	public GenePosition(final String text) {
		String[] strainGenePos = text.split(":", 2);
		this.gene = Gene.valueOf(strainGenePos[0]);
		this.position = Integer.parseInt(strainGenePos[1]);
	}

	public Integer getPolPosition() {
		// internal function, don't expose to GraphQL
		int absPos;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) { return false; }
		GenePosition gp = (GenePosition) obj;
		return new EqualsBuilder()
			.append(gene, gp.gene)
			.append(position, gp.position)
			.isEquals();
	}

	@Override
	public int compareTo(GenePosition o) {
		if (o == null) throw new NullPointerException("Null is incomprable.");
		int cmp = gene.compareTo(o.gene);
		if (cmp == 0) {
			cmp = Integer.valueOf(position).compareTo(o.position);
		}
		return cmp;
	}

	@Override
	public String toString() {
		return String.format("%s:%d", gene, position);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(63261, 362788935)
			.append(gene).append(position).toHashCode();
	}
}
