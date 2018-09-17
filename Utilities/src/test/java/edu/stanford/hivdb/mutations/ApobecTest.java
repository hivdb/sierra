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

import java.io.InputStream;
//import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.filetestutils.TestMutationsFiles;
import edu.stanford.hivdb.filetestutils.TestMutationsFiles.TestMutationsProperties;
import edu.stanford.hivdb.utilities.MutationFileReader;

public class ApobecTest {
	// Initialization
//	@Test
//	public void testApobecMapPopulation() {	
//		try {
//			Apobec.populateApobecMaps();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Test
	public void testBasicApobecMutVerification() {
		final Mutation apocecMut = new Mutation(Gene.RT, 239, "*");
		final Mutation drmMut = new Mutation(Gene.PR, 30, "N");
		assertFalse(Apobec.isApobecMutation(drmMut));
		assertTrue(Apobec.isApobecMutation(apocecMut));
	}
	
	@Test
	public void testBasicDrmMutVerification() {
		final Mutation drmMut = new Mutation(Gene.PR, 30, "N");
		final Mutation apocecMut = new Mutation(Gene.RT, 239, "*");
		assertFalse(Apobec.isApobecDRM(apocecMut));
		assertTrue(Apobec.isApobecDRM(drmMut));
	}
	
	@Test
	public void testExhaustiveApobecMutVerification() {
		final MutationSet apocecMuts = Apobec.getApobecMutsLU();
		apocecMuts.forEach(mut -> {
			assertFalse(Apobec.isApobecDRM(mut));
			assertTrue(Apobec.isApobecMutation(mut));
		});
	}
	
	@Test
	public void testExhaustiveDrmMutVerification() {
		final MutationSet drms = Apobec.getApobecDRMsLU();
		drms.forEach(mut -> {
			assertFalse(Apobec.isApobecMutation(mut));
			assertTrue(Apobec.isApobecDRM(mut));
		});
	}
	
	@Test
	public void testZeroMutsAtDRP() {
		final MutationSet nonDRPmuts = new MutationSet("PR6* PR17k PR:D30N IN:G140S RT:M230I");
		final Apobec a = new Apobec(nonDRPmuts);
		assertEquals(new MutationSet(""), a.getApobecMutsAtDRP());
	}
	
	@Test
	public void testMultipleMutsAtDRP() {
		final MutationSet eMutsAtDR = new MutationSet("PR48R IN140E RT190R");
		final Apobec a = new Apobec(eMutsAtDR);
		assertEquals(eMutsAtDR, a.getApobecMutsAtDRP());
	}
		
	@Test
	public void testZeroMuts() {
		final String expected = "There are no mutations present in this sequence.";
		final Apobec a = new Apobec(new MutationSet(""));
		assertEquals(0, a.getNumApobecMuts());
		assertEquals(expected, a.generateComment());
	}
	
	@Test
	public void testSingleApobecMut() {
		final String expected = "The following 1 APOBEC muts were present in the sequence: PR: G17K.";
		final Apobec a = new Apobec(new MutationSet("PR17k"));
		assertEquals(expected, a.generateComment());
	}
	
	@Test
	public void testSingleApobecMutWithStopCodon() {
		final String expected = "The following 1 APOBEC muts were present in the sequence: PR: W6*.";
		final Apobec a = new Apobec(new MutationSet("PR6*"));
		assertEquals(expected, a.generateComment());
	}
	
	@Test
	public void testMultipleApobecMuts() {
		final String expected = "The following 3 APOBEC muts were present in the sequence: PR: W6*, G17K; RT: W239*.";
		final Apobec a = new Apobec(new MutationSet("PR6* PR17k RT239*"));
		assertEquals(3, a.getNumApobecMuts());
		assertEquals(expected, a.generateComment());
	}
	
	@Test
	public void testSingleDRM() {
		final String expected = "The following 0 APOBEC muts were present in the sequence: . The following 1 DRMs in this sequence could reflect APOBEC activity: PR: D30N.";
		final MutationSet drm = new MutationSet("PR:D30N");
		final Apobec a = new Apobec(drm);
		assertEquals(0, a.getNumApobecMuts());
		assertEquals(drm.getDRMs(), a.getApobecDRMs());
		assertEquals(expected, a.generateComment());
	}
	
	@Test
	public void testMultipleDRMs() {
		final String expected = "The following 0 APOBEC muts were present in the sequence: . The following 3 DRMs in this sequence could reflect APOBEC activity: PR: D30N; RT: M230I; IN: G140S.";
		final MutationSet drms = new MutationSet("PR:D30N IN:G140S RT:M230I");
		final Apobec a = new Apobec(drms);
		assertEquals(0, a.getNumApobecMuts());
		assertEquals(drms.getDRMs(), a.getApobecDRMs());
		assertEquals(expected, a.generateComment());
	}
	
	@Test
	public void testMixedMuts() {
		final String expected = "The following 2 APOBEC muts were present in the sequence: PR: W6*, G17K. The following 3 DRMs in this sequence could reflect APOBEC activity: PR: D30N; RT: M230I; IN: G140S.";
		final MutationSet muts = new MutationSet("PR6* PR17k");
		final MutationSet drms = new MutationSet("PR:D30N IN:G140S RT:M230I");
		final MutationSet mixedMuts = muts.mergesWith(drms);
		final Apobec a = new Apobec(mixedMuts);
		assertEquals(mixedMuts.subtractsBy(mixedMuts.getDRMs()).size(), a.getNumApobecMuts());
		assertEquals(mixedMuts.subtractsBy(mixedMuts.getDRMs()), a.getApobecMuts());
		assertEquals(mixedMuts.getDRMs(), a.getApobecDRMs());
		assertEquals(expected, a.generateComment());
	}
	
	@Test
	public void testMixMutsFromFile() {
		final String eComment = "The following 4 APOBEC muts were present in the sequence: PR: G48ER, G52R; RT: M41I, G190R. The following 3 DRMs in this sequence could reflect APOBEC activity: PR: G73S; RT: D67N, M184I.";
		final MutationSet eMuts = new MutationSet("PR:48RE PR:52R RT:41I RT:190R");
		final MutationSet eDRMs = new MutationSet("PR:73S RT:67N RT:184I");
		final InputStream testMutationsInputStream = TestMutationsFiles.getTestMutationsInputStream(TestMutationsProperties.APOBEC_TEST);
		final List<MutationSet> mutationLists = MutationFileReader.readMutationLists(testMutationsInputStream);
		final Apobec a = new Apobec(mutationLists.get(0));
		final MutationSet muts = a.getApobecMuts();
		final MutationSet drms = a.getApobecDRMs();
		assertEquals("Apobec mutations not as expected.", eMuts, muts);
		assertEquals("DRMs not as expected.", eDRMs, drms);
		assertEquals(eComment, a.generateComment());
	}	
	
}
