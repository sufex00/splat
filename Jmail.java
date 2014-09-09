/*
	Jmail : provides email functionality: static use.
	written by Shafik Amin, 9-24-2003
*/

import java.net.*;
import java.util.*;
import java.io.*;

public class Jmail
{
	private static Socket s;
	private static PrintWriter out;
	private static String hostName;
	private static BufferedReader in;

	/* command line use */
	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.out.println("\nJmail v1.0, by Shafik Amin");
			System.out.println("USAGE : java Jmail <from> <to> <subject>");
			System.out.println("\n(The body of the message is read from standard in)");
		}

		else
		{
			try
			{
				int byteCount = System.in.available();
				byte[] buffer = new byte[byteCount];
				System.in.read(buffer, 0, byteCount);
				String body = new String(buffer);
				System.out.println(body);
				send(args[0], args[1], args[2], body);
			}
			catch(Exception e)
			{
				System.out.println("Error!\n " + e);
			}
		}
	}

	/* convenience version of send */
	public static void send(String from, String to, String subject, String body)
	{
		Vector v = new Vector();
		v.add(body);
		send(from, to, subject, v);
	}

	/** Allows you to send an email anywhere from the program
		@param fromWho the string that contains the emails "from who" field
		@param toWho the string that the address of the destination
		@param the databuffer to send
		**/
	public static void send(String fromWho,
							String toWho,
							String subject,
							Vector dataBuffer)
	{
		try
		{
			s = new Socket("smtp.west.cox.net",25); //SMTP
			hostName = InetAddress.getLocalHost().getHostName();
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader (s.getInputStream()));
		}

		catch (Exception e)
		{
			System.out.println(e + " Error getting your HostName! or the socket");
		}

		try
		{
			if (s == null)
				s = new Socket("smtpgate.email.arizona.edu",25); //SMTP
		}

		catch (Exception e)
		{
			System.out.println(e + " Error creating the socket");
		}

		String theMessage = "\n" + makeStringFromBuffer(dataBuffer);
		theMessage += "\r\n.";
		try
		{
			out.println("HELO " + hostName );
			out.flush();
			out.println("MAIL FROM: " + "<" + fromWho + ">");
			out.flush();
			out.println("RCPT TO: " + "<" + toWho + ">" );
			out.flush();
			out.println("DATA");
			out.flush();
			out.println("From: " + "< " + fromWho + " >");
			out.flush();
			out.println("To: " + "< " + toWho +" >");
			out.flush();
			out.println("Subject: " + subject);
			out.flush();
			//System.out.println("Sending messeage:" + theMessage);
			out.println(theMessage);
			out.flush();
			out.println("QUIT");
			out.flush();

			/* read the response off the server */
			String l;
			while ((l = in.readLine()) != null)
				System.out.println(l);
		}

		catch(Exception e)
		{
			System.out.println("Error on BufferedReader:" + e);
		}

		try {s.close();}

		catch(Exception e)
		{
			System.out.println(e + "socket closing error!" + e);
		}

	}

	/* convert a buffer to a string */
	private static String makeStringFromBuffer(Vector buffer)
	{
		String ret = "";
		for (int i = 0; i < buffer.size() ; i++)
		{
			ret += (buffer.get(i)).toString();
		}
		return ret;
	}
}