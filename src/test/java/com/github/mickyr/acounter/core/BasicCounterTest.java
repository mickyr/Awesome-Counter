package com.github.mickyr.acounter.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.mickyr.acounter.core.exception.IllegalCounterException;

public class BasicCounterTest {

	@Test
	public void testBasicIncrement() throws InterruptedException, IllegalCounterException {
		ResettableCounterRegistry registry = new ResettableCounterRegistry();
		registry.addCounter("second", CounterResetPolicy.PERSECOND);
		registry.addCounter("minute", CounterResetPolicy.PERMINUTE);
		registry.addCounter("manual", CounterResetPolicy.MANUAL);
		registry.incrementCount("second");
		registry.incrementCount("second");
		registry.incrementCount("minute");
		registry.incrementCount("minute");		
		registry.incrementCount("manual");
		registry.incrementCount("manual");
		
		assertTrue(registry.getCount("second") == 2);
		
		Thread.sleep(1000);
		assertTrue(registry.getCount("second") == 0);
		assertTrue(registry.incrementCount("second") == 1);
		
		assertTrue(registry.getCount("minute") == 2);
		assertTrue(registry.incrementCount("minute") == 3);
		
		assertTrue(registry.getCount("manual") == 2);
		assertTrue(registry.incrementCount("manual") == 3);
		
		registry.resetCounter("manual");
		
		try{
			registry.resetCounter("minute");
			fail("Only manual counters can be reset manually.");
		}catch(IllegalCounterException ex){
			System.out.println("As Expected only manual counters can be reset manually.");
		}
		
		assertTrue(registry.getCount("minute") == 3);
		assertTrue(registry.incrementCount("minute") == 4);	
		
		assertTrue(registry.getCount("manual") == 0);
		assertTrue(registry.incrementCount("manual") == 1);
		
	}

}
