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

import edu.stanford.hivdb.mutations.MutationSet;

public class MutationStats {

	private final double minPrevalence;
	private final long numUsuals;
	private final long numUnusuals;
	private final long numDRMs;
	private final long numStops;
	private final long numApobecMutations;
	private final long numApobecDRMs;
	
	public MutationStats(final double minPrevalence, final MutationSet mutations) {
		this.minPrevalence = minPrevalence;
		numUsuals = (
			mutations.stream()
			.filter(m -> !m.isUnusual())
			.count());
		numUnusuals = (
			mutations.stream()
			.filter(
				m -> m.isUnusual() &&
				!m.isApobecMutation() && !m.isApobecDRM()
			)
			.count());
		numDRMs = mutations.stream().filter(m -> m.isDRM()).count();
		numStops = mutations.stream().filter(m -> m.hasStop()).count();
		numApobecMutations = mutations.stream().filter(m -> m.isApobecMutation()).count();
		numApobecDRMs = mutations.stream().filter(m -> m.isApobecDRM()).count();
	}
	
	public double getMinPrevalence() { return minPrevalence; }
	public long getNumUsualMutations() { return numUsuals; }
	public long getNumUnusualMutations() { return numUnusuals; }
	public long getNumDRMs() { return numDRMs; }
	public long getNumStopCodons() { return numStops; }
	public long getNumApobecMutations() { return numApobecMutations; }
	public long getNumApobecDRMs() { return numApobecDRMs; }
	
}
