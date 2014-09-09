/** @author Gergely Kota

ProgressEvents hold some info

*/


public class ProgressEvent
{
	private final Progressable pr;
	private final String action;
	
	public ProgressEvent(Progressable p, String s)
	{
		pr = p;
		action = s;
	}
	
	public Progressable getSource()
	{
		return pr;
	}
	
	public String getAction()
	{
		return action;
	}


}