import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/** @author Gergely Kota

SelectiveInfo returns the read value or null if the field is disabled

*/

public class SelectiveInfo extends JPanel
{
	private JCheckBox jcb;
	private JTextField jtf;
	private JLabel label;
	private String id;
	private boolean selectable;

	public SelectiveInfo(String s)
	{
		this(s, true);
	}

	public SelectiveInfo(String s, boolean x)
	{
		this(s, x, 150); // because I like numbers.
	}

	public SelectiveInfo(String s, boolean x, int textWidth)
	{
		this(s, x, textWidth, false);
	}

	public SelectiveInfo(String s, boolean x, int textWidth, boolean balanced)
	{
		id = s.trim() + ":";
		selectable = x;
		jcb = new JCheckBox(id, true);
		JLabel jl = new JLabel(id);
		label = new JLabel();
		jcb.setPreferredSize(new Dimension(textWidth, 0));
		jl.setPreferredSize(new Dimension(textWidth, 0));
		jtf = new JTextField(15);
		setLayout(new BorderLayout());
		JPanel stuffing = new JPanel(new BorderLayout());
		stuffing.add(GUtil.filler(10), BorderLayout.WEST);
		stuffing.add(label, BorderLayout.CENTER);
		if(balanced)
		{
			jtf = new JTextField();
			JPanel leftside = new JPanel(new GridLayout(1,2));
			if(selectable)
				leftside.add(jcb);
			else
				leftside.add(jl);
			leftside.add(jtf);
			add(leftside, BorderLayout.WEST);
			add(stuffing, BorderLayout.CENTER);

		}
		else
		{
			add(jtf, BorderLayout.CENTER);
			if(selectable)
				add(jcb, BorderLayout.WEST);
			else
				add(jl, BorderLayout.WEST);
			add(stuffing, BorderLayout.EAST);
		}
		jcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				onClick();
			}
		});
	}

	public void clear()
	{
		jtf.setText("");
	}

	public void setDescription(String s)
	{
		label.setText(s);
	}

	public void set(String s)
	{
		jtf.setText(s);
	}

	private void onClick()
	{
		if(selectable)
			jtf.setEnabled(jcb.isSelected());
		else
			jcb.setSelected(true);
	}

	public String read()
	{
		if(jtf.isEnabled())
			return jtf.getText().trim();
		return null;
	}

	public void setEnabled(boolean x)
	{
		jcb.setSelected(x);
		onClick();
	}

	public static void main(String[] arhs)
	{
		JFrame jf = new JFrame();
		jf.getContentPane().add(new SelectiveInfo("Author"));

		jf.pack();
		jf.show();

	}

}
