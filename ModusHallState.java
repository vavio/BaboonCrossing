package mk.ukim.finki.os.synchronization.problems.modushall;

import mk.ukim.finki.os.synchronization.*;

public class ModusHallState extends AbstractState {

	public enum StatusState {
		NEUTRAL, HEATHENS_RULE, PRUDES_RULE, TRANSITION_TO_HEATHENS, TRANSITION_TO_PRUDES
	};

	private static final String HEATHENS_CANT_PASS = "The current state is PRUDES_RULES or TRANSITION_TO_PRUDES or NEUTRAL";
	private static final String PRUDES_CANT_PASS = "The current state is HEATHENS_RULES or TRANSITION_TO_HEATHENS";
	
	private static final int HEATHENS_CANT_PASS_POINTS = 15;
	private static final int PRUDES_CANT_PASS_POINTS = 15;

	private static final int WRONG_STATE_POINTS = 10;

	private static final String NOT_PARALLEL = "The crossing is not in parallel!";
;
	private static final int NOT_PARALLEL_POINTS = 5;



	StatusState state;

	private BoundCounterWithRaceConditionCheck prudesWaiting;
	private BoundCounterWithRaceConditionCheck prudesPassed;
	private BoundCounterWithRaceConditionCheck heathensPassed;
	private BoundCounterWithRaceConditionCheck heathensWaiting;

	public ModusHallState() {
		prudesWaiting = new BoundCounterWithRaceConditionCheck(0, null, 0, null, 0, 10, "Less than points"); 
		prudesWaiting = new BoundCounterWithRaceConditionCheck(0);
		prudesPassed = new BoundCounterWithRaceConditionCheck(0);
		heathensPassed = new BoundCounterWithRaceConditionCheck(0);
		heathensWaiting = new BoundCounterWithRaceConditionCheck(0);
		state = StatusState.NEUTRAL;
	}

	public void setNeutral() {
		synchronized (this) {
			if (prudesWaiting.getValue() == 0 && heathensWaiting.getValue() == 0)
				state = StatusState.NEUTRAL;
			else
				new PointsException(WRONG_STATE_POINTS, ErrorMessageNeutral());
		}
	}

	private String ErrorMessageNeutral() {
		return String.format(
				"NEUTRAL state can not be set with %d prudes and %d heathens.",
				prudesWaiting, heathensWaiting);
	}

	public void setHeathensRule() {
		synchronized (this) {
			if (getState() == StatusState.NEUTRAL && prudesWaiting.getValue() == 0)
				state = StatusState.HEATHENS_RULE;
			else
				new PointsException(WRONG_STATE_POINTS,
						ErrorMessageHeathensRules());
		}
	}

	private String ErrorMessageHeathensRules() {
		if (prudesWaiting.getValue() == 0)
			return "To set HEATHENS_RULE the previous state must be NEUTRAL";
		else
			return String.format("Can not set HEATHENS_RULE with %d prudes waiting to pass",
					prudesWaiting);
	}

	public void setTransitionHeathens() {
		synchronized (this) {
			if (state == StatusState.PRUDES_RULE
					&& heathensWaiting.getValue() > prudesWaiting.getValue())
				state = StatusState.TRANSITION_TO_HEATHENS;
			else
				new PointsException(WRONG_STATE_POINTS,
						ErrorMessageTransitionHeathens());
		}
	}

	private String ErrorMessageTransitionHeathens() {
		if (state != StatusState.PRUDES_RULE)
			return "To set TRANSITION_TO_HEATHENS the previous state must be PRUDES_RULES";
		else
			return String
					.format("#Prudes must be smaller than #Heathens. Current state: Heathens: %d - Prudes: %d",
							heathensWaiting, prudesWaiting);
	}

	public void setPrudesRule() {
		synchronized (this) {
			if (getState() == StatusState.NEUTRAL && heathensWaiting.getValue() == 0)
				state = StatusState.PRUDES_RULE;
			else
				new PointsException(WRONG_STATE_POINTS,
						ErrorMessagePrudesRules());
		}
	}

	private String ErrorMessagePrudesRules() {
		if (prudesWaiting.getValue() == 0)
			return "To set PRUDES_RULE the previous state must be NEUTRAL";
		else
			return String.format("Can not set PRUDES_RULE with %d heathens waiting to pass",
					heathensWaiting);
	}

	public void setTransitionPrudes() {
		synchronized (this) {
			if (state == StatusState.HEATHENS_RULE
					&& prudesWaiting.getValue() > heathensWaiting.getValue())
				state = StatusState.TRANSITION_TO_PRUDES;
			else
				new PointsException(WRONG_STATE_POINTS,
						ErrorMessageTransitionPrudes());
		}
	}

	private String ErrorMessageTransitionPrudes() {
		if (state != StatusState.PRUDES_RULE)
			return "To set TRANSITION_TO_PRUDES the previous state must be HEATHEN_RULES";
		else
			return String
					.format("#Heathens must be smaller than #Prudes. Current state: Heathens: %d - Prudes: %d",
							heathensWaiting, prudesWaiting);
	}

	public StatusState getState() {
		return state;
	}

	public void crossPrude()
	{
		synchronized (this) {
			if (state == StatusState.TRANSITION_TO_PRUDES)
			{
				log(prudesWaiting.decrementWithMin(false), "Prude is leaving the turnsteel");
				log(prudesPassed.incrementWithMax(false), "Prude is crossing the field");
			}
			else
				throw new PointsException(PRUDES_CANT_PASS_POINTS, PRUDES_CANT_PASS);
		}
	}
	
	public void crossHeathen()
	{
		synchronized (this) {
			if (state == StatusState.TRANSITION_TO_PRUDES)
			{
				log(heathensWaiting.decrementWithMin(false), "Heathen is leaving the turnsteel");
				log(heathensPassed.incrementWithMax(false), "Heathen is crossing the field");
			}
			else
				throw new PointsException(HEATHENS_CANT_PASS_POINTS, HEATHENS_CANT_PASS);
		}
	}
	
	

	private synchronized void reset() {
		prudesWaiting.setValue(0);
		prudesPassed.setValue(0);
		heathensWaiting.setValue(0);
		heathensPassed.setValue(0);
		state = StatusState.NEUTRAL;
	}

	@Override
	public synchronized void finalize() {
		if (prudesWaiting.getMax() == 1 && heathensWaiting.getMax() == 1) {
			logException(new PointsException(NOT_PARALLEL_POINTS, NOT_PARALLEL));
		}
	}

}
