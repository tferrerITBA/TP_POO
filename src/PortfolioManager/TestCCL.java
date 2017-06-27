package PortfolioManager;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/*
 * Timing is checked.
 * 
 * @author Alejandro Santoflaminio
 */

public class TestCCL {

	@Before
	public void setUp() throws Exception {
		//String s;
	}

	@Test(timeout=1000)
	public void testCCL() {
		CCL.getMinCCL();
		double a=CCL.getMinCCL();
	}

}
