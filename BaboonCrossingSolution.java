// 
// Eve ja verzijata na solution-ot so povikuvanja na fuknciite
// Se mislam dali da da imame i edna funkcija plus za da bidi state.neutral()

package mk.ukim.finki.os.synchronization.problems.babooncrossing;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;


import mk.ukim.finki.os.synchronization.ProblemExecution;
import mk.ukim.finki.os.synchronization.TemplateThread;

public class BaboonCrossingSolution {
	static Semaphore mutexRope;
	static Semaphore mutexLeft;
	static Semaphore mutexRight;
	static Semaphore turnStyle;
	static Semaphore onRope;

	static int left;
	static int right;

	public static void init() {
		mutexRope = new Semaphore(1);
		mutexLeft = new Semaphore(1);
		mutexRight = new Semaphore(1);
		turnStyle = new Semaphore(1);
		onRope = new Semaphore(5);
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
				state.leftRules();
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
				state.rightRules();
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
			int numScenarios = 300;

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
