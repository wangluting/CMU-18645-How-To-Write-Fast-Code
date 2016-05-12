package mapred.itemBase;
import java.io.IOException;
import java.lang.Boolean;

import mapred.job.Optimizedjob;
import mapred.util.FileUtil;
import mapred.util.SimpleParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;

import org.apache.hadoop.io.VLongWritable;
import org.apache.mahout.cf.taste.hadoop.item.VectorOrPrefWritable;
import org.apache.mahout.cf.taste.hadoop.item.VectorAndPrefsWritable;
import org.apache.hadoop.io.VIntWritable;


import org.apache.mahout.math.VectorWritable;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;



public class Driver {

		public static void main(String args[]) throws Exception {
				SimpleParser parser = new SimpleParser(args);

				String input = parser.get("input");
				String output = parser.get("output");
				String tmpdir = parser.get("tmpdir");

				getUserScoreVector(input, tmpdir + "/user_vector");
				getCooccurrenceMatrix(tmpdir + "/user_vector",tmpdir + "/cooccurrence"); 
				getUserSplit(tmpdir + "/user_vector",tmpdir + "/split_user");

				String[] inputs = {tmpdir + "/cooccurrence", tmpdir + "/split_user"};
				getVectorAndPref(inputs, tmpdir + "/vector_pref");
				getMulAndRecommand(tmpdir + "/vector_pref",output);

		}


		private static void getUserScoreVector(String input, String output)
						throws Exception {
				Optimizedjob job = new Optimizedjob(new Configuration(), input, output,
								"Get user score movie vector");
				job.setInputFormatClass(TextInputFormat.class);
				TextInputFormat.setInputPaths(job,new Path( input ));
				job.setOutputFormatClass(SequenceFileOutputFormat.class);		
				SequenceFileOutputFormat.setOutputPath(job, new Path(output));


				job.setOutputKeyClass(VLongWritable.class);
				job.setOutputValueClass(VectorWritable.class);
				job.setClasses(WikipediaToItemPrefsMapper.class, WikipediaToUserVectorReducer.class, null);
				job.setMapOutputClasses(VLongWritable.class, VLongWritable.class);

		Configuration getUserScoreVectorConf = job.getConfiguration();
    getUserScoreVectorConf.set("mapred.child.java.opts","-Xmx512m");
		getUserScoreVectorConf.setInt("mapreduce.task.io.sort.mb",180);
		getUserScoreVectorConf.setDouble("mapreduce.map.sort.spill.percent",0.99);

		// compress
		getUserScoreVectorConf.setBoolean("mapreduce.map.output.compress",true);
		getUserScoreVectorConf.setBoolean("mapreduce.output.fileoutputformat.compress",true);
		getUserScoreVectorConf.set("mapreduce.output.fileoutputformat.compress.type","BLOCK");
 		getUserScoreVectorConf.set("mapreduce.output.fileoutputformat.compress.codec","org.apache.hadoop.io.compress.BZip2Codec");
		
    job.run();
		}



		private static void getCooccurrenceMatrix(String input, String output)
						throws Exception {
				Optimizedjob job = new Optimizedjob(new Configuration(), input, output,
								"Get co-occurrence matrix");
				job.setInputFormatClass(SequenceFileInputFormat.class);
				SequenceFileInputFormat.setInputPaths(job,new Path( input ));				
	
 	
				job.setOutputFormatClass(SequenceFileOutputFormat.class);
				SequenceFileOutputFormat.setOutputPath(job, new Path(output));

				Configuration getCooccurrenceMatrixConf = job.getConfiguration();
				getCooccurrenceMatrixConf.set("mapred.child.java.opts","-Xmx512m");
				getCooccurrenceMatrixConf.setInt("mapreduce.task.io.sort.mb",180);
				getCooccurrenceMatrixConf.setDouble("mapreduce.map.sort.spill.percent",0.99);

				getCooccurrenceMatrixConf.setBoolean("mapreduce.map.output.compress",true);  // compress mapper's output
			  getCooccurrenceMatrixConf.setBoolean("mapreduce.output.fileoutputformat.compress",true);    // compres reducer's output
				getCooccurrenceMatrixConf.set("mapreduce.output.fileoutputformat.compress.type","BLOCK");
				getCooccurrenceMatrixConf.set("mapreduce.output.fileoutputformat.compress.codec","org.apache.hadoop.io.compress.BZip2Codec");

				SequenceFileInputFormat.setMinInputSplitSize(job, 2000000);
				SequenceFileInputFormat.setMaxInputSplitSize(job, 2500000);

				job.setClasses(UserVectorToCooccurrenceMapper.class, UserVectorToCooccurrenceReducer.class, null);
				job.setOutputKeyClass(VIntWritable.class);
				job.setOutputValueClass(VectorWritable.class);
				job.setMapOutputClasses(VIntWritable.class, VIntWritable.class);

				

				job.run();
		}

		private static void getWarpCooccurrence(String input, String output)
						throws Exception {
				Optimizedjob job = new Optimizedjob(new Configuration(), input, output,
								"Get co-occurrence warp vector");
				job.setInputFormatClass(SequenceFileInputFormat.class);
				SequenceFileInputFormat.setInputPaths(job,new Path( input ));
				job.setOutputFormatClass(SequenceFileOutputFormat.class);
				SequenceFileOutputFormat.setOutputPath(job, new Path(output));
				Configuration getWarpCooccurrenceConf = job.getConfiguration();

				getWarpCooccurrenceConf.setBoolean("mapreduce.map.output.compress",true);  // compress mapper's output
				//getWarpCooccurrenceConf.setBoolean("mapreduce.output.fileoutputformat.compress",true);    // compres reducer's output
				getWarpCooccurrenceConf.set("mapreduce.output.fileoutputformat.compress.type","BLOCK");
				getWarpCooccurrenceConf.set("mapreduce.output.fileoutputformat.compress.codec","org.apache.hadoop.io.compress.BZip2Codec");
				getWarpCooccurrenceConf.set("mapred.child.java.opts","-Xmx512m");
				getWarpCooccurrenceConf.setInt("mapreduce.task.io.sort.mb",180);
				getWarpCooccurrenceConf.setDouble("mapreduce.map.sort.spill.percent",0.99);

				job.setClasses(CooccurrenceColumnWrapperMapper.class, null, null);
				job.setMapOutputClasses(VIntWritable.class, VectorOrPrefWritable.class);
				job.setMapOutputClasses(VIntWritable.class, VectorOrPrefWritable.class);
				job.setMapOutputClasses(VIntWritable.class, VectorOrPrefWritable.class);
				job.setOutputKeyClass(VIntWritable.class);
				job.setOutputValueClass(VectorOrPrefWritable.class);
				job.run();
		}

		private static void getUserSplit(String input, String output)
						throws Exception {
				Optimizedjob job = new Optimizedjob(new Configuration(), input, output,
								"Get user split vector");
				job.setInputFormatClass(SequenceFileInputFormat.class);
				SequenceFileInputFormat.setInputPaths(job,new Path( input ));

				job.setOutputFormatClass(SequenceFileOutputFormat.class);
				SequenceFileOutputFormat.setOutputPath(job, new Path(output));
				job.setClasses(UserVectorSplitterMapper.class, null, null);
				job.setMapOutputClasses(VIntWritable.class, VectorOrPrefWritable.class);
				job.setOutputKeyClass(VIntWritable.class);
				job.setOutputValueClass(VectorOrPrefWritable.class);

				Configuration getUserSplitConf = job.getConfiguration();				
				getUserSplitConf.setBoolean("mapreduce.map.output.compress",true);  // compress mapper's output
				//getUserSplitConf.setBoolean("mapreduce.output.fileoutputformat.compress",true);    // compres reducer's output
				getUserSplitConf.set("mapreduce.output.fileoutputformat.compress.type","BLOCK");
				getUserSplitConf.set("mapreduce.output.fileoutputformat.compress.codec","org.apache.hadoop.io.compress.BZip2Codec");
				getUserSplitConf.set("mapred.child.java.opts","-Xmx512m");
				getUserSplitConf.setInt("mapreduce.task.io.sort.mb",180);
				getUserSplitConf.setDouble("mapreduce.map.sort.spill.percent",0.99);

				job.run();
		}


	private static void getVectorAndPref(String[] inputs, String output)
			throws Exception {
		Optimizedjob job = new Optimizedjob(new Configuration(), inputs, output, "Get vector and preference");
		job.setInputFormatClass(SequenceFileInputFormat.class);
		
		Configuration getVectorAndPrefConf = job.getConfiguration();

		// set input path, and Mappers for each path
		MultipleInputs.addInputPath(job, new Path(inputs[0]),SequenceFileInputFormat.class, CooccurrenceColumnWrapperMapper.class);
		MultipleInputs.addInputPath(job, new Path(inputs[1]),SequenceFileInputFormat.class, ToVectorAndPrefMapper.class); 

		// set Mapper's Output key, value		
		job.setMapOutputClasses(VIntWritable.class,VectorOrPrefWritable.class);

		job.setReducerClass(ToVectorAndPrefReducer.class);
		// set Reducer's output key, value		
		job.setOutputKeyClass(VIntWritable.class);
		job.setOutputValueClass(VectorAndPrefsWritable.class);

		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setOutputPath(job, new Path(output));
		
		getVectorAndPrefConf.setBoolean("mapreduce.map.output.compress",true);
		getVectorAndPrefConf.setBoolean("mapreduce.output.fileoutputformat.compress",true);
		getVectorAndPrefConf.set("mapreduce.output.fileoutputformat.compress.type","BLOCK");
		getVectorAndPrefConf.set("mapreduce.output.fileoutputformat.compress.codec","org.apache.hadoop.io.compress.BZip2Codec");
		getVectorAndPrefConf.set("mapred.child.java.opts","-Xmx512m");
		getVectorAndPrefConf.setInt("mapreduce.task.io.sort.mb",180);
		getVectorAndPrefConf.setDouble("mapreduce.map.sort.spill.percent",0.99);

		//System.out.println(conf.get("mapred.map.tasks"));
		job.run();
	}



		private static void getMulAndRecommand(String input, String output)
						throws Exception {
				Optimizedjob job = new Optimizedjob(new Configuration(), input, output,
								"Do matrix multiply and recommand");
				job.setInputFormatClass(SequenceFileInputFormat.class);

				SequenceFileInputFormat.setInputPaths(job,new Path( input ));

				Configuration getMulAndRecommendConf = job.getConfiguration();
				// compress
				getMulAndRecommendConf.setBoolean("mapreduce.map.output.compress",true);
				getMulAndRecommendConf.set("mapreduce.output.fileoutputformat.compress.type","BLOCK");
				getMulAndRecommendConf.set("mapreduce.output.fileoutputformat.compress.codec","org.apache.hadoop.io.compress.BZip2Codec");
				getMulAndRecommendConf.set("mapred.child.java.opts","-Xmx512m");
				getMulAndRecommendConf.setInt("mapreduce.task.io.sort.mb",180);
				getMulAndRecommendConf.setDouble("mapreduce.map.sort.spill.percent",0.99);

				//SequenceFileInputFormat.setMinInputSplitSize(job, 1000000);
				//SequenceFileInputFormat.setMaxInputSplitSize(job, 1500000);

				job.setOutputFormatClass(TextOutputFormat.class);
				TextOutputFormat.setOutputPath(job, new Path(output));
				
				job.setClasses(PartialMultiplyMapper.class, AggregateAndRecommendReducer.class, AggregateCombiner.class);
				job.setMapOutputClasses(VLongWritable.class, VectorWritable.class);
				job.run();
  }
}
