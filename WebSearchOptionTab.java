import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/** @author Gergely Kota

DefaultOptionTabs run a websearch + comparison, and display the result

*/



public class WebSearchOptionTab extends OptionTab
{
	private OptionPanel web, compare;
	private SelectiveInfo info;
	private DepartmentBox depts;
	private final String NONE = "Enter Website", ALL = "All Departments";

	public WebSearchOptionTab()
	{
		super("Spider Search");
		web = new WebOptionPanel(this);
		compare = new ComparisonOptionPanel(this);
		JPanel center = new JPanel(new BorderLayout());;

		info = new SelectiveInfo("Starting Site", false, 100);
		depts = new DepartmentBox();
		JPanel top = new JPanel(new BorderLayout());
		JPanel left = new JPanel(new BorderLayout());
		JPanel tag = new JPanel(new BorderLayout());
		tag.add(new JLabel("Select Department: "), BorderLayout.WEST);
		tag.add(depts, BorderLayout.CENTER);
		left.add(tag, BorderLayout.WEST);

		top.add(left, BorderLayout.CENTER);
		top.add(info, BorderLayout.NORTH);
		JPanel squeeze = new JPanel(new BorderLayout());
		squeeze.add(new NewInfo(), BorderLayout.WEST);
		top.add(squeeze, BorderLayout.SOUTH);

		center.add(GUtil.filler(5), BorderLayout.CENTER);
		center.add(web, BorderLayout.NORTH);
		center.add(compare, BorderLayout.SOUTH);
		setCenter(top);
		setAdvanced(center);
		setDescription("Enter the site you wish to start on, or select a department from the options");
	}

	public void go()
	{
		log("*****************************");
		if(info.read().equals(ALL.toString()))// && depts.getSelectedItem().equals(ALL))
		{
			String[] names = EntryList.entries();
			String[] sites = new String[names.length];
			for(int i = 0; i < names.length; i++)
				sites[i] = EntryList.read(names[i]);
			new RepaintRunner(sites).start();
		}
		else
		{
			String start = info.read();
			if("".equals(start) || start == null)
			{
				log("No department or starting website selected");
				return;
			}
			else
				start = FUtil.webfix(start);
				info.set(start);
				new RepaintRunner(new String[]{start}).start();
		}
	}

	public void setup()
	{
		web.setup();
		compare.setup();
	}

/* -------------------------------------------------- */
/* -------------------------------------------------- */

	private class RepaintRunner extends Thread
	{
		private String[] depts;
		public RepaintRunner(String[] d)
		{
			depts = d;
		}

		public void run()
		{
			setGo(false);
			boolean recycle = !"true".equals(Config.read("newfolder"));
			((WebOptionPanel)web).setRecycleDir(recycle);
			for(int i = 0; i < depts.length; i++)
				if(!isInterrupted())
					runSearch(depts[i]);
			((WebOptionPanel)web).setRecycleDir(false);
			((WebOptionPanel)web).setDirectory();
			display();
		}

		private boolean runSearch(String s)
		{
			if(s == null || "".equals(s))
				return false;
			setGo(false);
			AbstractWebSearch ws = (AbstractWebSearch) web.result();
			log("Setting starting url to " + s);
			ws.setWebsite(s);
//			ws = (WebSearch) web.result(ws);
			log("Starting a new WebSearch");
		/* ------------------------ */
		/* ------------------------ */
		/* ------------------------ */
			ws.addProgressListener(getProgressBar());
			ws.addLogger(WebSearchOptionTab.this);
			setWebSearch(ws);
		/* ------------------------ */
		/* ------------------------ */
		/* ------------------------ */
			ws.startSearch();
			// return ws.startSearch();
			// need to see if startSearch succeeded ... log if not
			return true; // for now
		}

		private void display()
		{
			log("Converting to text");
			FileConverter fc = new FileConverter(getWorkingDirectory());
			fc.addLogger(WebSearchOptionTab.this);
			fc.convert();
			log("Starting a new Comparison");
			DocumentComparison dc = (DocumentComparison) compare.result();
			if(dc == null)
			{
				log("Failure in creation of DocumentComparison");
				return;
			}
			dc.addProgressListener(getProgressBar());
			dc.addLogger(WebSearchOptionTab.this);
			DocumentComparisonResult hdc = dc.compare();
			log("Showing Comparison Result");
			setGo(true);
			new HTMLFrame(hdc).show();
		}
	}


	private class DepartmentBox extends JComboBox
	{
		public DepartmentBox()
		{
			reset();
			set();
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					set();
				}
			});
		}

		private void set()
		{
			Object temp = getSelectedItem();
			if(temp == null) return;
			info.set(EntryList.read(temp.toString()));
			if(getSelectedItem().equals(ALL))
				info.set(ALL);
		}

		public void reset()
		{
			removeAllItems();
			String[] s = EntryList.entries();
			addItem(ALL);
			addItem(NONE);
			for(int i = 0; i < s.length; i++)
				addItem(s[i]);
		}
	}

	private class NewInfo extends JPanel
	{
		SelectiveInfo dept, url;
		private final int WIDTH = 80;

		public NewInfo()
		{
			setLayout(new BorderLayout());
			JButton addnew = new JButton("Add Info");
			addnew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setNew();
				}
			});
			add(addnew, BorderLayout.EAST);

			JPanel input = new JPanel(new GridLayout(2,1));
			input.add(dept = new SelectiveInfo("Department", false, WIDTH));
			input.add(url = new SelectiveInfo("Website", false, WIDTH));
			add(input, BorderLayout.CENTER);
		}

		private void setNew()
		{
			String d = dept.read();
			String u = url.read();
			if(u == null || "".equals(u))
				return;
			if(d == null || "".equals(d))
				return;
			EntryList.write(d.trim(), u.trim());
			dept.clear();
			url.clear();
			depts.reset();
		}
	}

	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		jf.getContentPane().add(new WebSearchOptionTab());
		jf.pack();
		jf.show();
	}

}
