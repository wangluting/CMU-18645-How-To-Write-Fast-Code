package mapred.itemBase;

//import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.VIntWritable;


import org.apache.mahout.math.VectorWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.math.RandomAccessSparseVector;
import java.util.Iterator;
import org.apache.mahout.math.AbstractVector;
import org.apache.mahout.math.Vector;
import java.io.IOException;

public class UserVectorToCooccurrenceReducer extends Reducer<VIntWritable,VIntWritable,VIntWritable,VectorWritable> {
    public void reduce(VIntWritable itemIndex1,
                       Iterable<VIntWritable> itemIndex2s,
                        Context context)
              throws IOException, InterruptedException {
        
        Vector cooccurrenceRow = new RandomAccessSparseVector(Integer.MAX_VALUE, 100);
        for (VIntWritable intWritable : itemIndex2s) {
            int itemIndex2 = intWritable.get();
            cooccurrenceRow.set(itemIndex2,cooccurrenceRow.get(itemIndex2) + 1.0);
        }
        context.write(itemIndex1, new VectorWritable(cooccurrenceRow));
	} 
}
