import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/** @author Gergely Kota

OptionPanel allows the user to select a module and enter
information into textfields for it

*/

public abstract class OptionPanel extends JPanel implements ActionListener
{
	// need to have a list of existing classes
	// need to display textfield for each option ... how do??

	private Class superclass;
	private ClassSelector cs;
	private JButton go;
	private JPanel center;
	private Component inserted;
	private OptionTab owner;
	private final int BUFFER = 10;
	private final String LOCATION = BorderLayout.WEST;

	public OptionPanel(Class sc, OptionTab ot)
	{
		superclass = sc;
		owner = ot;
		setup();
	}

	public void setup()
	{
		invalidate();
		removeAll();
		setLayout(new BorderLayout());
		//setLayout(new GridLayout(1,2));
		center = new JPanel(new BorderLayout());
		JButton help = new JButton(superclass.getName() + " Help");
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				help();
			}
		});
//		GUtil.buttonFix(help, Color.yellow, "help.png");
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(help, LOCATION);
		//center.add(bottom, BorderLayout.SOUTH);

		JPanel centerWrap = new JPanel(new BorderLayout());
		centerWrap.add(center, BorderLayout.CENTER);
		centerWrap.add(GUtil.filler(BUFFER/2), BorderLayout.NORTH);
		centerWrap.add(GUtil.filler(BUFFER), BorderLayout.SOUTH);
		centerWrap.add(GUtil.filler(BUFFER), BorderLayout.EAST);
		centerWrap.add(GUtil.filler(BUFFER), BorderLayout.WEST);

		JPanel buf = new JPanel(new BorderLayout());
		cs = new InheritanceList(superclass);
		buf.add(cs.toComponent(), BorderLayout.CENTER);
		buf.add(GUtil.filler(BUFFER), BorderLayout.WEST);
		buf.add(GUtil.filler(BUFFER), BorderLayout.EAST);

		// add two center chunks into this
//		JPanel lefter = new JPanel(new BorderLayout());
//		lefter.add(buf, BorderLayout.NORTH);
//		lefter.add(centerWrap, BorderLayout.CENTER);
		add(buf, BorderLayout.NORTH);
		add(centerWrap, BorderLayout.CENTER);
//		add(lefter, BorderLayout.WEST);
		//JPanel south = new JPanel(new BorderLayout());
		if(inserted != null)
			setCenter(inserted);
		validate();
		repaint();
	}

	public void setCenter(Component c)
	{
		center.add(inserted = c, LOCATION);
	}

	public void help()
	{
		HelpFrame.getTop().goTopic(superclass.getName()).show();
	}

	public void setClass(Class c)
	{
		cs.setClass(c);
	}

/*
	public boolean started(Progressable p)
	{
		invalidate();
		remove(info);
		ProgressBar pb = new ProgressBar(p);
		add(pb, BorderLayout.SOUTH);
		validate();
		revalidate();
		repaint();
		System.out.println("Finished repainting ... we think");
		return true;
	}
*/
	public OptionTab getOwner()
	{
		return owner;
	}

	public abstract Object result();
	public abstract Object result(Object o);

	public Class getSuperClass()
	{
		return superclass;
	}

	public Class getSelectedClass()
	{
		return cs.getSelectedClass();
	}


	public void actionPerformed(ActionEvent ae)
	{
//		info.setDescription(getSelectedClass());
	}


	public static void main(String[] aarhs)
	{
		JFrame jf = new JFrame();
		OptionPanel op;
		jf.getContentPane().add(op = new WebOptionPanel(new WebSearchOptionTab()));
		jf.pack();
		jf.show();

	}


}



