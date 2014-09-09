import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.reflect.*;

public class ComponentedConfigWindow extends JFrame
{
	private static ArrayList components = new ArrayList();
	private static ComponentedConfigWindow instance = new ComponentedConfigWindow();
	private static boolean added = false, updated = true;

	static
	{
		addComponent(instance.new OptionsList(), "Global Options");
	}


	public static void addComponent(Component c, String title)
	{
		JPanel temp = new JPanel(new BorderLayout());
		JPanel buffer = new JPanel(new GridLayout(3,1));
		buffer.add(new JLabel());
		JPanel squeeze = new JPanel(new BorderLayout());
		buffer.add(squeeze);
		buffer.add(new JLabel());
		squeeze.add(new JLabel(title), BorderLayout.WEST);
		temp.add(buffer, BorderLayout.NORTH);
		temp.add(c, BorderLayout.CENTER);
		components.add(temp);
		updated = true;
	}


	public ComponentedConfigWindow()
	{
		super("Config file editor");
		setSize(750,600);
		setLocation(200,200);
		JScrollPane jsp = new JScrollPane();
		getContentPane().add(jsp);
		added = true;
		// make some structure to hold all the pieces

		JPanel outer = new JPanel(new BorderLayout());
		jsp.getViewport().add(outer);
		for(int i = 0; i < components.size(); i++)
		{
			outer.add((Component)components.get(i), BorderLayout.NORTH);
			outer.add(GUtil.filler(10, Color.black), BorderLayout.CENTER);
			JPanel temp = new JPanel(new BorderLayout());
			outer.add(temp, BorderLayout.SOUTH);
			outer = temp;
		}
	}

	public static void showWindow()
	{
		if(updated)
			instance = new ComponentedConfigWindow();
		updated = false;
		instance.show();
	}


	public static void main(String[] args)
	{
		showWindow();
	}



/* --------------------------------------------------------- */
/* --------------------------------------------------------- */
/* --------------------------------------------------------- */

	private class OptionsList extends JPanel
	{
		public final int CLASS = 0;
		public final int BOOLEAN = 1;
		public final int STRING = 2;
		public final int INT = 3;

		public OptionsList()
		{
			String[] keys = Config.getKeys();
			Arrays.sort(keys);
			setLayout(new GridLayout(keys.length, 1));

			OptionChunk[] chunks = new OptionChunk[keys.length];
			for(int i = 0; i < keys.length; i++)
			{
				String desc = Config.readDescription(keys[i]);
				int index = desc.indexOf(":");
				if(index >= 0)
					desc = desc.substring(0, index);
				chunks[i] = create(keys[i], desc);
			}

			Arrays.sort(chunks);
			for(int i = 0; i < chunks.length; i++)
				add(chunks[i]);

		}

		private OptionChunk create(String key, String type)
		{
			if("boolean".equals(type))
				return new BooleanChunk(key);
			if(type.indexOf("int") == 0)
				return new IntChunk(key);
			if(type.indexOf("Class") == 0)
				return new ClassChunk(key);
			return new StringChunk(key);
		}
	}


	private abstract class OptionChunk extends JPanel implements ActionListener, Comparable
	{
		String key;
		public OptionChunk(String key)
		{
			this.key = key;
			setLayout(new BorderLayout());
			JLabel k = new JLabel(key + ":");
			k.setPreferredSize(new Dimension(100, 0));

			String desc = Config.readDescription(key);
			int index = desc.indexOf(":");
			if(index >= 0)
				desc = desc.substring(index+1).trim();

			JLabel d = new JLabel(desc);
			d.setPreferredSize(new Dimension(350,0));
			JPanel spacer = new JPanel(new BorderLayout());
			spacer.add(d, BorderLayout.CENTER);
			spacer.add(GUtil.filler(10), BorderLayout.WEST);

			add(k, BorderLayout.WEST);
			add(spacer, BorderLayout.EAST);
			Config.addActionListener(this);
		}

		public abstract void actionPerformed(ActionEvent ae);

		public int compareTo(Object o)
		{
			// hack based on the class names ... lucky ordering :)
			if(getClass() == o.getClass())
				return key.compareTo(((OptionChunk)o).key);
			else
				return getClass().getName().compareTo(o.getClass().getName());
		}
	}

	private class IntChunk extends OptionChunk
	{
		private String key;
		private JSlider js;
		private JLabel val;

		public IntChunk(String k)
		{
			super(k);
			key = k;

			// get range and value;
			int value = 0;
			try
			{
				value = Integer.parseInt(Config.read(key));
			}
			catch(Exception e) {}
			String s = Config.readDescription(key);
			int min = 0, max = 100;
			try
			{
				s = s.substring(0, s.indexOf(":"));
				String a = s.substring(s.indexOf("[")+1, s.indexOf(","));
				String b = s.substring(s.indexOf(",")+1, s.indexOf("]"));
				min = Integer.parseInt(a.trim());
				max = Integer.parseInt(b.trim());
			}
			catch(Exception e) {}


			js = new JSlider(min, max, value);
			js.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent ce) {
					val.setText("" + js.getValue());
					Config.write(key, "" + js.getValue());
				}
			});
			val = new JLabel("" + value);
			JPanel buf = new JPanel(new BorderLayout());
			buf.add(val, BorderLayout.WEST);
			buf.add(js, BorderLayout.CENTER);
			add(buf, BorderLayout.CENTER);
		}

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals(key))
			try
			{
				js.setValue(Integer.parseInt(Config.read(key)));
			}
			catch(Exception e) {}
		}

	}

	private class BooleanChunk extends OptionChunk
	{
		private String key;
		private JCheckBox jcb;
		public BooleanChunk(String k)
		{
			super(k);
			key = k;
			jcb = new JCheckBox();
			if("true".equals(Config.read(key)))
				jcb.setSelected(true);
			else
				jcb.setSelected(false);
			add(jcb, BorderLayout.CENTER);
			jcb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					String s = "false";
					if(jcb.isSelected())
						s = "true";
					Config.write(key, s);
				}
			});
		}

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals(key))
				if("true".equals(Config.read(key)))
					jcb.setSelected(true);
				else
					jcb.setSelected(false);
		}

	}

	private class StringChunk extends OptionChunk
	{
		private String key;
		private JTextField jt;
		private JButton jb;
		private String content;

		public StringChunk(String k)
		{
			super(k);
			key = k;
			jt = new JTextField(content = Config.read(key));
			jt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					jb.doClick();
				}
			});
			jt.addCaretListener(new CaretListener() {
				public void caretUpdate(CaretEvent ce) {
					if(!content.equals(jt.getText()))
						jb.setEnabled(true);
					else
						jb.setEnabled(false);
				}
			});
			jb = new JButton("Change");
			jb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Config.write(key, content = jt.getText());
					jb.setEnabled(false);
				}
			});
			jb.setEnabled(false);
			JPanel buffer = new JPanel(new BorderLayout());
			buffer.add(jt, BorderLayout.CENTER);
			buffer.add(jb, BorderLayout.EAST);
			add(buffer, BorderLayout.CENTER);
		}

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals(key))
				jt.setText(Config.read(key));
		}

	}

	private class ClassChunk extends OptionChunk
	{
		private String key;
		private JComboBox jcb;
		public ClassChunk(String k)
		{
			super(k);
			key = k;
			jcb = new JComboBox();
			String desc = Config.readDescription(key);
			Class c = Object.class;
			try
			{
				String s = desc.substring(desc.indexOf("[")+1, desc.indexOf("]")).trim();
				c = Class.forName(s);
			}
			catch(Exception e) {}
			// load up the combo box
			Class[] c1 = Util.getLocalClasses(c);
			Class[] c2 = Util.getJarClasses(c);
			Class[] classes = Util.merge(c1, c2);
			int index = 0;
			int offset = 0;
			for(int i = 0; i < classes.length; i++)
				if(!Modifier.isAbstract(classes[i].getModifiers()))
				{
					String s = classes[i].getName();
					jcb.addItem(s);
					if(s.equals(Config.read(key)))
						index = i;
				}
				else
					offset--;
			jcb.setSelectedIndex(index + offset);
			add(jcb, BorderLayout.CENTER);

			jcb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Object o = jcb.getSelectedItem();
					Config.write(key, (String) o);
				}
			});
		}

		public void actionPerformed(ActionEvent ae)
		{

		}
	}
}



