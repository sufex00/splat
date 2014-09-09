import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/** @author Gergely Kota

SavedOptionTabs run a websearch + comparison, and display the result

*/



public class SavedOptionTab extends OptionTab
{
//	private OptionPanel compare;
	private SelectiveInfo file;

	public SavedOptionTab()
	{
		super("Saved Search");
		JPanel center = new JPanel(new BorderLayout());
		JPanel north = new JPanel(new GridLayout(2,1));
		file = new SelectiveInfo("File", false, 50);
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try
				{
					file.set(FileFrame.getOpenFile().getAbsolutePath());
				}
				catch(Exception e) {}
			}
		});
		JPanel small = new JPanel(new BorderLayout());
		small.add(file, BorderLayout.CENTER);
		small.add(browse, BorderLayout.EAST);
		JPanel buffer = new JPanel(new BorderLayout());
		buffer.add(small, BorderLayout.NORTH);
		setCenter(buffer);
		setDescription("Enter a file name to load a previous comparison result from");

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


	public void go()
	{
		// setWorkingDirectory somehow ...
		SplatFile f = new SplatFile(file.read());
		DocumentComparisonResult dcr = loadFile(f);
		if(dcr != null)
		{
			log("Restoring DocumentComparisonResult from " + f.getName());
			launch(dcr);
		}
		else
			log("Failed to restore from file " + f.getName());
	}

	public void setup()
	{
//		compare.setup();
	}

	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		jf.getContentPane().add(new SavedOptionTab());
		jf.pack();
		jf.show();
	}

}
