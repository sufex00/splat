/*
	SPlaT project: class that provides functionality for "crawling" around the web
	looking for specific pages.
	written by Shafik Amin, 5-13-2003
*/


/** Imports **/
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class WebSpider extends Thread
{
	/** Instance fields **/
	private static final String LINK_TAG 		= "<ahref=";
	private static final String LINK_TAG_UPPER  = "<AHREF=";
	private static final int EOF 		 		= getEOF();
	private static final String[] HACK 	 		= new String[]{""};
	private static int spiderID 		 		= 0;
	private static int num = 0;
	private URL myURL;
	private AbstractWebSearch mySearch;
	private BufferedReader urlReader;
	private int myDepth;
	private Object dummy = new Object();


	/* constructs a WebSpider with the String addr as the address */
	public WebSpider(ThreadGroup g, String addr, AbstractWebSearch search) throws MalformedURLException, IOException
	{
		super(g, "" + getNextSpiderID());
		mySearch = search;
		myURL = new URL(addr);
		urlReader = new BufferedReader(new InputStreamReader(myURL.openStream()));
		mySearch.incrementVisit();   // visited a new node
		myDepth = 0;
		//setDaemon(true);
	}

	/* Entry point for the thread */
	public void run()
	{
		num++;
		checkAndSignalEnd();

		/* if the search is over, dont bother */
		if (isInterrupted() || mySearch.shouldStop())
			return;

		/* If the search wants us to write this to disk */
		if (mySearch.shouldWriteToDisk(myURL.toString()))
				writeMeToDisk();

		if (!isInterrupted())
		{
			String[] arr = getAllLinks();
			for (int i = 0; i < arr.length; i++)
			{
				if (!isInterrupted() && isRelativeLink(arr[i]))
				{
					//System.out.println("arr[i] is relative: " + arr[i]);
					arr[i] = makeAbsolute(arr[i]);
				}

				if (!isInterrupted() && mySearch.shouldExplore(arr[i]))
				{
					/* spawn other WebSpiders recursively */
					try
					{
						if (keepSpawning(arr[i]))
						{
							WebSpider w = new WebSpider(this.getThreadGroup(), arr[i], mySearch);
							w.myDepth = this.myDepth + 1;
							w.start();
						}
						else
							return;
					}

					catch (Exception e)
					{
						//System.out.println("ERROR: with spawining other WebSpiders!");
						//System.out.println(e);
					}
				}
			}
		}

		synchronized (dummy)
		{
			if (this.isLastSpider())
				mySearch.unblock();
			mySearch.notifyListeners();
		}

		checkAndSignalEnd();
		num--;
	} /* end of run() */

	private boolean keepSpawning(String site)
	{
		return (myDepth < mySearch.getDepth() &&
		!isInterrupted() && site != null);
	}

	/* reads in the entire page, as is, ADDING \n to each line */
	private String readToEndRaw() throws IOException
	{
		/* read from the top every time */
		if (urlReader != null)
			urlReader =
			new BufferedReader(new InputStreamReader(myURL.openStream()));

		StringBuffer sb = new StringBuffer();
		while (urlReader.ready())
			sb.append(urlReader.readLine() + "\n");
		return sb.toString();
	}

	/* returns a String array of all the links in the page, assumes valid HTML */
	private String[] getAllLinks()
	{
		ArrayList result = new ArrayList();
		try
		{
			String page = readToEndStripped();
			page = page.replaceAll(LINK_TAG_UPPER, LINK_TAG); // for all cases

			StringBuffer sb = new StringBuffer(page),
				url = new StringBuffer();
			int pos = 0, end;

			while ((pos = sb.indexOf(LINK_TAG, pos)) >= 0)
			{
				pos += LINK_TAG.length() + 1;  // + 1 for quote
				url.setLength(0);

				end = sb.indexOf("\"", pos);
				if (end == -1) end = sb.length();

				url.append(sb.substring(pos, end));
				pos = end;

				/* only add the useful strings */
				if (url.length() > 0)
					result.add(url.toString());
			}
		}

		catch (InterruptedIOException ioe)
		{
			log("InterruptedIOException occured, halting...");
			mySearch.halt();
		}

		catch (Exception e) { System.out.println(e); }

		String[] toRet = new String[result.size()];
		for (int i = 0; i < result.size(); i++)
			toRet[i] = (String) (result.get(i));

		return toRet;
	}

	/* reads in the entire page, stripping it from all white space */
	private String readToEndStripped() throws IOException
	{
		StringBuffer sb = new StringBuffer();
		String raw = readToEndRaw();
		StringBuffer source = new StringBuffer(raw);

		for (int i = 0; i < source.length(); i++)
		{
			char c = source.charAt(i);
			if (!isWhiteSpace(c))
				sb.append(c);
		}

		return sb.toString();
	}

	private boolean isRelativeLink(String s)
	{
		URI u = null;
		try { u = new URI(s); }
		catch (URISyntaxException e)
		{
			//System.out.println("----------> BAD URI <----------");
			//System.out.println(e);
		}

		if (u != null && u.isAbsolute())
			return false;
		return true;
	}

	/* attempts to return the resolved String for the relative link */
	private String makeAbsolute(String relative)
	{
		String toRet = null;
		try
		{
			URI abs = new URI(myURL.toString());
			URI rel = new URI(relative);
			toRet = abs.resolve(rel).toString();
		}
		catch (Exception e)
		{
			//System.out.println("In makeAbsolute ---> :" + e);
		}

		return toRet;
	}

	/** Getters **/
	public BufferedReader getReader()
	{
		return urlReader;
	}

	public URL getURL()
	{
		return myURL;
	}

	/*
	   Writes the current page the spider is on to disk,
	   might need adding the option of hold.
	*/
	private void writeMeToDisk()
	{
		try
		{
			mySearch.incrementPreWrite();

			InputStream input = myURL.openStream();

			String localfile = getLocalFileName(myURL.toString());
			String fullname;
			File test;
			do
			{
				fullname = "" + mySearch.getFolder().getAbsolutePath() +
					File.separator + localfile;
				test = new File(fullname);
				localfile = "_" + localfile;

			}
			while(test.exists());
			localfile = localfile.substring(1);

			FileOutputStream output = new FileOutputStream(fullname);
			int c = 0, count = 0;

			while ((c = input.read()) != EOF)
			{

				output.write(c);
				count++;
			}

			input.close();
			output.close();
			mySearch.incrementWrite();

			log("FILE #" + (mySearch.getWriteCount()) +
			" written from location: " + myURL.toString() +
			", filename = " + localfile);
		}

		catch (MalformedURLException e)
		{
			//System.out.println("MalformedURLException occured while reading file from web:");
			//System.out.println(e);
		}

		catch (IOException e)
		{
			//System.out.println("IOException occured while reading file from web:");
			//System.out.println(e);
		}
	}

	/* given a url String, returns what the local file name should be */
	private String getLocalFileName(String addrOfFile)
	{
		int index = addrOfFile.lastIndexOf('/');
		if (index >= 0)
			return addrOfFile.substring(index + 1);
		return addrOfFile;
	}

	private boolean isWhiteSpace(char c)
	{
		return Pattern.matches("\\s", "" + c);
	}

	/* sends an end signal */
	private void checkAndSignalEnd()
	{
		/* if the search is over, dont bother */
	    if ((mySearch.shouldStop()))
		{
			this.getThreadGroup().interrupt();
			//mySearch.unblock();
		}
	}

	/* is *this* the last spider running? */
	private boolean isLastSpider()
	{
		boolean result;
		result = (getThreadCount() <= 1) && !((this.getName()).equals("0"));
		//System.out.println("Thread " + getName() + " called isLastSpider and will return " + result);
		//getThreadGroup().list();

		if (result)
		{
			System.out.println("\nThread " + getName() + " is exiting:");
			getThreadGroup().list();
			System.out.println("--------------");
		}

		return result;
	}

	/* Counts the number of threads in the spider group */
	public int getThreadCount()
	{
		ThreadGroup group = getThreadGroup();
		return group.enumerate(new Thread[group.activeCount() + 10], true);
	}

	/* Thread ID generator */
	public synchronized static int getNextSpiderID()
	{
		int toRet = spiderID;
		spiderID++;
		return toRet;
	}

	/* Finds the end of file */
	public static int getEOF()
	{
		return -1;
	}

	/* for synch. */
	public synchronized static String getTAG()
	{
		return LINK_TAG;
	}

	/* for synch. */
	public synchronized static String getTAGUP()
	{
		return LINK_TAG_UPPER;
	}

	private void log(String s)
	{
		mySearch.log(s);
	}
}
