package mapred.itemBase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.math.VectorWritable;

//import org.apache.mahout.math.VarLongWritable;
import org.apache.hadoop.io.VLongWritable;

import java.io.IOException;
import org.apache.mahout.math.Vector;

public class AggregateCombiner extends
       	Reducer<VLongWritable,VectorWritable,
                   VLongWritable,VectorWritable> {
    public void reduce(VLongWritable key,
                        Iterable<VectorWritable> values,
                        Context context)
              throws IOException, InterruptedException {
        
        Vector partial = null;
        for (VectorWritable vectorWritable : values) {
			partial = partial == null ? vectorWritable.get() : partial.plus(vectorWritable.get());
		}
        context.write(key, new VectorWritable(partial));
    }
}
