import javax.swing.*;

/** @author Gergely Kota

ValueMenu is a specialized JMenuItem, it can be queried for the last value
(as a String) that is has set something to.

*/

public class ValueMenuItem extends JMenuItem
{
	private String value;

	/** @param s the name of the ValueMenuItem
		@param initVal the initial value of this ValueMenuItem
		*/
	public ValueMenuItem(String s, String initVal)
	{
		super(s);
		value = initVal;
	}

	/** @return the most recently set value */
	public String value()
	{
		return value;
	}

	/** @param v the value to set the current state to */
	public void setValue(String v)
	{
		value = v;
	}
}
