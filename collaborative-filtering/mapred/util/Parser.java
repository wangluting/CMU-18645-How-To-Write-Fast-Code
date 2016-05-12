package mapred.util;

import java.util.ListIterator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.ParseException;

public class Parser extends BasicParser {

	private boolean ignoreUnrecognizedOption;

	public Parser(final boolean ignoreUnrecognizedOption) {
		this.ignoreUnrecognizedOption = ignoreUnrecognizedOption;
	}

	@Override
	protected void processOption(final String arg, final ListIterator iter)
			throws ParseException {
		boolean hasOption = getOptions().hasOption(arg);

		if (hasOption || !ignoreUnrecognizedOption) {
			super.processOption(arg, iter);
		}
	}

}