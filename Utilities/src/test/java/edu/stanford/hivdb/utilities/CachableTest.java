package edu.stanford.hivdb.utilities;

//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import edu.stanford.hivdb.mutations.Mutation;

public class CachableTest {

//	public static class Run implements Runnable {
//		public void run() {}
//	}
//
//	public static class Klass {
//		@Cachable.CachableField
//		public static String word;
//		@Cachable.CachableField
//		public char letter;
//
//		public int number;
//	}

//	@Before
//	public void clearSys() {
//		System.clearProperty("hivdb.updateCachable");
//		Cachable.forceUpdate = false;
//	}

//	@Test
//	public void testConstructor() {
//		Cachable cache = new Cachable(Mutation.class);
//		assertFalse(Cachable.forceUpdate);
//	}
//
//
//	@Test
//	public void testLoadStatic() {
//		Cachable cache = new Cachable(Klass.class);
//		cache.loadStatic();
//	}
//
//	@Test
//	public void testLoadStaticAfterUpdate() {
//		Cachable cache = new Cachable(Mutation.class);
//		Cachable.forceUpdate = true;
//		cache.loadStatic();
//	}
//
//	@Test
//	public void testSetup() {
//		Cachable cache = Cachable.setup(Mutation.class);
//	}
//
//	@Test
//	public void testSetupWithLoadData() {
//		Cachable cache = Cachable.setup(Mutation.class, new CachableTest.Run());
//	}

//	@Test
//	public void testMain() {
//		System.setProperty("hivdb.updateCachable", CachableTest.Klass.class.getName());
//		Cachable.main(null);
//		assertTrue(Cachable.forceUpdate);
//	}
//
//	@Test
//	public void testMainWithNullClass() {
//		System.setProperty("hivdb.updateCachable", "");
//		Cachable.main(null);
//		assertTrue(Cachable.forceUpdate);
//	}
//
//	@Test(expected=ExceptionInInitializerError.class)
//	public void testMainWithNonexistentClass() {
//		System.setProperty("hivdb.updateCachable", "not-a-class");
//		Cachable.main(null);
//	}
//
//	@Test
//	public void testMainWithoutClass() {
//		Cachable.main(null);
//		assertFalse(Cachable.forceUpdate);
//	}
}
