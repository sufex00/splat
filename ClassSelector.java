import java.awt.event.*;
import java.awt.*;

/** @author Gergely Kota
	interface defines methods on an classselector
	*/
	
	
public interface ClassSelector
{
	public void setClass(Class c);
	public void addActionListener(ActionListener al);
	public Class getSelectedClass();
	public Component toComponent();
}
