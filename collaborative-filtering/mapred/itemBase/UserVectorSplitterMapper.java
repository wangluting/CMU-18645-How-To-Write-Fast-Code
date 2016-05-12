package mapred.itemBase;

//import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.VIntWritable;


//import org.apache.mahout.math.VarLongWritable;
import org.apache.hadoop.io.VLongWritable;

//import org.apache.hadoop.io.LongWritable;

import org.apache.mahout.math.VectorWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.util.Iterator;
import org.apache.mahout.cf.taste.hadoop.item.VectorOrPrefWritable;
import org.apache.mahout.math.Vector;

import java.io.IOException;

public class UserVectorSplitterMapper extends
    Mapper<VLongWritable,VectorWritable,
           VIntWritable,VectorOrPrefWritable> {
  
  	public void map(VLongWritable key,
                VectorWritable value,
				Context context) throws IOException, InterruptedException { 
  		
  		long userID = key.get();
		Vector userVector = value.get();
		Iterable<Vector.Element> itable = userVector.nonZeroes();
		Iterator<Vector.Element> it = itable.iterator();
		VIntWritable itemIndexWritable = new VIntWritable();

		while (it.hasNext()) {
  			Vector.Element e = it.next();
			  int itemIndex = e.index();
			  float preferenceValue = (float) e.get();
			  itemIndexWritable.set(itemIndex);
			  context.write(itemIndexWritable, new VectorOrPrefWritable(userID, preferenceValue));
		} 
	}
}
