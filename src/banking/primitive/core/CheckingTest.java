package banking.primitive.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.java.swing.plaf.gtk.GTKConstants.StateType;

import banking.primitive.core.Account.State;

public class CheckingTest {
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
		accountServer.newAccount("Checking", "CheckingTest1", 100.0f);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWithdrawBasedOnAmount() {
		assertFalse(accountServer.getAccount("CheckingTest1").withdraw(-0.01f));
		
		assertFalse(accountServer.getAccount("CheckingTest1").withdraw(0.0f));
		
		assertTrue(accountServer.getAccount("CheckingTest1").withdraw(0.1f));
	}
	
	@Test
	public void testWithdrawBasedOnState() {
		accountServer.getAccount("CheckingTest1").setState(State.CLOSED);
		assertFalse(accountServer.getAccount("CheckingTest1").withdraw(0.1f));
		
		accountServer.getAccount("CheckingTest1").setState(State.OPEN);
		assertTrue(accountServer.getAccount("CheckingTest1").withdraw(0.1f));
	}
	
	@Test
	public void testWithdrawFree() {
		
		for (int i = 1; i <= 10; i++) {
			accountServer.getAccount("CheckingTest1").withdraw(5.0f);
		}
		
		// check no additional withdraw fee after 10 withdraws
		assertTrue(accountServer.getAccount("CheckingTest1").getBalance() == (100 - 50));
		
		// Should trigger 10th withdraw fee
		accountServer.getAccount("CheckingTest1").withdraw(5.0f);
		assertTrue(accountServer.getAccount("CheckingTest1").getBalance() == (100 - 50) - 5 - 2);
		
	}
	
	@Test
	public void testWithdrawOverDrawnState() {
		
		// Remaining Balance is 0, should not be OVERDRAWN
		accountServer.getAccount("CheckingTest1").withdraw(100.0f);
		assertFalse(accountServer.getAccount("CheckingTest1").getState() == State.OVERDRAWN);
		
		// Reset Balance Back to 100
		accountServer.getAccount("CheckingTest1").deposit(100.0f);
		
		// Take out more than current balance, state should be OVERDRAWN
		accountServer.getAccount("CheckingTest1").withdraw(101.0f);
		assertTrue(accountServer.getAccount("CheckingTest1").getState() == State.OVERDRAWN);

	}
	
	@Test
	public void testDepositAmountBasedOnAccountState() {
		accountServer.getAccount("CheckingTest1").setState(State.CLOSED);
		assertFalse(accountServer.getAccount("CheckingTest1").deposit(100.0f));
		
		accountServer.getAccount("CheckingTest1").setState(State.OVERDRAWN);
		accountServer.getAccount("CheckingTest1").deposit(100.0f);
		assertTrue(accountServer.getAccount("CheckingTest1").getState() == State.OPEN);
		
		accountServer.getAccount("CheckingTest1").setState(State.OVERDRAWN);
		accountServer.getAccount("CheckingTest1").deposit(0.0f);
		assertTrue(accountServer.getAccount("CheckingTest1").getState() == State.OVERDRAWN);
	}
	
	@Test
	public void testDepositAmountBasedOnAmountDeposited() {
		assertFalse(accountServer.getAccount("CheckingTest1").deposit(-1.0f));
		assertFalse(accountServer.getAccount("CheckingTest1").deposit(0.0f));
		assertTrue(accountServer.getAccount("CheckingTest1").deposit(0.1f));
		assertTrue(accountServer.getAccount("CheckingTest1").getState() == State.OPEN);
	}

}
