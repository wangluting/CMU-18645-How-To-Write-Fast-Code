package mapred.ngramcount;

import java.io.IOException;

import mapred.util.Tokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class NgramCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	
	private int ng;
	
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] words = Tokenizer.tokenize(line);
		//luting
		for(int i = 0; i <= words.length-ng; i++) {
			StringBuilder bld = new StringBuilder();
			for(int j = 0; j < ng; j++) {
				if(j > 0) {
					bld.append(" ");
				}
				bld.append(words[i+j]);
			}
			context.write(new Text(bld.toString()), new IntWritable(1));
		}
	/*	
		for (String word : words)
			context.write(new Text(word), NullWritable.get());
	*/
	}
	
	//luting
	@Override
	protected void setup(Context context) 
		throws IOException, InterruptedException {
			super.setup(context);
			Configuration config = context.getConfiguration();
			ng = config.getInt("n", 1);
			
		}
	
}
