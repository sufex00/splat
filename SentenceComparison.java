import java.io.*;
import javax.swing.*;
import java.util.*;

/** @author Gergely Kota

SentenceComparison compares every sentence with every other sentence
in the other document. For each sentence, a running max of how much
it cheats is held, in the end the average of the cheat values for
all the sentences is used for the cheat value of the document

Algorithm based on one created by Tom Slattery and Joshua Louie for
a previous version of SPLAT
*/

public class SentenceComparison extends DocumentComparison
{
	private String delims = ".?!;\n";

	public SentenceComparison(File loc)
	{
		super(loc);
		setTolerance(50);
	}

	public PairComparison compare(File f1, File f2)
	{
		PairComparison pc = new PairComparison(f1, f2);
		Sentence[] s1 = build(f1);
		Sentence[] s2 = build(f2);

		for(int i = 0; i < s1.length; i++)
			for(int j = 0; j < s2.length; j++)
			{
				s2[j].setScore(s2[j].cheatFrom(s1[i]));
				s1[i].setScore(s1[i].cheatFrom(s2[j]));
			}

		// log results
		double x = getTolerance()/100.0;
		for(int i = 0; i < s1.length; i++)
			if(s1[i].score > x)
				pc.log(s1[i].index, s1[i].index + s1[i].sentence.length(), 0, 0);
		for(int i = 0; i < s2.length; i++)
			if(s2[i].score > x)
				pc.log(0, 0, s2[i].index, s2[i].index + s2[i].sentence.length());

		pc.setScore(getScore(s2));
		return pc;
	}

	public static String description()
	{
		return "Compares documents based on the similarity of sentences";
	}

	private double getScore(Sentence[] s)
	{
		double sum = 0;
		for(int i = 0; i < s.length; i++)
			if(s[i].score > getTolerance()/100.0)
				sum += s[i].score;
		return sum/s.length;
	}

	public String canonicalForm(File f)
	{
		return getOriginalContent(f).toLowerCase();
	}

	public int indexMap(File f, int index)
	{
		return index;
	}

	private Sentence[] build(File f)
	{
		ArrayList temp = new ArrayList();
		int index = 0;
		String all = getContent(f);
		StringTokenizer sentences = new StringTokenizer(all, delims);
		while(sentences.hasMoreTokens())
		{
			String s = sentences.nextToken();
			// set the index oof where this sentence really starts
			// this will be the first occurence after the prev sentence's index
			index = all.indexOf(s, index);
			temp.add(new Sentence(s, index));
		}
		Object[] o = temp.toArray();
		Sentence[] s = new Sentence[o.length];
		for(int i = 0; i < s.length; i++)
			s[i] = (Sentence) o[i];
		return s;
	}


	private class Sentence
	{
		private int index;
		private String sentence;
		private ArrayList words;
		private double score;

		public Sentence(String s, int i)
		{
			index = i;
			sentence = s;
			words = new ArrayList();
			score = 0;
			StringTokenizer st = new StringTokenizer(s);
			while(st.hasMoreTokens())
			{
				String temp = st.nextToken().intern();
				if(!words.contains(temp))
					words.add(temp);
			}
		}

		public void setScore(double x)
		{
			if(x > score)
				score = x;
		}

		public double cheatFrom(Sentence s)
		{
			int total = 0;
			for(int i = 0; i < words.size(); i++)
				if(s.words.contains(words.get(i)))
					total++;
			return ((double)total)/words.size();
		}


	}



/* --------------------------------------------- */
/* --------------------------------------------- */

	public static void main(String[] args)
	{
		long x = System.currentTimeMillis();
		SentenceComparison sc = new SentenceComparison(new File("Downloads"));
		sc.setTolerance(60);
//		sc.read();
//		System.out.println(System.currentTimeMillis() - x);
//		sc.compare();
//		System.out.println(System.currentTimeMillis() - x);
		new HTMLFrame(sc.read().compare(new File("Downloads/TextPad.txt"))).show();

	}



}