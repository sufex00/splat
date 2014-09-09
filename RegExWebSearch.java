/*
	Regular Expression Search for the WebSpider, for SPLaT.
	Written by Shafik Amin, 10-06-2003
*/

/** Imports **/
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class RegExWebSearch extends WebSearch
{
	private String regEx;

	/* description tag */
	public static String description()
	{
		return "Performs regular expression filtering on links";
	}

	/* Constructors */
	public RegExWebSearch()
	{
		super();
		regEx = ".*";
	}

	/* sets the internal regular expression */
	public void setRegEx(String s)
	{
		regEx = s;
	}

	/* override the "WebSearch" behavior */
	public boolean shouldWriteToDisk(String url)
	{
		if (stop)
			return false;

		if (!super.shouldWriteToDisk(url)) return false;

		/* otherwise, filter potential write */
		return Pattern.matches(regEx, url);
	}

	/* override the "WebSearch" behavior */
	public boolean shouldExplore(String url)
	{
		if (stop)
			return false;

		if (!super.shouldExplore(url)) return false;

		/* otherwise, filter potential explores */
		return Pattern.matches(regEx, url);
	}
}


