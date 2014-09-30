package mk.ukim.finki.os.synchronization.problems.modushall;

import mk.ukim.finki.os.synchronization.*;

public class ModusHallState extends AbstractState {

	public enum StatusState {
		NEUTRAL, HEATHENS_RULE, PRUDES_RULE, TRANSITION_TO_HEATHENS, TRANSITION_TO_PRUDES
	};

	private static final String HEATHENS_CANT_PASS = "The current state is PRUDES_RULES or TRANSITION_TO_PRUDES";
	private static final String PRUDES_CANT_PASS = "The current state is HEATHENS_RULES or TRANSITION_TO_HEATHENS";

	private static final int HEATHENS_CANT_PASS_POINTS = 15;
	private static final int PRUDES_CANT_PASS_POINTS = 15;

	private static final int WRONG_STATE_POINTS = 10;

	private static final String NOT_PARALLEL = "The crossing is not in parallel!";

	private static final int NOT_PARALLEL_POINTS = 5;

	StatusState state;

	private BoundCounterWithRaceConditionCheck prudesWaiting;
	private BoundCounterWithRaceConditionCheck prudesPassed;
	private BoundCounterWithRaceConditionCheck heathensPassed;
	private BoundCounterWithRaceConditionCheck heathensWaiting;

	public ModusHallState() {
		prudesWaiting = new BoundCounterWithRaceConditionCheck(0);
		prudesWaiting = new BoundCounterWithRaceConditionCheck(0);
		prudesPassed = new BoundCounterWithRaceConditionCheck(0);
		heathensPassed = new BoundCounterWithRaceConditionCheck(0);
		heathensWaiting = new BoundCounterWithRaceConditionCheck(0);
		state = StatusState.NEUTRAL;
	}

	public void setNeutral() {
		Switcher.forceSwitch(5);
		synchronized (this) {
			if (prudesWaiting.getValue() == 0
					&& heathensWaiting.getValue() == 0)
				state = StatusState.NEUTRAL;
			else
				new PointsException(WRONG_STATE_POINTS, ErrorMessageNeutral());
		}
	}

	private String ErrorMessageNeutral() {
		return "NEUTRAL state can not be set with " + prudesWaiting.getValue()
				+ " prudes and " + heathensWaiting.getValue() + " heathens.";
	}

	public void setHeathensRule() {
		synchronized (this) {
			if (getState() == StatusState.NEUTRAL
					&& prudesWaiting.getValue() == 0)
				state = StatusState.HEATHENS_RULE;
			
		}
	}

	private String ErrorMessageHeathensRules() {
		if (prudesWaiting.getValue() == 0)
			return "To set HEATHENS_RULE the previous state must be NEUTRAL";
		else
			return "Can not set HEATHENS_RULE with " + prudesWaiting.getValue()
					+ " prudes waiting to pass";
	}

	public void setTransitionHeathens() {
		synchronized (this) {
			if (state == StatusState.PRUDES_RULE
					&& heathensWaiting.getValue() > prudesWaiting.getValue())
				state = StatusState.TRANSITION_TO_HEATHENS;

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
			if (getState() == StatusState.NEUTRAL
					&& heathensWaiting.getValue() == 0)
				state = StatusState.PRUDES_RULE;

		}
	}

	private String ErrorMessagePrudesRules() {
		if (prudesWaiting.getValue() == 0)
			return "To set PRUDES_RULE the previous state must be NEUTRAL";
		else
			return "Can not set PRUDES_RULE with " + heathensWaiting.getValue()
					+ " heathens waiting to pass";
	}

	public void setTransitionPrudes() {
		synchronized (this) {
			if (state == StatusState.HEATHENS_RULE
					&& prudesWaiting.getValue() > heathensWaiting.getValue())
				state = StatusState.TRANSITION_TO_PRUDES;

		}
	}

	private String ErrorMessageTransitionPrudes() {
		if (state != StatusState.PRUDES_RULE)
			return "To set TRANSITION_TO_PRUDES the previous state must be HEATHEN_RULES";
		else
			return "#Heathens must be smaller than #Prudes. Current state: Heathens: "
					+ heathensWaiting + " - Prudes: " + prudesWaiting;
	}

	public StatusState getState() {
		return state;
	}

	public void crossPrude() {
		Switcher.forceSwitch(5);
		synchronized (this) {
			if (state == StatusState.TRANSITION_TO_PRUDES
					|| state == StatusState.PRUDES_RULE) {
				log(prudesWaiting.decrementWithMin(false),
						"Prude is leaving the turnsteel");
				log(prudesPassed.incrementWithMax(false),
						"Prude is crossing the field");
			} 
		}
	}

	public void crossHeathen() {
		Switcher.forceSwitch(5);
		synchronized (this) {
			if (state == StatusState.TRANSITION_TO_HEATHENS
					|| state == StatusState.HEATHENS_RULE) {
				log(heathensWaiting.decrementWithMin(false),
						"Heathen is leaving the turnsteel");
				log(heathensPassed.incrementWithMax(false),
						"Heathen is crossing the field");
			}
			}
	}

	public void enterPrude() {
		Switcher.forceSwitch(5);
		synchronized (this) {
			log(prudesWaiting.incrementWithMax(false),
					"Prude is passing the turnsteel");
		}
	}

	public void enterHeathen() {
		Switcher.forceSwitch(5);
		synchronized (this) {
			log(heathensWaiting.incrementWithMax(false),
					"Heathen is passing the turnsteel");
		}
	}

	public synchronized void reset() {
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
