import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/** @author Gergely Kota

	Util class contains some useful static getter methods.

  */

public class Util
{
	/** @return array of found classes
		@param superClass the base class whose inheritance tree to limit search to
		finds all Class files in the working directory that extend the
		argument superclass.
		*/
	public static Class[] getLocalClasses(Class superClass)
	{
		ArrayList classes = new ArrayList();
		getLocalClasses(new File("."), superClass, classes);
		Class[] ret = new Class[classes.size()];
		for (int i = 0; i < classes.size(); i++)
			ret[i] = (Class)classes.get(i);
		return ret;
	}

	public static Class[] getJarClasses(Class superclass)
	{
		try
		{
			ArrayList classes = new ArrayList();
			JarReader jr = new JarReader("splat.jar");
			String[] s = jr.getFileNames();
			for(int i = 0; i < s.length; i++)
			{
				if(!s[i].endsWith(".class"))
					continue;
				if(s[i].indexOf("$") >= 0)
					continue;
				try
				{
					Class c = Class.forName(s[i].substring(0, s[i].lastIndexOf(".")));
					if(superclass.isAssignableFrom(c))
						classes.add(c);
				}
				catch(Exception e) {}
			}

			Debug.println(classes);
			Class[] temp = new Class[classes.size()];
			for(int i = 0; i < classes.size(); i++)
				temp[i] = (Class) classes.get(i);
			return temp;
		}
		catch(Exception e) {return null;}
	}

	private static void getLocalClasses(File dir, Class superClass, ArrayList classes)
	{
		File[] files = dir.listFiles(new JavaClassFilter());
		for (int i = 0; i < files.length; i++)
		{
			try
			{
				String s = files[i].getName().substring(0, files[i].getName().indexOf(".class"));
				Class temp = Class.forName(s);
				if (superClass.isAssignableFrom(temp))
					classes.add(temp);
			}
			catch (Exception e) { /* carry on */ }
		}
	}

	// implementing FilenameFilter to keep only classes
	private static class JavaClassFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			if(name.indexOf("$") != -1) // do no accept inners
				return false;
			if(name.endsWith(".class")) // accept classes only
				return true;
			return false;	// reject all others
		}
	}

	public static Class[] merge(Class[] c1, Class[] c2)
	{
		if(c1 == null)
			return c2;
		if(c2 == null)
			return c1;
		ArrayList list = new ArrayList();
		for(int i = 0; i < c1.length; i++)
			if(!list.contains(c1[i]))
				list.add(c1[i]);
		for(int i = 0; i < c2.length; i++)
			if(!list.contains(c2[i]))
				list.add(c2[i]);
		Class[] temp = new Class[list.size()];
		for(int i = 0; i < list.size(); i++)
			temp[i] = (Class) list.get(i);
		return temp;
	}


	public static void main(String[] args)
	{
		Class[] c1 = getJarClasses(DocumentComparison.class);
		Class[] c2 = getJarClasses(OptionTab.class);
		Class[] result = merge(c1, c2);
		for(int i = 0; i < c1.length; i++)
			System.out.println(c1[i]);
		System.out.println();
		for(int i = 0; i < c2.length; i++)
			System.out.println(c2[i]);
		System.out.println();
		for(int i = 0; i < result.length; i++)
			System.out.println(result[i]);
	}
}
