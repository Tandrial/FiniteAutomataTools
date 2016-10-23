package de.mkrane.finiteAutomataTools;

import de.mkrane.finiteAutomataTools.finiteAutomata.FiniteAutomata;

public class Blatt02 {

  public static void main(String[] args) {
    String[] tests = new String[] { "", "aaa", "aaabbab", "abababbbbaa", "bbaabaababbbabababab", "ab", "bb",
        "ababababababababab" };
    if (FiniteAutomata.convertRegExTominDFA("(a|b)*", "Aufgabe_2_4_a_", tests) == null)
      System.out.println("fail in 2_4_a");
    if (FiniteAutomata.convertRegExTominDFA("((a|ε)b*)*", "Aufgabe_2_4_b_", tests) == null)
      System.out.println("fail in 2_4_b");
    if (FiniteAutomata.convertRegExTominDFA("(a|b)*abb(a|b)*", "Aufgabe_2_4_c_", tests) == null)
      System.out.println("fail in 2_4_c");
    if (FiniteAutomata.convertRegExTominDFA("(a|b)*a(a|b)(a|b)", "Aufgabe_2_4_d_", tests) == null)
      System.out.println("fail in 2_4_d");
    System.out.println("done");
  }
}
