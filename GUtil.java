import javax.swing.*;
import java.awt.*;

/** @author Gergely Kota
	A class to provide some graphical utilities
	*/


public class GUtil
{
	public static JPanel filler(int n)
	{
		JPanel filler = new JPanel();
		filler.setPreferredSize(new Dimension(n,n));
		return filler;
	}

	public static JPanel filler(int n, Color c)
	{
		JPanel filler = filler(n);
		filler.setBackground(c);
		return filler;
	}

	public static ImageIcon getIcon(String s)
	{
		Toolkit tk = Toolkit.getDefaultToolkit();
		try
		{
			return new ImageIcon(tk.getImage(s));
		}
		catch(Exception e) {return null;}
	}

	public static boolean buttonFix(JButton jb, String s)
	{
		return buttonFix(jb, jb.getBackground(), s);
	}

	// assign the Icon from the file called s if it
	// can, else sets the background to c
	public static boolean buttonFix(JButton jb, Color c, String s)
	{
		if(s == null || !new java.io.File(s).exists())
		{
			jb.setBackground(c);
			return false;
		}
		ImageIcon temp = GUtil.getIcon(s);
		jb.setIcon(temp);
		return true;
	}



}
