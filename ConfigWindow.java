import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;



public class ConfigWindow extends JFrame implements ActionListener
{
	private final String FILE = "data/options.cfg";
	private final String ADD = "Create";
	private final String REPLACE = "Replace";
	private JScrollPane jsp;
	private ConfigList list;
	private JTextField key, value;
	private JButton jb;
	private int lastIndex = 0;
	
	private static final ConfigWindow instance = new ConfigWindow();
	
	public ConfigWindow()
	{
		setSize(400,400);
		setLocation(200,200);
		setTitle("Config file editor");
		Config.addActionListener(this);
		getContentPane().setLayout(new BorderLayout());
		
		setJMenuBar(new JMenuBar());
		JMenu options = new JMenu("Options");
		JMenuItem sort = new JMenuItem("Alphabetical Sort");
		JMenuItem def = new JMenuItem("Actual File");
		JMenuItem reload = new JMenuItem("Reload File");
		sort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				list.sort();
			}
		});
		def.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				list.unsort();
			}
		});
		reload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				list.reload();
			}
		});
		options.add(def);
		options.add(sort);
		options.add(reload);
		getJMenuBar().add(options);
		
		JPanel inputs = new JPanel(new BorderLayout());
		jb = makeButton();
		inputs.add(jb, BorderLayout.EAST);
		
		JPanel labels = new JPanel(new GridLayout(2,1));
		labels.add(new JLabel("Key:"));
		labels.add(new JLabel("Value:"));
		JPanel buffer = new JPanel(new BorderLayout());
		buffer.add(GUtil.filler(10), BorderLayout.WEST);
		buffer.add(GUtil.filler(10), BorderLayout.EAST);
		buffer.add(labels, BorderLayout.CENTER);
		inputs.add(buffer, BorderLayout.WEST);
		
		JPanel fields = new JPanel(new GridLayout(2,1));
		key = new JTextField();
		value = new JTextField();
		ActionListener ae = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				jb.doClick();
			}
		};
		key.addActionListener(ae);
		value.addActionListener(ae);
		key.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				list.goTo(key.getText());
			}
		});
		fields.add(key);
		fields.add(value);
		inputs.add(fields, BorderLayout.CENTER);
		getContentPane().add(inputs, BorderLayout.SOUTH);
		
		list = new ConfigList();
		jsp = new JScrollPane();// do stuff
		jsp.getViewport().add(list);
		getContentPane().add(jsp, BorderLayout.CENTER);
	}
	
	public static void showWindow()
	{
		instance.show();
	}
	
	private JButton makeButton()
	{
		JButton temp = new JButton(ADD);
		temp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				lastIndex = list.getSelectedIndex();
				if(Config.write(key.getText(), value.getText()))
					clear();
				list.setSelectedIndex(lastIndex);
			}
		});
		return temp;
	}

	private void clear()
	{
		key.setText("");
		value.setText("");
		jb.setText(ADD);
	}

	private void set(String s)
	{
		StringTokenizer st = new StringTokenizer(s);
		String temp = st.nextToken();
		key.setText(temp);
		StringBuffer sb = new StringBuffer();
		while(st.hasMoreTokens())
			sb.append(st.nextToken()).append(' ');
		value.setText(sb.toString().trim());
		jb.setText(REPLACE);		
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		list.reload();
	}
	
/* --------------------------------------------------------- */
/* --------------------------------------------------------- */

	private class ConfigList extends JList
	{
		Object[] data;
		private boolean sorted;
		
		public ConfigList()
		{
			sorted = false;
			addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent l) {
					String s = (String) getSelectedValue();
					if(s == null)
						return;
					s = s.trim();
					if(s.length() == 0 || s.indexOf("//") == 0)
						clear();
					else
						set(s);
				}
			});
			reload();
		}
		
		public void sort()
		{
			ArrayList temp = new ArrayList();
			for(int i = 0; i < data.length; i++)
			{
				String s = ((String) data[i]).trim();
				if(s.indexOf("//") != 0 && s.trim().length() > 0)
					temp.add(s);
			}
			Collections.sort(temp);
			setListData(temp.toArray());
			sorted = true;	
		}
		
		public void goTo(String s)
		{
			String out = Config.read(s);
			if(out != null)
			{
				value.setText(out);
				jb.setText(REPLACE);
			}
			else
				jb.setText(ADD);
		}
		
		public void unsort()
		{
			setListData(data);
			sorted = false;
		}
			
		public void reload()
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(FILE));
				ArrayList temp = new ArrayList();
				while(br.ready())
					temp.add(br.readLine());
				setListData(data = temp.toArray());
			}
			catch(Exception e) {}
			if(sorted)
				sort();
		}
	}	
/* --------------------------------------------------------- */
/* --------------------------------------------------------- */

	public static void main(String[] args)
	{
		new ConfigWindow().show();
	
	}


}
