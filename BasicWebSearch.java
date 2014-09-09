/*
	SPlaT : basic web search, limitation by number?
	written by Shafik Amin 5-14-03
*/

/** Imports **/
import java.util.*;
import java.net.*;

public abstract class BasicWebSearch extends AbstractWebSearch
{
	/** Instance variables **/
	private Hashtable visitTable;

	/** Constructors **/
	public BasicWebSearch()
	{
		super();
		visitTable = new Hashtable();
	}

	public BasicWebSearch(String fullURL) throws MalformedURLException
	{
		this();
		setWebsite(fullURL);
	}

	public boolean shouldExplore(String url)
	{
		if (url == null)
			return false;
		if (hasVisited(url))
			return false;
		visitTable.put(url, url);
		return true;
	}

	private boolean hasVisited(String url)
	{
		return visitTable.containsKey(url);
	}
}