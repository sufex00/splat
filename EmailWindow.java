import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** @author Gergely Kota
	EmailWindow sends using JMail
	*/


public class EmailWindow extends JFrame
{
	private static EmailWindow instance = new EmailWindow();

	private JButton send;
	private EmailPanel guts;

	public EmailWindow()
	{
		setTitle("Email the authors");
		setSize(400,400);
		setLocation(300,300);
		JPanel buffer = new JPanel(new BorderLayout());
		guts = new EmailPanel();
		send = new JButton("Send");
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				guts.send();
				hide();
			}
		});

		buffer.add(send, BorderLayout.EAST);
		getContentPane().add(guts, BorderLayout.CENTER);
		getContentPane().add(buffer, BorderLayout.SOUTH);
	}

	public static void showWindow()
	{
		instance.show();
	}

	public static void main(String[] args)
	{
		EmailWindow.showWindow();
	}



}