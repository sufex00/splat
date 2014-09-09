import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.reflect.*;
import java.util.*;

/** @author Gergely Kota
	InheritanceList is a graphical component that allows the user to select from a list of classes that inherit from some baseclass
	*/


public class InheritanceList extends JPanel implements ClassSelector, ActionListener
{
	//private Class superclass;
	private Class[] classes;
	private ArrayList listeners;
	private ClassList clist;
	private InfoPanel ip;

	public InheritanceList()
	{
		this(Object.class);
	}

	public InheritanceList(Class superclass)
	{
		Class[] c1 = Util.getLocalClasses(superclass);
		Class[] c2 = Util.getJarClasses(superclass);
		classes = Util.merge(c1, c2);
		listeners = new ArrayList();
		clist = new ClassList(classes);
		setLayout(new BorderLayout());
		JPanel top = new JPanel(new GridLayout(1,2));
		JPanel subtop = new JPanel(new BorderLayout());
		subtop.add(new JLabel("Select a(n) " + superclass.getName() + ":"), BorderLayout.WEST);
		subtop.add(clist, BorderLayout.EAST);
		subtop.add(GUtil.filler(10), BorderLayout.CENTER);
		top.add(subtop);
		JPanel right = new JPanel(new BorderLayout());
		right.add(GUtil.filler(10), BorderLayout.WEST);
		right.add(ip = new InfoPanel(), BorderLayout.CENTER);
		top.add(right);
		add(top, BorderLayout.WEST);
		clist.addActionListener(this);
	}

	public Component toComponent()
	{
		return this;
	}

	public void setClass(Class c)
	{
		clist.setClass(c);
	}

	public void addActionListener(ActionListener al)
	{
		listeners.add(al);
	}

	private void notifyListeners()
	{
		ActionEvent ae = new ActionEvent(this, hashCode(), "inheritance");
		for(int i = 0; i < listeners.size(); i++)
			((ActionListener) listeners.get(i)).actionPerformed(ae);
	}

	public Class getSelectedClass()
	{
		return clist.getSelectedClass();
	}

	public void actionPerformed(ActionEvent ae)
	{
		ip.setDescription(getSelectedClass());
	}



/* -------------------------------------------------------- */
/* -------------------------------------------------------- */
/* -------------------------------------------------------- */

	private class ClassList extends JComboBox
	{
		Object[] list;

		public ClassList(Class[] c)
		{
			ArrayList temp = new ArrayList();
			for(int i = 0; i < c.length; i++)
				if(ok(c[i]))
					temp.add(c[i]);
			list = temp.toArray();
			for(int i = 0; i < list.length; i++)
				addItem(((Class)list[i]).getName());
			addActionListener(new ListListener());
		}

		public Class getSelectedClass()
		{
			try
			{
				String s = (String) getSelectedItem();
				Class temp = Class.forName(s);
				for(int i = 0; i < list.length; i++)
					if(list[i].equals(temp))
						return (Class) list[i];
			}
			catch(Exception e) {}
			return null;
		}

		public boolean contains(Class c)
		{
			for(int i = 0; i < list.length; i++)
				if(c.equals(list[i]))
					return true;
			return false;
		}

		public boolean setClass(Class c)
		{
			if(!contains(c))
				return false;
			for(int i = 0; i < list.length; i++)
				if(c.equals(list[i]))
					setSelectedIndex(i);
			return true;
		}

		private boolean ok(Class c)
		{
			return !Modifier.isAbstract(c.getModifiers());
		}

		private class ListListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
//				current = (Class) getSelectedValue();
				notifyListeners();
			}
		}


	}


}
