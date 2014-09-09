import java.io.*;
import javax.swing.*;
import java.util.*;

/** @author Gergely Kota

DcoumentComparison takes a starting point File and converts
all of them to canonical text fcrmat (lowercase alphanumeric
with no white spaces)

Extending classes need to define their canonical form and
what comparisons they wish to run.
*/

public abstract class DocumentComparison implements Serializable, Progressable
{
	private File[] files;
	private File localDir;
	private HashMap strings;
	private HashMap actualStrings;
	private final String ENDING;
	private int tolerance;
	private DocumentComparisonResult result;
	private ArrayList list, loggers;
	private double progress;

	/** @param the File to search for documents in
		*/
	public DocumentComparison(File f)
	{
		String s = Config.read("endtype");
		if(s == null)
			s = "txt";
		ENDING = s;
		result = null;

		localDir = f;
		files = getFiles();
		strings = new HashMap();
		actualStrings = new HashMap();
		list = new ArrayList();
		loggers = new ArrayList();
		progress = 0;
	}

	public void addProgressListener(ProgressListener pl)
	{
		if(!list.contains(pl))
			list.add(pl);
	}

	public double getProgress()
	{
		return progress; // need to make this different
	}

	public void notifyListeners()
	{
		ProgressEvent pe = new ProgressEvent(this, "Comparing Documents");
		for(int i = 0; i < list.size(); i++)
			((ProgressListener)list.get(i)).progressPerformed(pe);
	}

	public void log(String s)
	{
		for(int i = 0; i < loggers.size(); i++)
			((Logger)loggers.get(i)).log(s);
	}

	public void addLogger(Logger l)
	{
		if(!loggers.contains(l))
			loggers.add(l);
	}



	private File[] getFiles()
	{
		ArrayList temp = getFiles(localDir);
		File[] f = new File[temp.size()];
		for(int i = 0; i < f.length; i++)
			f[i] = (File) temp.get(i);
		return f;
	}

	private ArrayList getFiles(File start)
	{
		File[] f = start.listFiles(new TextFilter());
		ArrayList found = new ArrayList();
		for(int i = 0; i < f.length; i++)
			found.add(f[i]);
		File[] dirs = start.listFiles(new DirFilter());
		for(int i = 0; i < dirs.length; i++)
			found.addAll(getFiles(dirs[i]));
//		System.out.println("Returning " + start + " " + found);
		return found;
	}

	public int getTolerance()
	{
		return tolerance;
	}

	public boolean setTolerance(int n)
	{
		if(n == 0)
			return false;
		tolerance = n;
		return true;
	}

	// reads in the found Files
	private String readFile(File f)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(f));
			while(br.ready())
				sb.append(br.readLine()).append("\n");
			return sb.toString();
		}
		catch(Exception e) {return "";} // failure to read is an empty string
	}

	/** @return a description of this comparison type
		*/
	public static String description()
	{
		return "Abstract BaseClass for all comparisons";
	}


	/** upon read, every DocumentComparison stores all legal files'
		canonical String forms for later data. The canonical form
		is defined by each extending class
		@return a DocumentComparison with all available files, ready to compare
		*/
	public DocumentComparison read()
	{
		log("Reading files in " + localDir.getAbsolutePath());
		for(int i = 0; i < files.length; i++)
		{
			File f = files[i];
			String s = readFile(f);
			actualStrings.put(f, s);
			String c = canonicalForm(f);
			strings.put(f, c);
		}
		return this;
	}

	/** @return a list of read files and the working directory
		*/
	public String toString()
	{
		String s = localDir.getAbsolutePath();
		for(int i = 0; i < files.length; i++)
			s += "\n   " + files[i].getName();
		return s;
	}

	/** @return the canonical content of each file by its index number
		*/
	public String getContent(File f)
	{
		return (String) strings.get(f);
	}

	/** @return the original content of each file by its index number
		*/
	public String getOriginalContent(File f)
	{
		return (String) actualStrings.get(f);
	}

	/** @param f the File whose index is to be found
		@return the index for the argument File
		*/
	/** @param i the index to get the File from
		@return the File at a given index
		*/
	public File[] getFileList()
	{
		return files;
	}


	public DocumentComparisonResult compare()
	{
		log("Comparing files in " + localDir.getAbsolutePath());
		boolean cheatFound = false;
		progress = 0;
		DocumentComparisonResult r = new DocumentComparisonResult();
		r.setWorkingDirectory(localDir);
		for(int i = 0; i < files.length; i++)
		{
			int interesting = 0;
			for(int j = 0; j < files.length; j++)
			{
				int num = files.length;
				if(i != j)
				{
					PairComparison pc = compare(files[j], files[i]);
					r.add(pc);
					if(pc.score() > 0)
						interesting++;
				}
				progress = (i*num + j + 1) / ((double)num * num);
				notifyListeners();
			}
			if(interesting != 0)
				cheatFound = true;
			log(files[i].getAbsolutePath() + " cheated from " + interesting + " other documents.");
		}
		if(!cheatFound)
			log("None of the submitted documents had cheat occurences");
		result = r;
		return result;
	}

	public DocumentComparisonResult compare(File f)
	{
		log("Comparing files in " + localDir.getAbsolutePath());
		progress = 0;
		int interesting = 0;
		DocumentComparisonResult r = new DocumentComparisonResult();
		r.setWorkingDirectory(localDir);
		for(int i = 0; i < files.length; i++)
			if(!files[i].equals(f))
			{
				double num = files.length;
				PairComparison pc = compare(files[i], f);
				r.add(pc);
				progress = (i + 1) / num;
				notifyListeners();
				if(pc.score() > 0)
					interesting++;
				log("Compared " + files[i].getName() + " and " + f.getName() + ": " + pc.score());
			}
		log(f.getAbsolutePath() + " cheated from " + interesting + " other documents.");
		result = r;
		return result;
	}

	public DocumentComparisonResult getResult()
	{
		if(result == null)
			return compare();
		return result;
	}


	/** @param f the File whose content is referred to
		@param adjustedIndex the index of a char in the canonical form content of the File
		@return the index of that same char in the original content of the File
		*/
	public abstract int indexMap(File f, int adjustedIndex);
	public abstract String canonicalForm(File f);
	public abstract PairComparison compare(File f1, File f2);

/* ----------------------------------------------------- */
/* private class that specifies what legal text files are */
	private class TextFilter implements FileFilter
	{
		public boolean accept(File f)
		{
			if(f.getName().endsWith(ENDING))
				return true;
			return false;
		}

	}

/* ----------------------------------------------------- */
/* private class that specifies what legal directories are */
	private class DirFilter implements FileFilter
	{
		public boolean accept(File f)
		{
			if(f.isDirectory())
				return true;
			return false;
		}

	}
/* ----------------------------------------------------- */

	public static void main(String[] arhs)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(new File("Downloads/Java.txt")));
			while(br.ready())
				sb.append(br.readLine()).append("\n");
			System.out.println(sb.toString());
			JFrame jf = new JFrame();
			JTextArea jta = new JTextArea();
			jta.setText(sb.toString());
			jf.getContentPane().add(jta);
			jf.setSize(400,800);
			jf.show();
		}
		catch(Exception e) {} // failure to read is an empty string
	}
}
