package mapred.itemBase;

//import org.apache.hadoop.io.LongWritable;
//import org.apache.mahout.math.VarLongWritable;
import org.apache.hadoop.io.VLongWritable;

import org.apache.mahout.math.VectorWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.util.regex.*;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import java.io.IOException;

public class WikipediaToUserVectorReducer extends Reducer<VLongWritable,VLongWritable,VLongWritable,VectorWritable> { 
  public void reduce(VLongWritable userID,
                     Iterable<VLongWritable> itemPrefs,
                     Context context)throws IOException, InterruptedException {
    
    Vector userVector = new RandomAccessSparseVector(Integer.MAX_VALUE, 100);
    for (VLongWritable itemPref : itemPrefs) {
      userVector.set(((int)itemPref.get())/10, ((int)itemPref.get())%10);
    }
    context.write(userID, new VectorWritable(userVector)); 
  }
}
