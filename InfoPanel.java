import javax.swing.*;
import java.awt.*;
import java.lang.reflect.*;
/** @author Gergely Kota

InfoPanel displays a description for a class

*/


public class InfoPanel extends JLabel
{
	private Color text;
	private String description;
	private int height;
	private final String EMPTY = "No description given";

	public InfoPanel()
	{
		description = "No entry selected";
	}

	public void setDescription(Class c)
	{
		try
		{
			Method m = c.getMethod("description", new Class[]{});
			Method[] ms = c.getDeclaredMethods();
			boolean local = false;
			for(int i = 0; i < ms.length; i++)
				if(ms[i].equals(m))
					local = true;
			if(local)
				description = (String) m.invoke(null, (Object[])null);
			else
				description = EMPTY;
		}
		catch(Exception e) {description = EMPTY;}
		setText(description);
	}

/* ----------------------------------------------------------- */
/* ----------------------------------------------------------- */
/* ----------------------------------------------------------- */
	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		InfoPanel ip = new InfoPanel();
		jf.getContentPane().add(ip);
		jf.pack();
		jf.show();
	}


}
