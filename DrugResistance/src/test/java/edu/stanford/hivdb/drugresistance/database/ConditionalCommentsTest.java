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

package edu.stanford.hivdb.drugresistance.database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.ConditionalComments.BoundComment;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.IUPACMutation;
import edu.stanford.hivdb.mutations.Mutation;

public class ConditionalCommentsTest {

	//@Test
	public void test() {
		Mutation mut1 = new IUPACMutation(Gene.RT, 41, "LI");
		Mutation mut2 = new IUPACMutation(Gene.RT, 215, "P");
		Mutation mut3 = new IUPACMutation(Gene.RT, 118, "VI");
		Mutation mut4 = new IUPACMutation(Gene.RT, 190, "V");
		Mutation mut5 = new IUPACMutation(Gene.RT, 35, "I");
		Mutation mut6 = new IUPACMutation(Gene.RT, 100, "I");
		Mutation mut7 = new IUPACMutation(Gene.PR, 73, "SC");
		Mutation mut8 = new IUPACMutation(Gene.PR, 90, "LM");
		Mutation mut9 = new IUPACMutation(Gene.PR, 84, "Q");

		List<Mutation> mutList = new ArrayList<Mutation>();
		mutList.add(mut1);
		mutList.add(mut2);
		mutList.add(mut3);
		mutList.add(mut4);
		mutList.add(mut5);
		mutList.add(mut6);
		mutList.add(mut7);
		mutList.add(mut8);
		mutList.add(mut9);

	}

	@Test
	public void testInsertion() {
		Mutation mut = new IUPACMutation(Gene.RT, 69, "_SS");
		List<BoundComment> result = ConditionalComments.getComments(mut);
		for (BoundComment cmt: result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "_");
		}
	}

	@Test
	public void testDeletion() {
		Mutation mut = new IUPACMutation(Gene.RT, 67, "-");
		List<BoundComment> result = ConditionalComments.getComments(mut);
		for (BoundComment cmt: result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "-");
		}
	}



}
