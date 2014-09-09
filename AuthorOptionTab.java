import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/** @author Gergely Kota

AuthorOptionTab allows the user to select a set of authors, their webpages
and the departments they belong to.

*/

public class AuthorOptionTab extends OptionTab
{
	private PackWindow window;
	private WebOptionPanel web;
	private ComparisonOptionPanel compare;

	public AuthorOptionTab()
	{
//		JScrollPane jsp = new JScrollPane();
//		jsp.getViewport().add();
		super("Author Search");
		setCenter(window = new PackWindow());
		web = new WebOptionPanel(this);
		compare = new ComparisonOptionPanel(this);
		JPanel buffer = new JPanel(new BorderLayout());
		buffer.add(web, BorderLayout.NORTH);
		buffer.add(compare, BorderLayout.SOUTH);
		buffer.add(GUtil.filler(10), BorderLayout.CENTER);
		setAdvanced(buffer);
		setDescription("Enter authors and their websites, along with a File to compare");
	}

	public void setup()
	{
		window.setup();
		web.setup();
	}

	public void go()
	{
		log("****************************");
		SplatFile temp = window.getFile();
		if(temp == null)
		{
			log("You must enter a file to compare");
			return;
		}

		AbstractWebSearch wst = window.result();
		final AbstractWebSearch ws = (AbstractWebSearch) web.result(wst);

		new RepaintRunner(ws).start();
	}

/* --------------------------------------------- */
/* --------------------------------------------- */

	private class RepaintRunner extends Thread
	{
		private AbstractWebSearch awb;

		public RepaintRunner(AbstractWebSearch a)
		{
			awb = a;
		}

		public void run()
		{
			setGo(false);
			try
			{
				// copy the picked file to the workingdirectory
				log("This module is under development and does not work properly");
				SplatFile temp = window.getFile();
				String path = getWorkingDirectory().getName();
				log("Working dir is " + path);
				String s = path + "/" + temp.getName();
				temp.copyTo(new File(s));

				// this is the modified name of the file
				String ss = s.substring(0, s.lastIndexOf("."));
				String end = Config.read("endtype");
				if(end == null)
					end = "txt";
				ss = ss + "." + end;
				temp = new SplatFile(ss);
				log("Comparing to " + temp.getAbsolutePath());
				getWorkingDirectory().protect();
				// all contents of this directory are now protected;
				// anything added from here on is not protected

				awb.addProgressListener(getProgressBar());
				awb.addLogger(AuthorOptionTab.this);
				setWebSearch(awb);
				awb.startSearch();
				log("Converting files to text");
				FileConverter fc = new FileConverter(getWorkingDirectory());
				fc.addLogger(AuthorOptionTab.this);
				fc.convert();

				log("Starting a new Comparison");
				DocumentComparison dc = (DocumentComparison) compare.result();
				dc.addProgressListener(getProgressBar());
				dc.addLogger(AuthorOptionTab.this);
				log("Showing Comparison Results");
				new HTMLFrame(dc.compare(temp)).show();
			}
			catch(Exception e)
			{
				log("Error: " + e.toString());
				log("Search failed, file is probably invalid");
			}
			setGo(true);
		}
	}



	private class PackWindow extends JScrollPane
	{
		private Pack[] packs;
		private SelectiveInfo file;

		public PackWindow()
		{
			this(6);
		}

		public PackWindow(int n)
		{
			file = new SelectiveInfo("File to Compare", false, 120);
			JButton jb = new JButton("Browse");
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					File f = FileFrame.getOpenFile();
					if(f != null)
						file.set(f.getAbsolutePath());
				}
			});
			JPanel top = new JPanel(new BorderLayout());
			top.add(file, BorderLayout.CENTER);
			top.add(jb, BorderLayout.EAST);
			JPanel panel = new JPanel(new GridLayout((n+1)/2,2));
			JPanel buffer = new JPanel(new BorderLayout());
			packs = new Pack[n];
			for(int i = 0; i < n; i++)
				panel.add(packs[i] = new Pack());
			buffer.add(panel, BorderLayout.CENTER);
			buffer.add(top, BorderLayout.NORTH);
			getViewport().add(buffer);
//			setPreferredSize(new Dimension(150,150));
		}

		public void setup()
		{
			for(int i = 0; i < packs.length; i++)
				packs[i].clear();
		}

		public AbstractWebSearch result()
		{
			AbstractWebSearch temp = new WebSearch();
			for(int i = 0; i < packs.length; i++)
				temp = packs[i].setParams(temp);
			return temp;
		}

		public SplatFile getFile()
		{
			String s = file.read();
			if(s == null || "".equals(s))
				return null;
			return new SplatFile(s);
		}
	}

/* --------------------------------------------- */
/* --------------------------------------------- */

	private class Pack extends JPanel
	{
		private SelectiveInfo author, url; //, dept;
		private JCheckBox jcb;
		private final int BORDER = 10;

	// commented out dept related things
		public Pack()
		{
			author = new SelectiveInfo("Author", false, 100);
			url = new SelectiveInfo("Website", false, 100);
//			dept = new SelectiveInfo("Department", false, 100);
			setLayout(new BorderLayout());
			JPanel center = new JPanel(new GridLayout(2,1));
			jcb = new JCheckBox("", true);
			center.add(author);
			center.add(url);
			JPanel jp = new JPanel(new BorderLayout());
			jp.add(center, BorderLayout.CENTER);
			jp.add(jcb, BorderLayout.WEST);
//			center.add(dept);
			add(GUtil.filler(BORDER), BorderLayout.NORTH);
			add(GUtil.filler(BORDER), BorderLayout.SOUTH);
			add(GUtil.filler(BORDER), BorderLayout.WEST);
			add(GUtil.filler(BORDER), BorderLayout.EAST);
			add(jp, BorderLayout.CENTER);
		}

		public AbstractWebSearch setParams(AbstractWebSearch w)
		{
			if(!jcb.isSelected())
				return w;
			if(w == null)
				return null;
			// set stuff on w from infos
			String a = author.read();
			String u = url.read();
//			String d = dept.read();
			if(!"".equals(a))
			{
				// set author information
				log("Added author " + a);
			}
			if(!"".equals(u))
			{
				u = FUtil.webfix(u);
				url.set(u);
				// set a website to start on
				log("Added website " + u + " for author " + a);
			}
//			if(!"".equals(d))
//				log("Added department " + d + " for author " + a);
			return w;
		}

		public void clear()
		{
			author.clear();
			url.clear();
//			dept.clear();
		}
	}


/* --------------------------------------------- */
/* --------------------------------------------- */

	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		jf.setSize(800, 600);
		jf.getContentPane().add(new AuthorOptionTab());
		jf.show();
	}

}
