import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/** @author Gergely Kota

This class deals with showing the text with html markups. Also, allows for some
basic functionality

*/

public class HTMLPanel extends JPanel implements ActionListener
{
	private JPanel[] edges = new JPanel[4];
	private JScrollPane jsp;
	private JLabel info;
	private JEditorPane pane;
	private File currentFile;
	private String color;

	private PairComparison currentPair = null;
	private int currentIndex = 0;

	public HTMLPanel()
	{
		this(Config.getInt("edgesize"));
	}

	/** @param size the thickness of the border in the panel
		@param c the Color of the border
		*/
	public HTMLPanel(int size)
	{
		Config.addActionListener(this);
		setLayout(new BorderLayout());
		pane = new JEditorPane();
		info = new JLabel();
		color = Config.read("htmlcolor");
		if(color == null)
			color = "FF0000"; // red by default
		// set to read html pages
		pane.setContentType("text/html; charset=EUC-JP");
		pane.setEditable(false);
		jsp = new JScrollPane();
		jsp.getViewport().add(pane);
		add(edges[0] = GUtil.filler(size), BorderLayout.NORTH);
		add(edges[1] = GUtil.filler(size), BorderLayout.SOUTH);
		add(edges[2] = GUtil.filler(size), BorderLayout.EAST);
		add(edges[3] = GUtil.filler(size), BorderLayout.WEST);

		JPanel interests = new JPanel(new BorderLayout());
		JPanel bottom = new JPanel(new BorderLayout());
		interests.add(jsp, BorderLayout.CENTER);
		interests.add(bottom, BorderLayout.SOUTH);
		bottom.add(saveButton(), BorderLayout.EAST);
		bottom.add(info, BorderLayout.CENTER);
		add(interests, BorderLayout.CENTER);
	}

	public void reload()
	{
		invalidate();
		pane.invalidate();
		resize();
		setHighlightColor();
		setColor();
		pane.validate();
		validate();
		repaint();
	}

	public void actionPerformed(ActionEvent ae)
	{
		String s = (String) ae.getSource();
		if(s.equals("edgesize"))
			resize();
		if(s.equals("edgecolor"))
			setColor();
		if(s.equals("htmlcolor"))
			setHighlightColor();
	}

	public void setHighlightColor()
	{
		String s3 = Config.read("htmlcolor");
		if(s3 != null)
			color = s3;
		reColor();
	}

	public void setHighlightColor(Color c)
	{
		color = Conversion.hexString(c);
		reColor();
	}

	public void resize()
	{
		String s2 = Config.read("edgesize");
		try
		{
			resize(Integer.parseInt(s2));
		}
		catch(Exception e) {}
	}
	/** Resizes the size of the border of the HTMLPanel
		@param size the size to set the border to
		*/
	public void resize(int size)
	{
		invalidate();
		for(int i = 0; i < 4; i++)
			edges[i].setPreferredSize(new Dimension(size, size));
		validate();
		repaint();
	}

	public void setColor()
	{
		String s1 = Config.read("edgecolor");
		if(s1 != null)
			setColor(Conversion.colorConvert(s1));
	}
	/** Sets the Color of the border of the HTMLPanel
		@param c the Color to set the border to
		*/
	public void setColor(Color c)
	{
		if(c == null)
			return;
		for(int i = 0; i < 4; i++)
			edges[i].setBackground(c);
		repaint();
	}


	/** Sets the content of the HTMLPanel to the given String associated with
		the given File
		@param s the String content of the window
		@param f the File the String came from
		*/
	public void setString(PairComparison pc, int index)
	{
		if(pc == null)
			return;
		if(index == 1)
		{
			pane.setText(pc.getHTML1(color));
			currentFile = pc.getFile1();;
			info.setText(pc.getFile1().getAbsolutePath());
		}
		else
		{
			pane.setText(pc.getHTML2(color));
			currentFile = pc.getFile2();
			info.setText(pc.getFile2().getAbsolutePath());
		}
		currentPair = pc;
		currentIndex = index;
		repaint();
	}

	public void setString(File f, String content)
	{
		currentFile = f;
		pane.setText(content);
		info.setText(f.getName());
	}

	public void reColor()
	{
		setString(currentPair, currentIndex);
	}

	/** Gets the String content of the HTMLPanel
		@return the String in this HTMLPanel
		*/
	public String getString()
	{
		return pane.getText();
	}

	/** Gets the current File from the HTMLPanel
		@return the File associated with this HTMLPanel
		*/
	public File getCurrentFile()
	{
		return currentFile;
	}

	private boolean save()
	{
		if(currentFile == null)
			return false;
		try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(FileFrame.getSaveFile()));
			pw.print(pane.getText());
			pw.close();
			return true;
		}
		catch(Exception e) {return false;}
	}

	private JButton saveButton()
	{
		JButton save = new JButton("Save This Panel");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				save();
			}
		});
		return save;
	}

/* ---------------------------------------------------------------- */
/* ---------------------------------------------------------------- */
/* ---------------------------------------------------------------- */

	public static void main(String[] args)
	{
		HTMLPanel hp = new HTMLPanel(3);
		JFrame jf = new JFrame();
		jf.setSize(400,400);
		jf.getContentPane().add(hp);
		String s = "This is an html page.";
		for(int i = 0; i < 5; i++)
			s = s + s;
			s = s + "<font color = \"FF0000\">" + s + "</font>";
//		hp.setString("<HTML>" + s + "</HTML>", new File("Yo"));
		jf.show();
	}

}
