/*
	Domain Search for the WebSpider, for SPLaT.
	Written by Shafik Amin, 10-06-2003
*/

/** Imports **/
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class DomainWebSearch extends WebSearch
{
	/** Instance fields **/
	private String myDomain;  // eg http://www.arizona.edu

	/** Constructors **/
	public DomainWebSearch()
	{
		super();
	}

	/* overridden */
	public void setWebsite(String url)
	{
		super.setWebsite(url);
		setDomainName();
	}

	/* override the "WebSearch" behavior */
	public boolean shouldWriteToDisk(String url)
	{
		if (!super.shouldWriteToDisk(url)) return false;

		/* otherwise, filter potential write */
		return url.indexOf(myDomain) >= 0;
	}

	/* override the "WebSearch" behavior */
	public boolean shouldExplore(String url)
	{
		if (!super.shouldExplore(url)) return false;

		/* otherwise, filter potential explores */
		return url.indexOf(myDomain) >= 0;
	}

	/* sets the domain name to *this*'s domain name. */
	private void setDomainName()
	{
		String site = getWebsite();
		Pattern p = Pattern.compile("/");
		myDomain = p.split(site)[2];
		myDomain = "http://" + myDomain;
	}

	/* description tag */
	public static String description()
	{
		return "Limits search to within the same domain name";
	}

	/** Test Driver **/
	public static void main(String[] args)
	{

		String toTest = "http://www.cs.arizona.edu/people/collberg";
		int number = 100;
		int seconds = 3600;
		File dl = new File("Test");
		dl.mkdirs();

		try
		{
			DomainWebSearch w = new DomainWebSearch();
			w.setWebsite(toTest);
			w.setDownloads(number);
			System.out.println(w.myDomain + " is the domain: ");

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