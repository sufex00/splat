import javax.swing.*;
import java.awt.*;

/** @author Gergely Kota
	EmailPanel provides fields to enter stuff into
	*/

public class EmailPanel extends JPanel
{
	private JTextArea body;
	private SelectiveInfo from, subject;
	private final String EMAIL1 = "shafik@email.arizona.edu";
	private final String EMAIL2 = "gergelyk@email.arizona.edu";

	public EmailPanel()
	{
		setLayout(new BorderLayout());
		JPanel buffer = new JPanel(new GridLayout(2,1));
		body = new JTextArea();
		from = new SelectiveInfo("From", false, 100);
		from.set("<Your email address>");
		subject = new SelectiveInfo("Subject", false, 100);
		buffer.add(from);
		buffer.add(subject);
		JScrollPane jsp = new JScrollPane();
		jsp.getViewport().add(body);
		add(buffer, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
	}


	public void send()
	{
		Jmail.send(from.read(), EMAIL1, "SPLAT: " + subject.read(), body.getText());
		Jmail.send(from.read(), EMAIL2, "SPLAT: " + subject.read(), body.getText());
		from.set("");
		subject.set("");
		body.setText("");
	}
}
