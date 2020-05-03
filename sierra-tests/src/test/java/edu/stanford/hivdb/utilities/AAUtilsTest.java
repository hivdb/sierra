package edu.stanford.hivdb.utilities;


import org.junit.Test;
import static org.junit.Assert.*;

public class AAUtilsTest {

	@Test
	public void normalizeAAsTest() {

		assertNull(AAUtils.normalizeAAs(null));

		assertEquals(AAUtils.normalizeAAs("~"), "-");
		assertEquals(AAUtils.normalizeAAs("d"), "-");
		assertEquals(AAUtils.normalizeAAs("del"), "-");
		assertEquals(AAUtils.normalizeAAs("delete"), "-");
		assertEquals(AAUtils.normalizeAAs("deletion"), "-");
		assertEquals(AAUtils.normalizeAAs("Delete"), "-");
		assertEquals(AAUtils.normalizeAAs("Deletion"), "-");

		assertEquals(AAUtils.normalizeAAs("D"), "D");

		assertEquals("-A", AAUtils.normalizeAAs("~a"));
		assertEquals(AAUtils.normalizeAAs("a~"), "-A");
		assertEquals(AAUtils.normalizeAAs("ad"), "-A");
		assertEquals(AAUtils.normalizeAAs("da"), "-A");
		assertEquals(AAUtils.normalizeAAs("adelete"), "-AEET");
		assertEquals(AAUtils.normalizeAAs("deletea"), "-A");
		assertEquals(AAUtils.normalizeAAs("adelbcd"), "--ABC");

		assertEquals(AAUtils.normalizeAAs("-a"), "-A");
		assertEquals(AAUtils.normalizeAAs("a-"), "-A");
		assertEquals(AAUtils.normalizeAAs("a-b"), "-AB");



		assertEquals(AAUtils.normalizeAAs("#"), "_");
		assertEquals(AAUtils.normalizeAAs("i"), "_");
		assertEquals(AAUtils.normalizeAAs("ins"), "_");
		assertEquals(AAUtils.normalizeAAs("insert"), "_");
		assertEquals(AAUtils.normalizeAAs("insertion"), "_");
		assertEquals(AAUtils.normalizeAAs("Insert"), "_");
		assertEquals(AAUtils.normalizeAAs("Insertion"), "_");

		assertEquals(AAUtils.normalizeAAs("I"), "I");


		assertEquals("_A", AAUtils.normalizeAAs("ia"));
		assertEquals(AAUtils.normalizeAAs("ai"), "A_");
		assertEquals(AAUtils.normalizeAAs("abci"), "ABC_");
		assertEquals(AAUtils.normalizeAAs("inserta"), "_A");
		assertEquals(AAUtils.normalizeAAs("a#b#c#"), "A_B_C_");

		assertEquals(AAUtils.normalizeAAs("."), "*");
		assertEquals(AAUtils.normalizeAAs("Z"), "*");


		assertEquals(AAUtils.normalizeAAs("jkihgfedca"), "JK_HGFE-CA");


	}

	@Test
	public void toHIVDBFormatTest() {

		assertEquals(AAUtils.toHIVDBFormat("INInsertionIN"), "IN#IN");
		assertEquals(AAUtils.toHIVDBFormat("INDeletionIN"), "IN~IN");
		assertEquals(AAUtils.toHIVDBFormat("INinsIN"), "IN#IN");
		assertEquals(AAUtils.toHIVDBFormat("INdelIN"), "IN~IN");
		assertEquals(AAUtils.toHIVDBFormat("INiIN"), "IN#IN");
		assertEquals(AAUtils.toHIVDBFormat("IN_IN"), "IN#IN");
		assertEquals(AAUtils.toHIVDBFormat("INdIN"), "IN~IN");
		assertEquals(AAUtils.toHIVDBFormat("IN-IN"), "IN~IN");

		assertEquals("K", AAUtils.toHIVDBFormat("K"));
		assertEquals("D", AAUtils.toHIVDBFormat("D"));
		assertEquals("I", AAUtils.toHIVDBFormat("I"));
		assertEquals("#", AAUtils.toHIVDBFormat("Insertion"));
		assertEquals("#", AAUtils.toHIVDBFormat("ins"));
		assertEquals("#", AAUtils.toHIVDBFormat("i"));
		assertEquals("#", AAUtils.toHIVDBFormat("_"));
		assertEquals("#", AAUtils.toHIVDBFormat("#"));
		assertEquals("~", AAUtils.toHIVDBFormat("Deletion"));
		assertEquals("~", AAUtils.toHIVDBFormat("del"));
		assertEquals("~", AAUtils.toHIVDBFormat("d"));
		assertEquals("~", AAUtils.toHIVDBFormat("-"));
		assertEquals("~", AAUtils.toHIVDBFormat("~"));
	}

	@Test
	public void toInternalFormatTest() {
		assertEquals(AAUtils.toInternalFormat("INInsertionIN"), "IN_IN");
		assertEquals(AAUtils.toInternalFormat("INDeletionIN"), "IN-IN");
		assertEquals(AAUtils.toInternalFormat("INinsIN"), "IN_IN");
		assertEquals(AAUtils.toInternalFormat("INdelIN"), "IN-IN");
		assertEquals(AAUtils.toInternalFormat("INiIN"), "IN_IN");
		assertEquals(AAUtils.toInternalFormat("IN#IN"), "IN_IN");
		assertEquals(AAUtils.toInternalFormat("INdIN"), "IN-IN");
		assertEquals(AAUtils.toInternalFormat("IN~IN"), "IN-IN");

		assertEquals("K", AAUtils.toInternalFormat("K"));
		assertEquals("D", AAUtils.toInternalFormat("D"));
		assertEquals("I", AAUtils.toInternalFormat("I"));
		assertEquals("_", AAUtils.toInternalFormat("Insertion"));
		assertEquals("_", AAUtils.toInternalFormat("ins"));
		assertEquals("_", AAUtils.toInternalFormat("i"));
		assertEquals("_", AAUtils.toInternalFormat("#"));
		assertEquals("_", AAUtils.toInternalFormat("_"));
		assertEquals("-", AAUtils.toInternalFormat("Deletion"));
		assertEquals("-", AAUtils.toInternalFormat("del"));
		assertEquals("-", AAUtils.toInternalFormat("d"));
		assertEquals("-", AAUtils.toInternalFormat("~"));
		assertEquals("-", AAUtils.toInternalFormat("-"));
	}

	@Test
	public void toASIFormatTest() {
		assertEquals(AAUtils.toASIFormat("INInsertionIN"), "INiIN");
		assertEquals(AAUtils.toASIFormat("INDeletionIN"), "INdIN");
		assertEquals(AAUtils.toASIFormat("INinsIN"), "INiIN");
		assertEquals(AAUtils.toASIFormat("INdelIN"), "INdIN");
		assertEquals(AAUtils.toASIFormat("IN#IN"), "INiIN");
		assertEquals(AAUtils.toASIFormat("IN_IN"), "INiIN");
		assertEquals(AAUtils.toASIFormat("IN~IN"), "INdIN");
		assertEquals(AAUtils.toASIFormat("IN-IN"), "INdIN");


		assertEquals("K", AAUtils.toASIFormat("K"));
		assertEquals("D", AAUtils.toASIFormat("D"));
		assertEquals("I", AAUtils.toASIFormat("I"));
		assertEquals("i", AAUtils.toASIFormat("Insertion"));
		assertEquals("i", AAUtils.toASIFormat("ins"));
		assertEquals("i", AAUtils.toASIFormat("_"));
		assertEquals("i", AAUtils.toASIFormat("#"));
		assertEquals("i", AAUtils.toASIFormat("i"));
		assertEquals("d", AAUtils.toASIFormat("Deletion"));
		assertEquals("d", AAUtils.toASIFormat("del"));
		assertEquals("d", AAUtils.toASIFormat("-"));
		assertEquals("d", AAUtils.toASIFormat("~"));
		assertEquals("d", AAUtils.toASIFormat("d"));

	}

}