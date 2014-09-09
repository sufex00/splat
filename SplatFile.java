import java.io.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;

/** @author Gergely Kota

SplatFile overrides File's delete to automatically do recursive deletion.
Also allows a File to be protected from deletion and can copy a file
elsewhere.

*/


public class SplatFile extends File
{
	private boolean protect;
	private HashMap children = new HashMap();

	public SplatFile(File parent, String child)
	{
		super(parent, child);
	}

	public SplatFile(String pathname)
	{
		super(pathname);
	}

	public SplatFile(String parent, String child)
	{
		super(parent, child);
	}

	public SplatFile(URI uri)
	{
		super(uri);
	}

/* ----------- Constructors above here ------------------- */

	public boolean copyTo(File f)
	{
		try
		{
			FileChannel in = new FileInputStream(this).getChannel();
			FileChannel out = new FileOutputStream(f).getChannel();
			in.transferTo((int)0, (int)in.size(), out);
			in.close();
			out.close();
			return true;
		}
		catch(Exception e) {return false;}
	}

	public boolean deleteAll()
	{
		File[] files = listFiles();
		if(files != null)
			for(int i = 0; i < files.length; i++)
			{
				SplatFile sf = (SplatFile) children.get(new SplatFile(files[i], "").getName());
				// if there is no such name in the protected list
				if(sf == null)
					new SplatFile(files[i], "").deleteAll();
				else
					sf.deleteAll();
			}
		if(!protect)
		{
			System.out.println("Deleting " + getName());
			return delete();
		}
		return false;
	}

	public void protect()
	{
		children = new HashMap();
		protect = true;
//		System.out.println(getName() + " protected");
		File[] files = listFiles();
		if(files == null)
			return;
		for(int i = 0; i < files.length; i++)
		{
			SplatFile sf = new SplatFile(files[i], "");
			children.put(sf.getName(), sf);
			sf.protect();
		}
	}


/* -------------------------------------------------------- */
/* -------------------------------------------------------- */

	public static void main(String[] args)
	{
		SplatFile sf = new SplatFile("C:/sp.pdf");
		sf.copyTo(new File("copied.pdf"));
//		SplatFile save = new SplatFile("c:/gergely.jpg");
//		save.copyTo(new File("Delete/gerg.jpg"));
//		System.out.println(sf.deleteAll());
//		System.out.println(sf.copyTo(new File("Test/gerg.jpg")));
	}

}