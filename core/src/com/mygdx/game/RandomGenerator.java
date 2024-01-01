package com.mygdx.game;

import java.util.LinkedList;
import java.util.Random;

public class RandomGenerator {

	private final Random randomGenerator = new Random();
	private LinkedList<Range> xRanges = new LinkedList<Range>();
	private LinkedList<Range> yRanges = new LinkedList<Range>();

	
	public int generateRandomInXRanges() {
		int randomRangeIndex = randomGenerator.nextInt(xRanges.size());
		Range selectedRange = xRanges.get(randomRangeIndex);
		
		return randomGenerator.nextInt(selectedRange.max - selectedRange.min + 1) + selectedRange.min;	
	}
	
	public int generateRandomInYRanges() {
		int randomRangeIndex = randomGenerator.nextInt(yRanges.size()); 
		Range selectedRange = yRanges.get(randomRangeIndex);
		
		return randomGenerator.nextInt(selectedRange.max - selectedRange.min + 1) + selectedRange.min;	
	}
	
	public void addXRange(Range range) {
		xRanges.add(range);
	}
	
	public void addYRange(Range range) {
		yRanges.add(range);
	}
	
	public void clearRanges() {
		xRanges.clear();
		yRanges.clear();
	}
}


