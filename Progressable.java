/** @author Gergely Kota

Progressable can be polled for its progress

*/


public interface Progressable
{
	public double getProgress(); // return value in range [0, 1]
	public void addProgressListener(ProgressListener pl);
}