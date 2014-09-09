import javax.swing.*;
import java.awt.event.*;

/** @author Gergwly Kota

SetMenu lumps and a ValueMenuItem another JMenuItem "set as default" into a JMenu. The
"set as default" JMenuItem writes the ValueMenuItem's current value to the config file.

*/

public class SetMenu extends JMenu
{
	/** @param title the name of the JMenu
		@param item the ValueMenuItem to lump
		@param key the key value to use when writing to the config file
		*/
	public SetMenu(String title, final ValueMenuItem item, final String key)
	{
		super(title);
		add(item);
		JMenuItem jmi = new JMenuItem("Set as Default");
		jmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// add ConfigWriter here ...
				Config.write(key, item.value());
			}
		});
		add(jmi);
	}
}
