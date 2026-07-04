package edu.stanford.hivdb.utilities;


import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.testutils.TestUtils;


public class FastaUtilsTest {

    @Test
    public void fetchGenbankTest() {
    	List<String> accessions = new ArrayList<>();
    	
    	accessions.add("AF096883");
    	accessions.add("Z48731");
    	
    	List<Sequence> result = FastaUtils.fetchGenbank(accessions);
    	
    	assertEquals(result.size(), 2);

    }

    @Test
    public void readFileTest() {
    	ClassLoader classLoader = getClass().getClassLoader();
    	String path  = classLoader.getResource(TestSequencesProperties.SMALL.toString()).getPath();
    	assertTrue(FastaUtils.readFile(path) instanceof List);
    }
    
    @Test(expected = RuntimeException.class)
    public void readFileTestException() {
    	String path = "";
    	FastaUtils.readFile(path);
    }

    @Test
    public void readStreamTest() {
        final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
    	assertTrue(FastaUtils.readStream(testSequenceInputStream) instanceof List);
    }
    
    @Test
    public void readStringTest() {
        final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
    	List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
    	String str = sequences.get(0).toString();
    	assertTrue(FastaUtils.readString(str) instanceof List);
    }

    @Test
    public void writeStreamTest() {
        final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
    	List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream); 	
   
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	FastaUtils.writeStream(sequences, out);

    }

    @Test
    public void writeStreamSHA512NameTest() {
        // A non-ASCII character in the header would crash the ASCII-only
        // aligner (postalign) if sent as-is. writeStream(..., true) must emit
        // the ASCII SHA-512 hash as the FASTA name instead, keeping the whole
        // stream ASCII-safe while the Sequence's real header stays untouched.
        Sequence seq = new Sequence("Pop11_771491955Ê_2020Aug25_virus", "ACGTACGT");
        // the header itself is left unchanged
        assertEquals("Pop11_771491955Ê_2020Aug25_virus", seq.getHeader());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastaUtils.writeStream(List.of(seq), out, true);
        byte[] bytes = out.toByteArray();

        // the emitted stream must not contain any non-ASCII byte
        for (byte b : bytes) {
            assertTrue("writeStream output must be ASCII-only", (b & 0x80) == 0);
        }

        // the FASTA name is the SHA-512 hash, not the original header
        String text = new String(bytes, StandardCharsets.US_ASCII);
        assertTrue(text.contains(seq.getSHA512()));
        assertFalse(text.contains("Pop11_771491955"));
    }

    @Test
    public void writeFileTest() {
        final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
    	List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
    	
    	String testPath = TestUtils.TEST_RESULT_FOLDER;
    	String filePath = testPath + "FastaUtilsTest.txt";
    	
    	FastaUtils.writeFile(sequences, filePath);

    }

    @Test
    public void writeFile2Test() {
        final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
    	List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
    	
    	String testPath = TestUtils.TEST_RESULT_FOLDER;
    	String filePath = testPath + "FastaUtilsTest.txt";
    	
    	FastaUtils.writeFile(sequences.get(0), filePath);
    }

    @Test
    public void writeStringTest() {
        final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
    	List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
    	
    	assertTrue(FastaUtils.writeString(sequences) instanceof String);
    }
}