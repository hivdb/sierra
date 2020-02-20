package edu.stanford.hivdb.testutils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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

}