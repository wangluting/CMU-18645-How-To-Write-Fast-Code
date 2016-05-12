package mapred.main;

import mapred.util.SimpleParser;

public class Entry {
	public static void main(String args[]) throws Exception  {
		long start = System.currentTimeMillis();
		System.out.println("Running program " + "collaborative filtering");
			
		mapred.itemBase.Driver.main(args);

		long end = System.currentTimeMillis();

		System.out.println(String.format("Runtime for program collaborative filtering: %d ms",
				end - start));
	}
}
