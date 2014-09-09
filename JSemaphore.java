/*
	Class that implements Semaphore functionality for use in
	threaded Java applications.
	written by Shafik Amin, 4-16-2003
*/

public final class JSemaphore
{
	/** Instance fields **/
	private int state;
	private long timeout;

	/* Constructors */
	public JSemaphore(int initial, long sectimeout)
	{
		state = initial < 0 ? 0 : initial;
		timeout = sectimeout < 0 ? 0 : sectimeout*1000;
	}

	public JSemaphore()
	{
		this(0, 60*60);
	}

	/* V's the semaphore's state, never blocks */
	public synchronized void V()
	{
		state++;
		notifyAll();
	}

	/* P's the semaphore's state, blocks when it reaches zero */
	public synchronized void P()
	{
		state--;
		if (state <= 0) /* change back to while for normal functionality */
		{
			try { wait(timeout); }
			catch (InterruptedException ie) { }
		}
	}
}