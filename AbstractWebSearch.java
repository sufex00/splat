/*
	SPlaT project : Class that uses WebSpiders for searching the web.
	written by Shafik Amin, 5-14-2003
*/

/** Imports **/
import java.net.*;
import java.io.*;
import java.util.*;

public abstract class AbstractWebSearch implements Progressable
{
	/** Instance fields **/
	private File downloadFolder;
	private String startURL;
	private int writtenCount;
	private int preWrittenCount;
	private int visitCount;
	private JSemaphore lockSem;
	private ThreadGroup spiderGroup;
	private String myAuthor;
	private int myDepth;
	private long myTime;
	private long startTime;
	private int numFiles;
	private static final int DEFAULT_DEPTH  = 4;
	private static final String DEFAULT_URL = "http://www.u.arizona.edu/~gergelyk/splat/spider.html";
	private String extensions;
	private ArrayList loggers;
	protected boolean stop;

/* -------------------------------------------- */
/* -------------------------------------------- */
/* -------------------------------------------- */
	private ArrayList progress = new ArrayList();


	public void addProgressListener(ProgressListener pl)
	{
		if(!progress.contains(pl))
			progress.add(pl);
	}

	public void notifyListeners()
	{
		ProgressEvent pe = new ProgressEvent(this, "Downloading Files");
		for(int i = 0; i < progress.size(); i++)
			((ProgressListener)(progress.get(i))).progressPerformed(pe);
	}

	public double getProgress()
	{
		double x1 = ((double)writtenCount)/numFiles;
		double delta = System.currentTimeMillis() - startTime;
		double x2 = delta/myTime;
		return Math.max(x1, x2);
	}


/* -------------------------------------------- */
/* -------------------------------------------- */
/* -------------------------------------------- */

	/** Constructors **/
	public AbstractWebSearch()
	{
		writtenCount = preWrittenCount = visitCount = numFiles = 0;
		downloadFolder = new File("."); // by defult store in current directory
		startURL = DEFAULT_URL;
		spiderGroup = new ThreadGroup("Spiders");
		myDepth = DEFAULT_DEPTH;
		startTime = 0;

		/* get list of file extentions */
		extensions = Config.read("filetype");
		loggers = new ArrayList();
		stop = false;
	}

	public AbstractWebSearch(String fullURL) throws MalformedURLException
	{
		this();
		setWebsite(fullURL);
	}

	/* start the entire search and data retrieval */
	public void startSearch()
	{
		try
		{
			lockSem = new JSemaphore(1, getTime());
			startTime = System.currentTimeMillis();
			WebSpider w = new WebSpider(spiderGroup, startURL, this);
			w.start();
			block();    // block here in hopes of the spider unblocking (or timeout)
			System.out.println("startSearch(): After Search Block!");
		}

		catch (Exception e)
		{
			System.out.println("Problem in startSearch!");
			System.out.println(e);
		}
	}

	/** Incrementers **/
	public synchronized void incrementWrite()
	{
		writtenCount++;
	}

	public synchronized void incrementPreWrite()
	{
		preWrittenCount++;
	}

	public synchronized void incrementVisit()
	{
		visitCount++;
	}

	/** Getters **/
	public synchronized int getWriteCount()
	{
		return writtenCount;
	}

	public synchronized int getPreWrittenCount()
	{
		return preWrittenCount;
	}

	public synchronized int getVisitCount()
	{
		return visitCount;
	}

	/* Semaphore state manipultors */
	public void block()
	{
		System.out.println("WEBSEARCH : Blocking Search!");
		lockSem.P();
	}

	public void unblock()
	{
		System.out.println("WEBSEARCH : Unblocking Search!");
		lockSem.V();
	}

	/* emergency halt */
	public void halt()
	{
		spiderGroup.interrupt();
		stop = true;
		this.unblock();
	}

	/** Setters **/
	public void setAuthor(String s)
	{
		myAuthor = s;
	}

	public void setDepth(int d)
	{
		myDepth = d;
	}

	public void setTime(int sec)
	{
		myTime = (long)(sec * 1000);
	}

	public void setDownloads(int num)
	{
		numFiles = num;
	}

	/** Getters **/
	public String getAuthor()
	{
		return myAuthor;
	}

	public int getDepth()
	{
		return myDepth;
	}

	public int getTime()
	{
		return (int)(myTime/1000);
	}

	public int getDownloads()
	{
		return numFiles;
	}

	/* is the time up? */
	public synchronized boolean isTimeUp()
	{
		if (System.currentTimeMillis() - startTime > myTime)
		{
			System.out.println("Time up, unblocking search...");
			return true;
		}
		return false;

	}

	/* should I keep recursively exploring? */
	public abstract boolean shouldExplore(String fullURL);

	/* should I stop the search? */
	public abstract boolean shouldStop();

	/* whether this particular file should be written to disk directly */
	public boolean shouldWriteToDisk(String fullURL)
	{
		if (shouldStop())
			return false;

		StringTokenizer st = new StringTokenizer(extensions);

		while (st.hasMoreTokens())
		{
			if (fullURL.toLowerCase().endsWith("." + st.nextToken()))
				return true;
		}
		return false;
	}

	/* return the folder name that will be used for storage */
	public File getFolder()
	{
		return downloadFolder;
	}

	public void setDownloadLocation(File f)
	{
		downloadFolder = f;
	}

	/* sets the start website */
	public void setWebsite(String s)
	{
		startURL = s;
	}

	/* returns the start website */
	public String getWebsite()
	{
		return startURL;
	}

	/* add a logger to this search */
	public void addLogger(Logger l)
	{
		loggers.add(l);
	}

	/* logs out to the loggers */
	public synchronized void log(String s)
	{
		for (int i = 0; i < loggers.size(); i++)
			((Logger)(loggers.get(i))).log(s);
	}
}