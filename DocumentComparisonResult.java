import java.util.*;
import java.io.*;

/** @author Gergely Kota

DocumentComparisonResult is a collection of PairComparisons

*/

public class DocumentComparisonResult implements Saveable
{
	private ArrayList pairs;
	private File workingDir;

	public DocumentComparisonResult()
	{
		pairs = new ArrayList();
	}

	public void writeResults(SplatFile sf)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<HTML>\n");
		sb.append("<font face = \"Courier New\">");
		ArrayList temp = getAll();
		Collections.sort(temp);
		for(int i = 0; i < temp.size(); i++)
			sb.append(((PairComparison)temp.get(i)).fullString()).append("<BR>");
		if(temp.size() == 0)
			sb.append("There were no documents to compare.\n");
		sb.append("</font>");
		sb.append("</HTML>");
		FUtil.write(sb.toString(), sf);
	}

	public void add(PairComparison pc)
	{
		if(!pairs.contains(pc))
			pairs.add(pc);
	}

	public void setWorkingDirectory(File f)
	{
		workingDir = f;
	}

	public File getWorkingDirectory()
	{
		return workingDir;
	}

	public ArrayList getAll()
	{
		return new ArrayList(pairs);
	}

	public PairComparison get(File f1, File f2)
	{
		for(int i = 0; i < pairs.size(); i++)
			if(((PairComparison)pairs.get(i)).getFile1().equals(f1))
				if(((PairComparison)pairs.get(i)).getFile2().equals(f2))
					return ((PairComparison)pairs.get(i));
		return null;
	}

	/** saves this object with its default name
		@return true if the save succeeded
		*/
	public boolean save()
	{
		return save(new File(getWorkingDirectory().getName() + "/" + name()));
	}

	/** saves this object with its default name
		@param f - the File name to save as
		@return true if the save succeeded
		*/
	public boolean save(File f)
	{
		 try
		 {
			 ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			 oos.writeObject(this);
			 oos.close();
			 return true;
		 }
		 catch(Exception e) {e.printStackTrace(); return false;}
	}

	public String name()
	{
		return "" + System.currentTimeMillis() + ".dcr";
	}

	public String toString()
	{
		return pairs.toString();
	}

}


