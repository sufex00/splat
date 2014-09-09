import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/** @author Gergely Kota

Overall Frame for displaying the results of an analysis

*/



public class HTMLFrame extends JFrame implements ActionListener
{
	private DocumentComparisonResult dcr;
	private HTMLPanel panel1, panel2;
	private JScrollPane infoScroll;
	private JTextArea info;
	private Dimension infoSize;
	private CheatList list;
	private JList sidebar;
	private int lastSelected; // needed to eliminate doubling changes.

	/** @param d the DocumentComparisonResult whose results are to be shown
		*/
	public HTMLFrame(DocumentComparisonResult d)
	{
		dcr = d;
		setTitle("Comparison Results");
		setSize(1200, 600);
		setLocation(200, 400);
		setJMenuBar(new JMenuBar());
		Config.addActionListener(this);
		reset();
		// allow dump to file
	}

	public void actionPerformed(ActionEvent ae)
	{
		String s = (String) ae.getSource();
		listenerReset(s);
	}

	private DocumentComparisonResult loadFile(File f)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			DocumentComparisonResult d;
			return (DocumentComparisonResult) ois.readObject();
		}
		catch(Exception e) {return null;}
	}

	public void loadComparison(DocumentComparisonResult d)
	{
		if(d == null)
			return;
		dcr = d;
		reset();
	}

	public void alphaSort()
	{
		sidebar.setListData(list.alphaSort().toArray());
		info.append("List in alphabetical order\n");
	}

	public void cheatSort()
	{
		sidebar.setListData(list.cheatSort().toArray());
		info.append("List in cheat order\n");
	}

	private void listenerReset(String s)
	{
		if(!(s.equals("info") || s.equals("infosize")))
			return;
		getContentPane().invalidate();
		String temp = Config.read("info");
		boolean x = "false".equals(temp)? false: true;
		// add way to set size from config.
		int size = Config.getInt("infosize");
		size = (size == Config.ILLEGAL)? 100: size;
		infoSize = new Dimension(size, size);
		infoScroll.setPreferredSize(x? infoSize: new Dimension(0, 0));
		getContentPane().validate();
		repaint();
	}

	private void reset()
	{
		invalidate();
		lastSelected = -1;
		getContentPane().invalidate();
		getContentPane().removeAll();
		String s = Config.read("info");
		boolean x = "false".equals(s)? false: true;
		info = new JTextArea();
		new SplatFile(dcr.getWorkingDirectory().getName() + "_Outputs").mkdirs();
		SplatFile sf = new SplatFile(dcr.getWorkingDirectory().getName() + "_Outputs/list.html");
		dcr.writeResults(sf);
		info.append("Wrote list of results to " + sf.getAbsolutePath() + "\n");
		infoScroll = new JScrollPane();
		infoScroll.getViewport().add(info);

		// add way to set size from config.
		int size = Config.getInt("infosize");
		size = (size == Config.ILLEGAL)? 100: size;
		infoSize = new Dimension(size, size);
		infoScroll.setPreferredSize(x? infoSize: new Dimension(0, 0));

		JMenuBar jmb = getJMenuBar();
		jmb.removeAll();
		setJMenuBar(jmb);
		jmb.add(new FileMenu());
//		jmb.add(new CheatMenu());
		JMenuItem alpha = new JMenuItem("Alphabetical Sort");
		alpha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				alphaSort();
			}
		});
		JMenuItem cheat = new JMenuItem("Cheat Sort");
		cheat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cheatSort();
			}
		});
		JMenu sort = new JMenu("Sort");
		sort.add(alpha);
		sort.add(cheat);
		jmb.add(sort);

		jmb.add(new VisualOptionsMenu());

		JMenu help = new JMenu("Help");
		JMenuItem h = new JMenuItem("Help");
		h.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				HelpFrame.getTop().goTopic("HTMLFrame").show();
			}
		});
		help.add(h);
		jmb.add(help);

		getContentPane().setLayout(new BorderLayout());
		JPanel buffer = new JPanel(new GridLayout(1,2));

		list = new CheatList();
		JScrollPane side = new JScrollPane();
		sidebar = new JList(list.toArray());
		cheatSort();
		sidebar.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int temp = sidebar.getSelectedIndex();
				if(temp == lastSelected)
					return;
				lastSelected = temp;

				PairComparison pc = (PairComparison) sidebar.getSelectedValue();
				loadPairComparison(pc);
			}
		});

		side.getViewport().add(sidebar);
		getContentPane().add(side, BorderLayout.WEST);
		// set color and size from config file
		int edge = Config.getInt("edgesize");
		edge = (edge == Config.ILLEGAL)? 3: edge;

		buffer.add(panel1 = new HTMLPanel(edge));
		buffer.add(panel2 = new HTMLPanel(edge));
		getContentPane().add(buffer, BorderLayout.CENTER);
		getContentPane().add(infoScroll, BorderLayout.SOUTH);

		validate();
		getContentPane().validate();

	}

	public void loadPairComparison(PairComparison pc)
	{
		if(pc == null)
			return;
		panel1.setString(pc, 1);
		panel2.setString(pc, 2);
		info.append("Showing (" + pc.getFile1().getName() + ", " + pc.getFile2().getName() + ")\n");
	}

	private String format(double x)
	{
		int n = (int) (1000*x);
		return "" + n/10 + "." + n%10 + "%";
	}

	/** @return the description of what this class does */
	public static String description()
	{
		return "Shows the results of comparisons side by side";
	}


/* ------------------------------------------------------ */
/* private classes for menu stuff ... 	*/

	// this JMenu is the "File" section in the Menus
	private class FileMenu extends JMenu
	{
		public FileMenu()
		{
			super("File");

			JMenuItem load = new JMenuItem("Load a Comparison Result");
			load.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try
					{
						File f = FileFrame.getOpenFile();
						loadComparison(loadFile(f));
					}
					catch(Exception e) {}
				}
			});
			add(load);

			JMenuItem serial = new JMenuItem("Save Current Comparison Result");
			serial.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dcr.save();
				}
			});
			add(serial);

			JMenuItem serialas = new JMenuItem("Save Current Comparison Result As...");
			serialas.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dcr.save(FileFrame.getSaveFile());
				}
			});
			add(serialas);

			JMenuItem save = new JMenuItem("Save Current Pair");
			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					saveCurrent();
				}
			});
			add(save);

			JMenuItem saveas = new JMenuItem("Save Current Pair As...");
			saveas.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					saveCurrent(FileFrame.getSaveFile());
				}
			});
			add(saveas);

			JMenuItem saveall = new JMenuItem("Save All Pairs");
			saveall.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					saveAll();
				}
			});
			add(saveall);

/*
			JMenuItem jmi = new JMenuItem("Close");
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					hide();
					removeNotify();
				}
			});
			add(jmi);
			*/
		}

		public void saveAll()
		{
			// Nothing here yet ... need to iterate through all of them
			String working = dcr.getWorkingDirectory().getName() + "_Outputs/";
			SplatFile f = new SplatFile(working);
			f.mkdirs();
			f.protect();
			for(int i = 0; i < list.size(); i++)
			{
				PairComparison pc = (PairComparison) list.get(i);
				loadPairComparison(pc);
				String name1 = pc.getFile1().getName();
//				name1 = name1.substring(0, name1.lastIndexOf("."));
				String name2 = pc.getFile2().getName();
//				name2 = name2.substring(0, name2.lastIndexOf("."));
				File out = new File(working + name1 + "-" + name2 + ".html");
				saveCurrent(out);
			}
		}

		private void saveCurrent()
		{
			File f = FileFrame.getSaveFile();
			if(f != null)
				saveCurrent(f);
		}


		private void saveCurrent(File out)
		{
			// fix this at some point ... should be simple
			try
			{
				// write an HTML page in two tables, with headers, etc
				// I guess this could have some options ... sheesh.
				StringBuffer content = new StringBuffer();
				File f1 = panel1.getCurrentFile(), f2 = panel2.getCurrentFile();
				content.append("<HTML>");
				content.append("<TABLE border=2>");
					content.append("<TR>");
						content.append("<TD><B>");
							content.append("Original: ").append(f1.getAbsolutePath());
						content.append("</TD></B>");
						content.append("<TD><B>");
							content.append(f2.getAbsolutePath());
							content.append(" ").append("cheats ");
							content.append(format(dcr.get(f1, f2).score()));
						content.append("</TD></B>");
					content.append("</TR>");
					content.append("<TR valign=\"top\">");
						content.append("<TD>");
							content.append(panel1.getString());
						content.append("</TD>");
						content.append("<TD>");
							content.append(panel2.getString());
						content.append("</TD>");
					content.append("</TR>");
				content.append("</TABLE>");
				content.append("</HTML>");

				PrintWriter pw = new PrintWriter(new FileWriter(out));
				pw.println(content.toString());
				pw.close();
				info.append("Wrote [" + f1.getName() + ", " + f2.getName() + "] to " + out.getAbsolutePath() +"\n");
			}
			catch(Exception e) {}

		}
	}

/* ------------------------------------------------------ */
/* ------------------------------------------------------ */
	// this JMenu is for visual options
	private class VisualOptionsMenu extends JMenu
	{
		public VisualOptionsMenu()
		{
			super("Visual Options");

			// create decent choice menus ...
			final ValueMenuItem jmi1 = new ValueMenuItem("Border Color", Config.read("edgecolor"));
			jmi1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Color c = ColorFrame.getColor();
					panel1.setColor(c);
					panel2.setColor(c);
					// this is no good ... needs to do hex form
					jmi1.setValue(Conversion.hexString(c));
				}
			});
			add(new SetMenu("Border Color", jmi1, "edgecolor"));

/*
			final ValueMenuItem jmi2 = new ValueMenuItem("Border Size", Config.read("edgesize"));
			jmi2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int n = ValueFrame.getInt();
					panel1.resize(n);
					panel2.resize(n);
					jmi2.setValue("" + n);
				}
			});
			add(new SetMenu("Border Thickness", jmi2, "edgesize"));
*/

			final ValueMenuItem font = new ValueMenuItem("Highlight Color", Config.read("htmlcolor"));
			font.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Color c = ColorFrame.getColor();
					if(c == null)
						return;
					panel1.setHighlightColor(c);
					panel2.setHighlightColor(c);
					font.setValue(Conversion.hexString(c));
				}
			});
			add(new SetMenu("HTML Highlight", font, "htmlcolor"));

			add(new JSeparator());

	// allow size to be setable?? or just on/off??
	// might as well make it setable really ...?
			boolean x = infoScroll.getPreferredSize().width == 0? false: true;
			final JCheckBoxMenuItem box = new JCheckBoxMenuItem("Info", null, x);
			box.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					getContentPane().invalidate();
					if(box.getState())
						infoScroll.setPreferredSize(infoSize);
					else
						infoScroll.setPreferredSize(new Dimension(0, 0));
					getContentPane().validate();
					getContentPane().repaint();
				}
			});
			add(box);
		}
	}

/* ------------------------------------------------------ */
/* ------------------------------------------------------ */

	private class CheatList extends ArrayList
	{
		public CheatList()
		{
			ArrayList a = dcr.getAll();
			for(int i = 0; i < a.size(); i++)
				if(((PairComparison)a.get(i)).score() > 0)
					add(a.get(i));

			cheatSort();
		}

		public CheatList alphaSort()
		{
			Collections.sort(this, new Comparator() {
				public int compare(Object o1, Object o2)
				{
					try
					{
						PairComparison pc1 = (PairComparison) o1;
						PairComparison pc2 = (PairComparison) o2;
						String s1 = "" + pc1.getFile1() + pc1.getFile2();
						String s2 = "" + pc2.getFile1() + pc2.getFile2();
						return s1.compareTo(s2);
					}
					catch(Exception e) {return -1;}
				}
			});
			return this;
		}

		public CheatList cheatSort()
		{
			Collections.sort(this, new Comparator() {
				public int compare(Object o1, Object o2)
				{
					try
					{
						PairComparison pc1 = (PairComparison) o1;
						PairComparison pc2 = (PairComparison) o2;
						if(pc1.score() > pc2.score())
							return -1;
						if(pc2.score() > pc1.score())
							return 1;
						return 0;
					}
					catch(Exception e) {return -1;}
				}
			});
			return this;
		}
	}


/*
	// this JMenu is for listing and getting to cheats
	private class CheatMenu extends JMenu
	{
		public CheatMenu()
		{
			super("Cheats");
			for(int i = 0; i < hdcr.size(); i++)
				add(file1Menu(i));
		}

		private JMenu file1Menu(int i)
		{
			JMenu jm = new JMenu(hdcr.getFile(i).getName());
			for(int j = 0; j < hdcr.size(); j++)
				if(i != j)
					jm.add(file2Menu(i, j));
			return jm;
		}

		private JMenu file2Menu(int i, int j)
		{
			JMenu jm = new JMenu(hdcr.getFile(j).getName() + "  " + format(hdcr.getPairComparison(i, j).score()));
			for(int k = 0; k < hdcr.size(i, j); k++)
				jm.add(cheat(i, j, k));
			return jm;
		}

		private JMenuItem cheat(final int i, final int j, final int k)
		{
			JMenuItem jmi = new JMenuItem(hdcr.getPairComparison(i, j).toString(k));
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					// getHTML for File i compared to j, and File j compared to i
					panel1.setString(hdcr.getHTML(i, j, k), hdcr.getFile(i));
					panel2.setString(hdcr.getHTML(j, i, k), hdcr.getFile(j));
					info.append("(" + hdcr.getFile(i).getName() + ", " + hdcr.getFile(j).getName() + ") Case " + (k+1) + "\n");
				}
			});
			return jmi;
		}
	}
*/

/* ------------------------------------------------------ */
/* ------------------------------------------------------ */
/* ------------------------------------------------------ */

	public static void main(String[] args)
	{
		DocumentComparison ss = new SentenceComparison(new File("Delete"));
		new HTMLFrame(ss.read().compare()).show();
	}
}
