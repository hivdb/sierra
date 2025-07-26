package edu.stanford.hivdb.motif;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.motif.MotifPosition;
import edu.stanford.hivdb.motif.MotifUtils;

public class MotifUtilsTest {
	
	@Test
	public void testParseMotif() {
		List<MotifPosition> motif = MotifUtils.parseMotif("N-~P-[ST]");
		System.out.println(motif);
	}

}
