package com.github.mickyr.acounter.core;

import java.util.concurrent.atomic.AtomicInteger;

public class ResettableCounter {
	private String counterResetString;
	private AtomicInteger count;
	private CounterResetPolicy policy;

	public ResettableCounter(String counterResetString, AtomicInteger counter, CounterResetPolicy policy) {
		this.counterResetString = counterResetString;
		this.count = counter;
		this.policy = policy;
	}

	public AtomicInteger getCount() {
		return count;
	}

	public CounterResetPolicy getPolicy() {
		return policy;
	}

	public String getCounterResetString() {
		return counterResetString;
	}

	public void setCounterResetString(String counterResetString) {
		this.counterResetString = counterResetString;
	}

}