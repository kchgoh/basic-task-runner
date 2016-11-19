package basictaskrunner;

import java.util.Date;

public class DummyMessageListener {
	/**
	 * Stop going into any wait or further work. 
	 */
	private volatile boolean stopSignal;
	/**
	 * Ensure the stop interrupt is only when sleep, not during IO to sink
	 */
	private volatile boolean isSleeping;
	/**
	 * Wait on this to ensure the stop is REALLY done. otherwise may get race
	 * condition like this instance is not quite done yet but the caller thinks
	 * it's done and cut client connection.
	 */
	private volatile boolean stopCompleted;
	private volatile Thread runningThread;

	public void run(OutputSink status, String echo) {
		runningThread = Thread.currentThread();
		while(!stopSignal) {
			status.onUpdate(new Date().toString() + " : " + echo);
			
			if(!stopSignal) {
				try {
					isSleeping = true;
					Thread.sleep(10000L);
					isSleeping = false;
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		status.onUpdate("Listener stopped");
		status.onCompleted();
		stopCompleted = true;
	}
	
	public void stop() {
		// NB race condition still possible if we set stopSignal just after the IF-check
		// but not yet in Thread.sleep. then the interrupt would miss and the running thread
		// have to finish the sleep. it's possible to hack it further by checking the sleep
		// status again in the stopCompleted spin wait loop, but i'll leave it for now.
		stopSignal = true;
		if(isSleeping)
			runningThread.interrupt();
		while(!stopCompleted)
			;
	}
}
