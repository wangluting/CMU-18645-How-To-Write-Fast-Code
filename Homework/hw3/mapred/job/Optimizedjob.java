package mapred.job;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * A simplified interface of the original Job class.
 * 
 * Provides mechanisms for automatically choosing reducer number based on
 * available reducers.
 * 
 * @author Dongzhen Piao
 * 
 */
public class Optimizedjob extends Job {

	private List<String> inputs;

	private String output;

	private String jobName;

	private int reduceJobs;

	public Optimizedjob(Configuration conf, String input, String output,
			String jobName) throws IOException {
		super(conf);

		this.inputs = new LinkedList<String>();
		this.inputs.add(input);
		this.output = output;
		this.jobName = jobName;
		reduceJobs = 0; // Initalizing to 0 means equal to number of maximum
						// reducers
	}

	/**
	 * Optimize based on current setting
	 * 
	 * @throws IOException
	 */
	private void setup() throws IOException {
		JobConf job_conf = new JobConf(conf);
		JobClient job_client = new JobClient(job_conf);
		ClusterStatus cluster_status = job_client.getClusterStatus();
		int reducer_capacity = cluster_status.getMaxReduceTasks();

		// IO format
		setInputFormatClass(TextInputFormat.class);
		setOutputFormatClass(TextOutputFormat.class);

		// Input file/dir
		for (String input : inputs)
			TextInputFormat.addInputPath(this, new Path(input));
		TextOutputFormat.setOutputPath(this, new Path(output));

		FileSystem fs = FileSystem.get(URI.create(output), conf);
		fs.delete(new Path(output), true);
		// CommonFileOperations.rmr(output);

		if (reduceJobs == 0)
			setNumReduceTasks(reducer_capacity);
		else
			setNumReduceTasks(reduceJobs);

		setJobName(jobName);
		setJarByClass(Optimizedjob.class);

	}

	public void addInput(String input) {
		inputs.add(input);
	}

	public void setReduceJobs(int reduceJobs) {
		this.reduceJobs = reduceJobs;
	}

	/**
	 * Sets classes for mapper, reducer and combiner.
	 * 
	 * The reducer and combiner can be null, in which case there won't be reduce
	 * or combine step.
	 * 
	 * @param mapperClass
	 * @param reducerClass
	 * @param combinerClass
	 */
	public void setClasses(Class<? extends Mapper<?, ?, ?, ?>> mapperClass,
			Class<? extends Reducer<?, ?, ?, ?>> reducerClass,
			Class<? extends Reducer<?, ?, ?, ?>> combinerClass) {
		setMapperClass(mapperClass);
		if (reducerClass != null)
			setReducerClass(reducerClass);

		if (combinerClass != null)
			setCombinerClass(combinerClass);
	}

	/**
	 * Sets the output format of map step. Usually it's Text, or IntWritable.
	 * @param mapOutputKeyClass
	 * @param mapOutputValueClass
	 */
	public void setMapOutputClasses(Class<?> mapOutputKeyClass,
			Class<?> mapOutputValueClass) {
		setMapOutputKeyClass(mapOutputKeyClass);
		setMapOutputValueClass(mapOutputValueClass);
	}

	/**
	 * Runs the job. 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 */
	public void run() throws IOException, InterruptedException,
			ClassNotFoundException {
		setup();

		long start = System.currentTimeMillis();
		this.waitForCompletion(true);
		long end = System.currentTimeMillis();

		System.out.println(String.format("Runtime for Job %s: %d ms", jobName,
				end - start));

	}
}
