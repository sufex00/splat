import java.io.*;
import java.util.*;

/** @author Gergely Kota

File conversion utility reads in all Files in the specified directory.
Upon covert(), it uses the DependencyTree of provided conversions to
convert all files to the final type in the least amount of steps.

*/

public class FileConverter
{
	private File currentDir;
	private String dirName;
	private File[] files;
	private DependencyTree dt;
	private String endtype;
	private ArrayList loggers;
	private static HashMap filemap = new HashMap();

	public FileConverter(File f)
	{
		currentDir = f;
		loggers = new ArrayList();

		dirName = clean(currentDir.getAbsolutePath());
		// and damn windows for using an escape character in their filepaths
		endtype = Config.read("endtype");
		endtype = (endtype == null)? "txt": endtype;
		files = f.listFiles(new TypeFilter());
		dt = new DependencyTree("txt");
		createTree();
		dt.order();
	}

	public static void add(File became, File was)
	{
		filemap.put(became, was);
	}

	public static File getPrevious(File f)
	{
		return (File) filemap.get(f);
	}

	public static File getOriginal(File f)
	{
		File temp = (File) filemap.get(f);
		if(temp == null)
			return f;
		while(filemap.get(temp) != null)
			temp = (File) filemap.get(temp);
		return temp;
	}


	private void log(String s)
	{
		for(int i = 0; i < loggers.size(); i++)
			((Logger)loggers.get(i)).log(s);
	}

	public void addLogger(Logger l)
	{
		if(!loggers.contains(l))
			loggers.add(l);
	}

	private String clean(String s)
	{
		char[] c = s.toCharArray();
		for(int i = 0; i < c.length; i++)
			if(c[i] == '\\')
				c[i] = '/';
		return new String(c);
	}

	// create the Tree of conversion possibilites. Each vertex is a filetype,
	// each edge represents the ability to convert from the parent to the child
	private void createTree()
	{
		String[] types = getTypes();
		// for each type, log all conversion possibilities
		// for the (i==j) case, use that to check conversion to the endtype
		for(int i = 0; i < types.length; i++)
			for(int j = 0; j < types.length; j++)
				if(i != j)
					addNode(types[i], types[j]);
				else
					addNode(types[i]);
	}

	private void addNode(String t1)
	{
		addNode(t1, endtype);
	}

	private void addNode(String t1, String t2)
	{
		if(Config.read(t1 + "2" + t2) != null)
			dt.addPair(t1, t2);
	}


	/** performs the conversions on all the files based on the DependencyTree
		result generated earlier.
		*/
	public void convert()
	{
		DependencyTree.Vertex[] v = dt.getOrder();
		for(int i = 0; i < v.length-1; i++)
		{
			// get all files of the type to convert
			// get the type to convert to
			// call the conversion command for these types on the file
			File[] f = currentDir.listFiles(new QuickFilter(v[i].name()));
			String next = v[i].next().name();
			for(int j = 0; j < f.length; j++)
				convert(f[j], (v[i].name() + "2" + next));
		}

		File[] dirs = currentDir.listFiles(new DirFilter());
		if(dirs == null)
			return;
		for(int i = 0; i < dirs.length; i++)
		{
			// add the same listeners
			FileConverter recurser = new FileConverter(dirs[i]);
			for(int j = 0; j < loggers.size(); j++)
				recurser.addLogger((Logger)loggers.get(j));
			recurser.convert();
		}
	}

	// parses the generic command in the config file and replaces
	// keywords with the appropriate values.
	private void convert(File f, String key)
	{
		String command = Config.read(key);
		String name = f.getName();
		Debug.println(command);
		command = command.replaceAll("workingdir", dirName);
		Debug.println(command);
		command = command.replaceAll("infile", name);
		String infile = name;
		Debug.println(command);
		String end = key.substring(key.lastIndexOf("2")+1);
		name = name.substring(0, name.lastIndexOf(".")+1) + end;
		File test = new File(dirName + "/" + name);
		while(test.exists())
			test = new File(dirName + "/" + (name = "_" + name));
		command = command.replaceAll("outfile", name);
		String outfile = name;
		Debug.println(command);
		try
		{
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			int exit = p.exitValue();
			String action = exit == 0? "Succeeded" : "Failed";
//			Debug.println("Succeeded: " + command);
			log("Converted " + f.getAbsolutePath() + " to " + test.getAbsolutePath() + " ... " + action);
			FileConverter.add(new File(outfile), new File(infile));
		}
		catch(Exception e)
		{
			Debug.println("Failed: " + command);
			log("FAILED: " + command);
		}

	}

	/** shows all the found types and what each is converted to in the process
		*/
	public String toString()
	{
		String s = "";
		for(int i = 0; i < files.length; i++)
			s += files[i].getName() + "\n";
		s += dt.orderString();
		return s;
	}

	// reads the types of files in use from the config file
	// has a hardcoded default for ps and pdf.
	private String[] getTypes()
	{
		String s = Config.read("filetype");
		if(s == null)
			return new String[]{"ps", "pdf"}; // default

		StringTokenizer st = new StringTokenizer(s);
		String[] types = new String[st.countTokens()];
		for(int i = 0; i < types.length; i++)
			types[i] = st.nextToken().toLowerCase();

		return types;
	}


/* ---------------------------------------------- */
/* ---------------------------------------------- */
/* ---------------------------------------------- */

	// picks up all Files that are of the types specified in the config file
	private class TypeFilter implements FileFilter
	{
		private String[] types;

		public TypeFilter()
		{
			types = getTypes();
		}

		public boolean accept(File pathname)
		{
			String name = pathname.getName();

			for(int i = 0; i < types.length; i++)
				if(name.endsWith("." + types[i]))
					return true;
			return false;
		}

	}

	private class DirFilter implements FileFilter
	{
		public boolean accept(File f)
		{
			return f.isDirectory();
		}
	}

	// picks up all Files that are of the passed type
	private class QuickFilter implements FileFilter
	{
		private String end;

		public QuickFilter(String s)
		{
			end = "." + s;
		}

		public boolean accept(File pathname)
		{
			String name = pathname.getName().toLowerCase();
			if(name.endsWith(end))
				return true;
			return false;
		}
	}


/* ---------------------------------------------- */
/* ---------------------------------------------- */
/* ---------------------------------------------- */

	public static void main(String[] args)
	{
		FileConverter fc = new FileConverter(new File("Test/Test"));
//		System.out.println(" ------------- ");
//		System.out.println(fc);
		fc.convert();
	}
}
