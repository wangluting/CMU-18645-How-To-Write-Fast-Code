package mapred.hashtagsim;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class HashtagReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> value,
			Context context)
			throws IOException, InterruptedException {		
		
		Map<String, Integer> counts = new TreeMap<String, Integer>();
		String[] tmp;
		for(Text word : value) {
			String[] hashtag_count = word.toString().split(";");
			for(int i = 0; i < hashtag_count.length; i++) {
				tmp = hashtag_count[i].split(":");
				Integer count = counts.get(tmp[0]);
				if (count == null)
					count = 0;
				count += Integer.parseInt(tmp[1]);
				counts.put(tmp[0], count);
				}
		}
		
		/*
		String line = value.toString();
		String[] hashtag_count = line.split(";");
		String[] tmp;
		for(int i = 0; i < hashtag_count.length; i++) {
			tmp = hashtag_count[i].split(":");
			Integer count = counts.get(tmp[0]);
			if (count == null)
				count = 0;
			count += Integer.parseInt(tmp[1]);
			counts.put(tmp[0], count);
		}
		*/
		
		/*
		for (Text word : value) {
			String w = word.toString().split("");
			Integer count = counts.get(w);
			if (count == null)
				count = 0;
			count++;
			counts.put(w, count);
		}
		*/
		
		/*
		 * We're serializing the word cooccurrence count as a string of the following form:
		 * 
		 * word1:count1;word2:count2;...;wordN:countN;
		 */
		if(counts.size() > 1) { 
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Integer> e : counts.entrySet()) {
			builder.append(e.getKey() + ":" + e.getValue() + ";");
		}
		context.write(key, new Text(builder.toString()));
		}
		
	}
}
