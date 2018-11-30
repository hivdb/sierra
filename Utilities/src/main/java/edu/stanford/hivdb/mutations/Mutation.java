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
import java.util.List;
import java.util.Set;

public interface Mutation extends Comparable<Mutation> {
	
	/**
	 * Exception for invalid mutation
	 */
	public static class InvalidMutationException extends RuntimeException {
		private static final long serialVersionUID = 4271016133470715497L;

		public InvalidMutationException(String message, Exception e) {
			super(message, e);
		}
		
		public InvalidMutationException(String message) {
			super(message);
		}
	}

	// /**
	//  * Split a mixture into a set of mutations.
	//  * 
	//  * @return A set of mutations
	//  */
	// public Set<Mutation> split();

	/**
	 * Merges with another Mutation object and returns the merged mutation.
	 *
	 * @param another
	 * @return A new merged Mutation object
	 * @throws IllegalArgumentException, UnsupportedOperationException
	 */
	public Mutation mergesWith(Mutation another);
	
	/**
	 * Merges with another Collection<Character> of
	 * AAs and returns the merged mutation.
	 * 
	 * @param otherAAChars
	 * @return A new merged Mutation object
	 */
	public Mutation mergesWith(Collection<Character> otherAAChars);
	

	/**
	 * Subtracts by another Mutation object and returns the result mutation.
	 *
	 * @param another
	 * @return A new Mutation object
	 */
	public Mutation subtractsBy(Mutation another);

	/**
	 * Subtracts by another Collection<Character> of
	 * AAs and returns the result mutation.
	 *
	 * @param otherAAChars
	 * @return A new Mutation object
	 */
	public Mutation subtractsBy(Collection<Character> otherAAChars);

	/**
	 * Intersects with another Mutation object and returns the result mutation.
	 *
	 * @param another
	 * @return A new merged Mutation object
	 * @throws IllegalArgumentException
	 */
	public Mutation intersectsWith(Mutation another);

	/**
	 * Intersects with another Collection<Character> of
	 * AAs and returns the result mutation.
	 *
	 * @param otherAAChars
	 * @return A new merged Mutation object
	 */
	public Mutation intersectsWith(Collection<Character> otherAAChars);

	/**
	 * Checks if the mutation is at a drug resistance position
	 * 
	 * @return true if the mutation is at DRM position
	 */
	public boolean isAtDrugResistancePosition();

	/**
	 * Checks if the position is unsequenced.
	 * 
	 * @return true if the position is unsequenced.
	 */
	public boolean isUnsequenced();
	
	/**
	 * Checks if the mutation is a DRM
	 * 
	 * @return true if the mutation is a DRM
	 */
	public boolean isDRM();
	
	/**
	 * Gets gene of this mutation.
	 * 
	 * @return A Gene
	 */
	public Gene getGene();
	
	/**
	 * Gets the subtype B reference at this position.
	 * 
	 * TODO: We should probably return Character instead.
	 * 
	 * @return A single character string
	 */
	public String getReference();
	
	/**
	 * Gets the position number of this mutation.
	 *
	 * @return An integer >= 1
	 */
	public int getPosition();
	
	/**
	 * Gets the GenePosition object of this mutation.
	 *
	 * @return A GenePosition object
	 */
	public GenePosition getGenePosition();
	
	/**
	 * Gets the amino acids string of this mutation.
	 *
	 * @return A String of amino acids (include insertion/deletion)
	 */
	public String getDisplayAAs();
	
	/**
	 * Gets a set of amino acid characters.
	 *
	 * - "_" represents an insertion;
	 * - "-" represents a deletion;
	 * - "*" represents a stop codon.
	 * 
	 * @return A Set<Character> of amino acids/insertion/deletion/stop codons
	 */
	public Set<Character> getDisplayAAChars();
	
	/**
	 * Gets original amino acids string of this mutation.
	 *
	 * @return A String of amino acids (include insertion/deletion)
	 */
	public String getAAs();
	
	/**
	 * Gets the original set of amino acid characters.
	 *
	 * - "_" represents an insertion;
	 * - "-" represents a deletion;
	 * - "*" represents a stop codon.
	 * 
	 * @return A Set<Character> of amino acids/insertion/deletion/stop codons
	 */
	public Set<Character> getAAChars();
	
	/**
	 * Gets a set of single amino acid mutations.
	 * 
	 * @return A Set<Mutation>
	 */
	public Set<Mutation> split();
	
	/**
	 * Gets the triplet (codon) of this mutation.
	 *
	 * @return A string contains three characters (ACGT...) 
	 */
	public String getTriplet();
	
	/**
	 * Gets the inserted NAs of this mutation.
	 * 
	 * @return A string contains multiples of three characters (ACGT...)
	 */
	public String getInsertedNAs();
	
	/**
	 * Checks if the mutation contains an insertion.
	 *
	 * @return true if the mutation contains an insertion.
	 */
	public boolean isInsertion();
	
	/**
	 * Checks if the mutation contains a deletion.
	 *
	 * @return true if the mutation contains a deletion.
	 */
	public boolean isDeletion();
	
	/**
	 * Checks if the mutation contains an insertion or a deletion.
	 *
	 * @return true if the mutation contains an insertion or a deletion.
	 */
	public boolean isIndel();
	
	/**
	 * Checks if the Mutation object is a mixture of
	 * multiple amino acids / insertion / deletion / stop codon
	 *
	 * @return true if the mutation is a mixture
	 */
	public boolean isMixture();

	/**
	 * Checks if the Mutation object is a mixture with
	 * subtype B reference amino acid presents
	 *
	 * @return true if AAs contains subtype B reference
	 */
	public boolean hasReference();
	
	/**
	 * Checks if the mutation contains a stop codon
	 *
	 * @return true if the mutation contains a stop codon
	 */
	public boolean hasStop();

	/**
	 * Checks if the mutation is considered unusual
	 *
	 * @return true if the mutation is considered unusual
	 */
	public boolean isUnusual();

	/**
	 * Checks if the mutation is an SDRM mutation
	 *
	 * @return true if the mutation is an SDRM mutation 
	 */
	public boolean isSDRM();
	
	/**
	 * Checks if the mutation codon contains BDHVN
	 *
	 * @return true if the codon contains BDHVN
	 */
	public boolean hasBDHVN();
	
	/**
	 * Checks if the mutation is highly ambiguous
	 *
	 * @return true if the mutation is highly ambiguous
	 */
	public boolean isAmbiguous();

	/**
	 * Checks if the mutation is considered APOBEC-mediated (non-DRM)
	 *
	 * @return true if the mutation is considered APOBEC-mediated
	 */
	public boolean isApobecMutation();
	
	/**
	 * Checks if the mutation is a DRM considered APOBEC-mediated
	 *
	 * @return true if the mutation is a DRM considered APBOEC-mediated
	 */
	public boolean isApobecDRM();
	
	/**
	 * Gets the highest AA prevalence of this mutation.
	 *
	 * @return A double number of the prevalence (max: 100)
	 */
	public double getHighestMutPrevalence();

	/**
	 * Re-ordering AAs to place subtype B
	 * reference (if presented) at the first
	 *
	 * @return a string of AAs
	 */
	public String getAAsWithRefFirst();

	/**
	 * Gets AAs without subtype B reference (if presented)
	 *
	 * @return a string of AAs
	 */
	public String getAAsWithoutReference ();

	/**
	 * Retrieve the primary mutation type of current mutation.
	 * 
	 * @return a MutType object
	 */
	public MutType getPrimaryType();
	
	/**
	 * Retrieve all mutation types of current mutation.
	 *
	 * The reason we return a List here is because some mixture,
	 * for example, RT215SWY has multiple types.
	 * 
	 * @return a List of MutType objects
	 */
	public List<MutType> getTypes();

	public boolean equals(Object o);

	public int hashCode();

	/**
	 * Standard method to convert this mutation to a string.
	 *
	 * @return a String to represent this mutation.
	 */
	public String toString();

	/**
	 * Method to convert this mutation to a short version string.
	 *
	 * @return String
	 */
	public String getShortText();

	/**
	 * Sort by gene, position and aas.
	 *
	 * The order implemented by this method is similar
	 * to the order implemented by this SQL query:
	 *   SELECT * FROM mutations ORDER BY gene, pos, aas;
	 *
	 */
	public int compareTo (Mutation mut);

	/**
	 * Compares two mutations to determine if they share a non-reference amino acid
	 * 
	 * Reference and stop codon are not be responsible for a match.
	 * 
	 * @param queryMut
	 * @return true if the Mutation and queryMut share at least one non-reference amino acid
	 */
	public boolean containsSharedAA(Mutation queryMut);
	
	/**
	 * Compares with given queryAAChars to determine if they shared at least an amino acid
	 * 
	 * Reference and stop codon are not be responsible for a match.
	 * 
	 * @param queryMut
	 * @param ignoreRefOrStops
	 * @return true if the Mutation and queryMut share at least one amino acid
	 */
	public boolean containsSharedAA(Set<Character> queryAAChars, boolean ignoreRefOrStops);

	/**
	 * The ASI format consists of an optional reference aa and a position followed by one or more
	 * upper case amino acids. Insertions are represented by 'i', deletions by 'd', and stops by 'Z;
	 */
	public String getASIFormat();

	/**
	 * In HIVDB_Rules, insertions are denoted by '#' and deletions by '~'
	 * This differs from Insertion, _, and i and from Deletion, '-', and d
	 * @returns HIVDBformat
	 */
	public String getHIVDBFormat();

	/**
	 * If the insertion is known, report it out in the following format T69S_SS
	 * If the insertion is not known report it out as T69Insertion
	 * Report deletions as T69Deletion
	 * If there is a mixture that contains the reference aa, move the ref to
	 * the beginning of the mixture.
	 * Report the reference before the position (i.e. M184V)
	 */
	public String getHumanFormat();

	public String getShortHumanFormat();

	/**
	 * Similar to getHumanFormat() except that the preceding reference is removed.
	 */
	public String getHumanFormatWithoutLeadingRef();

	public String getHumanFormatWithGene();
}