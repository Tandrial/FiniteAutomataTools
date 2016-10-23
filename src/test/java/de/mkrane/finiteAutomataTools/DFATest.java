package de.mkrane.finiteAutomataTools;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.mkrane.finiteAutomataTools.finiteAutomata.DFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.FiniteAutomata;
import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;
import de.mkrane.finiteAutomataTools.parser.Parser;
import de.mkrane.finiteAutomataTools.regularExpr.Expression;

public class DFATest {

  private static final String[] tests = new String[] { "", "aaa", "aaabbab", "abababbbbaa", "bbaabaababbbabababab",
      "ab", "bb", "ababababababababab" };

  private static final String[] exprs = new String[] { "(a|b)*", "((a|ε)b*)*", "(a|b)*abb(a|b)*", "(a|b)*a(a|b)(a|b)" };

  @Test
  public void testConvertNFAToDFA() {
    for (String expr : exprs) {
      try {
        Expression e = Parser.parse(expr);
        NFA nfa = e.toNFA(false);
        DFA dfa = nfa.convertToDFA();
        assertTrue(FiniteAutomata.sameLanguage(nfa, dfa, tests));
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  @Test
  public void testMinimizeDFA() {
    for (String expr : exprs) {
      try {
        Expression e = Parser.parse(expr);
        NFA nfa = e.toNFA(false);
        DFA dfa = nfa.convertToDFA();
        DFA dfa_min = dfa.minimize();
        assertTrue(FiniteAutomata.sameLanguage(nfa, dfa_min, tests));
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }

  @Test
  public void testDirectToDFA() {
    for (String expr : exprs) {
      try {
        Expression e = Parser.parse(expr);
        NFA nfa = e.toNFA(false);
        DFA dfa_direkt = e.toDFA(false);
        assertTrue(expr + " failed to correctly convert!", FiniteAutomata.sameLanguage(nfa, dfa_direkt, tests));
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
  }
}
