package de.mkrane.finiteAutomataTools.regularExpr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.mkrane.finiteAutomataTools.finiteAutomata.DFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;

public abstract class Expression {

  protected static long                       nextID = 0;
  protected static Map<Integer, Set<Integer>> followPos;

  protected static long getNextID() {
    return nextID++;
  }

  protected long         id       = -1;
  protected Set<Integer> firstPos = null;
  protected Set<Integer> lastPos  = null;

  public abstract NFA toNFA(boolean debugOutput);

  protected abstract boolean isNullable();

  protected abstract Set<Integer> getFirstPos();

  protected abstract Set<Integer> getLastPos();

  protected abstract Set<String> getAlphabet();

  protected void calcFollowPos() {
  }

  protected abstract void setId();

  public static DFA toDFA(Expression root, boolean debugOutput) {
    Expression.followPos = new HashMap<>();
    root.setId();
    root.calcFollowPos();

    DFA dfa = new DFA();
    // dfa.addToAlphabet(root.getAlphabet());
    //
    // StateCollection S_D = new StateCollection();
    // // initialize S_D to contain only the unmarked state firstpos(root),
    // State start = State.mergeStates(root.getFirstPos());
    // start.setInitial(true);
    // S_D.add(start);
    // Deque<State> queue = new ArrayDeque<>();
    // queue.add(start);
    // dfa.getStates().resetMarked();
    //
    // // while (es gibt einen unmarkierten Zustand T ∈ SD) {
    // while (!queue.isEmpty()) {
    // // markiere T;
    // State currentState = queue.remove();
    //
    // // // for (jedes Eingabesymbol a ∈ Σ) {
    // for (String c : dfa.getAlphabet()) {
    //
    // // U := VereinigungsMenge von followpos({Literals.pos mit a als
    // // Zeichne})
    // Set<Integer> followResult = epsilonClosure(currentState);
    // State U = S_D.getStateByIds(followResult);
    // if (U == null)
    // U = State.mergeStates(currentState.getIncludedIds());
    // // δD(T, a) := U;
    // dfa.addTransition(currentState, c, U.asSet());
    //
    // // if (U ∉ SD) {
    // if (!S_D.contains(U)) {
    // // füge U als unmarkierten Zustand zu SD hinzu;
    // S_D.add(U);
    // queue.add(U);
    // }
    // }
    //
    // }
    // dfa.addStates(S_D);
    //
    // dfa.renameStates();

    return dfa;

  }

  public void printDebug(NFA nfa) {
    System.out.println("[*] " + this.getClass().getSimpleName() + " with L( " + this + " ):");
    System.out.println(nfa);
  }
}
