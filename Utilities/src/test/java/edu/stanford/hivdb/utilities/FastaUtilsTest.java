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

package edu.stanford.hivdb.utilities;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FastaUtilsTest {

	@Test
	public void testReadString() {
		List<Sequence> expecteds = new ArrayList<>();
		expecteds.add(new Sequence(
			"seq1", "AV---GTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC..."));
		expecteds.add(new Sequence(
			"seq2-cc.z", "ACGTACGTACGAACGTCATGCATCCATGCATGTAGCTAGCTAGCTAGC"));
		expecteds.add(new Sequence(
			"seq3", "RMRHARMSSDVN"));
		assertEquals(
			expecteds,
			FastaUtils.readString(
				"#comment1\n" +
				">empty\n" +
				">seq1\n" +
				"AV---GTACGTACGTACGT\n" +
				"CATGCATGCATGCATG\n" +
				"TAGCTAGCTAGCTAGC...\n\n" +
				"#comment2\n" +
				">seq2-cc.z\n" +
				"ACGTACGTACGAACGT\n" +
				"CATGCATCCATGCATG\n" +
				"TAGCTAGCTAGCTAGC\n" +
				">seq3\n" +
				"| rm -rf harmless\n" +
				"< /dev/null"
			));
		List<Sequence> expecteds2 = new ArrayList<>();
		assertEquals(
			expecteds2,
			FastaUtils.readString(">EMPTY-SEQUENCE"));
		List<Sequence> expecteds3 = new ArrayList<>();
		expecteds3.add(new Sequence(
			"UnnamedSequence", "ACGTACGTACGT"));
		assertEquals(
			expecteds3,
			FastaUtils.readString("ACGTACGTACGT"));
	}

	@Test
	public void testReadFile() {
		List<Sequence> expecteds = new ArrayList<>();
		expecteds.add(new Sequence(
			"seq1", "AV---GTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC..."));
		expecteds.add(new Sequence(
			"seq2-cc.z", "ACGTACGTACGAACGTCATGCATCCATGCATGTAGCTAGCTAGCTAGC"));
		expecteds.add(new Sequence(
			"seq3", "RMRHARMSSDVN"));
		assertEquals(
			expecteds, FastaUtils.readFile(
			"src/test/resources/FASTAReaderTestSample.fasta"));
	}

	@Test
	public void testWriteFile() throws IOException {
		List<Sequence> sequences = new ArrayList<>();
		sequences.add(new Sequence(
			"seq1", "AV---GTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC..."));
		sequences.add(new Sequence(
			"seq2-cc.z", "ACGTACGTACGAACGTCATGCATCCATGCATGTAGCTAGCTAGCTAGC"));
		sequences.add(new Sequence(
			"seq3", "RMRHARMSSDVN"));
		String testFilePath = "/tmp/fastaUtilsWriteFile.fasta";
		FastaUtils.writeFile(sequences, testFilePath);
		String result = new String(Files.readAllBytes(Paths.get(testFilePath)));
		String expecteds =
			">seq1\n" +
			"AVGTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC\n" +
			">seq2-cc.z\n" +
			"ACGTACGTACGAACGTCATGCATCCATGCATGTAGCTAGCTAGCTAGC\n" +
			">seq3\n" +
			"RMRHARMSSDVN\n";
		assertEquals(expecteds, result);
		Files.deleteIfExists(Paths.get(testFilePath));

		FastaUtils.writeFile(sequences.get(0), testFilePath);
		result = new String(Files.readAllBytes(Paths.get(testFilePath)));
		expecteds =
			">seq1\n" +
			"AVGTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC\n";
		assertEquals(expecteds, result);
		Files.deleteIfExists(Paths.get(testFilePath));
	}

	@Test
	public void testConstructor() throws NoSuchMethodException, SecurityException {
		Constructor<FastaUtils> c = FastaUtils.class.getDeclaredConstructor();
		assertFalse(c.isAccessible());
	}

}
