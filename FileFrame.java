import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** @author Gergely Kota

FileFrame is a popup menu that prompts for a File to select.
It shows a JFileChooser in a modal JDialog, and senses when
a File has been selected. At this point, it minimizes the window
thereby allowing the program flow to continue. The selected File
is then returned.
*/

public class FileFrame extends JDialog
{
	private File file;
	private boolean dirs;
	private static FileFrame instance;
	private static boolean cancel;
	private Chooser c;

	// initializer creates the single instance the first time
	// the class is accessed
	static
	{
		instance = new FileFrame();
	}

	// force single instance of FileFrame via static initializer
	// this is because the same task is done each time - the window
	// pops up and returns a selected File.
	private FileFrame()
	{
		setTitle("File Chooser");
		setLocation(300,300);
		setModal(true);
		String s = Config.read("workingdir");
		if(s == null)
			s = ".";
		dirs = false;
		getContentPane().add(c = new Chooser(s), BorderLayout.CENTER);
		pack();
	}

	/** pauses the flow of the program until a File is selected.
		It then returns that File and allows the calling program to continue.
		@return the selected File
		*/
	public static synchronized File getFile(String text, boolean dirs)
	{
		// set the File to return to null and cancel to false
		// once this or the cancel is set, the flow continues
		instance.file = null;
		cancel = false;
		instance.dirs = dirs;
		instance.c.setApproveButtonText(text);
		if(instance.dirs)
			instance.c.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		else
			instance.c.setFileSelectionMode(JFileChooser.FILES_ONLY);
		Timer check = new Timer(2, null);
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(instance.file != null)
					instance.hide();
				if(instance.cancel)
					instance.hide();
			}
		});
		check.start();
		instance.show();
		check.stop();
		return instance.file;
	}

	public static synchronized File getOpenFile()
	{
		return getFile("Open", false);
	}

	public static synchronized File getSaveFile()
	{
		return getFile("Save", false);
	}

	public static synchronized File getDirectory()
	{
		return getFile("Select", true);
	}

	// extension of JFileChooser adapted specifically for this task
	private class Chooser extends JFileChooser
	{
		public Chooser(String s)
		{
			super(new File(s));
		}

		public void approveSelection()
		{
			if(dirs)
				file = getCurrentDirectory();
			else
				file = getSelectedFile();
		}

		public void cancelSelection()
		{
			cancel = true;
		}
	}

/* ---------------------------------------------------- */
/* ---------------------------------------------------- */
/* ---------------------------------------------------- */

	public static void main(String[] args)
	{
		System.out.println(FileFrame.getOpenFile());
//		System.out.println(FileFrame.getSaveFile());
		System.out.println(FileFrame.getDirectory());
	}
}
