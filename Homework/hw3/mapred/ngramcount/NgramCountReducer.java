package mapred.ngramcount;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class NgramCountReducer extends Reducer<Text, NullWritable, Text, IntWritable> {

	@Override
	protected void reduce(Text key, Iterable<NullWritable> value,
			Context context)
			throws IOException, InterruptedException {
		int count = 0;
		for (NullWritable n : value)
			count++;
		
		context.write(key, new IntWritable(count));
	}
}
