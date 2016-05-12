package mapred.itemBase;

//import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.VIntWritable;

//import org.apache.mahout.math.VarLongWritable;
import org.apache.hadoop.io.VLongWritable;

import org.apache.mahout.math.VectorWritable;
//import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.mapreduce.Mapper;
import java.util.Iterator;
import org.apache.mahout.math.Vector;
import java.io.IOException;

public class UserVectorToCooccurrenceMapper extends Mapper<VLongWritable,VectorWritable,VIntWritable,VIntWritable> {
	public void map(VLongWritable userID,
              VectorWritable userVector,
              Context context)
    throws IOException, InterruptedException {
 
    Iterable<Vector.Element> iterator = userVector.get().nonZeroes(); 
	  Iterator<Vector.Element> it=iterator.iterator();
		while (it.hasNext()) {
	    int index1 = it.next().index();
      Iterable<Vector.Element> it2able = userVector.get().nonZeroes();
			Iterator<Vector.Element> it2=it2able.iterator();
	    while (it2.hasNext()) {
	      int index2 = it2.next().index();
	      context.write(new VIntWritable(index1), new VIntWritable(index2));
		} 
	}
}
}
