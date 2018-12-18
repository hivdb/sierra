package edu.stanford.hivdb.utilities;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TSVTest {

	@Test
	public void testDumpHeader() {
		String[] header = {"this", "is", "a", "test"};
		String eDumpedHeader = "this\tis\ta\ttest";
		assertEquals(eDumpedHeader, TSV.dumpsHeader(header));
	}

	@Test
	public void testDumpHeaderFromList() {
		List<String> header = Arrays.asList("this", "is", "a", "test");
		String eDumpedHeader = "this\tis\ta\ttest";
		assertEquals(eDumpedHeader, TSV.dumpsHeader(header));
	}

	@Test
	public void testDumpsBody() {
		String[][] body = {	{"this", "is", "line", "one"},
							{"this", "is", "line", "two"},
							{"line", "three"}};
		String eBody = 	"this	is	line	one\n" +
						"this	is	line	two\n" +
						"line	three";
		int eBodyLength = 44;
		String bodyDump = TSV.dumpsBody(body);
		assertEquals(eBodyLength, bodyDump.length());
		assertEquals(eBody, bodyDump);
	}

	@Test
	public void testDumpsBodyFromLists() {
		List<List<String>> body = Arrays.asList(Arrays.asList("this", "is", "line", "one"),
												Arrays.asList("this", "is", "line", "two"),
												Arrays.asList("line", "three"));
		String eBody = 	"this	is	line	one\n" +
						"this	is	line	two\n" +
						"line	three";
		int eBodyLength = 44;
		String bodyDump = TSV.dumpsBody(body);
		assertEquals(eBodyLength, bodyDump.length());
		assertEquals(eBody, bodyDump);
	}

	@Test
	public void testDumpsFromArrayAndCollection() {
		String[] header = {"this", "is", "a", "test"};
		List<List<String>> body = Arrays.asList(Arrays.asList("this", "is", "line", "one"),
												Arrays.asList("this", "is", "line", "two"),
												Arrays.asList("line", "three"));
		String eDump = 	"this	is	a	test\n" +
						"this	is	line	one\n" +
						"this	is	line	two\n" +
						"line	three";
		String bodyDump = TSV.dumps(header, body);
		System.out.println(bodyDump);
		assertEquals(eDump, bodyDump);
	}

	@Test
	public void testDumpsFromCollections() {
		List<String> header = Arrays.asList("this", "is", "a", "test");
		List<List<String>> body = Arrays.asList(Arrays.asList("this", "is", "line", "one"),
												Arrays.asList("this", "is", "line", "two"),
												Arrays.asList("line", "three"));
		String eDump = 	"this	is	a	test\n" +
						"this	is	line	one\n" +
						"this	is	line	two\n" +
						"line	three";
		String bodyDump = TSV.dumps(header, body);
		System.out.println(bodyDump);
		assertEquals(eDump, bodyDump);
	}
}
