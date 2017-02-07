package banking.primitive.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import banking.primitive.core.Account.State;

public class SavingsTest {
	private static AccountServer accountServer = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		accountServer = AccountServerFactory.getMe().lookup();
	}
	
	@Before
	public void setUp() throws Exception {
		accountServer = AccountServerFactory.getMe().lookup();
		accountServer.newAccount("Savings", "SavingsTest1", 0.0f);
		accountServer.newAccount("Savings", "SavingsTest2", 0.0f);
		accountServer.newAccount("Savings", "SavingsTest3", 0.0f);
		accountServer.newAccount("Savings", "SavingsTest4", 0.0f);
		accountServer.newAccount("Savings", "SavingsTest5", 0.0f);
		accountServer.newAccount("Savings", "SavingsTest6", 0.0f);
		accountServer.newAccount("Savings", "SavingsTest7", 0.0f);
		accountServer.newAccount("Savings", "SavingsTest8", 0.0f);
		
		// Withdraw accounts
		accountServer.newAccount("Savings", "SavingsTest9", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest10", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest11", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest12", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest13", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest14", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest15", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest16", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest17", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest18", 100.0f);
		accountServer.newAccount("Savings", "SavingsTest19", 100.0f);
	}
	

	/*
	 * Testing Savings.Withdraw()
	 */

	@Test
	// 
	public void testDeposit() {
		assertTrue(accountServer.getAccount("SavingsTest1").deposit(100)); // Testing the deposit method. With correction method, now returns TRUE appropriately. 
	}
	
	// STATEMENT COVERAGE: The following should cover all statements 
	
	@Test
	public void stateDCoverageOne(){
		assertTrue(accountServer.getAccount("SavingsTest2").deposit(100));
	}
	
	@Test
	// Covers 
	public void stateDCoverageTwo(){
		assertFalse(accountServer.getAccount("SavingsTest3").deposit(0));
	}

	// EDGE COVERAGE: The following should ensure all edges are crossed. 
	
	@Test
	// Test used for Edge & Condition coverage. 
	public void edgeDCoverageOne(){
		assertTrue(accountServer.getAccount("SavingsTest4").deposit(0.49f));
	}
	
	// CONDITION COVERAGE: The following should cover the various conditional cases
	
	@Test
	// Testing outer-conditional when Account.getState() == State.CLOSED && deposit > 0
	public void conditionDCoverageOne(){
		accountServer.getAccount("SavingsTest5").setState(State.CLOSED);
		assertFalse(accountServer.getAccount("SavingsTest5").deposit(1));
	}	

	@Test
	// Testing outer-conditional when Account.getState() == State.OPEN && deposit > 0
	public void conditionDCoverageTwo(){
		accountServer.getAccount("SavingsTest6").setState(State.OPEN);
		assertTrue(accountServer.getAccount("SavingsTest6").deposit(1));
	}	

	@Test
	// Testing inner-conditional when Account.getState() == State.OPEN && deposit >= .5f
	public void conditionDCoverageThree(){
		accountServer.getAccount("SavingsTest7").setState(State.OPEN);
		assertTrue(accountServer.getAccount("SavingsTest7").deposit(1));
	}	
	
	@Test
	// Testing inner-conditional when Account.getState() == State.OPEN && deposit <.5f
	public void conditionDCoverageFour(){
		accountServer.getAccount("SavingsTest8").setState(State.OPEN);
		assertTrue(accountServer.getAccount("SavingsTest8").deposit(.49f));
	}
	
	
	/* 
	 * TESTING Savings.Withdraw() 
	 */
	
	@Test
	// Testing statement & edge coverage: Nothing withdrawn. 
	public void stateWCoverageOne(){
		assertFalse(accountServer.getAccount("SavingsTest9").withdraw(0));
	}
	
	@Test
	// Testing statement coverage && conditional coverage; when numWithdraws > 3
	public void stateWCoverageTwo(){
		for(int i=0;i<4;i++){
			accountServer.getAccount("SavingsTest10").withdraw(20);
			System.out.println(accountServer.getAccount("SavingsTest10").getBalance());
		}	
		assertTrue(accountServer.getAccount("SavingsTest10").getBalance() == (100-((4*20 + 1)))); // $1 fee for last $19 remaining in Savings account.
	}
	
	@Test
	// Testing statement && conditional coverage; when overdrawn
	public void stateWCoverageThree(){
		assertTrue(accountServer.getAccount("SavingsTest10").withdraw(105)); 
	}
	
	@Test
	// Testing Edge coverage: numWithdraws * 4, overdraw. Edges from 32--->42
	public void edgeWCoverageOne(){
		for(int i=0;i<4;i++)
			accountServer.getAccount("SavingsTest11").withdraw(20); // $19 remaining
		
		accountServer.getAccount("SavingsTest11").withdraw(20); // Overdraw
		
		assertTrue((accountServer.getAccount("SavingsTest11").getBalance() == -2) && (accountServer.getAccount("SavingsTest11").getState() == State.OVERDRAWN));
	}
	
	@Test
	// Testing edge coverage: numWithdraws < 3 with no overdraw, Edges: 32-->36-->39-->42
	public void edgeWCoverageTwo(){
		assertTrue(accountServer.getAccount("SavingsTest12").withdraw(20));
	}
	
	@Test
	// Testing edge && conditional coverage 32-->36-->39-->41. No overdraw, < 3 withdraws
	public void edgeWCoverageThree(){
		assertTrue(accountServer.getAccount("SavingsTest13").withdraw(20));
	}
	
	
	@Test
	//Testing #33 conditional: (**FALSE && TRUE**, TRUE && TRUE, FALSE && FALSE)
	public void conditionalWCoverageOne(){
		accountServer.getAccount("SavingsTest14").setState(State.CLOSED);
		assertFalse(accountServer.getAccount("SavingsTest14").withdraw(5));
	}
	@Test
	//Testing #33 conditional: (FALSE && TRUE, **TRUE && TRUE**, FALSE && FALSE)
	public void conditionalWCoverageTwo(){
		accountServer.getAccount("SavingsTest15").setState(State.OPEN);
		assertTrue(accountServer.getAccount("SavingsTest15").withdraw(5));
	}
	@Test
	//Testing #33 conditional: (FALSE && TRUE, TRUE && TRUE, **FALSE && FALSE**)
	public void conditionalWCoverageThree(){
		accountServer.getAccount("SavingsTest16").setState(State.CLOSED);
		assertFalse(accountServer.getAccount("SavingsTest16").withdraw(0));
	}
	
	
	
}
