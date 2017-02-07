package banking.primitive.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import banking.primitive.core.Account.State;

public class AccountServerTest {
	private static AccountServer accountServer = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		accountServer = AccountServerFactory.getMe().lookup();
	}

	@Before
	public void setUp() throws Exception {
		accountServer.newAccount("Checking", "CheckingTest1", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest1", 200.0f);
		accountServer.newAccount("Checking", "CheckingTest2", 300.0f);
		accountServer.newAccount("Savings", "SavingsTest2", 400.0f);
	}

	/**
	 * testNewAccountsType checks for valid input types in the 1st parameter
	 */
	@Test (expected=java.lang.IllegalArgumentException.class)
	public void testNewAccountType() {
        assertTrue(!accountServer.newAccount("BLAH", "BadChecking1", 10.0f));
        assertNull(accountServer.getAccount("BadChecking1"));
	}
	
    /**
     * testNewAccountsBalance checks for valid input values in the 3rd parameter
     */
    @Test (expected=java.lang.IllegalArgumentException.class)
    public void testNewAccountBalance() {
      assertTrue(!accountServer.newAccount("Checking", "BadChecking2", -10.0f));
      assertTrue(!accountServer.newAccount("Savings", "BadSavings2", -10.0f));
      assertNull(accountServer.getAccount("BadChecking2"));
      assertNull(accountServer.getAccount("BadChecking2"));
    }   

    /**
     * testNewAccountDuplicate checks that accounts with duplicate names can't be added
     */
    @Test
    public void testNewAccountDuplicate() {
        assertTrue(!accountServer.newAccount("Savings", "CheckingTest1", 500.0f));
        assertTrue(!accountServer.newAccount("Savings", "SavingsTest1", 500.0f));
    }
    
	/**
	 * testNewAccount checks the behavior of adding an account
	 * Question: What should the BVA result in here?
	 */
	@Test
	public void testNewAccount() {
		// Get all the accounts
		List<Account> accounts = accountServer.getAllAccounts();
		
		// Add the accounts
		if (accountServer.newAccount("Checking",  "Checkingnewtest1", 100.0f) &&
		    accountServer.newAccount("Savings",  "Savingsnewtest1", 100.0f)) {
		
		    // Now when I get the accounts again there should be 2 new ones
	        List<Account>updatedAccounts = accountServer.getAllAccounts();
	        // this assert checks that we didn't blow away one that shouldn't be touched
	        assertTrue(updatedAccounts.containsAll(accounts));
	        // These check what we put in got in
	        assertNotNull(accountServer.getAccount("Checkingnewtest1"));
	        assertNotNull(accountServer.getAccount("Savingsnewtest1"));
		} else {
		    fail("failed to add new acocunts");
		}
	}

	@Test
	public void testGetAccount() {
		accountServer.newAccount("Checking", "CheckingGetTest", 100.0f);
		
		// how were each of these tests arrived at?
		assertNotNull(accountServer.getAccount("CheckingGetTest"));
		assertNull(accountServer.getAccount(null));
		assertNull(accountServer.getAccount(""));
		assertNull(accountServer.getAccount("  blah   blah   "));
		assertNull(accountServer.getAccount("CheckingGetTes"));
		assertNull(accountServer.getAccount("heckingGetTest"));
		assertNull(accountServer.getAccount("CheckingGetTest2"));
		assertNull(accountServer.getAccount("Checking GetTest"));
		assertNull(accountServer.getAccount("checkinggettest"));
	}
	
	// SER316 Task 2 Step 3 Part d
	// 
	
	@Test
	public void testcloseAccount() {
		accountServer.newAccount("Checking", "CheckingCloseTest", 100.0f);
		
		// Test successful closing of account
		assertTrue(accountServer.closeAccount("CheckingCloseTest"));
		assertTrue(accountServer.getAccount("CheckingCloseTest").getState() == State.CLOSED);
		
		// Test closing of NE account
		assertFalse(accountServer.closeAccount("NotExistentAccount"));
		
	}
	
	@Test
	public void testGetActiveAccounts() {
		
		// Create accounts and set their state to closed
		accountServer.newAccount("Checking", "ClosedAccount1", 100.0f);
		accountServer.newAccount("Checking", "ClosedAccount2", 100.0f);
		accountServer.newAccount("Checking", "ClosedAccount3", 100.0f);
		
		accountServer.getAccount("ClosedAccount1").setState(State.CLOSED);
		accountServer.getAccount("ClosedAccount2").setState(State.CLOSED);
		accountServer.getAccount("ClosedAccount3").setState(State.CLOSED);
		
		List<Account> result = accountServer.getActiveAccounts();
		
		// For every active account retrieved, check there is one of the closed accounts as well as if there
		// are any closed accounts inside active account array list
		for (int i = 0; i < result.size(); i++) {
			assertTrue(result.get(i) != accountServer.getAccount("ClosedAccount1"));
			assertTrue(result.get(i) != accountServer.getAccount("ClosedAccount2"));
			assertTrue(result.get(i) != accountServer.getAccount("ClosedAccount3"));
			assertFalse(result.get(i).getState() == State.CLOSED);
		}
	}
	
	@Test
	public void testSaveAccounts() throws IOException {
		// Test proper saving of accounts
		assertTrue(accountServer.saveAccounts());
	}
}
