package org.frc3620.scout;

public class Rating {
	private int sum;
	private int attempts;
	private StringBuilder sparkLine = new StringBuilder();
	
	public void recordSkip() {
		sparkLine.append("\u00b7");
	}
	
	public void recordRank(int i) {
		// TODO fix this (single digit)
		sparkLine.append("" + i);
		sum += i;
		attempts ++;
	}
	
	public String getSparkline () {
	  return sparkLine.toString();
	}
	
	public double getAverageRating() {
   return Util.ratio(sum, attempts);
	}

}
