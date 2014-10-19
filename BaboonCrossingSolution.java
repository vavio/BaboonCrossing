package mk.ukim.finki.os.synchronization.problems.babooncrossing;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;


import mk.ukim.finki.os.synchronization.ProblemExecution;
import mk.ukim.finki.os.synchronization.TemplateThread;

/**
 * 
 * @author Valentin Ambaroski
 * @author Vladica Jovanovski
 * 
 */

public class BaboonCrossingSolution {
	
	//Kontrola na jazheto od koja strana pominuvaat
	static Semaphore mutexRope;
	
	//Kontrola za broj na majmuni koi chekaat levo i desno
	static Semaphore mutexLeft;
	static Semaphore mutexRight;
	
	//Kontrola na majmuni za vlez vo chekalnata
	static Semaphore turnStyle;
	
	//Kontrola na brojot na majmuni koi se kacheni na jazhe
	static Semaphore onRope;

	static int left;
	static int right;

	public static void init() {
		mutexRope = new Semaphore(1);
		mutexLeft = new Semaphore(1);
		mutexRight = new Semaphore(1);
		turnStyle = new Semaphore(1);
		onRope = new Semaphore(6);
		left = right = 0;
	}

	public static class BaboonLeft extends TemplateThread {

		public BaboonLeft(int numRuns) {
			super(numRuns);
		}

		@Override
		public void execute() throws InterruptedException {
			turnStyle.acquire();
			state.enter(this);
			mutexLeft.acquire();
			left++;
			if (left == 1)
			{
				mutexRope.acquire();
				state.leftPassing();
			}
			mutexLeft.release();
			turnStyle.release();

			onRope.acquire();
			state.cross(this);
			onRope.release();

			mutexLeft.acquire();
			left--;
			state.leave(this);
			if (left == 0) {
				mutexRope.release();
			}
			
			mutexLeft.release();
		}

	}

	public static class BaboonRight extends TemplateThread {

		public BaboonRight(int numRuns) {
			super(numRuns);
		}

		@Override
		public void execute() throws InterruptedException {
			turnStyle.acquire();
			state.enter(this);
			mutexRight.acquire();
			right++;
			if (right == 1)
			{
				mutexRope.acquire();
				state.rightPassing();
			}
			mutexRight.release();
			turnStyle.release();

			onRope.acquire();
			state.cross(this);
			onRope.release();

			mutexRight.acquire();
			right--;
			state.leave(this);
			if (right == 0) {
				mutexRope.release();
			}
			mutexRight.release();
		}
	}

	static BaboonCrossingState state = new BaboonCrossingState();

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			run();
		}
	}

	public static void run() {
		try {
			int numRuns = 1;
			int numScenarios = 500;

			HashSet<Thread> threads = new HashSet<Thread>();

			for (int i = 0; i < numScenarios; i++) {
				BaboonLeft l = new BaboonLeft(numRuns);
				BaboonRight r = new BaboonRight(numRuns);
				threads.add(l);
				threads.add(r);
			}

			init();

			ProblemExecution.start(threads, state);
			System.out.println(new Date().getTime());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
