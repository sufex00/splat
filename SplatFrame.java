import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/** @author Gergely Kota

SplatFrame is the entry point for Splat.

*/

public class SplatFrame extends JFrame
{
	private ArrayList tabs;
	private JTabbedPane jtb;

	public SplatFrame()
	{
		// set JFrame stuff
		setLocation(200,200);
		setSize(800, 600);
		setTitle("Splat");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Make tabs
		jtb = new JTabbedPane();
		tabs = new ArrayList();
		if("true".equals(Config.read("showhome")))
			addTab("Home", new BrowserPanel());
		addTab("Local Search", new LocalOptionTab());
		addTab("Spider Search", new WebSearchOptionTab());
		addTab("Author Search", new AuthorOptionTab());
		addTab("Saved Search", new SavedOptionTab());
		getContentPane().add(jtb);

		// create some basic menus
		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);
		JMenu file = new JMenu("File");
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cleanup();
				System.exit(0);
			}
		});
		file.add(quit);
		jmb.add(file);

		JMenu config = new JMenu("Config");
		JMenuItem jmi = new JMenuItem("Configurations");
		jmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// ConfigWindow.showWindow();
				ComponentedConfigWindow.showWindow();
			}
		});
		config.add(jmi);

		final SpecialCheckBoxMenuItem del = new SpecialCheckBoxMenuItem("Cleanup On Exit");
		Config.addActionListener(del);
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Config.write("deleteonexit", "" + del.isSelected());
			}
		});
		// uncomment to put the cleanup on exit button back in the menu
		//config.add(del);
		jmb.add(config);

		JMenu help = new JMenu("Help");
		JMenuItem h = new JMenuItem("Help");
		h.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				new HelpFrame().goTopic("General").show();
			}
		});
		help.add(h);

		JMenuItem email = new JMenuItem("Email Authors");
		email.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				EmailWindow.showWindow();
			}
		});
		help.add(email);

		JMenuItem l = new JMenuItem("License");
		l.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				show("help/license.txt");
			}
		});
		help.add(l);

		JMenuItem a = new JMenuItem("About");
		a.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				show("help/about.txt");
			}
		});
		help.add(a);
		jmb.add(help);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cleanup();
			}
		});
	}

	public void addTab(String title, JPanel ot)
	{
		jtb.add(title, ot);
		tabs.add(ot);
	}

	private void cleanup()
	{
		for(int i = 0; i < tabs.size(); i++)
		try
		{
			((OptionTab)tabs.get(i)).cleanup();
		}
		catch(Exception e) {}
	}

	public void show(String file)
	{
		// this needs to read from a file: license.txt
		String license;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			StringBuffer sb = new StringBuffer();
			while(br.ready())
				sb.append(br.readLine()).append("\n");
			license = sb.toString();
		}
		catch(Exception e) {license = "Source file not found";}
		JOptionPane.showMessageDialog(null, license);
	}


	private class SpecialCheckBoxMenuItem extends JCheckBoxMenuItem implements ActionListener
	{
		public SpecialCheckBoxMenuItem(String s)
		{
			super(s);
			boolean b = !"false".equals(Config.read("deleteonexit"));
			setSelected(b);
		}

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals("deleteonexit"))
				setSelected("true".equals(Config.read("deleteonexit")));
		}
	}


	public static void main(String[] args)
	{
		new SplatFrame().show();
	}


}
