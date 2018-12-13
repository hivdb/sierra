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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.MutationPrevalences.MutationPrevalence;

public class MutationSet extends TreeSet<Mutation> {

	private static final String MESSAGE_ON_WRITE =
		"Modification to MutationSet is not allowed.";

	private static final long serialVersionUID = -1835692164622753L;

	private transient Map<GenePosition, Mutation> genePositionMap;

	public MutationSet(Collection<Mutation> mutations) {
		genePositionMap = new TreeMap<>();
		_addAll(mutations);
		// prevent any future changes to genePositionMap
		genePositionMap = Collections.unmodifiableMap(genePositionMap);
	}

	public MutationSet(Mutation... mutations) {
		this(Arrays.asList(mutations));
	}

	/**
	 * Parse the given string then create mutation set.
	 *
	 * Supported delimiters:
	 * 	- space ( )
	 * 	- tabulation (\t)
	 * 	- new line (\n)
	 * 	- carriage return (\r)
	 * 	- dot (.)
	 * 	- comma (,)
	 * 	- semicolon (;)
	 * 	- plus (+)
	 *
	 * Supported single mutation formats:
	 * 	- with consensus (P1X)
	 * 	- without consensus (52R)
	 * 	- lowercase indels (69i or 44d)
	 * 	- dash/underscore indels (69_XX or 44-)
	 * 	- "hivdb" indels (69#XX or 44~)
	 * 	- word indels (69Insertion, 44Deletion)
	 * 	- stop codon (122*)
	 *
	 * All duplicated mutations are removed before returning.
	 *
	 * @param gene
	 * @param mutationsStr
	 * @return A list of Mutation objects
	 */
	public MutationSet(Gene gene, String formattedMuts) {
		this(parseString(gene, formattedMuts));
	}

	public MutationSet(Gene gene, Collection<String> formattedMuts) {
		this(parseStringCollection(gene, formattedMuts));
	}

	public MutationSet(String formattedMuts) {
		this(parseString(null, formattedMuts));
	}
	
	public MutationSet displayAmbiguities() {
		List<Mutation> tmpMuts = new ArrayList<>();
		for (Mutation mut : this) {
			tmpMuts.add(new AAMutation(
				mut.getGene(), mut.getPosition(),
				mut.getAAChars(), 0xff));
		}
		return new MutationSet(tmpMuts);
	}

	private static List<Mutation>
			parseString(Gene gene, String formattedMuts) {
		if (formattedMuts == null) {
			return new ArrayList<>();
		}
		return parseStringCollection(
			gene,
			Arrays.asList(formattedMuts.split("[\\s,;+\\.]+"))
		);
	}

	private static List<Mutation>
			parseStringCollection(Gene gene, Collection<String> formattedMuts) {
		return formattedMuts
			.stream()
			.filter(mStr -> mStr.length() > 0)
			.map(mStr -> IUPACMutation.parseString(gene, mStr))
//			Since parseString throws exceptions instead of returning 
//			null mutations, the call to filter below may be redundant.
//			.filter(mut -> mut != null) 	 
			.collect(Collectors.toList());
	}

	// private addAll method for internal usage
	private boolean _addAll(Collection<? extends Mutation> muts) {
		List<Boolean> rr = muts
			.stream().map(mut -> _add(mut)).collect(Collectors.toList());
		return rr.stream().anyMatch(r -> r);
	}

	// private add method for internal usage
	private boolean _add(Mutation mut) {
		if (mut == null) {
			return false;
		}
		GenePosition gp = mut.getGenePosition();
		Mutation origMut = genePositionMap.getOrDefault(gp, null);
		if (mut.equals(origMut) || mut.getReference().equals(mut.getAAs())) {
			return false;
		}
		if (origMut != null) {
			mut = origMut.mergesWith(mut.getAAChars());
			super.remove(origMut);
		}
		super.add(mut);
		genePositionMap.put(gp, mut);
		return true;
	}
	
	// Begin of all write methods
	@Override
	public boolean addAll(Collection<? extends Mutation> muts) {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}

	@Override
	public boolean add(Mutation mut) {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}

	@Override
	public boolean removeAll(Collection<?> muts) {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}

	@Override
	public boolean retainAll(Collection<?> muts) {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}

	@Override
	public boolean remove(Object m) {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}

	@Override
	public Mutation pollFirst() {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}

	@Override
	public Mutation pollLast() {
		throw new UnsupportedOperationException(MESSAGE_ON_WRITE);
	}
	// End of all write methods

	/**
	 * Merges with another MutationSet and returns the result.
	 *
	 * Example:
	 *   self = new MutationSet(RT, "48VER");
	 *   another = new MutationSet(RT, "48A,48L,36E");
	 *   assertEquals(
	 *     new MutationSet(RT, "48AELRV,36E"),
	 *     self.mergesWith(another));
	 *
	 * @param another
	 * @return A new MutationSet object contains matched mutations
	 */
	public MutationSet mergesWith(Collection<Mutation> another) {
		List<Mutation> newList = new ArrayList<>(this);
		newList.addAll(another);
		return new MutationSet(newList);
	}

	/**
	 * Just a shortcut to mergesWith(Collection).
	 */
	public MutationSet mergesWith(Mutation... mutations) {
		return mergesWith(new MutationSet(mutations));
	}

	/**
	 * Intersects with another MutationSet and returns the result.
	 *
	 * Example:
	 *   self = new MutationSet(RT, "48VER");
	 *   another = new MutationSet(RT, "48E,48AR,36E");
	 *   assertEquals(
	 *     new MutationSet(RT, "48ER"),
	 *     self.intersectsWith(another));
	 *
	 * @param another
	 * @return A new MutationSet object contains matched mutations
	 */
	public MutationSet intersectsWith(Collection<Mutation> another) {
		Set<GenePosition> gpKeys = new HashSet<>(genePositionMap.keySet());
		Set<GenePosition> gpKeysAnother;
		if (another instanceof MutationSet) {
			gpKeysAnother = ((MutationSet) another).genePositionMap.keySet();
		} else {
			gpKeysAnother = another
				.stream()
				.map(m -> m.getGenePosition())
				.collect(Collectors.toSet());
		}
		gpKeys.retainAll(gpKeysAnother);
		List<Mutation> mutations = new ArrayList<>();
		for (GenePosition gp : gpKeys) {
			Mutation thisMut = genePositionMap.get(gp);
			Set<Character> otherAAChars = new TreeSet<>();
			if (another instanceof MutationSet) {
				otherAAChars.addAll(
					((MutationSet) another).genePositionMap.get(gp).getAAChars()
				);
			}
			else {
				for (Mutation mut : another) {
					if (mut.getGenePosition().equals(gp)) {
						otherAAChars.addAll(mut.getAAChars());
					}
				}
			}
			mutations.add(thisMut.intersectsWith(otherAAChars));
		}
		return new MutationSet(mutations);
	}

	/**
	 * Just a shortcut to intersectsWith(Collection).
	 */
	public MutationSet intersectsWith(Mutation... mutations) {
		return intersectsWith(new MutationSet(mutations));
	}

	/**
	 * New MutationSet with elements in this but not in another.
	 */
	public MutationSet subtractsBy(Collection<Mutation> another) {
		MutationSet anotherSet;
		if (another instanceof MutationSet) {
			anotherSet = (MutationSet) another;
		}
		else {
			anotherSet = new MutationSet(another);
		}
		List<Mutation> mutations = new ArrayList<>();
		for (GenePosition gp : genePositionMap.keySet()) {
			Mutation thisMut = genePositionMap.get(gp);
			Mutation anotherMut = anotherSet.genePositionMap.get(gp);
			if (anotherMut == null) {
				mutations.add(thisMut);
			}
			else {
				mutations.add(thisMut.subtractsBy(anotherMut.getAAChars()));
			}
		}
		return new MutationSet(mutations);
	}

	public MutationSet subtractsBy(Mutation... mutations) {
		return subtractsBy(new MutationSet(mutations));
	}

	private MutationSet filterBy(Predicate<Mutation> predicate) {
		return new MutationSet(
			this
			.stream()
			.filter(predicate)
			.collect(Collectors.toList()));
	}

	private <T> Map<T, MutationSet> filterAndGroupBy(
			Predicate<Mutation> predicate, Function<Mutation, T> function) {
		Map<T, List<Mutation>> tmp = this
			.stream()
			.filter(predicate)
			.collect(Collectors.groupingBy(function));
		Map<T, MutationSet> r = new TreeMap<>();
		for (Map.Entry<T, List<Mutation>> e : tmp.entrySet()) {
			r.put(e.getKey(), new MutationSet(e.getValue()));
		}
		return r;
	}

	private <T> Map<T, MutationSet> groupBy(Function<Mutation, T> function) {
		Map<T, List<Mutation>> tmp = this
			.stream()
			.collect(Collectors.groupingBy(function));
		Map<T, MutationSet> r = new TreeMap<>();
		for (Map.Entry<T, List<Mutation>> e : tmp.entrySet()) {
			r.put(e.getKey(), new MutationSet(e.getValue()));
		}
		return r;
	}

	/**
	 * Filter mutations by gene then groups the result by their primary MutType.
	 *
	 * @param gene
	 * @return Map<MutType, MutationSet>
	 */
	public Map<MutType, MutationSet> groupByMutType(final Gene gene) {
		Map<MutType, MutationSet> r = filterAndGroupBy(
			mut -> mut.getGene() == gene,
			mut -> mut.getPrimaryType());
		List<MutType> mutTypes = gene.getMutationTypes();
		if (r.size() < mutTypes.size()) {
			for (MutType mt: mutTypes) {
				r.putIfAbsent(mt, new MutationSet());
			}
		}
		return r;
	}

	/**
	 * Filter mutations by given mutation type.
	 *
	 * @param mutType
	 * @return MutationSet
	 */
	public MutationSet getByMutType(final MutType mutType) {
		return filterBy(mut -> mut.getPrimaryType() == mutType);
	}

	/**
	 * Groups mutations by their gene.
	 *
	 * @return Map<Gene, MutationSet>
	 */
	public Map<Gene, MutationSet> groupByGene() {
		return groupBy(mut -> mut.getGene());
	}

	/**
	 * Returns only mutations of a specific gene.
	 * @param gene
	 * @return A MutationSet contains all mutations of the given gene
	 */
	public MutationSet getGeneMutations(Gene gene) {
		return filterBy(mut -> mut.getGene() == gene);
	}

	/**
	 * Returns only mutations which are insertions.
	 * @return A new MutationSet instance
	 */
	public MutationSet getInsertions() {
		return filterBy(mut -> mut.isInsertion());
	}

	/**
	 * Returns only mutations which are deletions.
	 * @return A new MutationSet instance
	 */
	public MutationSet getDeletions() {
		return filterBy(mut -> mut.isDeletion());
	}

	/**
	 * Returns only mutations which contain stop codons.
	 * @return A new MutationSet instance
	 */
	public MutationSet getStopCodons() {
		return filterBy(mut -> mut.hasStop());
	}

	/**
	 * Returns only mutations which contain ambiguous codons.
	 * @return A new MutationSet instance
	 */
	public MutationSet getAmbiguousCodons() {
		return filterBy(mut -> mut.isAmbiguous());
	}

	/**
	 * Returns mutations that contain at least an unusual AA.
	 * @return A new MutationSet instance
	 */
	public MutationSet getUnusualMutations() {
		return filterBy(mut -> mut.isUnusual());
	}

	/**
	 * Creates a map of mutations and their prevalence in HIVDB.
	 * When a mutation has more than one amino acid, the prevalence of the
	 * most prevalent mutation is returned.
	 *
	 * @return Map<Mutation, Double> mutPrevalences
	 */
	public Map<Mutation, Double> getHighestMutPrevalences() {
		Map<Mutation, Double> r = new LinkedHashMap<>();
		for (Mutation mut : this) {
			r.put(mut, mut.getHighestMutPrevalence());
		}
		return r;
	}

	/**
	 * Returns only mutations which are at DR positions.
	 * @return A new MutationSet instance
	 */
	public MutationSet getAtDRPMutations() {
		return filterBy(mut -> mut.isAtDrugResistancePosition());
	}

	/**
	 * Returns only mutations which are DRMs.
	 * @return A new MutationSet instance
	 */
	public MutationSet getDRMs(DrugClass drugClass) {
		Gene gene = drugClass.gene();
		return filterBy(mut -> {
			MutType type = mut.getPrimaryType();
			if (mut.getGene() != gene || type == MutType.Other) {
				return false;
			}
			else if (
					type == MutType.NRTI &&
					drugClass != DrugClass.NRTI) {
				return false;
			}
			else if (
					type == MutType.NNRTI &&
					drugClass != DrugClass.NNRTI) {
				return false;
			}
			return true;
		});
	}
	
	public MutationSet getDRMs() {
		return filterBy(mut -> mut.getPrimaryType() != MutType.Other);
	}

	/** Returns a mutation at specified gene position.
	 *
	 * @param gp a GenePosition object
	 * @return The matched mutation
	 */
	public Mutation get(GenePosition gp) {
		return genePositionMap.get(gp);
	}

	/** Returns a mutation at specified gene position.
	 *
	 * @param gene
	 * @param pos
	 * @return The matched mutation
	 */
	public Mutation get(Gene gene, int pos) {
		GenePosition gp = new GenePosition(gene, pos);
		return genePositionMap.get(gp);
	}

	/** Returns a set of non-mixture mutations for all mutations.
	 * 
	 * @return The mutation set
	 */
	public Set<Mutation> getSplitted() {
		Set<Mutation> splittedMuts = new TreeSet<>();
		for(Mutation mut : genePositionMap.values()) {
			splittedMuts.addAll(mut.split());
		}
		return splittedMuts;
	}

	/** Returns list of mutation positions
	 * 
	 * @return a list of mutation positions 
	 */
	public List<GenePosition> getPositions() {
		return new ArrayList<>(genePositionMap.keySet());
	}

	/** Check if the given position is an insertion
	 *
	 * @param gene
	 * @param pos
	 * @return Boolean
	 */
	public boolean hasInsertionAt(Gene gene, int pos) {
		Mutation mut = get(gene, pos);
		return mut == null ? false : mut.isInsertion();
	}

	/** Check if the given position is a deletion
	 *
	 * @param gene
	 * @param pos
	 * @return Boolean
	 */
	public boolean hasDeletionAt(Gene gene, int pos) {
		Mutation mut = get(gene, pos);
		return mut == null ? false : mut.isDeletion();
	}

	/**
	 * Returns true if contains any mutation that its AAs shared with mutRef.
	 *
	 * @param anotherMut
	 * @return boolean
	 */
	public boolean hasSharedAAMutation(Mutation anotherMut) {
		return hasSharedAAMutation(anotherMut, true);
	}

	/**
	 * Returns true if contains any mutation that its AAs shared with mutRef.
	 *
	 * @param anotherMut
	 * @param ignoreRefOrStops
	 * @return boolean
	 */
	public boolean hasSharedAAMutation(Mutation anotherMut, boolean ignoreRefOrStops) {
		GenePosition gp = anotherMut.getGenePosition();
		Mutation selfMut = genePositionMap.get(gp);
		if (selfMut == null) {
			return false;
		}
		return selfMut.containsSharedAA(anotherMut.getAAChars(), ignoreRefOrStops);
	}

	public Map<Mutation, List<MutationPrevalence>> getPrevalences() {
		return MutationPrevalences.groupPrevalenceByPositions(this);
	}
	
	public String join(
			CharSequence delimiter,
			Function<Mutation, CharSequence> mutationToString) {
		if (this.isEmpty()) {
			// to be compatible with the old output
			return "None";
		}
		return this
			.stream()
			.map(mutationToString)
			.collect(Collectors.joining(delimiter));
	}

	public String join(
			Character delimiter,
			Function<Mutation, CharSequence> mutationToString) {
		return join("" + delimiter, mutationToString);
	}

	public String join(CharSequence delimiter) {
		return join(delimiter, Mutation::getHumanFormat);
	}

	public String join(Character delimiter) {
		return join("" + delimiter, Mutation::getHumanFormat);
	}

	public String join(Function<Mutation, CharSequence> mutationToString) {
		return join(",", mutationToString);
	}

	public String join() {
		return join(",", Mutation::getHumanFormat);
	}

	public List<String> toStringList(
			Function<Mutation, CharSequence> mutationToString) {
		return this
			.stream()
			.map(mut -> mutationToString.apply(mut).toString())
			.collect(Collectors.toList());
	}

	public List<String> toStringList() {
		return toStringList(Mutation::getHumanFormat);
	}

	public List<String> toASIFormat() {
		return toStringList(Mutation::getASIFormat);
	}
}
