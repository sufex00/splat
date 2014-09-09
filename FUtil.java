import java.io.*;


/** @author Gergely Kota
	File Utilities
	*/
	
public class FUtil
{
	public static String toString(File f)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			StringBuffer sb = new StringBuffer();
			while(br.ready())
				sb.append(br.readLine()).append("\n");
			return sb.toString();
		}
		catch(Exception e) {return null;}
	}
	
	public static void write(String s, File to)
	{
		try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(to));
			pw.println(s);
			pw.close();
		}
		catch(Exception e) {}
	}
	
	public static String webfix(String s)
	{
		s = s.trim();
		String temp = s.toLowerCase();
		if(temp.indexOf("http://") == 0)
			return s;
		return "http://"+s;
	}



}
