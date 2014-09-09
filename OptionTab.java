import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/** @author Gergely Kota

OptionTabs hold OptionPanels and combine their info to run Splats

*/


public abstract class OptionTab extends JPanel implements Logger, ActionListener
{
	private SplatFile workingDirectory;
	private JButton go, help;
	private boolean ready;
	private JTextArea info, log;
	private InfoPanel logp;
	private JPanel content, jp;
	private HeaderPanel header;
	private ArrayList files;
	private final static ArrayList createdDirs = new ArrayList();
	private AbstractWebSearch current;
	private String title;
	private ProgressBar progressBar;

	public OptionTab(String t)
	{
		title = t;
		ready = true;
		setLayout(new BorderLayout());
		jp = new JPanel(new GridLayout(2,1));
		setButtons();

		JPanel jpbuffer = new JPanel(new BorderLayout());
		jpbuffer.add(jp, BorderLayout.NORTH);
		info = new JTextArea();
		info.setEditable(false);
		log = new JTextArea();
		log.setEditable(false);
		files = new ArrayList();
		JPanel texts = new JPanel(new BorderLayout());
		texts.add(new InfoPanel(info, "Info"), BorderLayout.NORTH);
		texts.add(logp = new InfoPanel(log, "Log"), BorderLayout.CENTER);
		texts.add(progressBar = new ProgressBar(), BorderLayout.SOUTH);
		// this is where the local-to-tab options used to be
		header = new HeaderPanel();
		// texts.add(header, BorderLayout.SOUTH);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(texts, BorderLayout.CENTER);
		add(bottom, BorderLayout.CENTER);

		content = new JPanel(new BorderLayout());
		// put go and help on the side
		content.add(jpbuffer, BorderLayout.EAST);
		add(content, BorderLayout.NORTH);
		Config.addActionListener(this);
	}

	public String getTitle()
	{
		return title;
	}

	public ProgressBar getProgressBar()
	{
		return progressBar; //header.getProgressBar();
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals("helpbutton"))
			setButtons();
		if(ae.getSource().equals("gobutton"))
			setButtons();
	}

	public void setButtons()
	{
		jp.removeAll();
		go = new JButton();
		jp.add(go, BorderLayout.NORTH);
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ready)
					go();
				else
					killSearch();
			}
		});
		current = null;
		help = new JButton();
		jp.add(help, BorderLayout.SOUTH);
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String s = OptionTab.this.getClass().getName();
				HelpFrame.getTop().goTopic(s).show();
			}
		});
		if(!GUtil.buttonFix(go, ready? Color.green: Color.red, ready? Config.read("gobutton"): Config.read("stopbutton")))
			go.setText(ready? "Go": "Stop");
		if(!GUtil.buttonFix(help, Color.yellow, Config.read("helpbutton")))
			help.setText("Help");
	}

	public void setWebSearch(AbstractWebSearch awb)
	{
		current = awb;
	}

	private void killSearch()
	{
		if(current == null)
			return;
		current.halt();
		log("Terminated WebSearch");
		setGo(true);
	}

	public void goClick()
	{
		go.doClick();
	}

	public void setGo(boolean x)
	{
//		go.setEnabled(x);
		ready = x;
		setButtons();
	}

	public abstract void go();
	public abstract void setup();

	public void cleanup()
	{
		if("true".equals(Config.read("deleteonexit")))
			for(int i = 0; i < files.size(); i++)
				((SplatFile) files.get(i)).deleteAll();
	}

	public void log(String s)
	{
		if(!s.endsWith("\n"))
			s += "\n";
		log.append(time() + s);
		logp.scrollToBottom();
	}

	public String time()
	{
		GregorianCalendar gc = new GregorianCalendar();
		String hour = "" + gc.get(gc.HOUR_OF_DAY);
		String minute = "" + gc.get(gc.MINUTE);
		while(minute.length() < 2)
			minute = "0" + minute;
		String second = "" + gc.get(gc.SECOND);
		while(second.length() < 2)
			second = "0" + second;
		return hour + ":" + minute + ":" + second + " - ";
	}

	public void setDescription(String s)
	{
		info.setText(s);
	}

	public void setCenter(Component c)
	{
		content.add(c, BorderLayout.CENTER);
	}

	public void setAdvanced(Component c)
	{
		header.addContent(c);
	}

	public SplatFile getWorkingDirectory()
	{
		if(workingDirectory == null)
			workingDirectory = createDirectory();
		return workingDirectory;
	}

	public void launch(DocumentComparisonResult dcr)
	{
		new HTMLFrame(dcr).show();
	}

	public void setWorkingDirectory(SplatFile f)
	{
		if(!f.isDirectory())
			f.mkdirs();
		else
			f.protect();
		files.add(workingDirectory = f);
	}

	public SplatFile createDirectory()
	{
		String s = Config.read("workingdir");
		int counter = 0;
		File toRet = new File(s + "/Download_" + counter++);
		do
		{
			createdDirs.add(toRet);
			toRet = new File(s + "/Download_" + counter++);
		}
		while(toRet.exists() || alreadyCreated(toRet));
		createdDirs.add(toRet);
		return new SplatFile(toRet, "");
	}

	private boolean alreadyCreated(File f)
	{
		for(int i = 0; i < createdDirs.size(); i++)
			if(f.getName().equals(((File)createdDirs.get(i)).getName()))
				return true;
		return false;
	}

/* -------------------------------------------- */
/* -------------------------------------------- */

	private class HeaderPanel extends JPanel
	{
		private JButton toggle;
		private Component center;
		private boolean on;
		private ProgressBar pb;

		public HeaderPanel()
		{
			// make progress bar not exist by default
			this(false);
		}

		public HeaderPanel(boolean progress)
		{
			setLayout(new BorderLayout());
			add(GUtil.filler(4), BorderLayout.NORTH);
			add(GUtil.filler(4), BorderLayout.SOUTH);
			add(GUtil.filler(4), BorderLayout.EAST);
			add(GUtil.filler(4), BorderLayout.WEST);
			final JPanel inner = new JPanel(new BorderLayout());
			toggle = new JButton(title + " Parameters");
			toggle.setEnabled(false);
			on = false;
			toggle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					OptionTab.this.invalidate();
					if(on)
						inner.remove(center);
					else
						inner.add(center, BorderLayout.CENTER);
					on = !on;
					OptionTab.this.validate();
					OptionTab.this.repaint();
				}
			});
			JPanel north = new JPanel(new BorderLayout());
			north.add(toggle, BorderLayout.WEST);
			if(progress)
				north.add(pb = new ProgressBar(), BorderLayout.CENTER);
			inner.add(north, BorderLayout.SOUTH);
		//	inner.add(center, BorderLayout.CENTER);
			add(inner, BorderLayout.CENTER);
		}

		public ProgressBar getProgressBar()
		{
			return pb;
		}

		public void addContent(Component c)
		{
			toggle.setEnabled(true);
			center = c;
			ComponentedConfigWindow.addComponent(center, getTitle() + " Parameters");
//			add(center, BorderLayout.CENTER);
		}
	}

/* -------------------------------------------- */
/* -------------------------------------------- */

	private class InfoPanel extends JPanel
	{
		private JScrollPane jsp;
		private int BIG = 100000;
		public InfoPanel(JTextArea jtf, String title)
		{
			setLayout(new BorderLayout());
			JPanel buffer = new JPanel(new BorderLayout());
			buffer.add(new JLabel(title.trim() + ":"), BorderLayout.WEST);
			add(buffer, BorderLayout.NORTH);

			jsp = new JScrollPane();
			jsp.getViewport().add(jtf);
			add(jsp, BorderLayout.CENTER);
		}

		public void scrollToBottom()
		{
			jsp.scrollRectToVisible(new Rectangle(0, BIG/2, BIG, BIG));
		}

	}

	public static void main(String[] args)
	{
//		o.getWorkingDirectory();
	}

}
