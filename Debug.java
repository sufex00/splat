import java.awt.event.*;

/** @author Gergely Kota
 Debug prints to standard out if the debug key is true in the config file

 */


public class Debug implements ActionListener
{
	private static boolean print = false;
	private static Debug instance;

	static
	{
		init();
		instance = new Debug();
		Config.addActionListener(instance);
	}

	private static void init()
	{
		String s = Config.read("debug");
		if(s == null)
			print = false;
		else if("true".equals(s.toLowerCase()))
			print = true;
		else
			print = false;
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals("debug"))
			init();
	}

	public static void println(Object o)
	{
		if(print)
			System.out.println(o);
	}
	
	public static void println(double d)
	{
		if(print)
			System.out.println(d);
	}
	
	public static void println(long l)
	{
		if(print)
			System.out.println(l);
	}
	
	public static void println(boolean b)
	{
		if(print)
			System.out.println(b);
	}

/* ------------------------------------------------- */
/* ------------------------------------------------- */
/* ------------------------------------------------- */

	public static void main(String[] aregs)
	{
		Debug.println("Testing ...");

	}

}
