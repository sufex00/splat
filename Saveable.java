/** @author Gergely Kota

Save interface calls for methods to save a class

*/
import java.io.*;

public interface Saveable extends Serializable
{
	public boolean save();
	public boolean save(File f);
}


