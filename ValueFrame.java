import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/** @author Gergely Kota

ValueFrame is a popup menu that prompts for a value to select.
It shows a JTextField in a modal JDialog, and senses when
a value has been selected. At this point, it minimizes the window
thereby allowing the program flow to continue. The selected value
is then returned.

*/


public class ValueFrame extends JDialog
{
	private ValueChooser ic;
	private String val;
	private boolean cancel;
	/** return value when cancel is selected */
	public static final int ILLEGAL = Integer.MIN_VALUE;
	private static final int UNSET = Integer.MAX_VALUE;
	private static ValueFrame instance;

	// initializer creates the single instance the first time
	// the class is accessed
	static
	{
		instance = new ValueFrame();
	}

	// force single instance of IntFrame via static initializer
	// this is because the same task is done each time - the window
	// pops up and returns a selected color.
	private ValueFrame()
	{
		// setSize(400, 300);
		setModal(true);
		setLocation(300, 300);
		setResizable(false);
		getContentPane().add(ic = new ValueChooser(), BorderLayout.CENTER);


		JPanel jp = new JPanel(new GridLayout(1,2));
		JButton jb1 = new JButton("Select");
		jb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				val = ic.get();
			}
		});
		jp.add(jb1);

		JButton jb2 = new JButton("Cancel");
		jb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cancel = true;
			}
		});
		jp.add(jb2);

		getContentPane().add(jp, BorderLayout.SOUTH);
		pack();
	}


	public static synchronized String getString(String title)
	{
		instance.setTitle(title);
		instance.val = null;
		instance.cancel = false;
		Timer check = new Timer(2, null);
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(instance.val != null)
					instance.hide();
				if(instance.cancel)
					instance.hide();
			}
		});
		check.start();
		instance.show();
		check.stop();
		return instance.val;
	}

	/** pauses the flow of the program until a String is entered.
		It then returns that String and allows the calling program to continue.
		@return the entered String
		*/
	public static synchronized String getString()
	{
		return getString("Enter a String");
	}

	/** pauses the flow of the program until an int is entered.
		It then returns that int and allows the calling program to continue.
		@return the entered int
		*/
	public static synchronized int getInt(String title)
	{
		try
		{
			String s = getString(title);
			if(s == null)
				return ILLEGAL;
			return Integer.parseInt(s);
		}
		catch(Exception e) {return getInt();}
	}

	public static synchronized int getInt()
	{
		return getInt("Enter an Int");
	}

	/** pauses the flow of the program until a double is entered.
		It then returns that double and allows the calling program to continue.
		@return the entered double
		*/
	public static synchronized double getDouble(String title)
	{
		try
		{
			String s = getString(title);
			if(s == null)
				return (double) ILLEGAL;
			return Double.parseDouble(s);
		}
		catch(Exception e) {return getDouble();}
	}

	public static synchronized double getDouble()
	{
		return getDouble("Enter a Double");
	}

	// customized JPanel with a JTextField and reader methods.
	private class ValueChooser extends JPanel
	{
		private JTextField jtf;
		public ValueChooser()
		{
			setPreferredSize(new Dimension(150,30));
			setLayout(new BorderLayout());
			add(jtf = new JTextField());
		}

		public String get()
		{
			try
			{
				val = jtf.getText();
				jtf.setText("");
				return val;
			}
			catch(Exception e) {return null;}
		}
	}

/* -------------------------------------------------- */
/* -------------------------------------------------- */
/* -------------------------------------------------- */
	public static void main(String[] args)
	{
		System.out.println("Started");
		double i = ValueFrame.getDouble();
		System.out.println("Ended");
		System.out.println(i);

		try
		{
			Thread.sleep(2000);
		}
		catch(Exception e) {}

		System.out.println("Started");
		i = ValueFrame.getDouble();
		System.out.println("Ended");
		System.out.println(i == ILLEGAL);
		System.out.println(i);
	}


}
