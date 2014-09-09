import java.util.*;

/** @author Gergely Kota

DependencyTree is a class that creates a tree from a set of vertex pairs
(parent, child). Definition: there is a directed edge from the parent to the child.
The vertices are labeled by Strings (which are their names). No two vertices
can have the same name (they are assumed to be the same vertex). Once a tree
is built, there is an option to order the vertices: this associates a value
with each vertex that is its minimum distance from the target vertex (which
will always have value 0). All edges have weight 1.

*/

public class DependencyTree
{
	/** ENDCODE is the name of the goal vertex */
	public final String ENDCODE;
	/** END is the goal vertex */
	public final Vertex END;
	private VertexList vertices;

	/** All DependencyTrees are assumed to have a goal vertex. The name of this
		vertex needs to be specified at the creation of the DependencyTree.
		@param endcode is the name of the goal vertex.
		*/
	public DependencyTree(String endcode)
	{
		ENDCODE = endcode;
		END = new Vertex(ENDCODE);
		vertices = new VertexList();
		vertices.add(END);
	}

	/** adds a pair of Vertex objects with a directed edge from parent to child
		@param parent the Vertex from which the edge leaves
		@param child the Vertex where the edge points
		*/
	public void addPair(Vertex parent, Vertex child)
	{
		addPair(parent.name(), child.name());
	}

	/** adds a pair of Vertex object with a directed edge from parent to child
		@param parent the name of the Vertex from which the edge leaves
		@param child the name of the Vertex where the edge points
		*/
	public void addPair(String parent, String child)
	{
		Vertex p, c;
		c = (child == null)? END: new Vertex(child);
		child = (c == END)? "End": child;
		p = (parent == null)? END: new Vertex(parent);
		parent = (p == END)? "End": parent;

		vertices.add(p);
		vertices.add(c);
		p = vertices.getName(parent);
		c = vertices.getName(child);
		p.children.add(c);
		c.parents.add(p);
	}

	/** order creates the distance from target values for all vertices.
		Vertices from which there is no path to the goal are left with
		a value if Integer.MAX_VALUE
		*/
	public void order()
	{
		for(int i = 0; i < vertices.size(); i++)
			vertices.getVertex(i).order = Integer.MAX_VALUE;
		END.order = 0;
		END.order();
	}

	/** @return all the Vertices, ordered by greatest distance to target first.
		*/
	public Vertex[] getOrder()
	{
		Vertex[] temp = new Vertex[vertices.size()];
		for(int i = 0; i < temp.length; i++)
			temp[i] = vertices.getVertex(i);

		for(int i = 0; i < temp.length; i++)
			for(int j = 0; j < temp.length-1; j++)
				if(temp[j].order < temp[j+1].order)
				{
					Vertex v = temp[j];
					temp[j] = temp[j+1];
					temp[j+1] = v;
				}

		return temp;
	}

	/** @return a String representation of the order of Vertices and their nexts
		*/
	public String orderString()
	{
		String s = "";
		for(int i = 0; i < vertices.size(); i++)
		{
			Vertex temp = vertices.getVertex(i);
			s += temp.name() + ": " + temp.order + " -> " + temp.next() + "\n";
		}
		return s;
	}

	/** @return a list of all the vertices and their toStrings
		*/
	public String toString()
	{
		return vertices.toString();
	}



/* ---------------------------------------- */
/* ---------------------------------------- */
/* ---------------------------------------- */

	// specialized class to hold Vertex objects
	// add is fixed to now allow duplicates
	private class VertexList extends ArrayList
	{
		public void add(Vertex v)
		{
			if(!contains(v))
				super.add(v);
		}

		public Vertex getVertex(int i)
		{
			return (Vertex)get(i);
		}

		public Vertex getName(String name)
		{
			for(int i = 0; i < size(); i++)
				if(getVertex(i).name().equals(name))
					return getVertex(i);
			return null;
		}
	}


	/** @author Gergely Kota
		Vertex contains a list of its children and parents, also its name
		*/
	public class Vertex
	{
		private String name;
		private VertexList parents, children;
		private int order;

		/** @param s the name of the Vertex
			*/
		public Vertex(String s)
		{
			name = s;
			parents = new VertexList();
			children = new VertexList();
			order = Integer.MAX_VALUE;
		}

		/** @return the name of the Vertex
			*/
		public String name()
		{
			return name;
		}

		// order is defined on the Vertex, it is called recursively
		// starting with the goal vertex
		private void order()
		{
			for(int i = 0; i < parents.size(); i++)
			{
				// for every parent, if it is more than 1 heavier than I am.
				// its weight can be reduced. If its weight is reduced,
				// the parent needs to be ordered again to fix all of its
				// parents' values
				Vertex temp = parents.getVertex(i);
				if(temp.order > order + 1)
				{
					temp.order = order + 1;
					temp.order();
				}
			}
		}

		/** @return the Vertex that has weight one less than this vertex.
			next is the Vertex who has one less weight than this Vertex.
			This is the next stage to be accomplished once this Vertex is
			processed.
			*/
		public Vertex next()
		{
			for(int i = 0; i < children.size(); i++)
			{
				Vertex temp = children.getVertex(i);
				if(temp.order == order - 1)
					return temp;
			}
			return null;
		}

		/** @return true iff the argument is a Vertex with the same name
			*/
		public boolean equals(Object o)
		{
			try
			{
				return name.equals(((Vertex)o).name());
			}
			catch(Exception e) {return false;}
		}

		/** @return the name of the Vertex and a list of its parents and children
			*/
		public String toString()
		{
			String s = "{" + name() + ":  Parents: ";
			for(int i = 0; i < parents.size(); i++)
				s += (parents.getVertex(i).name() + " ");
			s += ",  Children: ";
			for(int i = 0; i < children.size(); i++)
				s += (children.getVertex(i).name() + " ");
			s += "}";
			return s;
		}
	}


/* -------- TEST CODE ---------------- */

	public static void main(String[] arhhs)
	{
		DependencyTree dt = new DependencyTree("txt");

		dt.addPair("A", "B");
		dt.addPair("A", "C");
		dt.addPair("B", "C");
		dt.addPair("C", "txt");
		dt.addPair("D", "A");
		dt.addPair("E", "F"); // detached component

		System.out.println(dt);
		dt.order();
		System.out.println(dt.orderString());
		System.out.println(" ------------ ");
		Vertex[] v = dt.getOrder();
		for(int i = 0; i < v.length; i++)
			System.out.println(v[i] + " ");
	}
}
