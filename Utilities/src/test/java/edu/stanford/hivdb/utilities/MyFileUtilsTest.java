package edu.stanford.hivdb.utilities;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MyFileUtilsTest {
	
	File file;
	String filePath;
	String fileContent = "content";
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before 
	public void resetFile() throws IOException {
		file = folder.newFile("test.txt");
		filePath = file.getAbsolutePath();
		assertEquals(0, file.length());
	}
	
	@Test
	public void testWriteFile() throws IOException {
		MyFileUtils.writeFile(file, fileContent);
		assertEquals(file.getParentFile(), folder.getRoot());
		assertEquals(fileContent.length(), file.length());
		Files.lines(file.toPath())
			 .forEach(line -> assertEquals(fileContent, line));
	}
	
	@Test
	public void testWriteFileFromPath() throws IOException {	
		MyFileUtils.writeFile(filePath, fileContent);
		assertEquals(file.getParentFile(), folder.getRoot());
		assertEquals(fileContent.length(), file.length());
		Files.lines(file.toPath())
			 .forEach(line -> assertEquals(fileContent, line));
	}
	
	@Test(expected=RuntimeException.class)
	public void testWriteFileFailure() {
		file = new File("");
		MyFileUtils.writeFile(file, fileContent);
	}
	
	@Test
	public void testAppendFile() throws IOException {	
		MyFileUtils.writeFile(filePath, fileContent);
		MyFileUtils.appendFile(filePath, fileContent);
		assertEquals(file.getParentFile(), folder.getRoot());
		assertEquals(2 * fileContent.length(), file.length());
		Files.lines(file.toPath())
			 .forEach(line -> assertEquals(fileContent + fileContent, line));
	}
	
	@Test(expected=RuntimeException.class)
	public void testAppendFileFailure() {
		MyFileUtils.appendFile("", fileContent);
	}
	
	@Test
	public void testReadResource() throws IOException {
		BufferedReader br = MyFileUtils.readResource(this.getClass(), "utilities.properties");
		assertEquals("mysqlHIVDB_Scores=jdbc:mysql://10.77.6.244/HIVDB_Scores", br.readLine());
	}
}