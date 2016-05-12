/**
 * 
 */
package mapred.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


/**
 * @author qing
 *
 */
public class CommonFileOperations {
	private static FileSystem fs ;
	
	static{
		Configuration conf = new Configuration();
		try {
			fs = FileSystem.get(conf);
			//fs = FileSystem.get(URI.create("s3n://park_18645/"), conf);
		} catch (IOException e) {
			
		}
	}
	public static void deleteIfExists(String file) throws IOException{
		deleteIfExists(file,FileSystem.get(new Configuration()));
	}
	
	public static void deleteIfExists(String file, FileSystem fs) throws IOException{
		if(fs.exists(new Path(file))){
			if(!fs.delete(new Path(file), true)){
				throw new IOException("Cannot delete file : " + file);
			}
		}
	}
	
	public static FileSystem getFileSystem() {
		return fs;
	}
	

	
	public static OutputStream openFileForWrite(String file) throws IOException{
		return openFileForWrite(file,FileSystem.get(new Configuration()));
	}
	
	public static OutputStream openFileForWrite(String file, boolean isOnHDFS) throws IOException{
		if(isOnHDFS)
			return openFileForWrite(file,FileSystem.get(new Configuration()));
		else
			return new FileOutputStream(file);
	}
	
	public static OutputStream openFileForWrite(String file, FileSystem fs) throws IOException{
		deleteIfExists(file,fs);
		FSDataOutputStream ins = fs.create(new Path(file));
		return ins;
	}
	
	public static void rmr(String file) throws IOException{
		fs.delete(new Path(file), true);
	}
	
	
	
	public static void copyToHDFS(String local, String hdfsFile) throws IOException{
		fs.copyFromLocalFile(false, true, new Path(local), new Path(hdfsFile));
	}
	
	public static String getHomeDirectory(){
		return fs.getHomeDirectory().toString();
	}
	
	public static void copyFromHDFS(String hdfsFile, String local) throws IOException{
		rmr(local);
		File f =new File(local);
		f.getAbsoluteFile().getParentFile().mkdirs();
		fs.copyToLocalFile(false,new Path(hdfsFile), new Path(local));
	}
	

	
	public static String[] getAllChildFileHDFS(String file){
		Path p = new Path(file);
		try {
			FileStatus[] fst = fs.listStatus(p);
			int i = 0;
			for(FileStatus f : fst){
				if(!f.isDir()){
					i++;
				}
			}
			String[] ret = new String[i];
			i = 0;
			for(FileStatus f : fst){
				if(!f.isDir()){
					ret[i++] = f.getPath().toString();
				}
			}
			return ret;
		} catch (IOException e) {
			
			return null;
		}
		
	}
	
	public static String[] getAllChildDirHDFS(String file){
		Path p = new Path(file);
		try {
			FileStatus[] fst = fs.listStatus(p);
			int i = 0;
			for(FileStatus f : fst){
				if(f.isDir()){
					i++;
				}
			}
			String[] ret = new String[i];
			i = 0;
			for(FileStatus f : fst){
				if(f.isDir()){
					ret[i++] = f.getPath().toString();
				}
			}
			return ret;
		} catch (IOException e) {
			
			return null;
		}
	}
	
	
	/**
	 * List all files in a directory that matches a given pattern
	 * @param dir     The directory to list
	 * @param pattern The pattern which the file's FULL PATH matches, if it is NULL, all file will be included (but no dir will be included)
	 * @param subdir  If it is true, then the sub directories will be searched
	 * @return  The files in the directory that matches the pattern
	 * @throws IOException  Anything bad
	 */
	public static String[] listAllFiles(String dir, String pattern, boolean subdir) throws IOException{
		
		Pattern pat = null;
		
		if(pattern!=null){
			pat = Pattern.compile(pattern);
		}
		List<String> allFiles = new LinkedList<String>();
		
		FileStatus[] ft = fs.listStatus(new Path(dir));
		
		for(FileStatus f : ft){
			if(f.isDir() && subdir){
				String[] partial = listAllFiles(f.getPath().toString(),pattern,subdir);
				if(partial == null) continue;
				for(String s : partial) allFiles.add(s);
			}else if(!f.isDir()){ // common file
				if(pat == null){
					allFiles.add(f.getPath().toString());
				}else{
					if(pat.matcher(f.getPath().toString()).matches()){
						allFiles.add(f.getPath().toString());
					}
				}
			}
		}
		String[] str = new String[allFiles.size()];
		int i  = 0;
		for(String s: allFiles) str[i++] = s;
		return str;
	}

	
	public static String[] readLines(InputStream inp) throws IOException{
		return readLines(inp,"UTF-8");
	}
	public static String[] readLines(InputStream inp, String encoding) throws IOException{
		LinkedList<String> l = new LinkedList<String>();
		BufferedReader bf = new BufferedReader(new InputStreamReader(inp,encoding));
		String line;
		while((line = bf.readLine())!=null){
			l.add(line);
		}
		int i = 0;
		String[] ret = new String[l.size()];
		for(String s : l) ret[i++] = s;
		return ret;
	}

	public static void copyFromDir(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Get the file size on HDFS
	 * IF the path is normal file, the size will be returned,
	 * if the path is a directory, the size of all children will be returned.
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static long getFileSize(String inputFile) throws IOException {
		Path p = new Path(inputFile);
		long count = 0;
		FileStatus status = fs.getFileStatus(p);
		if(!status.isDir()){
			count += status.getLen()*status.getBlockSize();
		}else{
			FileStatus[] child = fs.globStatus(new Path(inputFile + "/*"));
			for(FileStatus f : child){
				count += getFileSize(f.getPath().toString());
			}
		}
		
		return count;
	}

	public static boolean fileExists(String tOutput) {
		try {
			return fs.exists(new Path(tOutput));
		} catch (IOException e) {
			return false;
		}
	}
	
}
