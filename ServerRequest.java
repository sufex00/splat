/*
	SPlaT project: Object that requests a search from a web server,
	Singleton design : written by Shafik Amin, 5-24-2003
*/

/** Imports **/
import java.net.*;
import java.io.*;
import java.util.*;

public class ServerRequest
{
	private static final String[] serverScripts =
		{
			"http://www.google.com/search",
		};

	/* Static Instance Factory, returns null if not supported */
	public static ServerRequest getServerRequestFrom(String filename)
	{
		try { return new ServerRequest(filename); }
		catch (Exception e) { return null; }
	}

	/** Instance variables **/
	private URL myURL;
	private String filename;
	private Properties props;

	/* constructors, private for instance control */
	private ServerRequest(String filename) throws Exception
	{
		props = new Properties();
		FileInputStream in = new FileInputStream(filename);
		props.load(in);
		myURL = new URL(props.getProperty("URL"));
		props.remove("URL");
		this.filename = filename;
	}

	/*
	   Sends a search request to the server, takes a filename
	   that stores all the request's properties,
	   returns an input stream.
	*/
	public String getRequestResult()
	{
		try
		{
			/* Send request */
			URLConnection connection = myURL.openConnection();
			connection.setDoOutput(true);
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    connection.setUseCaches(false);
		    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			PrintWriter out = new PrintWriter(connection.getOutputStream());
			Enumeration enu = props.keys();

			while (enu.hasMoreElements())
			{
				String key = (String)enu.nextElement();
				String value = props.getProperty(key);
				out.print(key);
				System.out.println(key);
				out.print('=');
				System.out.println('=');
				out.print(URLEncoder.encode(value, "UTF-8"));
				System.out.println(value);
				if (enu.hasMoreElements())
					out.print('&');
				System.out.println("-------");
			}
			out.close();

			/* Read response */
			BufferedReader in = new BufferedReader(new
								InputStreamReader(connection.getInputStream()));
			String line;
			StringBuffer ret = new StringBuffer();

			while ((line = in.readLine()) != null)
				ret.append(line + "\n");

			in.close();
			return ret.toString();
		}

		catch (Exception e)
		{
			System.out.println("REQUEST FAILED: " + "\n" + e);
		}
		return null;
	}

	/** Test **/
	public static void main(String[] args)
	{
		ServerRequest s = ServerRequest.getServerRequestFrom("request.txt");
		System.out.println(s.getRequestResult());
	}
}
