package mk.ukim.finki.os.synchronization.problems.modushall;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import mk.ukim.finki.os.synchronization.ProblemExecution;
import mk.ukim.finki.os.synchronization.TemplateThread;
import mk.ukim.finki.os.synchronization.problems.modushall.ModusHallState.StatusState;



public class ModusHallSolution {

	static int heathens;
	static int prudes;
	static Semaphore mutex;
	static Semaphore heathenTurn;
	static Semaphore prudeTurn;
	static Semaphore heathenQueue;
	static Semaphore prudeQueue;

	public static void init() {
		heathens = 0;
		prudes = 0;
		mutex = new Semaphore(1);
		heathenTurn = new Semaphore(1);
		prudeTurn = new Semaphore(1);
		heathenQueue = new Semaphore(0);
		prudeQueue = new Semaphore(0);
	}

	public static class Heathen extends TemplateThread {

		public Heathen(int numRuns) {
			super(numRuns);
		}

		@Override
		public void execute() throws InterruptedException {

			heathenTurn.acquire();
			state.enterHeathen();
			heathenTurn.release();

			mutex.acquire();
			heathens++;

			if (state.getState() == StatusState.NEUTRAL) {
				state.setHeathensRule();
				mutex.release();
			} else if (state.getState() == StatusState.PRUDES_RULE) {
				if (heathens > prudes) {
					state.setTransitionHeathens();
					prudeTurn.acquire();
				}
				mutex.release();
				heathenQueue.acquire();
			} else if (state.getState() == StatusState.TRANSITION_TO_HEATHENS) {
				mutex.release();
				heathenQueue.acquire();
			} else {
				mutex.release();
			}

			// cross the field

			mutex.acquire();
			state.crossHeathen();
			heathens--;

			if (heathens == 0) {
				if (state.getState() == StatusState.TRANSITION_TO_PRUDES) {
					prudeTurn.release();
				}
				if (prudes != 0) {
					prudeQueue.release(prudes);
					state.setPrudesRule();
				} else {
					state.setNeutral();
				}
			}

			if (state.getState() == StatusState.HEATHENS_RULE) {
				if (prudes > heathens) {
					state.setTransitionPrudes();
					heathenTurn.acquire();
				}
			}
			mutex.release();
		}

	}

	public static class Prude extends TemplateThread {

		public Prude(int numRuns) {
			super(numRuns);
		}

		@Override
		public void execute() throws InterruptedException {

			prudeTurn.acquire();
			state.enterPrude();
			prudeTurn.release();			
			
			mutex.acquire();
			prudes++;

			if (state.getState() == StatusState.NEUTRAL) {
				state.setPrudesRule();
				mutex.release();
			} else if (state.getState() == StatusState.HEATHENS_RULE) {
				if (prudes > heathens) {
					state.setTransitionPrudes();
					heathenTurn.acquire();
				}
				mutex.release();
				prudeTurn.acquire();
			} else if (state.getState() == StatusState.TRANSITION_TO_PRUDES) {
				mutex.release();
				prudeQueue.acquire();
			} else {
				mutex.release();
			}

			// cross the field

			mutex.acquire();
			state.crossPrude();
			prudes--;

			if (prudes == 0) {
				if (state.getState() == StatusState.TRANSITION_TO_HEATHENS) {
					heathenTurn.release();
				}
				if (heathens != 0) {
					heathenQueue.release(heathens);
					state.setHeathensRule();
				} else
					state.setNeutral();
			}

			if (state.getState() == StatusState.PRUDES_RULE) {
				if (heathens > prudes) {
					state.setTransitionHeathens();
					prudeTurn.acquire();
				}
			}
			mutex.release();
		}

	}

	static ModusHallState state = new ModusHallState();

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			run();
		}
	}

	public static void run() {
		try {
			int numRuns = 1;
			int numScenarios = 200;

			HashSet<Thread> threads = new HashSet<Thread>();

			for (int i = 0; i < numScenarios; i++) {
				Prude p = new Prude(numRuns);
				Heathen h = new Heathen(numRuns);
				threads.add(p);
				threads.add(h);
			}

			init();

			ProblemExecution.start(threads, state);
			System.out.println(new Date().getTime());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
