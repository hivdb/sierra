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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.primitives.Chars;

import edu.stanford.hivdb.aapcnt.HIVAminoAcidPercents;

public class AAMutation implements Mutation {
	
	private static final HIVAminoAcidPercents allAAPcnts = HIVAminoAcidPercents.getInstance("all", "All");

	private final Gene gene;
	private final int position;
	private final Set<Character> aaChars;
	private final int maxDisplayAAs;
	private transient Character ref;
	private transient List<MutType> types;
	private transient Boolean isAtDrugResistancePosition;
	
	public static Set<Character> normalizeAAChars(Set<Character> aaChars) {
		if (aaChars == null) { return null; }
		aaChars = new TreeSet<>(aaChars);
		if (aaChars.contains('#') || aaChars.contains('i')) {
			aaChars.remove('#');
			aaChars.remove('i');
			aaChars.add('_');
		}
		if (aaChars.contains('~') || aaChars.contains('d')) {
			aaChars.remove('~');
			aaChars.remove('d');
			aaChars.add('-');
		}
		if (aaChars.contains('Z') || aaChars.contains('.')) {
			aaChars.remove('Z');
			aaChars.remove('.');
			aaChars.add('*');
		}
		return Collections.unmodifiableSet(aaChars);
	}
	
	public AAMutation(Gene gene, int position, char aa) {
		this(gene, position, new char[] {aa}, 4);
		
	}
	
	public AAMutation(Gene gene, int position, char[] aaCharArray) {
		this(gene, position, new TreeSet<>(Chars.asList(aaCharArray)), 4);
	}

	public AAMutation(Gene gene, int position, char[] aaCharArray, int maxDisplayAAs) {
		this(gene, position, new TreeSet<>(Chars.asList(aaCharArray)), maxDisplayAAs);
	}

	public AAMutation(Gene gene, int position, Set<Character> aaChars) {
		this(gene, position, aaChars, 4);
	}
	
	public AAMutation(Gene gene, int position, Set<Character> aaChars, int maxDisplayAAs) {
		if (position > gene.getLength()) {
			throw new IllegalArgumentException("Length is out of bounds for this gene.");
		}
		this.gene = gene;
		this.aaChars = normalizeAAChars(aaChars);
		this.position = position;
		this.maxDisplayAAs = maxDisplayAAs;
	}
	
	protected int getMaxDisplayAAs() { return maxDisplayAAs; }

	@Override
	public Mutation mergesWith(Mutation another) {
		if (gene != another.getGene() || position != another.getPosition()) {
			throw new IllegalArgumentException(String.format(
				"The other mutation must be at this position: %d (%s)",
				position, gene.toString()));
		}
		return mergesWith(another.getAAChars());
	}
	
	@Override
	public Mutation mergesWith(Collection<Character> otherAAChars) {
		Set<Character> newAAChars = getAAChars();
		newAAChars.addAll(otherAAChars);
		return new AAMutation(gene, position, newAAChars, maxDisplayAAs);
	}

	@Override
	public Mutation subtractsBy(Mutation another) {
		if (another == null ||
			gene != another.getGene() ||
			position != another.getPosition()
		) {
			// duplicate self
			return new AAMutation(gene, position, getAAChars(), maxDisplayAAs);
		}
		return subtractsBy(another.getAAChars());
	}
	
	@Override
	public Mutation subtractsBy(Collection<Character> otherAAChars) {
		Set<Character> newAAChars = getAAChars();
		newAAChars.removeAll(otherAAChars);
		if (newAAChars.size() == 0) {
			return null;
		}
		return new AAMutation(gene, position, newAAChars, maxDisplayAAs);
	}

	@Override
	public Mutation intersectsWith(Mutation another) {
		if (gene != another.getGene() ||
			position != another.getPosition()
		) {
			throw new IllegalArgumentException(String.format(
				"The other mutation must be at this position: %d (%s)",
				position, gene.toString()));
		}
		return intersectsWith(another.getAAChars());
	}
	
	@Override
	public Mutation intersectsWith(Collection<Character> otherAAChars) {
		Set<Character> newAAChars = getAAChars();
		newAAChars.retainAll(otherAAChars);
		if (newAAChars.size() == 0) {
			return null;
		}
		return new AAMutation(gene, position, newAAChars, maxDisplayAAs);
	}

	@Override
	public final boolean isAtDrugResistancePosition() {
		if (isAtDrugResistancePosition == null) {
			isAtDrugResistancePosition = DRMs.isAtDRPosition(this);
		}
		return isAtDrugResistancePosition;
	}

	@Override
	public boolean isUnsequenced() { return false; }
	
	@Override
	public final Gene getGene() { return gene; }

	@Override
	public final String getReference() {
		return "" + getRefChar();
	}
	
	protected final char getRefChar() {
		if (ref == null) {
			ref = gene.getReference(position).charAt(0);
		}
		return ref;
	}

	@Override
	public final int getPosition() {return position; }

	@Override
	public final GenePosition getGenePosition() {
		return new GenePosition(gene, position);
	}

	@Override
	public String getDisplayAAs() {
		if (aaChars.size() > maxDisplayAAs) {
			return "X";
		}
		return StringUtils.join(aaChars.toArray());
	}

	@Override
	public final Set<Character> getDisplayAAChars() {
		Set<Character> myAAChars = new TreeSet<>(aaChars);
		if (myAAChars.size() > maxDisplayAAs) {
			myAAChars.clear();
			myAAChars.add('X');
		}
		return myAAChars;
	}
	
	@Override
	public String getAAs() {
		return StringUtils.join(aaChars.toArray());
	}
	
	@Override
	public final Set<Character> getAAChars() {
		return new TreeSet<>(aaChars);
	}

	@Override
	public final Set<Mutation> split() {
		Set<Mutation> r = new TreeSet<>();
		for (char aa : getAAChars()) {
			if (aa == getRefChar()) {
				// ignore reference
				continue;
			}
			r.add(new AAMutation(gene, position, aa));
		}
		return r;
	}
	
	@Override
	public String getTriplet() { return ""; }
	
	@Override
	public String getInsertedNAs() { return ""; }
	
	@Override
	public final boolean isInsertion() { return getAAChars().contains('_'); }
	
	@Override
	public final boolean isDeletion() { return getAAChars().contains('-'); }
	
	@Override
	public final boolean isIndel() {
		Set<Character> myAAChars = getAAChars();
		return myAAChars.contains('_') || myAAChars.contains('-');
	}
	
	@Override
	public final boolean isMixture() {
		Set<Character> myAAChars = getAAChars();
		return myAAChars.size() > 1 || myAAChars.contains('X');
	}

	@Override
	public final boolean hasReference () { return getAAChars().contains(getRefChar()); }

	@Override
	public final boolean hasStop() { return getAAChars().contains('*'); }

	@Override
	public final boolean isUnusual() {
		Set<Character> myAAChars = getAAChars();
		if (myAAChars.contains('X')) {
			return true;
		}
		return allAAPcnts.containsUnusualAA(gene, position, StringUtils.join(myAAChars.toArray()));
	}

	@Override
	public final boolean isSDRM() { return Sdrms.isSDRM(this); }

	@Override
	public final boolean isDRM() { return DRMs.isDRM(this); }
	
	@Override
	public boolean hasBDHVN() {
		// no way to tell in BasicMutation
		return false;
	}

	@Override
	public boolean isAmbiguous() {
		Set<Character> myAAChars = getAAChars();
		return hasBDHVN() || myAAChars.size() > maxDisplayAAs || myAAChars.contains('X');
	}

	@Override
	public final boolean isApobecMutation() { return Apobec.isApobecMutation(this); }

	@Override
	public final boolean isApobecDRM() { return Apobec.isApobecDRM(this); }

	@Override
	public final double getHighestMutPrevalence() {
		Set<Character> myAAChars = getAAChars();
		myAAChars.remove(getRefChar());
		myAAChars.remove('X');
		if (myAAChars.isEmpty()) {
			return .0;
		}
		
		return allAAPcnts.getHighestAAPercentValue(
			gene, position, StringUtils.join(myAAChars.toArray())) * 100;
	}

	@Override
	public String getAAsWithRefFirst() {
		Set<Character> myAAChars = getDisplayAAChars();
		StringBuilder resultAAs = new StringBuilder();
		if (myAAChars.remove(getRefChar())) {
			resultAAs.append(getRefChar());
		}
		resultAAs.append(StringUtils.join(myAAChars.toArray()));
		return resultAAs.toString();
	}

	@Override
	public final MutType getPrimaryType() {
		return getTypes().get(0);
	}
	
	@Override
	public final List<MutType> getTypes() {
		if (types == null) {
			List<MutType> r = MutationTypePairs.lookupByMutation(this);
			if (r.isEmpty()) {
				r.add(MutType.Other);
			}
			types = Collections.unmodifiableList(r);
		}
		return types;
	}

	@Override
	public String getAAsWithoutReference () {
		Set<Character> myAAChars = getDisplayAAChars();
		myAAChars.remove(getRefChar());
		return StringUtils.join(myAAChars.toArray());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) { return true; }
		if (o == null) { return false; }
		if (!(o instanceof AAMutation)) { return false;}
		AAMutation m = (AAMutation) o;

		// isDeletion and isInsertion is related to aas
		return new EqualsBuilder()
			.append(gene, m.gene)
			.append(position, m.position)
			.append(getAAChars(), m.getAAChars())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(4541, 83345463)
			.append(gene)
			.append(position)
			.append(getAAChars())
			.toHashCode();
	}
	
	@Override
	public final String toString() {
		return getHumanFormat();
	}

	@Override
	public final String getShortText() {
		return getShortHumanFormat();
	}

	@Override
	public int compareTo(Mutation mut) {
		int cmp = gene.compareTo(mut.getGene());
		if (cmp == 0) {
			cmp = new Integer(position).compareTo(mut.getPosition());
		}
		if (cmp == 0) {
			cmp = getAAs().compareTo(mut.getAAs());
		}
		return cmp;
	}

	@Override
	public final boolean containsSharedAA(Mutation queryMut) {
		if (this.gene.equals(queryMut.getGene()) &&
			this.position == queryMut.getPosition()
		) {
			return containsSharedAA(queryMut.getAAChars(), true);
		}
		return false;
	}

	@Override
	public boolean containsSharedAA(
		Set<Character> queryAAChars, boolean ignoreRefOrStops
	) {
		Set<Character> myAAChars = getAAChars();
		myAAChars.retainAll(queryAAChars);
		if (ignoreRefOrStops) {
			// Remove reference and stop codons so
			// that they are not responsible for a match
			myAAChars.remove(getRefChar());
			myAAChars.remove('*');
		}
		return !myAAChars.isEmpty();
	}

	@Override
	public final String getASIFormat() {
		String fmtAAs = StringUtils.join(getAAChars().toArray());
		fmtAAs = (
			fmtAAs
			.replace('_', 'i')
			.replace('-', 'd')
			.replaceAll("[X*]", "Z")
		);
		return String.format("%s%d%s", getRefChar(), position, fmtAAs);
	}

	@Override
	public final String getHIVDBFormat() {
		String fmtAAs = StringUtils.join(getAAChars().toArray());
		fmtAAs = (
			fmtAAs
			.replace('_', '#')
			.replace('-', '~')
		);
		return String.format("%d%s", position, fmtAAs);
	}

	@Override
	public final String getHumanFormat() {
		String fmtAAs = getAAsWithRefFirst();
		fmtAAs = (
			fmtAAs
			.replaceAll("^_$", "Insertion")
			.replaceAll("^-$", "Deletion")
		);
		return String.format("%s%d%s", getRefChar(), position, fmtAAs);
	}

	@Override
	public final String getShortHumanFormat() {
		String fmtAAs = getAAsWithRefFirst();
		fmtAAs = (
			fmtAAs
			.replaceAll("^_$", "i")
			.replaceAll("^-$", "d")
		);
		return String.format("%s%d%s", getRefChar(), position, fmtAAs);
	}

	@Override
	public final String getHumanFormatWithoutLeadingRef() {
		return (getHumanFormat().substring(1));
	}

	@Override
	public final String getHumanFormatWithGene() {
		return String.format("%s_%s", gene, getHumanFormat());
	}
}