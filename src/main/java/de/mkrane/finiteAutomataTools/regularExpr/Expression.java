package de.mkrane.finiteAutomataTools.regularExpr;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mkrane.finiteAutomataTools.finiteAutomata.DFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.State;
import de.mkrane.finiteAutomataTools.finiteAutomata.StateCollection;
import de.mkrane.finiteAutomataTools.parser.ParseException;
import de.mkrane.finiteAutomataTools.parser.Parser;

public abstract class Expression {

  private static final String                 endMarker = "†";

  protected static int                        nextID    = 1;
  protected static Map<Integer, Set<Integer>> followPosLookUp;
  protected static Map<String, Set<Integer>>  literalLookUp;

  protected static int getNextID() {
    return nextID++;
  }

  protected int          id       = -1;
  protected Set<Integer> firstPos = null;
  protected Set<Integer> lastPos  = null;

  public abstract NFA toNFA(boolean debugOutput);

  protected abstract boolean isNullable();

  protected abstract Set<Integer> getFirstPos();

  protected abstract Set<Integer> getLastPos();

  protected abstract Set<String> getAlphabet();

  protected abstract void calcFollowPos();

  protected abstract void setId();

  public DFA toDFA(boolean debugOutput) {

    DFA dfa = new DFA();
    dfa.addToAlphabet(this.getAlphabet());
    Expression.followPosLookUp = new HashMap<>();
    Expression.literalLookUp = new HashMap<>();
    Expression root = new Concat(this, new Literal(endMarker));
    root.setId();
    root.calcFollowPos();
    StateCollection S_D = new StateCollection();
    // initialize S_D to contain only the unmarked state firstpos(root),
    State start = State.mergeStates(root.getFirstPos());
    start.setInitial(true);
    S_D.add(start);
    Deque<State> queue = new ArrayDeque<>();
    queue.add(start);
    dfa.getStates().resetMarked();

    // while (es gibt einen unmarkierten Zustand T ∈ SD) {
    while (!queue.isEmpty()) {
      // markiere T;
      State currentState = queue.remove();

      // // for (jedes Eingabesymbol a ∈ Σ) {
      for (String c : dfa.getAlphabet()) {

        // U := VereinigungsMenge von followpos({Literals.pos mit a als
        // Zeichne})
        Set<Integer> followResult = new HashSet<>();

        for (Integer follow : currentState.getIncludedIds()) {
          if (literalLookUp.get(c).contains(follow)) {
            followResult.addAll(followPosLookUp.get(Integer.valueOf(follow)));
          }
        }
        State U = S_D.getStateByIds(followResult);
        if (U == null)
          U = State.mergeStates(followResult);
        // δD(T, a) := U;
        dfa.addTransition(currentState, c, U.asSet());

        // Check if final state
        if (U.getIncludedIds().contains(literalLookUp.get(Expression.endMarker).iterator().next()))
          U.setFinal(true);

        // if (U ∉ SD) {
        if (!S_D.contains(U)) {
          // füge U als unmarkierten Zustand zu SD hinzu;
          S_D.add(U);
          queue.add(U);
        }
      }

    }
    dfa.addStates(S_D);

    dfa.renameStates();
    return dfa;
  }

  public void printDebug(NFA nfa) {
    System.out.println("[*] " + this.getClass().getSimpleName() + " with L( " + this + " ):");
    System.out.println(nfa);
  }

  public static void main(String[] args) {

    try {
      String expr = "(a|b)*abb";
      Expression tree = Parser.parse(expr);
      System.out.println(tree.toDFA(false));
    } catch (UnsupportedEncodingException | ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
