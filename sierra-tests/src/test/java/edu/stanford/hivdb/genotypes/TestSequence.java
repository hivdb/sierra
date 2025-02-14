package edu.stanford.hivdb.genotypes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class TestSequence {

	public String sequence;
	public int firstNA;
	public int lastNA;
	public String accession;

	public static TestSequence loadResource(String fileName) {
		InputStream json = (
			TestSequence.class.getClassLoader()
			.getResourceAsStream("GenotypeTestSequences/" + fileName));
		return new Gson().fromJson(
				new BufferedReader(new InputStreamReader(json)),
			    TestSequence.class);
	}

}
