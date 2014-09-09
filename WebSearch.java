/*

	SPlaT: Search that blocks until certain conditions are met.
	written by Shafik Amin, 6-3-2003
*/

/** Imports **/
import java.util.*;
import java.net.*;
import java.io.*;

public class WebSearch extends BasicWebSearch
{
	/* Empty Constructor, needs to be set */
	public WebSearch()
	{
		super();
	}

	/* Constructor, num: number of files to download */
	public WebSearch(int num, String fullURL) throws MalformedURLException
	{
		super(fullURL);
		setDownloads(num);
	}

	public static String description()
	{
		return "Follows all links looking for desired file-types";
	}

	/* override the default shouldExplore method, to make sure to end the search when needed */
	public /* synchronized */ boolean shouldExplore(String url)
	{
		if (stop)
			return false;

		if (!shouldStop())
			return super.shouldExplore(url);
		else
		{
			//System.out.println("Returning false in shouldExplore...");
			return false;
		}
	}

	/* similar overriding to shouldExplore */
	public /* synchronized */ boolean shouldWriteToDisk(String url)
	{
		if (stop)
			return false;

		if (!shouldStop())
			return super.shouldWriteToDisk(url);
		else
		{
			System.out.println("Returning false in shouldWriteToDisk...");
			return false;
		}
	}

	public /* synchronized */ boolean shouldStop()
	{
		//return (getPreWrittenCount() == getDownloads() &&
		//		getWriteCount() >= getDownloads()) ||
		//		isTimeUp();

		return stop || (getPreWrittenCount() >= getDownloads()) ||
				isTimeUp();
	}

	/** Test Driver **/
	public static void main(String[] args)
	{

		String toTest = "http://www.u.arizona.edu/~gergelyk/splat/spider.html";
		int number = 50;
		int seconds = 6;
		int numloops = 1;
		File dl = new File("Test");
		dl.mkdirs();

		for (int i = 0; i < numloops; i++)
		{
			try
			{
				WebSearch w = new WebSearch();
				w.setWebsite(toTest);
				w.setDownloads(number);
				w.setDepth(4);
				w.setTime(seconds); // # of seconds
				w.setDownloadLocation(dl);
				w.startSearch();

				System.out.println("Search ended!!");
			}

			catch (Exception e)
			{
				System.out.println(e);
			}
		}
	}
}
