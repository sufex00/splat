public class Tester //implements Progressable
{
	private long time = System.currentTimeMillis();
	private double length;
	private int count, MAX = 1000000000;

	public Tester(int n)
	{
		length = 1000*n;
	}

	public double getProgress()
	{

		return ((double)count) / MAX;
	}
}