import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/** @author Gergely Kota

Progress Bar shows progress of a Progressable

*/


public class ProgressBar extends JPanel implements ProgressListener
{
	private Progressable owner;
//	private Timer timer;
	private double progress;
	private int edge = 2, size = 16;
	private Color color = new Color(0, 136, 157);
	private String source;

	public ProgressBar()
	{
		setPreferredSize(new Dimension(size, size));
		source = "";
	}

	public void setProgress(double x)
	{
		progress = x;
		progress = progress < 0? 0 : progress;
		progress = progress > 1? 1 : progress;
		revalidate();
		repaint();
	}

	public void progressPerformed(ProgressEvent pe)
	{
		source = pe.getAction();
		setProgress(pe.getSource().getProgress());
	}

	public ProgressBar setColor(Color c)
	{
		color = c;
		return this;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.gray.darker().darker());
		g.drawRect(0,0, getWidth()-1, getHeight()-1);
		g.setColor(Color.green.darker().darker());
		g.setColor(color);
		int x = getWidth() - 2*edge;
		int y = getHeight() - 2*edge;
		g.fillRect(edge, edge, (int) Math.round(progress*x), y);

		String s = "" + Math.round(100*progress) + " %";
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.PLAIN, 2*size/3));
		int width = g.getFontMetrics().stringWidth(s);
		int left = (x - width)/2;
		g.drawString(s, left, size-edge);
		g.drawString(source, 5, size-edge);

	}

	public static void main(String[] args)
	{
		JFrame jf1 = new JFrame();
		jf1.setSize(500, 80);
		jf1.setLocation(0,0);
		jf1.getContentPane().setLayout(new GridLayout(2,1));
//		jf1.getContentPane().add(new ProgressBar(new Tester(10)).setColor(Color.blue));
//		jf1.getContentPane().add(new ProgressBar(new Tester(5)));
		jf1.show();
	}

}
