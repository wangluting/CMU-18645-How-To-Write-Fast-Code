package mapred.itemBase;

import java.util.Queue;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.mahout.math.VectorWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.cf.taste.hadoop.MutableRecommendedItem;
import org.apache.mahout.cf.taste.hadoop.RecommendedItemsWritable;
import org.apache.mahout.cf.taste.hadoop.TasteHadoopUtils;
import org.apache.mahout.cf.taste.hadoop.TopItemsQueue;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.math.RandomAccessSparseVector;

//import org.apache.mahout.math.VarLongWritable;
import org.apache.hadoop.io.VLongWritable;

import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.function.Functions;
import org.apache.mahout.math.map.OpenIntLongHashMap;
import org.apache.mahout.cf.taste.impl.recommender.*;
import java.io.IOException;

public class AggregateAndRecommendReducer extends
        Reducer<VLongWritable,VectorWritable,
                VLongWritable,RecommendedItemsWritable> {

 private int recommendationsPerUser=2;
 
    public void reduce(VLongWritable key,
            Iterable<VectorWritable> values,
            Context context)
            throws IOException, InterruptedException {
        
        Vector recommendationVector = null;
        for (VectorWritable vectorWritable : values) {
            recommendationVector = recommendationVector == null ? vectorWritable.get() : recommendationVector.plus(vectorWritable.get()); 
        }
        Queue<RecommendedItem> topItems = new PriorityQueue<RecommendedItem>( recommendationsPerUser + 1, Collections.reverseOrder(ByValueRecommendedItemComparator.getInstance()));
		Iterable<Vector.Element> recommendationVectorIterable = recommendationVector.nonZeroes();
    Iterator<Vector.Element> recommendationVectorIterator = recommendationVectorIterable.iterator();
		while (recommendationVectorIterator.hasNext()) {
			Vector.Element element = recommendationVectorIterator.next(); 
			int index = element.index();
			float value = (float) element.get();
			if (topItems.size() < recommendationsPerUser) {
			    topItems.add(new GenericRecommendedItem(index, value));
			  } 
			else if (value > topItems.peek().getValue()) {
			  	topItems.add(new GenericRecommendedItem(index, value));
				topItems.poll();
			}
		}

		List<RecommendedItem> recommendations = new ArrayList<RecommendedItem>(topItems.size());
		recommendations.addAll(topItems);
		Collections.sort(recommendations, ByValueRecommendedItemComparator.getInstance());
		context.write( key, new RecommendedItemsWritable(recommendations));
	}
}
