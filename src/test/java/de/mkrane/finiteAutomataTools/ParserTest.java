package de.mkrane.finiteAutomataTools;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import de.mkrane.finiteAutomataTools.parser.ParseException;
import de.mkrane.finiteAutomataTools.parser.Parser;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParserTest extends TestCase {

  public ParserTest(String testName) {
    super(testName);
  }

  public static TestSuite suite() {
    return new TestSuite(ParserTest.class);
  }

  @Test
  public void testSimple() throws UnsupportedEncodingException, ParseException {
    String[] testsSimple = { "a", "ab", "a|b", "a*" };
    for (String s : testsSimple) {
      String result = Parser.parse(s).toString();
      assertEquals(s + " but was " + result, s, result);
    }
  }

  @Test
  public void testAlt() throws UnsupportedEncodingException, ParseException {
    String[] testsSimple = { "a*|b", "ab|c", "a|b|c" };
    for (String s : testsSimple) {
      String result = Parser.parse(s).toString();
      assertEquals(s + " but was " + result, s, result);
    }
  }

  @Test
  public void testIter() throws UnsupportedEncodingException, ParseException {
    String[] testsSimple = { "(ab)*", "(a|b)*", "(ab*)*" };
    for (String s : testsSimple) {
      String result = Parser.parse(s).toString();
      assertEquals(s + " but was " + result, s, result);
    }
  }

  @Test
  public void testConcat() throws UnsupportedEncodingException, ParseException {
    String[] testsSimple = { "ab|bc", "((ab)*cd)*", "daaaff" };
    for (String s : testsSimple) {
      String result = Parser.parse(s).toString();
      assertEquals(s + " but was " + result, s, result);
    }
  }

  @Test
  public void testComplex() throws UnsupportedEncodingException, ParseException {
    String[] testsSimple = { "(a|b)*", "((a|ε)b*)*", "(a|b)*abb(a|b)*", "(a|b)*a(a|b)(a|b)" };
    for (String s : testsSimple) {
      String result = Parser.parse(s).toString();
      assertEquals(s + " but was " + result, s, result);
    }
  }
}
