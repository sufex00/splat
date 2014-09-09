import java.io.*;
import java.util.*;

/** @author Gergely Kota

PairComparison contains the results for the comparison of 2 files

*/

public class PairComparison implements Serializable, Comparable
{
	private File file1, file2;
	private String orig1, orig2;
//	private String html1, html2;
	private boolean[] used1, used2;
	private ArrayList set1, set2;
	private int cheats;
	private double score;

	private String lastColor1, lastColor2, lastResult1, lastResult2;

	public PairComparison(File f1, File f2)
	{
		file1 = f1;
		file2 = f2;
		lastColor1 = lastColor2 = lastResult1 = lastResult2 = null;
		set1 = new ArrayList();
		set2 = new ArrayList();
		cheats = 0;
		score = -1;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file1));
			StringBuffer sb = new StringBuffer();
			while(br.ready())
				sb.append(br.readLine()).append("\n");
			orig1 = sb.toString();

			br = new BufferedReader(new FileReader(file2));
			sb = new StringBuffer();
			while(br.ready())
				sb.append(br.readLine()).append("\n");
			orig2 = sb.toString();

		}
		catch(Exception e) {}
		used1 = new boolean[orig1.length()]; // false by default
		used2 = new boolean[orig2.length()];

	}

	public int compareTo(Object o)
	{
		try
		{
			return (int) (1000 * (((PairComparison) o).score - score));
		}
		catch(Exception e) {return -1;}
	}

	public void log(int start1, int end1, int start2, int end2)
	{
		cheats++;
		for(int i = start1; i < end1; i++)
			used1[i] = true;
		for(int i = start2; i < end2; i++)
			used2[i] = true;
	}

	public double score()
	{
		if(score < 0)
		 	return ((double)cheats)/orig1.length();
		return score;
	}

	public void setScore(double x)
	{
		score = x;
	}

	public File getFile1()
	{
		return file1;
	}

	public File getFile2()
	{
		return file2;
	}

	public String getHTML1(String colorcode)
	{
		if(!colorcode.equals(lastColor1))
		{
			lastColor1 = colorcode;
			lastResult1 = getHTML(orig1, used1, colorcode);
		}
		return lastResult1;
	}

	public String getHTML2(String colorcode)
	{
		if(!colorcode.equals(lastColor2))
		{
			lastColor2 = colorcode;
			lastResult2 = getHTML(orig2, used2, colorcode);
		}
		return lastResult2;
	}

	public boolean equals(Object o)
	{
		try
		{
			PairComparison other = (PairComparison) o;
			if(!getFile1().equals(other.getFile1()))
				return false;
			if(!getFile2().equals(other.getFile2()))
				return false;
			return true;
		}
		catch(Exception e) {return false;}
	}

	public String toString()
	{
		// we will ideally use the very original names the files were
//		String s = FileConverter.getOriginal(getFile1()).getName() + ", ";
//		s += FileConverter.getOriginal(getFile2()).getName();
		String s = getFile1().getName() + ", ";
		s += getFile2().getName();
		int i = (int) (1000*score());
		int dec = i/10;
		String cheat = "[" + ((dec<10)?" ":"") + dec + "." + (i%10) + "%]";
		return cheat + " " + s;
	}
	
	public String fullString()
	{
		String s = getFile1().getAbsolutePath() + ", ";
		s += getFile2().getAbsolutePath();
		int i = (int) (1000*score());
		String cheat = "[" + (i/10) + "." + (i%10) + "%]";
		return cheat + " " + s;
	}

	private String getHTML(String original, boolean[] map, String color)
	{
		int max = map.length;
		StringBuffer sb = new StringBuffer();
		sb.append("<HTML>");

		int count = 0;
		while(count < max)
		{
			int start = count;
			while(count < max && map[count] == false) {count++;}
			sb.append(original.substring(start, count));

			start = count;
			while(count < max && map[count] == true) {count++;}
			sb.append(wrap(original.substring(start, count), color));
		}

		sb.append("</HTML>");
		return sb.toString().replaceAll("\n", "<BR>");
	}

	private String wrap(String content, String color)
	{
		return "<font color = " + color + ">" + content + "</font>";
	}



	public static void main(String[] args)
	{
		new PairComparison(new File("Downloads/Borland.txt"), new File("Downloads/tr.txt"));


	}
}
