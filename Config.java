import java.util.*;
import java.io.*;
import java.awt.event.*;

/** @author Gergely Kota

Config provides an interface for reading and writing from
the options.cfg file by keys and values.

*/

public class Config
{
	private static final ArrayList listeners = new ArrayList();
	private static HashMap strings;
	private static HashMap descriptions;
	/** INPUT is the File from which options are read */
	public static final File INPUT = new File("data/options.cfg");
	public static final File DESC = new File("data/options.help");
	/** ESCAPE is the sequence that comments the line */
	public static final String ESCAPE = "//";
	/** ILLEGAL is the return for a non-existent int */
	public static final int ILLEGAL = Integer.MIN_VALUE;

	// read in key/value pairs on startup
	static
	{
		strings = init(INPUT);
		descriptions = init(DESC);
	}

	public static void addActionListener(ActionListener al)
	{
		listeners.add(al);
	}

	private static void notifyListeners(String source)
	{
		ActionEvent ae = new ActionEvent(source, 0, "write");
		for(int i = 0; i < listeners.size(); i++)
			((ActionListener)listeners.get(i)).actionPerformed(ae);
	}

	// init reads the config file and sets up all key/value pairs
	// in a HashMap. NOTE: All keys are mapped to lowercase for
	// case insensitivity
	private static HashMap init(File f)
	{
		HashMap temp = new HashMap();
		try
		{
			// create a reader
			BufferedReader br = new BufferedReader(new FileReader(f));
			while(br.ready())
			{
				// try each line of the config file
				try
				{
					// read a line
					// ignore if it starts with "//"
					// pull key off the front
					// the rest is the value
					// add value hashed by key
					String s = br.readLine().trim();
					if(s.startsWith(ESCAPE))
						continue;
					StringTokenizer st = new StringTokenizer(s);
					String key = st.nextToken().toLowerCase();
					String value = "";
					while(st.hasMoreTokens())
						value += " " + st.nextToken();

					if(key != null)
						temp.put(key, value.trim());
				}
				// if there are any problems, ignore this line
				catch(Exception e) {}
			}
		}
		// failed reader results in no key/value pairs.
		catch(Exception e) {}
		return temp;
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
	public static boolean write(String key, String value)
	{
		if(key == null || value == null)
			return false;

		key = key.trim();
		if(key.equals("//"))
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
				StringTokenizer st = new StringTokenizer(temp);
				if(st.hasMoreTokens())
					if(key.equals(st.nextToken()))
					{
						temp = key + " " + value;
						replaced = true;
					}

				sb.append(temp).append("\n");
			}
			if(!replaced)
				sb.append(key).append(" ").append(value).append("\n");
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
			strings = init(INPUT);
			notifyListeners(key);
			return true;
		}
		catch(Exception e) {return false;}
	}

	/** @param key the String by which the lookup occurs
		@return the int value for that key
		returns the value associated with the argument key as an int
		in case of failure, ILLEGAL is returned
		*/
	public static int getInt(String key)
	{
		try
		{
			return Integer.parseInt(read(key));
		}
		catch(Exception e) {return ILLEGAL;}
	}

	// no instances exist.
	private Config() {}

	/** @param the key to look up a value for
		@return the value associated with that key
		read a value associated with a key from the config file
		*/
	public static String read(String key)
	{
		if(strings == null)
			return null;
		return (String) strings.get(key.toLowerCase());
	}

	public static String readDescription(String key)
	{
		if(descriptions == null)
			return null;
		return (String) descriptions.get(key.toLowerCase());
	}

	public static String[] getKeys()
	{
		Object[] o = strings.keySet().toArray();
		String[] s = new String[o.length];
		for(int i = 0; i < o.length; i++)
			s[i] = (String) o[i];
		return s;
	}


/* -------------------------------------------- */
/* -------------------------------------------- */
/* -------------------------------------------- */

	public static void main(String[] ahrs)
	{
		Debug.println(read("edgesize"));
		Debug.println(write("edgesize", "3"));
		Debug.println(read("edgesize"));


	}
}
