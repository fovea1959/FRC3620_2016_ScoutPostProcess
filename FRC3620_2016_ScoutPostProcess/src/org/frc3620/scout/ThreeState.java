package org.frc3620.scout;

public class ThreeState {
	SuccessFail partial, full;
	
	public void skip() {
		partial.skip();
		full.skip();
	}

	public void partialFailure() {
		partial.failure();
		full.skip();
	}

	public void partialSuccess() {
		partial.success();
		full.skip();
	}

	public void fullFailure() {
		partial.success();
		full.failure();
	}

	public void fullSuccess() {
		partial.success();
		full.success();
	}

}
