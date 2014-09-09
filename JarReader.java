/*
	Reads data from a Jar/Zip file,
	written by Shafik Amin
*/

import java.util.jar.*;
import java.io.*;
import java.util.*;

public class JarReader extends JarFile
{
	public JarReader(String filename) throws IOException
	{
		super(filename);
	}

	/* get all file names in the zip/jar as a String[] */
	public String[] getFileNames()
	{
		ArrayList arr = new ArrayList();
		Enumeration e = entries();
		while (e.hasMoreElements())
			arr.add(e.nextElement().toString());
		return (String[])(arr.toArray(new String[1]));
	}

	/* subfilename is a compressed file inside the zip */
	public InputStream open(String subfilename) throws IOException
	{
		return getInputStream(getEntry(subfilename));
	}

	/* read a whole file into a string (Directories
	   will return an empty string) */
	public String readIntoString(String subfilename)
	{
		String toRet = "";
		try
		{
			InputStream in = open(subfilename);
			BufferedInputStream bin = new BufferedInputStream(in);
			char temp;
			while ((temp = (char)(bin.read())) != (char)-1)
				toRet += temp;
		}

		catch (Exception e)
		{
			System.out.println(e);
		}

		return toRet;
	}

	/* for convenience */
	public StringBuffer readIntoStringBuffer(String subfilename)
	{
		return new StringBuffer(readIntoString(subfilename));
	}

	/* Tester */
	public static void main(String[] args)
	{
		try
		{
			JarReader r = new JarReader("test.zip");
			System.out.println(r.readIntoString("test.txt"));
			String[] s = r.getFileNames();

			for (int i =0; i < s.length; i++)
			{
				System.out.println(s[i]);
			}

		}

		catch (Exception e) { System.out.println(e); }
	}
}