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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FastaUtilsTest {

	static List<Sequence> eSequences = new ArrayList<>();

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	File tmpFile;
	String tmpFilePath;

	@BeforeClass
	public static void initSequences() throws IOException {
		eSequences.add(new Sequence(
				"seq1", "AV---GTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC..."));
		eSequences.add(new Sequence(
				"seq2-cc.z", "ACGTACGTACGAACGTCATGCATCCATGCATGTAGCTAGCTAGCTAGC"));
		eSequences.add(new Sequence(
				"seq3", "RMRHARMSSDVN"));
	}

	@Before
	public void initTempFile() throws IOException {
		tmpFile = tmpFolder.newFile("test.txt");
		tmpFilePath = tmpFile.getAbsolutePath();
		assertEquals(0, tmpFile.length());
	}

	@Test
	public void testConstructor() throws NoSuchMethodException, SecurityException {
		Constructor<FastaUtils> c = FastaUtils.class.getDeclaredConstructor();
		assertFalse(c.isAccessible());
	}

	@Test
	public void testFetchGenbank() {
		List<String> accessions = Arrays.asList("186416");
		Sequence seq = FastaUtils.fetchGenbank(accessions).get(0);
		assertEquals(Integer.valueOf(1966), seq.getLength());
		assertEquals("M13437.1 Human ovarian beta-B inhibin mRNA", seq.getHeader());
		assertEquals("d0b264921eec89f8ab2a57c11bad292c", seq.getMD5());
		assertEquals("35af5703862dd708a8ac1edc6b740e272e2ea85ee77643e5f0" +
					 "46e283d5eb2823e44f9772ab8f4d9d356f0e8ffc484d916b43" +
					 "0dec1db960c48125a528f859f97d", seq.getSHA512());
	}

	@Test
	public void testFetchGenbankWithException() {
		FastaUtils.fetchGenbank(Arrays.asList("{public void run}"));
	}

	@Test
	public void testReadString() {
		assertEquals(
			eSequences,
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
	}

	@Test
	public void testReadStringOfEmptySeq() {
		List<Sequence> emptySequence = new ArrayList<>();
		assertEquals(
			emptySequence,
			FastaUtils.readString(">EMPTY-SEQUENCE"));
	}

	@Test
	public void testReadStringOfUnamedSeq() {
		List<Sequence> unamedSeq = new ArrayList<>();
		unamedSeq.add(new Sequence(
			"UnnamedSequence", "ACGTACGTACGT"));
		assertEquals(
			unamedSeq,
			FastaUtils.readString("ACGTACGTACGT"));
	}

	@Test
	public void testReadStringOfError() {
		List<Sequence> emptySeq = new ArrayList<>();
		assertEquals(
			emptySeq,
			FastaUtils.readString(" Error"));
		assertEquals(
			emptySeq,
			FastaUtils.readString("Bad id."));
	}

	@Test
	public void testReadFile() {
		assertEquals(
			eSequences, FastaUtils.readFile(
			"src/test/resources/FASTAReaderTestSample.fasta"));
	}

	@Test(expected=RuntimeException.class)
	public void testReadFileWithException() {
		FastaUtils.readFile("/%$%$/");
	}

	@Test
	public void testWriteStream() throws IOException {
		OutputStream stream = new FileOutputStream(tmpFilePath);
		FastaUtils.writeStream(eSequences, stream);
		List<Sequence> seq = FastaUtils.readFile(tmpFilePath);
		assertEquals(eSequences.get(1), seq.get(1));
	}

	@Test
	public void testWriteFileOfSingleSeq() throws IOException {
		FastaUtils.writeFile(eSequences.get(0), tmpFilePath);
		String result = new String(Files.readAllBytes(Paths.get(tmpFilePath)));
		String eSeq =
			">seq1\n" +
			"AVGTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC\n";
		assertEquals(eSeq, result);
	}

	@Test
	public void testWriteFileOfMultipleSeqs() throws IOException {
		FastaUtils.writeFile(eSequences, tmpFilePath);
		String result = new String(Files.readAllBytes(Paths.get(tmpFilePath)));
		String eSeqs =
			">seq1\n" +
			"AVGTACGTACGTACGTCATGCATGCATGCATGTAGCTAGCTAGCTAGC\n" +
			">seq2-cc.z\n" +
			"ACGTACGTACGAACGTCATGCATCCATGCATGTAGCTAGCTAGCTAGC\n" +
			">seq3\n" +
			"RMRHARMSSDVN\n";
		assertEquals(eSeqs, result);
	}

	@Test(expected=RuntimeException.class)
	public void testWriteFileWithExcpetion() {
		FastaUtils.writeFile(eSequences.get(0), "/%$%$/");
	}

	@Test
	public void testWriteString() {
		String seq = FastaUtils.writeString(Arrays.asList(eSequences.get(0)));
		assertTrue(seq.startsWith(eSequences.get(0).toString()));
	}
}
