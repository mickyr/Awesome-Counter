# ResettableThreadSafeCounter
A Simple api to create thread safe counters in java that are reset based in time interval. Named counters that are reset to 0 on a daily, hourly or per minute basis. A sample usage - Keep counter for a specific application/Business event to be used for logging or checking for limits. Check if particular resource has been used more than its daily limit and stop access to it.

The following test would help explain its usage:

```java

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
		
		//The counter with per second reset policy resets after 1 second
		assertTrue(registry.getCount("second") == 0);
		assertTrue(registry.incrementCount("second") == 1);
		
		assertTrue(registry.getCount("minute") == 2);
		assertTrue(registry.incrementCount("minute") == 3);
		
		assertTrue(registry.getCount("manual") == 2);
		assertTrue(registry.incrementCount("manual") == 3);
		
		//Manually reset manual policy counter
		registry.resetCounter("manual");
		
		assertTrue(registry.getCount("manual") == 0);
		assertTrue(registry.incrementCount("manual") == 1);		
		
		try{
			registry.resetCounter("minute");
			fail("Only manual counters can be reset manually.");
		}catch(IllegalCounterException ex){
			System.out.println("As Expected only manual counters can be reset manually.");
		}
		
		assertTrue(registry.getCount("minute") == 3);
		assertTrue(registry.incrementCount("minute") == 4);	

```
