import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;


/** @author Gergely Kota
	BrowserPanel is a scrollable html-reader with basic web browser functionality
*/


public class BrowserPanel extends JPanel implements ActionListener
{
	private JEditorPane panel;
	private Header header;
	private Stack backStack, forwardStack;
	private final String ERROR = "ERRORSTRING";
	private String HOME;
	private JLabel hover;
	private final String EMPTY = " ";

	public BrowserPanel()
	{
		setLayout(new BorderLayout());
		JScrollPane jsp = new JScrollPane();
		hover = new JLabel(EMPTY);
		panel = new JEditorPane();
		panel.setEditable(false);
		panel.setContentType("text/html");
		panel.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent h) {
				if(h.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					go(h.getURL());
				else if(h.getEventType() == HyperlinkEvent.EventType.ENTERED)
					hover.setText(h.getURL().toString());
				else if(h.getEventType() == HyperlinkEvent.EventType.EXITED)
					hover.setText(EMPTY);
			}
		});
		jsp.getViewport().add(panel);
		add(jsp, BorderLayout.CENTER);
		add(header = new Header(), BorderLayout.NORTH);
		add(hover, BorderLayout.SOUTH);
		backStack = new Stack();
		forwardStack = new Stack();
		setHome();
		go(HOME);
		Config.addActionListener(this);
	}

	private void setHome()
	{
		HOME = Config.read("homepage");
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals("homepage"))
			setHome();
	}

	public void go(URL u)
	{
		forwardStack = new Stack();
		setContent(u);
	}

	private void go(String file)
	{
		forwardStack = new Stack();
		try
		{
			file = fix(file);
			if(file.indexOf("http") == 0)
				go(new URL(file));
			else if(file.indexOf("file") == 0)
				go(new URL(file));
			else
				go(new java.io.File(file).toURL());
		}
		catch(Exception e) {}
	}

	private void setContent(URL u)
	{
		backStack.push(u);
		try
		{
//			panel.setText(EMPTY);
			header.update();
			panel.setPage(u);
		}
		catch(Exception e) {error(u.toString());}
	}

	private String fix(String address)
	{
		if(address.indexOf("www.") == 0)
			return "http://" + address;

		return address;
	}

	private void error(String s)
	{
		panel.setText("Error opening: " + s);
	}


/* ------------------------------------------------- */
/* ------------------------------------------------- */

	private class Header extends JPanel implements ActionListener
	{
		JButton refresh, home, back, forward, go;
		JTextField address;
		JLabel hover;

		public Header()
		{
			setLayout(new BorderLayout());
			reload();
			Config.addActionListener(this);
		}

		public void update()
		{
			back.setEnabled(backStack.size() > 1);
			forward.setEnabled(forwardStack.size() > 0);
			address.setText(backStack.peek().toString());
		}

		private void reload()
		{
			invalidate();
			removeAll();
			JPanel left = new JPanel(new GridLayout(1,4));

			refresh = new JButton();
			home = new JButton();
			back = new JButton();
			forward = new JButton();

			left.add(back);
			left.add(forward);
			// lose the refresh for now .. it was being dumb
			//left.add(refresh);
			left.add(home);
			add(left, BorderLayout.WEST);

			go = new JButton("Go");
			add(go, BorderLayout.EAST);
			address = new JTextField();
			add(address, BorderLayout.CENTER);


			back.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if(backStack.size() < 2)
						return;
					forwardStack.push(backStack.pop());
					setContent((URL) backStack.pop());
				}
			});

			forward.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if(forwardStack.isEmpty())
						return;
					backStack.push(forwardStack.pop());
					setContent((URL) backStack.pop());
				}
			});

			home.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					go(HOME);
				}
			});

			refresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setContent((URL) backStack.pop());
				}
			});

			go.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					go(address.getText());
				}
			});

			address.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					go.doClick();
				}
			});

			setIcons();
			validate();
			repaint();
		}

		public void actionPerformed(ActionEvent ae)
		{
			if(((String)ae.getSource()).indexOf("browser") == 0)
				reload();
		}

		private void setIcons()
		{
			Color def = Color.green.darker().darker();
			if(!GUtil.buttonFix(refresh, def, Config.read("browserrefresh")))
				refresh.setText("Refresh");
			if(!GUtil.buttonFix(home, def, Config.read("browserhome")))
				home.setText("Home");
			if(!GUtil.buttonFix(back, def, Config.read("browserback")))
				back.setText("Back");
			if(!GUtil.buttonFix(forward, def, Config.read("browserforward")))
				forward.setText("Forward");
		}

	}

/* ------------------------------------------------- */
/* ------------------------------------------------- */

	public static void main(String[] args)
	{
		JFrame jf = new JFrame();
		jf.setSize(600,600);
		jf.getContentPane().add(new BrowserPanel());
		jf.show();

	}

}
