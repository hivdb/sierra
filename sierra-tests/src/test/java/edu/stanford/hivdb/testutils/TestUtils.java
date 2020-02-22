package edu.stanford.hivdb.testutils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import edu.stanford.hivdb.genotypes.GenotypeRegressionTest;
import edu.stanford.hivdb.utilities.MyFileUtils;

public class TestUtils {
	
	private static String TEST_RESULT_FOLDER = "./test/"; 


	public static void writeFile(String filePath, String output) {
	
		filePath = TEST_RESULT_FOLDER + filePath;
		MyFileUtils.writeFile(filePath, output.toString());
	}
	
	public static FileOutputStream getFileOutputStream(String filePath) throws FileNotFoundException {
		filePath = TEST_RESULT_FOLDER + filePath;
		
		return new FileOutputStream(filePath);
	}
	
	public static InputStream readTestResource(String filePath) throws FileNotFoundException {
		InputStream json = (
				TestUtils.class.getClassLoader()
				.getResourceAsStream(filePath));
		
		return json;
	}

	public static void writeTSVFile(String filePath, String[] header, List<List<String>> rows) {
		StringBuilder fileContent = new StringBuilder();
		String headerLine = String.join("\t", header);
		
		fileContent.append(headerLine);
		fileContent.append("\n");
		
		
		for (List<String> row: rows) {
			fileContent.append(
					String.join("\t", row)
					);
			fileContent.append("\n");
		}

		writeFile(filePath, fileContent.toString());
		
		return;
	}
	
	public static boolean isTravisBuild() {
		return System.getenv().getOrDefault("TRAVIS", "false").equals("true");
	}
}