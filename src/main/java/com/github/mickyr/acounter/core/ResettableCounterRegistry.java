package com.github.mickyr.acounter.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.mickyr.acounter.core.CounterResetPolicy;
import com.github.mickyr.acounter.core.ResettableCounter;
import com.github.mickyr.acounter.core.exception.IllegalCounterException;

public class ResettableCounterRegistry {
	
    private ConcurrentHashMap<String, ResettableCounter> counterRegistry = new ConcurrentHashMap<String, ResettableCounter>();

    /**
     * Add a new counter to the registry with the specified name and policy
     * 
     * @author mickyr
     * @param counterName the name for the counter to be added in the registry
     * @param policy the reset policy for the counter
     * @return true if a new counter was added, false if a counter with the same name exists
     */
    public boolean addCounter(String counterName, CounterResetPolicy policy){
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    	DateFormat dateFormat = new SimpleDateFormat(policy.getFormat());
    	String policyResetString = (policy.equals(CounterResetPolicy.MANUAL))?"":dateFormat.format(cal.getTime());
    	if(counterRegistry.putIfAbsent(counterName, new ResettableCounter(policyResetString, new AtomicInteger(), policy)) == null)
    		return true;
    	else return false;
    }
    
    /**
     * Increments the count of the counter
     * @param counterName the name of the counter to increment
     * @return the incremented counter value
     * @throws IllegalCounterException 
     */
    public int incrementCount(String counterName) throws IllegalCounterException{
        ResettableCounter counter = counterRegistry.get(counterName);
        if(counter == null)throw new IllegalCounterException("No such counter: "+counterName+" exists in this registry. Please add counter to this registry by using addCounter");
        DateFormat dateFormat = new SimpleDateFormat(counter.getPolicy().getFormat());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        //For scheduled reset counters, create new representation of time and compare it
        //to the last representation. If it has changed, the time window must have moved over
        //to a new one, so reset the counter
        if(!counter.getPolicy().equals(CounterResetPolicy.MANUAL) && !dateFormat.format(cal.getTime()).equals(counter.getCounterResetString())){
            int count = counter.getCount().get();
            if(counter.getCount().compareAndSet(count, 0))
                counter.setCounterResetString(dateFormat.format(cal.getTime()));
            counter.getCount().incrementAndGet();
            return counter.getCount().get();
        }
        else return counter.getCount().incrementAndGet();
    }
    
    /**
     * Returns the current count of the counter
     * Checks if the counter needs to be reset before returning count
     * 
     * @author mickyr
     * @param counterName The name of the counter
     * @return the current count of the counter
     * @throws IllegalCounterException 
     */
    public int getCount(String counterName) throws IllegalCounterException{
        ResettableCounter counter = counterRegistry.get(counterName);
        if(counter == null)throw new IllegalCounterException("No such counter: "+counterName+" exists in this registry. Please add counter to this registry by using addCounter");
        DateFormat dateFormat = new SimpleDateFormat(counter.getPolicy().getFormat());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        //For scheduled reset counters, create new representation of time and compare it
        //to the last representation. If it has changed, the time window must have moved over
        //to a new one, so reset the counter
        if(!counter.getPolicy().equals(CounterResetPolicy.MANUAL) && !dateFormat.format(cal.getTime()).equals(counter.getCounterResetString())){
            int count = counter.getCount().get();
            if(counter.getCount().compareAndSet(count, 0))
                counter.setCounterResetString(dateFormat.format(cal.getTime()));
        }
        return counter.getCount().get();
    }
    
    /**
     * Resets the counter to 0 and return the last count
     * 
     * @author mickyr
     * @param counterName The name of the counter to reset
     * @return the last count before resetting
     * @throws IllegalCounterException 
     */
    public int resetCounter(String counterName) throws IllegalCounterException{
        ResettableCounter counter = counterRegistry.get(counterName);
        if(counter == null || !counter.getPolicy().equals(CounterResetPolicy.MANUAL))throw new IllegalCounterException("No such counter: "+counterName+" exists in this registry or counter is not MANUAL. Please add counter to this registry by using addCounter");    	
        return counter.getCount().getAndSet(0);
    }
   
}