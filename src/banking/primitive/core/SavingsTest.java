package banking.primitive.core;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SavingsTest {
	private static AccountServer accountServer = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		accountServer = AccountServerFactory.getMe().lookup();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
		accountServer.newAccount("Savings", "SavingTest1", 100.0f);
	}


	@Test
	public void testDeposit() {
		assertTrue(accountServer.getAccount("SavingTest1").deposit(100)); // Testing the deposit method. With correction method, now returns TRUE appropriately. 
	}

}
