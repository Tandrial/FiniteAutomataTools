package de.mkrane.finiteAutomataTools;

import java.net.URISyntaxException;
import java.util.Arrays;

import de.mkrane.finiteAutomataTools.finiteAutomata.FiniteAutomata;

public class Main {
  public static void main(String[] args) throws URISyntaxException {

    if (args.length < 1) {

      System.out.println("Usage : java -jar "
          + new java.io.File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getName()
          + " <OutputFileName> \"<RegExpression>\" [<TestCases>]*");
    } else {
      String name = args[0] + "_";
      String expr = args[1];
      String[] testCases = Arrays.copyOfRange(args, 1, args.length);
      FiniteAutomata.convertRegExTominDFA(expr, name, testCases);
      System.out.println("done");
    }
  }
}
