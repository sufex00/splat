import java.io.*;
import java.util.*;

/** @author Gergely Kota

SingleSubstringDocumentComparison compares documents by finding identical
substrings in the canonical form (which is lowercase with all punctuation and
whitespace stripped out) and marking them as cheated. The result is reported
all together, the various cheats are not separated, instead they are all
shown at the same time.
*/


public class SubstringComparison extends DocumentComparison
{
	private static final int DEFAULT_DETAIL = 50;
	private static final String legals = "abcdefghijklmnopqrstuvwxyz0123456789";
	private int[][] map;
	private int count;
	private HashMap mappings;

	/** @param the File to look for documents in
		*/
	public SubstringComparison(File loc)
	{
		super(loc);
		mappings = new HashMap();
		count = 0;
		setTolerance(10);
	}


	/** @param orig the original content of the file
		@return all whitespace and punctuation is stripped from the input
		*/
	public String canonicalForm(File f)
	{
		String orig = getOriginalContent(f);
		int[] map = new int[orig.length()];
		StringBuffer sb = new StringBuffer(orig.toLowerCase());
		for(int i = 0, count = 0; i < sb.length(); i++, count++)
		{
			map[i] = count;
			if(legals.indexOf(sb.charAt(i)) < 0)
			{
				sb.deleteCharAt(i);
				i--;
			}
		}
		mappings.put(f, map);
		return sb.toString();
	}

	public static String description()
	{
		return "Checks for cheats by finding identical substrings";
	}

	public PairComparison compare(File f1, File f2)
	{
		PairComparison pr = new PairComparison(f1, f2);
		pr.setScore(Math.random());
		// need to check for identical substrings of at least min length
		String s1 = getContent(f1);
		String s2 = getContent(f2);
		int tol = getTolerance();
		int end1 = s1.length();
		boolean[] score2 = new boolean[s2.length()];

		for(int i = 0; i < end1-tol+1; i++)
		{
//			System.out.println("On iteration: " + i + " for " + f1 + ", " + f2);
			boolean found = false;
			int count = 0;
			String check = s1.substring(i, i+tol);
			count = s2.indexOf(check, count);
			while(count >= 0)
			{
//				System.out.println("Found match at " + count);
				found = true;
				int index1 = indexMap(f1, i);
				int _end1 = indexMap(f1, i+tol);
				int index2 = indexMap(f2, count);
				int _end2 = indexMap(f2, count+tol);
				pr.log(index1, _end1, index2, _end2);
				for(int b = 0; b < tol; b++)
					score2[b+count] = true;
				count = s2.indexOf(check, count+1);
			}

			if(!found)
				continue;

			count = s1.indexOf(check, i+1);
			while(count >= 0)
			{
				int index1 = indexMap(f1, count);
				int _end1 = indexMap(f1, count+tol);
				pr.log(index1, _end1, 0, 0);
				count = s1.indexOf(check, count+1);
			}
		}

		int total = 0;
		for(int i = 0; i < score2.length; i++)
			if(score2[i] == true)
				total++;
		pr.setScore(((double)(total))/score2.length);

		return pr;
	}


	public int indexMap(File f, int in)
	{
		return ((int[]) mappings.get(f))[in];
	}

	/** Compares two documents canonical strings by searching for identical
		substrings of at least length tol. All such occurences are logged
		into one CheatPack. This means that all content marked "cheated" will
		be displayed as cheated all at once.
		@param tol the minimum length of identical substring considered a cheat.
		*/

/* ----------------------------------------------------- */
/* Main method ... used for testing and running module   */
/* ----------------------------------------------------- */
	public static void main(String[] aerfgd)
	{
		DocumentComparison dc = new SubstringComparison(new File("Downloads")).read();
		System.out.println(dc);
		run(dc, 10);
		run(dc, 20);
		run(dc, 40);
		run(dc, 80);
		System.out.println("\nDone!");
	}

	private static void run(DocumentComparison dc, int tol)
	{
		System.out.println("*******************************************************************");
		long start = System.currentTimeMillis();
		dc.setTolerance(tol);
		DocumentComparisonResult dcp = dc.compare();
		System.out.println(dcp);
		System.out.println("[" + tol + "] Comparison took " + (System.currentTimeMillis() - start) + " ms.\n");
	}



}