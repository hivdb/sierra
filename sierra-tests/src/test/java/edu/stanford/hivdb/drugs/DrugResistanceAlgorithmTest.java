package edu.stanford.hivdb.drugs;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import edu.stanford.hivdb.testutils.TestUtils;

public class DrugResistanceAlgorithmTest {
	
    private final static HIV hiv = HIV.getInstance();
    private static DrugResistanceAlgorithm<HIV> algorithm;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testDrugResistanceAlgorithm() throws IOException {
    	InputStream stream = TestUtils.readTestResource("algorithms/HIVDB_8.9.xml");
   	 	String xmlText = IOUtils.toString(stream, StandardCharsets.UTF_8);
    	DrugResistanceAlgorithm<HIV> algo = new DrugResistanceAlgorithm<HIV>(
    			hiv, xmlText);
    	
    	assertNotNull(algo);
    }
    
    @Test
    public void testDrugResistanceAlgorithm2() throws IOException {
    	InputStream stream = TestUtils.readTestResource("algorithms/HIVDB_8.9.xml");
   	 	String xmlText = IOUtils.toString(stream, StandardCharsets.UTF_8);
    	DrugResistanceAlgorithm<HIV> algo = new DrugResistanceAlgorithm<HIV>(
    			"HIVDB_8.9", hiv, xmlText);
    	
    	assertNotNull(algo);
    }

    @Test
    public void testDrugResistanceAlgorithm3() throws IOException {
    	InputStream stream = TestUtils.readTestResource("algorithms/HIVDB_8.9.xml");
   	 	String xmlText = IOUtils.toString(stream, StandardCharsets.UTF_8);
    	DrugResistanceAlgorithm<HIV> algo = new DrugResistanceAlgorithm<HIV>(
    			"HIVDB_8.9", "HIVDB", "8.9", "2019-10-07", hiv, xmlText);
    	
    	assertNotNull(algo);
    }
    
    @Test
    public void testDrugResistanceAlgorithm2WithException() {
   	 	String xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + 
   	 			"<!DOCTYPE ALGORITHM SYSTEM \"http://sierra2.stanford.edu/sierra/ASI2.1.dtd\">\n" + 
   	 			"<ALGORITHM>\n" + 
   	 			"  <ALGNAME>HIVDB</ALGNAME>\n" + 
   	 			"  <ALGVERSION>8.9</ALGVERSION>\n" + 
   	 			"  <ALGDATE>2019-10-07</ALGDATE>\n" + 
   	 			"</ALGORITHM>";
   	 	thrown.expect(ExceptionInInitializerError.class);
    	new DrugResistanceAlgorithm<HIV>("HIVDB", hiv, xmlText);
    	
    	// INFO: ExceptionInInitializerError in initGeneMap won't be triggered
    	//       if getAlgorithmInfo is ok.
    }
    
    private static void initInstance() throws IOException {
    	InputStream stream = TestUtils.readTestResource("algorithms/HIVDB_8.9.xml");
   	 	String xmlText = IOUtils.toString(stream, StandardCharsets.UTF_8);
    	algorithm = new DrugResistanceAlgorithm<HIV>(
    			hiv, xmlText);
    }
    static {
    	try {
			initInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testGetName() {
    	assertEquals(algorithm.getName(), "HIVDB_8.9");
    }
    
    @Test
    public void testGetDisplay() {
    	assertEquals(algorithm.getDisplay(), "HIVDB 8.9");
    }
    
    @Test
    public void testName() {
    	assertEquals(algorithm.getName(), algorithm.name());
    }
    
    @Test
    public void testGetFamily() {
    	assertEquals(algorithm.getFamily(), "HIVDB");
    }
    
    @Test
    public void testGetVersion() {
    	assertEquals(algorithm.getVersion(), "8.9");
    }
    
    @Test
    public void testGetPublishDate() {
    	assertEquals(algorithm.getPublishDate(), "2019-10-07");
    }
    
    @Test
    public void testGetOriginalLevelText() {
    	assertEquals(algorithm.getOriginalLevelText(), "Susceptible");
    }
    
    @Test
    public void testGetOriginalLevelSIR() {
    	assertEquals(algorithm.getOriginalLevelSIR(), "S");
    }
    
    @Test
    public void testGetASIGene() {
    	assertEquals(algorithm.getASIGene(hiv.getGene("HIV1PR")).getName(), "PR");
    }
    
    @Test
    public void testGetXMLText() throws IOException {
    	InputStream stream = TestUtils.readTestResource("algorithms/HIVDB_8.9.xml");
   	 	String xmlText = IOUtils.toString(stream, StandardCharsets.UTF_8);
    	assertEquals(algorithm.getXMLText(), xmlText);
    }
    
    @Test
    public void testGetEnumCompatName() throws IOException {
    	assertEquals(algorithm.getEnumCompatName(), "HIVDB_8_9");
    	
    	InputStream stream = TestUtils.readTestResource("algorithms/HIVDB_8.9.xml");
   	 	String xmlText = IOUtils.toString(stream, StandardCharsets.UTF_8);
    	DrugResistanceAlgorithm<HIV> algo = new DrugResistanceAlgorithm<HIV>(
    			"9", "HIVDB", "8.9", "2019-10-07", hiv, xmlText);
    	
    	assertEquals(algo.getEnumCompatName(), "_9");
    }
    
    @Test
    public void testToString() {
    	assertEquals(algorithm.toString(), algorithm.name());
    }
}