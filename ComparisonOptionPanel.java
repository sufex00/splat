import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.reflect.*;
import java.io.*;
import java.awt.event.*;

/** @author Gergely Kota

ComparisonOptionPanel allows choice of comparisons and
lets user select the tolerance

*/

public class ComparisonOptionPanel extends OptionPanel implements ActionListener
{
	private CompPanel cp;

	public ComparisonOptionPanel(OptionTab ot)
	{
		super(DocumentComparison.class, ot);
		setCenter(cp = new CompPanel());
		setClass();
		Config.addActionListener(this);
	}

	private void setClass()
	{
		try
		{
			setClass(Class.forName(Config.read("comparison")));
		}
		catch(Exception e) {setClass(SentenceComparison.class);}
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals("comparison"))
			setClass();
	}

	public Object result()
	{
		return cp.result();
	}

	public Object result(Object o)
	{
		return cp.result((DocumentComparison)o);
	}


	private class CompPanel extends JPanel implements ActionListener
	{
		private SelectiveInfo tol;

		public CompPanel()
		{
			setLayout(new BorderLayout());
			JPanel filler = new JPanel();
			add(tol = new SelectiveInfo("Tolerance", false, 150, true), BorderLayout.NORTH);
			tol.set(Config.read("tolerance"));
			tol.setDescription("[1, 100] indicating needed similarity to be considered cheating");
			Config.addActionListener(this);
		}

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals("tolerance"))
				tol.set(Config.read("tolerance"));
		}

		public Object result()
		{
			DocumentComparison dc = null;
			try
			{
				Class c = getSelectedClass();
				Constructor cons = c.getConstructor(new Class[]{File.class});
				File f = getOwner().getWorkingDirectory();
				dc = (DocumentComparison) cons.newInstance(new Object[]{f});
				getOwner().log("Using " + c.getName());
			}
			catch(Exception e) {return null;}
			return result(dc);
		}

		public Object result(DocumentComparison dc)
		{
			try
			{
				int i = Integer.parseInt(tol.read());
				dc.setTolerance(i);
					getOwner().log("Set comparison tolerance to " + dc.getTolerance());
			}
			catch(Exception e) {}
			dc.read();
			Debug.println("Read DocumentCompnarison " + dc);
			return dc;
		}
	}
}

