package mapred.ngramcount;

import java.io.IOException;
import mapred.job.Optimizedjob;
import mapred.util.SimpleParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

public class Driver {

	public static void main(String args[]) throws Exception {
		SimpleParser parser = new SimpleParser(args);

		String input = parser.get("input");
		String output = parser.get("output");

		getJobFeatureVector(input, output);

	}

	private static void getJobFeatureVector(String input, String output)
			throws IOException, ClassNotFoundException, InterruptedException {
		Optimizedjob job = new Optimizedjob(new Configuration(), input, output,
				"Compute NGram Count");

		job.setClasses(NgramCountMapper.class, NgramCountReducer.class, null);
		job.setMapOutputClasses(Text.class, NullWritable.class);

		job.run();
	}	
}
