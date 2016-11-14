package app;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class RuntimeExecutor
{
	private long timeout = Long.MAX_VALUE;

	/**
	 * Default constructor - Timeout set to Long.MAX_VALUE
	 */
	public RuntimeExecutor()
	{
		// Do nothing
	}

	/**
	 * Constructor
	 * @param timeout Set the timeout for the external application to run
	 */
	public RuntimeExecutor(long timeout)
	{
		this.timeout = timeout;
	}

	/**
	 * Execute a Runtime process
	 * @param command - The command to execute
	 * @param env - Environment variables to put in the Runtime process
	 * @return The output from the process
	 * @throws IOException
	 * @throws TimeoutException - Process timed out and did not return in the specified amount of time
	 */
	public String execute(String command, String[] env) throws IOException, TimeoutException
	{
		Process p = Runtime.getRuntime().exec(command, env);

		// Set a timer to interrupt the process if it does not return within the timeout period
		Timer timer = new Timer();
		timer.schedule(new InterruptScheduler(Thread.currentThread()), this.timeout);

		try
		{
			p.waitFor();
		} catch (InterruptedException e)
		{
			// Stop the process from running
			p.destroy();
			throw new TimeoutException(command + "did not return after "+this.timeout+" milliseconds");
		}
		finally
		{
			// Stop the timer
			timer.cancel();
			
			// clear interrupted status
			Thread.interrupted();
		}

		// Get the output from the external application
		StringBuilder buffer = new StringBuilder();
		BufferedInputStream br = new BufferedInputStream(p.getInputStream());
		while (br.available() != 0)
		{
			buffer.append((char) br.read());
		}
		String res = buffer.toString().trim();
		return res;
	}


	private class InterruptScheduler extends TimerTask
	{
		Thread target = null;

		public InterruptScheduler(Thread target)
		{
			this.target = target;
		}

		@Override
		public void run()
		{
			target.interrupt();
		}
	}
}
