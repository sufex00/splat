/*
	Smarter Search for the WebSpider, for SPLaT.
	Written by Shafik Amin, 10-06-2003
*/

/** Imports **/
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class SmartWebSearch extends DomainWebSearch
{

	private static String[] stopWords = getStopWordArray();

	/** Constructors **/
	public SmartWebSearch()
	{
		super();
	}

	/* override the "WebSearch" behavior */
	public boolean shouldWriteToDisk(String url)
	{
		if (!super.shouldWriteToDisk(url)) return false;
		return isGoodURL(url);
	}

	/* override the "WebSearch" behavior */
	public boolean shouldExplore(String url)
	{
		if (!super.shouldExplore(url)) return false;
		return isGoodURL(url);
	}

	/* determines if the url passes all the "stop words" */
	private boolean isGoodURL(String url)
	{
		for (int i = 0; i < stopWords.length; i++)
		{
			String pat = ".*" + stopWords[i] +".*";
			if (Pattern.matches(pat, url))
				return false;
		}

		return true;
	}

	/* description tag */
	public static String description()
	{
		return "Performs additional \"smart\" filtering on links";
	}

	public static String[] getStopWordArray()
	{
		return new String[]
		{
			"lecture", "final", "resume", "exam", "midterm",
			"homework", "techrap","tr-?[0-9][0-9]+",
			"thesis", "handout", "slides", "talk",
			"course", "classes", "presentation", "syllabus"
		};
	}

	/** Test Driver **/
	public static void main(String[] args)
	{

		String toTest = "http://www.u.arizona.edu/~gergelyk/splat/spider.html";
		int number = 2;
		int seconds = 1200;
		File dl = new File("Test");
		dl.mkdirs();

		try
		{
			WebSearch w = new SmartWebSearch();
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

/*

Here are the stop-words I use.

function stopWords(S) {
   S = tolower(S)
   if (S ~ /lecture/) return 1
   if (S ~ /final/) return 1
   if (S ~ /resume/) return 1
   if (S ~ /exam/) return 1
   if (S ~ /midterm/) return 1
   if (S ~ /homework/) return 1
   if (S ~ /techrep/) return 1
   if (S ~ /tr-?[0-9][0-9]+/) return 1
   if (S ~ /thesis/) return 1
   if (S ~ /handout/) return 1
   if (S ~ /slides/) return 1
   if (S ~ /talk/) return 1
   if (S ~ /course/) return 1
   if (S ~ /classes/) return 1
   if (S ~ /presentation/) return 1
   if (S ~ /syllabus/) return 1
   return 0
}

/Papers from/{
   page = page $0
   page = page "<a href=\"file://" FILENAME "\">" FILENAME "</a>"
   next
}
/<body>/{FoundOne=0; page=""; next; }
/<html>/{next}
/<\/body>/{if (FoundOne) print page}
/<\/html>/{next}
/against/ {
   percent = $1
   sub("%:","",percent)
   percent += 0
   if ((percent < 20) || (percent > 95)) next

/CC

*/

