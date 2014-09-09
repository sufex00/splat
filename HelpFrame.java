import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

/** @author Gergely Kota

HelpFrame handles all the help files for SPLAT.

*/

public class HelpFrame extends JFrame implements ActionListener
{
	private File[] files;
	private String[] endings;
	private HashMap contents, filemap;
//	private HTMLPanel window;
	private JEditorPane window;
	private JList topics;
	private JScrollPane jsp;
	private static final Stack stack = new Stack();
	private final int offset = 24;

	public HelpFrame()
	{
		jsp = new JScrollPane();
//		window = new HTMLPanel();
		window = new JEditorPane();
		window.setContentType("text/html");
		window.setEditable(false);
		JScrollPane winjsp = new JScrollPane();
		winjsp.getViewport().add(window);
		actionPerformed(null);
		Config.addActionListener(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(jsp, BorderLayout.WEST);
		getContentPane().add(winjsp, BorderLayout.CENTER);
		setSize(600,400);
		int x = offset*stack.size();
		setLocation(400+x, 200+x);
		setTitle("Splat Help");
		stack.add(this);

		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);

		JMenu file = new JMenu("File");
		JMenuItem newF = new JMenuItem("New Window");
		JMenuItem reload = new JMenuItem("Refresh Info");
		file.add(newF);
		file.add(reload);
		jmb.add(file);

		newF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				newFrame();
			}
		});

		reload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				reload();
			}
		});
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae == null || ae.getSource().equals("helptype") || ae.getSource().equals("helpdir"))
			init();
	}

	private void init()
	{
		try
		{
			String s = Config.read("helptype");
			StringTokenizer st = new StringTokenizer(s);
			String[] temp = new String[st.countTokens()];
			int count = 0;
			while(st.hasMoreTokens())
				temp[count++] = st.nextToken();
			endings = temp;
		}
		catch(Exception e)
		{
			endings = new String[]{"html", "htm", "txt"};
		}
		reload();
	}

	private void reload()
	{
		topics = collectTopics();
		jsp.getViewport().removeAll();
		jsp.getViewport().add(topics);
		if(files.length > 0)
			goTopic(topic(files[0]));
	}

	private void newFrame()
	{
		new HelpFrame().show();
	}

	public static HelpFrame getTop()
	{
		if(stack.size() == 0)
			return new HelpFrame();
		return (HelpFrame) stack.peek();
	}

	private JList collectTopics()
	{
		JList temp = new JList();
		String loc = Config.read("helpdir");
		if(loc == null)
			loc = ".";
		files = collectFiles(new File(loc));
		contents = collectContents();
		Arrays.sort(files);
		String[] s = new String[files.length];
		for(int i = 0; i < files.length; i++)
			s[i] = topic(files[i]);

		temp.setListData(s);
		temp.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				setTopic((String) topics.getSelectedValue());
			}
		});
		return temp;
	}

	private HashMap collectContents()
	{
		HashMap temp = new HashMap();
		filemap = new HashMap();
		for(int i = 0; i < files.length; i++)
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(files[i]));
				StringBuffer sb = new StringBuffer();
				while(br.ready())
					sb.append(br.readLine()).append("<BR>");
				temp.put(topic(files[i]), sb.toString());
				filemap.put(topic(files[i]), files[i]);
			}
			catch(Exception e) {}
		}
		return temp;
	}

	private String topic(File f)
	{
		return f.getName().substring(0, f.getName().lastIndexOf("."));
	}

	private File[] collectFiles(File dir)
	{
		if(!dir.isDirectory())
			return new File[0];
		return dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				for(int i = 0; i < endings.length; i++)
					if(f.getName().toLowerCase().endsWith(endings[i]))
						return true;
				return false;
			}
		});
	}

	private void setTopic(String topic)
	{
		String s = (String) contents.get(topic);
//		File f = (File) filemap.get(topic);
		if(s != null)
			window.setText(s);
	}


	public HelpFrame goTopic(String s)
	{
		// find help dealing with this class and go there
		if(s == null)
			return this;

		for(int i = 0; i < files.length; i++)
			if(topic(files[i]).equalsIgnoreCase(s))
				topics.setSelectedIndex(i);

		return this;
	}

	// not yet supported
	public HelpFrame goKeyword(String s)
	{
		if(s == null)
			return this;
		return this;
	}


	public static void main(String[] arghs)
	{
//		new HelpFrame().goTopic("WebSearch").show();
//		new HelpFrame().show();
		new HelpFrame().show();
	}
}
