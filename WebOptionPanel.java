import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/** @author Gergely Kota

WebOptionPanel allows the user to select a module and enter
information into textfields for it, specialized for WebSearches

*/

public class WebOptionPanel extends OptionPanel
{
	// need to have a list of existing classes
	// need to display textfield for each option ... how do??

	private WebFields wf;

	public WebOptionPanel(OptionTab ot)
	{
		super(WebSearch.class, ot);
		setCenter(wf = new WebFields());
		setClass();
		Config.addActionListener(this);
	}

	private void setClass()
	{
		try
		{
			setClass(Class.forName(Config.read("websearch")));
		}
		catch(Exception e) {setClass(WebSearch.class);}
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals("websearch"))
			setClass();
	}


	public Object result()
	{
		return wf.result();
	}

	public Object result(Object o)
	{
		return wf.result((WebSearch)o);
	}

	public void setRecycleDir(boolean x)
	{
		wf.setRecycleDir(x);
	}

	public void setDirectory()
	{
		wf.setDirectory();
	}


	private class WebFields extends JPanel implements ActionListener
	{
		private SelectiveInfo regex, destination, depth, num, time;
		private boolean freshDir = false;

		public WebFields()
		{
			regex = new SelectiveInfo("Exclude", false, 150, true);
			regex.setDescription("Enter regular expressions to filter");
			destination = new SelectiveInfo("Download Location", false, 150, true);
			destination.setDescription("Select a directory to download files to");
			setDirectory();
			depth = new SelectiveInfo("Max Search Depth", false, 150, true);
			depth.setDescription("Select the depth that links will be followed to");
			depth.set(Config.read("maxdepth"));
			num = new SelectiveInfo("Max Articles", false, 150, true);
			num.setDescription("Select the number of articles to download");
			num.set(Config.read("maxdownloads"));
			time = new SelectiveInfo("Max Time", false, 150, true);
			time.setDescription("Select the maximum time (seconds) the websearch should run for");
			time.set(Config.read("timeout"));

			setLayout(new BorderLayout());
			JPanel entries = new JPanel(new GridLayout(5,1));
			entries.add(destination);
			entries.add(depth);
			entries.add(num);
			entries.add(time);
			entries.add(regex);
			add(entries, BorderLayout.CENTER);
			Config.addActionListener(this);

	/*		JButton s = new JButton("Select All");
			s.addActionListener(new ButtonTask(true));
			JButton ds = new JButton("Deselect All");
			ds.addActionListener(new ButtonTask(false));
			JPanel south = new JPanel(new BorderLayout());
			south.add(s, BorderLayout.WEST);
			south.add(ds, BorderLayout.EAST);
			add(south, BorderLayout.SOUTH);
	*/

		}

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals("maxdownloads"))
				num.set(Config.read("maxdownloads"));
			if(ae.getSource().equals("timeout"))
				time.set(Config.read("timeout"));
			if(ae.getSource().equals("maxdepth"))
				depth.set(Config.read("maxdepth"));
		}

		public void setRecycleDir(boolean x)
		{
			freshDir = x;
		}

		private void setDirectory()
		{
			if(freshDir)
				return;
			destination.set(getOwner().createDirectory().getName());
			freshDir = true;
		}

		public class ButtonTask implements ActionListener
		{
			private boolean set;
			public ButtonTask(boolean x)
			{
				set = x;
			}

			public void actionPerformed(ActionEvent ae)
			{
				regex.setEnabled(set);
				destination.setEnabled(set);
				num.setEnabled(set);
				depth.setEnabled(set);
				time.setEnabled(set);
			}
		}

		public AbstractWebSearch result()
		{
			AbstractWebSearch ws;
			try
			{
				Class c = getSelectedClass();
				ws = (AbstractWebSearch) c.newInstance();
			}
			catch(Exception e) {e.printStackTrace(); return null;}
			return result(ws);
		}

		public AbstractWebSearch result(AbstractWebSearch ws)
		{
			String reges, destinationS, depthS, numS, timeS;

			reges = regex.read();
			destinationS = destination.read();
			depthS = depth.read();
			numS = num.read();
			timeS = time.read();



			try
			{
				((RegExWebSearch) ws).setRegEx(reges);
				getOwner().log("Set filtered expression to " + reges);
			}
			catch(Exception e)
			{
				getOwner().log("Current websearch does not support regular expression filtering.");
			}

			if(destinationS != null && !destinationS.equals(""))
			{
				SplatFile f = new SplatFile(destinationS);
				// delete at the end only if starting a new directory
//				if(!f.isDirectory())
//					f.deleteOnExit();
				getOwner().setWorkingDirectory(f);
				ws.setDownloadLocation(f);
				getOwner().log("Set download location to " + destinationS);
			}

			try
			{
				ws.setDownloads(Integer.parseInt(numS));
				getOwner().log("Set number of downloads to " + numS);
			}
			catch(Exception e) {}
			try
			{
				ws.setDepth(Integer.parseInt(depthS));
				getOwner().log("Set maximum depth to " + depthS);
			}
			catch(Exception e) {}
			try
			{
				ws.setTime(Integer.parseInt(timeS));
				getOwner().log("Set timeout to " + timeS + " seconds");
			}
			catch(Exception e) {}

//			freshDir = false;
			setDirectory();
			return ws;
		}

	}


}



