package mapred.util;
/*
License for Java 1.5 'Tiger': A Developer's Notebook
     (O'Reilly) example package

Java 1.5 'Tiger': A Developer's Notebook (O'Reilly) 
by Brett McLaughlin and David Flanagan.
ISBN: 0-596-00738-8

You can use the examples and the source code any way you want, but
please include a reference to where it comes from if you use it in
your own products or services. Also note that this software is
provided by the author "as is", with no expressed or implied warranties. 
In no event shall the author be liable for any direct or indirect
damages arising in any way out of the use of this software.
*/

import java.util.Iterator;
import java.io.*;

/**
 * This class allows line-by-line iteration through a text file.
 * The iterator's remove() method throws UnsupportedOperatorException.
 * The iterator wraps and rethrows IOExceptions as IllegalArgumentExceptions.
 */
public class InputLines implements Iterable<String> {

  // Used by the InputLinesIterator class below
  InputStream is;

  public InputLines(InputStream is) { 
    this.is = is; 
  }

  // This is the one method of the Iterable interface
  public Iterator<String> iterator() { 
    return new InputLinesIterator(); 
  }


  // This non-static member class is the iterator implementation
  class InputLinesIterator implements Iterator<String> {

    // The stream we're reading from
    BufferedReader in;

    // Return value of next call to next()
    String nextline;

    public InputLinesIterator() {
      // Open the file and read and remember the first line.
      // We peek ahead like this for the benefit of hasNext().
      try {
        in = new BufferedReader(new InputStreamReader(is));
        nextline = in.readLine();
      } catch(IOException e) { 
        throw new IllegalArgumentException(e); 
      }
    }

    // If the next line is non-null, then we have a next line
    public boolean hasNext() { 
      return nextline != null; 
    }

    // Return the next line, but first read the line that follows it.
    public String next() {
      try {
        String result = nextline;

        // If we haven't reached EOF yet
        if (nextline != null) {  
          nextline = in.readLine(); // Read another line
          if (nextline == null) 
            in.close();             // And close on EOF
        }

        // Return the line we read last time through.
        return result;
      } catch(IOException e) { 
        throw new IllegalArgumentException(e); 
      }
    }

    // The file is read-only; we don't allow lines to be removed.
    public void remove() { 
      throw new UnsupportedOperationException(); 
    }
  }

  public static void main(String[] args) {
    for(String line : new InputLines(System.in))
      System.out.println(line);
  }
}
