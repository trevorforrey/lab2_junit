package banking.primitive.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import com.sun.java.swing.plaf.gtk.GTKConstants.StateType;

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
		accountServer.newAccount("Checking", "CheckingTest2", 100.0f);
		accountServer.newAccount("Checking", "CheckingTest3", 100.0f);
		accountServer.newAccount("Checking", "CheckingTest4", 100.0f);
		accountServer.newAccount("Checking", "CheckingTest5", 100.0f);
		accountServer.newAccount("Checking", "CheckingTest6", 100.0f);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWithdrawBasedOnAmount() {
		
		//Withdraw should only work on amounts > 0
		
		assertFalse(accountServer.getAccount("CheckingTest1").withdraw(-0.01f));
		assertFalse(accountServer.getAccount("CheckingTest1").withdraw(0.0f));
		assertTrue(accountServer.getAccount("CheckingTest1").withdraw(0.1f));
	}
	
	@Test
	public void testWithdrawBasedOnState() {
		
		// Test withdraw not available on closed account
		accountServer.getAccount("CheckingTest2").setState(State.CLOSED);
		assertFalse(accountServer.getAccount("CheckingTest2").withdraw(0.1f));
		
		// Test withdraw possible on open account
		accountServer.getAccount("CheckingTest2").setState(State.OPEN);
		assertTrue(accountServer.getAccount("CheckingTest2").withdraw(0.1f));
	}
	
	@Test
	public void testWithdrawFree() {
		
		// withdrawal fee is taken out from account after 10th withdrawal
		
		// Withdraw 10 times
		for (int i = 1; i <= 10; i++) {
			accountServer.getAccount("CheckingTest3").withdraw(5.0f);
		}
		
		// check no additional withdraw fee after 10 withdraws
		assertTrue(accountServer.getAccount("CheckingTest3").getBalance() == (100 - 50));
		
		// Should trigger 10th withdraw fee
		accountServer.getAccount("CheckingTest3").withdraw(5.0f);
		assertTrue(accountServer.getAccount("CheckingTest3").getBalance() == (100 - 50) - 5 - 2);
		
	}
	
	@Test
	public void testWithdrawOverDrawnState() {
		
		// Remaining Balance is 0, should not be OVERDRAWN
		accountServer.getAccount("CheckingTest4").withdraw(100.0f);
		assertFalse(accountServer.getAccount("CheckingTest4").getState() == State.OVERDRAWN);
		
		// Reset Balance Back to 100
		accountServer.getAccount("CheckingTest4").deposit(100.0f);
		
		// Take out more than current balance, state should be OVERDRAWN
		accountServer.getAccount("CheckingTest4").withdraw(101.0f);
		assertTrue(accountServer.getAccount("CheckingTest4").getState() == State.OVERDRAWN);

	}
	
	@Test
	public void testDepositAmountBasedOnAccountState() {
		
		// Deposits can not be made on closed accounts
		accountServer.getAccount("CheckingTest5").setState(State.CLOSED);
		assertFalse(accountServer.getAccount("CheckingTest5").deposit(100.0f));
		
		// After a deposit > 0, an account in OVERDRAWN state should change to
		// the state of OPEN
		accountServer.getAccount("CheckingTest5").setState(State.OVERDRAWN);
		accountServer.getAccount("CheckingTest5").deposit(100.0f);
		assertTrue(accountServer.getAccount("CheckingTest5").getState() == State.OPEN);
		
		// A deposit = 0 should not change an OVERDRAWN account to OPEN
		accountServer.getAccount("CheckingTest5").setState(State.OVERDRAWN);
		accountServer.getAccount("CheckingTest5").deposit(0.0f);
		assertTrue(accountServer.getAccount("CheckingTest5").getState() == State.OVERDRAWN);
	}
	
	@Test
	public void testDepositAmountBasedOnAmountDeposited() {
		
		// Only deposits > 0 are valid
		
		assertFalse(accountServer.getAccount("CheckingTest6").deposit(-1.0f));
		assertFalse(accountServer.getAccount("CheckingTest6").deposit(0.0f));
		assertTrue(accountServer.getAccount("CheckingTest6").deposit(0.1f));
		assertTrue(accountServer.getAccount("CheckingTest6").getState() == State.OPEN);
	}

}
