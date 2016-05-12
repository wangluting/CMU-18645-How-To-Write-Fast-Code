package mapred.itemBase;

//import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.io.VIntWritable;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.cf.taste.hadoop.item.VectorOrPrefWritable;
import java.io.IOException;

public class CooccurrenceColumnWrapperMapper extends
    Mapper<VIntWritable,VectorWritable,
           VIntWritable,VectorOrPrefWritable> {
  
  	public void map(VIntWritable key, VectorWritable value, Context context) throws IOException, InterruptedException { 
  		context.write(key, new VectorOrPrefWritable(value.get()));
	} 
}
