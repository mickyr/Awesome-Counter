# Awesome-Counter Wiki

Awesome-Counter is a simple api to create thread safe counters in java that are reset based on different policies(e.g. manual, time-interval based).  
It allows you to create registries with named counters that are reset to 0 on a policy(e.g. daily, hourly or per minute) basis.  
The most important thing is that it is **Thread-safe and yet fast** ( Hopefully :) ). So it should make a good server side component for performance oriented web-applications.  

A sample usage - Keep counter for a specific application/Business event to be used for logging, monitoring or alerting if certain thresholds are rechaed within a certain time limit or simply to keep a minute by minute graph or user interations with a feature in your application or to check if particular resource has been used more than its time based limit and stop access to it. 

The following test snippet should help explain its usage:

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
