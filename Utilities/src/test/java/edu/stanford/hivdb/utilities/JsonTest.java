package edu.stanford.hivdb.utilities;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.mutations.GenePosition;
import edu.stanford.hivdb.mutations.MutationSet;

public class JsonTest {
	
	@Test
	public void testDumpsWithMutatinSet() {
		String json = 	"[\n" + 
						"  {\n" + 
						"    \"type\": \"IUPAC\",\n" +
						"    \"aas\": \"T\",\n" + 
						"    \"triplet\": \"\",\n" + 
						"    \"insertedNAs\": \"\",\n" + 
						"    \"gene\": \"PR\",\n" + 
						"    \"position\": 55,\n" + 
						"    \"maxDisplayAAs\": 4\n" +
						"  },\n" + 
						"  {\n" +
						"    \"type\": \"AA\",\n" +
						"    \"gene\": \"PR\",\n" + 
						"    \"position\": 56,\n" + 
						"    \"aaChars\": [\n" +
						"      \"T\"\n" +
						"    ],\n" +
						"    \"maxDisplayAAs\": 4\n" +
						"  }\n" + 
						"]";
		MutationSet set = Json.loads(json, MutationSet.class);
		String eJson = Json.dumps(set);
		assertEquals(eJson, json);
	}
	
	@Test
	public void testDumpsWithGenePosition() {
		String json = 	"{\n" + 
						"  \"gene\": \"PR\",\n" + 
						"  \"position\": 55\n" + 
						"}";
		GenePosition gp = new GenePosition("PR:55");
		String eJson = Json.dumps(gp);		
		assertEquals(eJson, json);
	}
	
	@Test
	public void testLoadsMutatinSetFromClass() {
		MutationSet eSet = new MutationSet("PR:55T PR:56T");
		MutationSet jsonSet = Json.loads(Json.dumps(eSet), MutationSet.class);
		assertEquals(jsonSet, eSet);
	}
	
	@Test
	public void testLoadsWithGenePositionFromClass() {
		GenePosition eGp = new GenePosition("PR:55");
		GenePosition jsonGp = Json.loads("\"PR:55\"", GenePosition.class);
		assertEquals(eGp, jsonGp);
	}
	
	@Test
	public void testLoadsMutatinSetFromType() {
		MutationSet eSet = new MutationSet("PR:55T PR:56T");
		MutationSet jsonSet = Json.loads(Json.dumps(eSet), TypeToken.get(MutationSet.class).getType());
		assertEquals(jsonSet, eSet);
	}
	
	@Test
	public void testLoadsWithGenePositionFromType() {
		GenePosition eGp = new GenePosition("PR:55");
		GenePosition jsonGp = Json.loads("\"PR:55\"", TypeToken.get(GenePosition.class).getType());
		assertEquals(eGp, jsonGp);
	}
	
	@Test
	public void testLoadsFromReaderAndClass() {
		GenePosition eGp = new GenePosition("PR:55");
		Reader json = new StringReader("\"PR:55\"");
		GenePosition jsonGp = Json.loads(json, GenePosition.class);
		assertEquals(eGp, jsonGp);
	}
	 
	@Test
	public void testLoadsFromReaderAndType() {
		MutationSet eSet = new MutationSet("PR:55T PR:56T");
		Reader json = new StringReader(Json.dumps(eSet));
		MutationSet jsonSet = Json.loads(json, TypeToken.get(MutationSet.class).getType());
		assertEquals(eSet, jsonSet);
	}
}