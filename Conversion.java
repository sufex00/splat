import java.awt.*;

/** @author Gergely Kota

	Conversion class contains some static methods to convert between some
	classes and their string descriptions.

*/

public class Conversion
{
	/** @param c the Color to find the representation of
		@return the String representation of the argument Color
		Convert a Color to an RGB String representation in hex.
		Invalids return "000000"
		*/
	public static String hexString(Color c)
	{
		if(c == null)
			return "000000";
		return hexString(c.getRed()) + hexString(c.getGreen()) + hexString(c.getBlue());
	}

	/** @param n the int to convert to hex in String format
		@return the String representation of the argument int
		Convert an int to a String in hex, invalids return "00"
		*/
	public static String hexString(int n)
	{
		if(n < 0 || n > 255)
			return "00";
		return "" + hexConvert(n/16) + hexConvert(n%16);
	}

	/** @param n the int to convert to a hexadecimal character
		@return the char representation in hex of the argument int
		Convert an int to a hex char. Invalids return 0
		*/
	public static char hexConvert(int n)
	{
		if(n < 0 || n > 15)
			return (char) 0;
		if(n < 10)
			return (char) ('0' + n);
		return (char) ('A' + (n-10));
	}

	/** @param s the String to generate a Color from
		@return the Color represented in RGB by the argument String
		Create a Color from a String representing its RGB value.
		Invalids return null
		*/
	public static Color colorConvert(String s)
	{
		try
		{
			int r = Integer.parseInt(s.substring(0,2), 16);
			int g = Integer.parseInt(s.substring(2,4), 16);
			int b = Integer.parseInt(s.substring(4,6), 16);
			return new Color(r, g, b);
		}
		catch(Exception e) {return null;}
	}


	/** @param s the String to generate an Object from
		@param c the Class type to return
		@return the Object represented by the String
		*/
	public static Object convert(Class c, String s)
	{
		// switch case for converting to the correct
		// class type of the given String
		// add as needed, desired, whatever
		try
		{
			if(c == int.class)
				return new Integer(s);
			if(c == char.class)
				return new Character(s.charAt(0));
			if(c == double.class)
				return new Double(s);
			if(c == float.class)
				return new Float(s);
			if(c == long.class)
				return new Long(s);
			if(c == boolean.class)
				return new Boolean(s);
			if(c == byte.class)
				return new Byte(s);
			return s;
		}
		catch(Exception e) {return null;}
	}



}