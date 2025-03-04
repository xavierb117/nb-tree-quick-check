import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NbQuickCheckTest {

  // We will capture System.out in these tests to verify output of print methods
  // You do not need to memorize how to do this, but look at it with curiosity!
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  // Before each test set up a fake system out to capture output
  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(new TeeOutputStream(outContent, originalOut)));
  }

  // After each test go back to the real system out
  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    outContent.reset();
  }


  // Used for testing purposes so you can still see your print statements when debugging
  // You do not need to modify this 
  static class TeeOutputStream extends OutputStream {
    private final OutputStream first;
    private final OutputStream second;

    public TeeOutputStream(OutputStream first, OutputStream second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public void write(int b) {
      try {
        first.write(b);
        second.write(b);
      } catch (Exception e) {
        throw new RuntimeException("Error writing to TeeOutputStream", e);
      }
    }

    @Override
    public void flush() {
      try {
        first.flush();
        second.flush();
      } catch (Exception e) {
        throw new RuntimeException("Error flushing TeeOutputStream", e);
      }
    }

    @Override
    public void close() {
      try {
        first.close();
        second.close();
      } catch (Exception e) {
        throw new RuntimeException("Error closing TeeOutputStream", e);
      }
    }
  }
}
