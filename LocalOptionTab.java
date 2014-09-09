import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/** @author Gergely Kota

LocalOptionTabs run a websearch + comparison, and display the result

*/



public class LocalOptionTab extends OptionTab
{
	private OptionPanel compare;
	private SelectiveInfo file;

	public LocalOptionTab()
	{
		super("Local Comparison");
		JPanel center = new JPanel(new BorderLayout());
		JPanel north = new JPanel(new GridLayout(2,1));
		file = new SelectiveInfo("Directory", false, 80);
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try
				{
					file.set(FileFrame.getDirectory().getAbsolutePath());
				}
				catch(Exception e) {}
			}
		});

		JPanel small = new JPanel(new BorderLayout());
		small.add(file, BorderLayout.CENTER);
		small.add(browse, BorderLayout.EAST);
		compare = new ComparisonOptionPanel(this);
		center.add(compare, BorderLayout.SOUTH);
		JPanel buffer = new JPanel(new BorderLayout());
		buffer.add(small, BorderLayout.NORTH);
		setCenter(buffer);
		setAdvanced(compare);
		setDescription("Enter a directory whose contents are to be compared");
	}

	public void go()
	{
		SplatFile f = new SplatFile(file.read());
		log("*************************");
		if(!f.isDirectory())
		{
			log("Invalid directory entered");
			return;
		}
		new RepaintRunner().start();
	}

	private class RepaintRunner extends Thread
	{

		public void run()
		{
			setWorkingDirectory(new SplatFile(file.read()));
			log("Converting to text");
			FileConverter fc = new FileConverter(getWorkingDirectory());
			fc.addLogger(LocalOptionTab.this);
			fc.convert();
			log("Starting a new Comparison");
			DocumentComparison dc = (DocumentComparison) compare.result();
			if(dc == null)
			{
				log("Could not create DocumentComparison");
				return;
			}
			dc.addLogger(LocalOptionTab.this);
			dc.addProgressListener(getProgressBar());
			DocumentComparisonResult hdc = dc.getResult();
			log("Showing Comparison Result");
			launch(hdc);
		}
	}

	public void setup()
	{
		compare.setup();
	}

	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		jf.getContentPane().add(new LocalOptionTab());
		jf.pack();
		jf.show();
	}

}
