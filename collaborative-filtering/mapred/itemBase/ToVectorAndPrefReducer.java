// Adapted from mahout source code ToVectorAndPrefReducer.java

package mapred.itemBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapreduce.Reducer;
// import org.apache.mahout.math.VarIntWritable;
import org.apache.hadoop.io.VIntWritable;
import org.apache.mahout.math.Vector;
import org.apache.mahout.cf.taste.hadoop.item.VectorOrPrefWritable;
import org.apache.mahout.cf.taste.hadoop.item.VectorAndPrefsWritable;

public final class ToVectorAndPrefReducer extends
    Reducer<VIntWritable, VectorOrPrefWritable,VIntWritable, VectorAndPrefsWritable> {

  private final VectorAndPrefsWritable vectorAndPrefs = new VectorAndPrefsWritable();

  @Override
  protected void reduce(VIntWritable key,
                        Iterable<VectorOrPrefWritable> values,
                        Context context) throws IOException, InterruptedException {

    List<Long> userIDs = new ArrayList<Long>();
    List<Float> prefValues = new ArrayList<Float>();
    Vector similarityMatrixColumn = null;
    for (VectorOrPrefWritable value : values) {
      if (value.getVector() == null) {
        // Then this is a user-pref value
        userIDs.add(value.getUserID());
        prefValues.add(value.getValue());
      } else {
        // Then this is the column vector
        if (similarityMatrixColumn != null) {
          throw new IllegalStateException("Found two similarity-matrix columns for item index " + key.get());
        }
        similarityMatrixColumn = value.getVector();
      }
    }

    if (similarityMatrixColumn == null) {
      return;
    }

    vectorAndPrefs.set(similarityMatrixColumn, userIDs, prefValues);
    context.write(key, vectorAndPrefs);
  }

}
