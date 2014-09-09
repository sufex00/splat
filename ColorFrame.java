import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** @author Gergely Kota

ColorFrame is a popup menu that prompts for a color to select.
It shows a JColorChooser in a modal JDialog, and senses when
a color has been selected. At this point, it minimizes the window
thereby allowing the program flow to continue. The selected color
is then returned.

*/

public class ColorFrame extends JDialog
{
	private JColorChooser jcc;
	private Color color;
	private static ColorFrame instance;
	private boolean cancel;

	// initializer creates the single instance the first time
	// the class is accessed
	static
	{
		instance = new ColorFrame();
	}

	// force single instance of ColorFrame via static initializer
	// this is because the same task is done each time - the window
	// pops up and returns a selected color.
	private ColorFrame()
	{
		// setSize(400, 300);
		setTitle("Color Selection");
		setModal(true);
		setLocation(300, 300);
		setResizable(false);
		getContentPane().add(jcc = new JColorChooser(), BorderLayout.CENTER);

		JButton jb = new JButton("Select Color");
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				color = jcc.getColor();
			}
		});
		JButton jbc = new JButton("Cancel");
		jbc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cancel = true;
			}
		});

		JPanel bottom = new JPanel(new GridLayout(1,2));
		bottom.add(jb);
		bottom.add(jbc);
		getContentPane().add(bottom, BorderLayout.SOUTH);
		pack();
	}

	/** pauses the flow of the program until a Color is selected.
		It then returns that Color and allows the calling program to continue.
		@return the selected Color
		*/
	public static synchronized Color getColor()
	{
		// set the color to return to null
		// start a timer that checks if the color to return has been set
		// when it has, the window is minimized so that the control can continue
		instance.color = null;
		instance.cancel = false;
		Timer check = new Timer(2, null);
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(instance.color != null)
					instance.hide();
				if(instance.cancel)
					instance.hide();
			}
		});
		check.start();
		instance.show();
		check.stop();
		return instance.color;
	}

/* ------------------------------------------------------- */
/* ------------------------------------------------------- */
/* ------------------------------------------------------- */
	public static void main(String[] args)
	{
		Debug.println("Started");
		Color c = ColorFrame.getColor();
		Debug.println("Ended");
		Debug.println(c);

		Debug.println("Started");
		c = ColorFrame.getColor();
		Debug.println("Ended");
		Debug.println(c);
	}


}
