package mapred.util;

import java.util.ListIterator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

/**
 * A simplified command line option parser. Get the option value on the run.
 * Pitfall is it ignores all unrecognized options.
 * 
 * @author Dongzhen Piao
 * 
 */
public class SimpleParser {

	String[] args;

	public SimpleParser(String[] args) {
		this.args = args;
	}

	public String get(String option) {
		Parser parser = new IgnoreUnrecognizedOptionParser();
		Options options = new Options();
		options.addOption(option, true, option);
		CommandLine line = null;
		String value = null;
		try {
			line = parser.parse(options, args);
			value = line.getOptionValue(option);
			if (value == null)
				throw new ParseException("Option value for " + option + " is not found.");
		} catch (ParseException e) {
			System.err.println("Option value for " + option + " is not found.");
			System.exit(1);
			//e.printStackTrace();
		}
		
		return value;
	}
	
	public Integer getInt(String option) {
		return Integer.parseInt(get(option));
	}
	
	public double getDouble(String option) {
		return Double.parseDouble(get(option));
	}
	
	public Boolean getBoolean(String option) {
		return Boolean.parseBoolean(get(option));
	}

	class IgnoreUnrecognizedOptionParser extends BasicParser {
		@Override
		protected void processOption(final String arg, final ListIterator iter)
				throws ParseException {
			boolean hasOption = getOptions().hasOption(arg);
			if (hasOption) {
				super.processOption(arg, iter);
			}
		}

	}
	/**
	 * Example usage
	 */
	public static void main(String[] args) {
		String option = "abc";
		System.out.println("The option value for " + option + " is " + new SimpleParser(args).get(option));
	}

}

