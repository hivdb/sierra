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

import java.util.Set;

import edu.stanford.hivdb.mutations.Mutation;
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
		Set<Mutation> splitted = (
			mutations.stream()
			.map(m -> m.split())
			.reduce((m1, m2) -> {
				m1.addAll(m2);
				return m1;
			})
			.get());
		numUsuals = (
			splitted.stream()
			.filter(m -> m.getHighestMutPrevalence() >= 0.1)
			.count());
		numUnusuals = (
			splitted.stream()
			.filter(
				m -> m.getHighestMutPrevalence() < 0.1 &&
				!m.isApobecMutation() && !m.isApobecDRM()
			)
			.count());
		numDRMs = splitted.stream().filter(m -> m.isDRM()).count();
		numStops = splitted.stream().filter(m -> m.hasStop()).count();
		numApobecMutations = splitted.stream().filter(m -> m.isApobecMutation()).count();
		numApobecDRMs = splitted.stream().filter(m -> m.isApobecDRM()).count();
	}
	
	public double getMinPrevalence() { return minPrevalence; }
	public long getNumUsualMutations() { return numUsuals; }
	public long getNumUnusualMutations() { return numUnusuals; }
	public long getNumDRMs() { return numDRMs; }
	public long getNumStopCodons() { return numStops; }
	public long getNumApobecMutations() { return numApobecMutations; }
	public long getNumApobecDRMs() { return numApobecDRMs; }
	
}
