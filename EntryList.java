import java.util.*;
import java.io.*;

/** @author Gergely Kota

EntryList provides an interface for reading and writing from
the departments.txt file by keys and values.

*/

public class EntryList
{
	private static HashMap strings;
	private static ArrayList depts;
	/** INPUT is the File from which options are read */
	public static final File INPUT = new File("data/departments.txt");
	/** ESCAPE is the sequence that comments the line */
	public static final String ESCAPE = "//";
	/** DELIM is the sequence that separates dept and url */
	public static final String DELIM = "==";
	/** ILLEGAL is the return for a non-existent int */
	public static final int ILLEGAL = Integer.MIN_VALUE;

	// read in key/value pairs on startup
	static
	{
		init();
	}

	// init reads the config file and sets up all key/value pairs
	// in a HashMap. NOTE: All keys are mapped to lowercase for
	// case insensitivity
	private static void init()
	{
		strings = new HashMap();
		depts = new ArrayList();
		try
		{
			// create a reader
			BufferedReader br = new BufferedReader(new FileReader(INPUT));
			while(br.ready())
			{
				// try each line of the config file
				try
				{
					// read a line
					// check if it starts with ESCAPE
					// pull key off the front
					// the rest is the value
					// add value hashed by key
					String s = br.readLine().trim();
					if(s.startsWith(ESCAPE))
						continue;
					String dept = s.substring(0, s.indexOf(DELIM)).trim();
					String url = s.substring(s.indexOf(DELIM)+DELIM.length()).trim();
					strings.put(dept.toLowerCase(), url);
					depts.add(dept);
				}
				// if there are any problems, ignore this line
				catch(Exception e) {}
			}
		}
		// failed reader results in no key/value pairs.
		catch(Exception e) {}
		Collections.sort(depts);
	}

	/** @param key the String by which the lookup occurs
		@param value the String to associate with the key
		@return true if the write succeeded
		write allows a program to add or change information in the
		config file. If the key is already used, the associated value
		is replaced. If the key is new, the new key/value pair is added
		to the end of the config file. In either case, the config file
		is then reread to reflect the changes immediately.
		*/
	public static boolean write(String dept, String url)
	{
		if(dept == null || url == null)
			return false;

		boolean worked = true;
		boolean replaced = false;
		StringBuffer sb = new StringBuffer();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(INPUT));
			while(br.ready())
			{
				// read one line at a time
				// for each, check if it uses the passed key
				// if so, replace its value, else keep it as is
				String temp = br.readLine();
				if(temp.toLowerCase().trim().startsWith(dept.toLowerCase()))
				{
					temp = dept.trim() + " " + DELIM + " " + url.trim();
					replaced = true;
				}

				sb.append(temp).append("\n");
			}
			if(!replaced)
				sb.append(dept).append(" ").append(DELIM).append(" ").append(url).append("\n");
		}
		// make a note in case of any failure
		catch(Exception e) {worked = false;}

		// sb now holds the new contents of the file.
		// if there was a failure, do not write the file (to avoid corruption or loss)
		if(!worked)
			return false;

		try
		{
			// write the contents of the new file to the config file
			// reread the contents of the file to reflect the changes
			PrintWriter pw = new PrintWriter(new FileWriter(INPUT));
			pw.print(sb.toString());
			pw.close();
			init();
			return true;
		}
		catch(Exception e) {return false;}
	}


	// no instances exist.
	private EntryList() {}

	/** @param the key to look up a value for
		@return the value associated with that key
		read a value associated with a key from the config file
		*/
	public static String read(String dept)
	{
		if(strings == null)
			return null;
		return (String) strings.get(dept.toLowerCase());
	}

	public static String[] entries()
	{
		Object[] o = depts.toArray();
		String[] s = new String[o.length];
		for(int i = 0; i < s.length; i++)
			s[i] = (String) o[i];
		return s;
	}


/* -------------------------------------------- */
/* -------------------------------------------- */
/* -------------------------------------------- */

	public static void main(String[] ahrs)
	{
		System.out.println(read("Arizona"));
		System.out.println(write("asu", "www.cs.asu.edu"));
		System.out.println(read("ASU"));


	}
}
