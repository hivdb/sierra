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

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.drugs.DrugClass;

public class TsmsTest {

	@Test
	public void testGetAllTsms() {
		MutationSet muts;

		// case 1
		muts = new MutationSet(
			"RT_M41L,RT_K65N,RT_D67S,RT_K70Q,RT_L74I,RT_V75M,RT_I94L,RT_K101H," +
			"RT_K122E,RT_I135T,RT_V179F,RT_M184V,RT_G190Q,RT_H208Y,RT_H221Y");

		assertEquals(
			new MutationSet(
				"RT_M41L,RT_K65N,RT_D67S,RT_K70Q,RT_L74I,RT_V75M,RT_I94L," +
				"RT_K101H,RT_V179F,RT_M184V,RT_G190Q,RT_H208Y,RT_H221Y"),
			new MutationSet(Tsms.getAllTsms(muts))
		);

		// case 2
		muts = new MutationSet("PR10I,PR33F,PR82I,RT65N,IN148H,RT69T_TT");
		assertEquals(
			new MutationSet("RT65N,IN148H,RT69Insertion"),
			new MutationSet(Tsms.getAllTsms(muts)));

		// case 3
		muts = new MutationSet("PR10I,PR48V,PR33F,PR82I,RT65N,RT67-,RT103N,IN148H");
		assertEquals(
			new MutationSet("PR48V,RT65N,RT67Deletion,RT103N,IN148H"),
			new MutationSet(Tsms.getAllTsms(muts)));
	}

	@Test
	public void testGetAllTsmsByDrugClass() {
		MutationSet muts = new MutationSet("RT:31L RT:44A RT:547R PR:47A PR:10R IN:51Y");
		MutationSet eNRTIMuts = new MutationSet("RT:31L RT:44A RT:547R");
		MutationSet eNNRTIMuts = new MutationSet();
		MutationSet ePIMuts = new MutationSet("PR:47A PR:10R");
		MutationSet eINSTIMuts = new MutationSet("IN:51Y");
		assertEquals(eNRTIMuts, Tsms.getTsmsForDrugClass(DrugClass.NRTI, muts));
		assertEquals(eNNRTIMuts, Tsms.getTsmsForDrugClass(DrugClass.NNRTI, muts));
		assertEquals(ePIMuts, Tsms.getTsmsForDrugClass(DrugClass.PI, muts));
		assertEquals(eINSTIMuts, Tsms.getTsmsForDrugClass(DrugClass.INSTI, muts));
	}
}
