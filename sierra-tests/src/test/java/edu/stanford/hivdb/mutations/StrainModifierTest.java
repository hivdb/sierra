package edu.stanford.hivdb.mutations;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.google.common.collect.Lists;

import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.mutations.StrainModifier.CIGARFlag;
import edu.stanford.hivdb.mutations.StrainModifier.PosModifier;
import edu.stanford.hivdb.viruses.Gene;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

public class StrainModifierTest {
	
	
	private final static HIV2 hiv2 = HIV2.getInstance();
	
	@Test
	public void testConstructor() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		
		StrainModifier strainModifier = gene.getTargetStrainModifier("HIV2A");
				
		assertNotNull(strainModifier);
	}
	
	@Test
	public void testGetTargetStrain() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		StrainModifier strainModifier = gene.getTargetStrainModifier("HIV2A");		
		
		assertEquals("HIV2A", strainModifier.getTargetStrain());
	}
	
	@Test
	public void testGetCIGARCase1() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		StrainModifier strainModifier = gene.getTargetStrainModifier("HIV2A");		
		Pair<Integer, CIGARFlag> mod = strainModifier.getCIGAR().get(0);
		
		assertEquals(293, (int) mod.getLeft());
		assertEquals(CIGARFlag.M, mod.getRight());
	}

	@Test
	public void testGetCIGARCase2() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		StrainModifier strainModifier = gene.getTargetStrainModifier("HIV1");		
		List<Pair<Integer, CIGARFlag>> cigarList = strainModifier.getCIGAR();
		
		assertEquals(Lists.newArrayList(
			Pair.of(272, CIGARFlag.M),
			Pair.of(2, CIGARFlag.D),
			Pair.of(11, CIGARFlag.M),
			Pair.of(1, CIGARFlag.D),
			Pair.of(5, CIGARFlag.M),
			Pair.of(5, CIGARFlag.D)
		), cigarList);
	}

	@Test
	public void testGetCIGARCase3() {
		Gene<HIV2> gene = hiv2.getGene("HIV2ART");
		StrainModifier strainModifier = gene.getTargetStrainModifier("HIV1");		
		List<Pair<Integer, CIGARFlag>> cigarList = strainModifier.getCIGAR();
		
		assertEquals(Lists.newArrayList(
			Pair.of(345, CIGARFlag.M),
			Pair.of(1, CIGARFlag.I),
			Pair.of(214, CIGARFlag.M)
		), cigarList);
	}
	
	@Test
	public void testGetPosModifiersCase1() {
		StrainModifier strainModifier = new StrainModifier("HIV1", "272M2D11M1D5M5D");
		Map<Integer, PosModifier> posMods = strainModifier.getPosModifiers();
		assertEquals(posMods, strainModifier.getPosModifiers());
		// 272M
		for (int i = 1; i <= 272; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(i, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.M, posMod.getFlag());
		}
		// 2D
		for (int i = 273; i <= 274; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(272, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.D, posMod.getFlag());
		}
		// 11M
		for (int i = 275; i <= 285; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(i - 2, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.M, posMod.getFlag());
		}
		// 1D
		for (int i = 286; i <= 286; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(283, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.D, posMod.getFlag());
		}
		// 5M
		for (int i = 287; i <= 291; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(i - 3, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.M, posMod.getFlag());
		}
		// 5D
		for (int i = 292; i <= 296; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(288, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.D, posMod.getFlag());
		}
	}

	@Test
	public void testGetPosModifiersCase2() {
		StrainModifier strainModifier = new StrainModifier("HIV1", "345M1I214M");
		Map<Integer, PosModifier> posMods = strainModifier.getPosModifiers();
		// 345M
		for (int i = 1; i <= 344; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(i, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.M, posMod.getFlag());
		}
		// 1I
		for (int i = 345; i <= 345; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(345, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.I, posMod.getFlag());
		}
		// 214M
		for (int i = 346; i <= 559; i ++) {
			PosModifier posMod = posMods.get(i);
			assertEquals(i + 1, (int) posMod.getTargetPos());
			assertEquals(1, (int) posMod.getSize());
			assertEquals(CIGARFlag.M, posMod.getFlag());
		}
	}
	
}