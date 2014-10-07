import java.util.concurrent.Semaphore;

public class Baboon {
	static Semaphore mutexRope;
	static Semaphore mutexLeft;
	static Semaphore mutexRight;
	static Semaphore turnStyle;
	static Semaphore onRope; 
	
	static int left;
	static int right;
	
	public class BaboonLeft implements Runnable{
		@Override
		public void run(){
			turnStyle.acquire();
			mutexLeft.acquire();
			left++;
			if (left == 1)
				mutexRope.acquire();
			mutexLeft.release();
			turnStyle.release();
			
			onRope.acquire();
			//	cross();
			onRope.release();
			
			mutexLeft.acquire();
			left--;
			if (left == 0) {
				mutexRope.release();
			}
			mutexLeft.release();
		}
	}
	
	public class BaboonRight implements Runnable{
		@Override
		public void run(){
			turnStyle.acquire();
			mutexRight.acquire();
			right++;
			if (right == 1)
				mutexRope.acquire();
			mutexRight.release();
			turnStyle.release();
			
			onRope.acquire();
			//	cross();
			onRope.release();
			
			mutexRight.acquire();
			right--;
			if (right == 0) {
				mutexRope.release();
			}
			mutexRight.release();
		}
	}
	
	static void init() {
		mutexRope = new Semaphore(1);
		mutexLeft = new Semaphore(1);
		mutexRight = new Semaphore(1);
		turnStyle = new Semaphore(1);
		onRope = new Semaphore(5);
		left = right = 0;
	}
	
	public static void main(String[] args) {
	}
}
