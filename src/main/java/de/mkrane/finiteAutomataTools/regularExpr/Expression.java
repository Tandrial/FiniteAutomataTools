package de.mkrane.finiteAutomataTools.regularExpr;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mkrane.finiteAutomataTools.finiteAutomata.DFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.FiniteAutomata;
import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.State;
import de.mkrane.finiteAutomataTools.finiteAutomata.StateCollection;

public abstract class Expression {

  protected static int                        nextID          = 1;
  protected static Map<Integer, Set<Integer>> followPosLookUp = new HashMap<>();;
  protected static Map<String, Set<Integer>>  literalLookUp   = new HashMap<>();;

  private StringBuilder                       logger          = FiniteAutomata.logger;

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

  public DFA toDFA() {
    DFA dfa = new DFA();
    dfa.addToAlphabet(this.getAlphabet());
    Expression.followPosLookUp.clear();
    Expression.literalLookUp.clear();

    Expression end = new Literal("†");

    Expression root = new Concat(this, end);
    root.setId();
    root.calcFollowPos();
    logger.append("[*] followPos:\n");
    logger.append(Expression.followPosLookUp + "\n");

    StateCollection S_D = new StateCollection();
    // initialize S_D to contain only the unmarked state firstpos(root),
    State start = State.mergeStates(root.getFirstPos());
    start.setInitial(true);
    S_D.add(start);

    logger.append("[*] Initial S_D = " + S_D + '\n');

    Deque<State> queue = new ArrayDeque<>();
    queue.add(start);

    // while (es gibt einen unmarkierten Zustand T ∈ S_D) {
    while (!queue.isEmpty()) {
      // markiere T;
      State currentState = queue.remove();
      logger.append("[*] Checking [" + currentState + "]\n");

      // for (jedes Eingabesymbol a ∈ Σ) {
      for (String c : dfa.getAlphabet()) {

        // U := VereinigungsMenge von followpos({Literals.pos mit a als
        // Zeichne})
        logger.append(String.format("\tT:= combine(DTran([%s], '%s')) =", currentState, c));
        Set<Integer> followResult = new HashSet<>();
        for (Integer follow : currentState.getIncludedIds()) {
          if (literalLookUp.get(c).contains(follow)) {
            followResult.addAll(followPosLookUp.get(Integer.valueOf(follow)));
            logger.append(" followpos(" + follow + ") +");
          }
        }
        logger.delete(logger.length() - 1, logger.length());

        State U = S_D.getStateByIds(followResult);
        if (U == null)
          U = State.mergeStates(followResult);

        // δ_D(T, a) := U;
        dfa.addTransition(currentState, c, U.asSet());
        logger.append(String.format("=> [%s]%n", U.getName()));
        // Check if final state
        if (U.getIncludedIds().contains(Integer.valueOf(end.id)))
          U.setFinal(true);

        // if (U ∉ S_D) {
        if (!S_D.contains(U)) {
          // füge U als unmarkierten Zustand zu SD hinzu;
          S_D.add(U);
          queue.add(U);
        }
      }
    }
    logger.append("[*] States and Lambda done\n");
    dfa.addStates(S_D);

    logger.append("[*] Renaming States\n");
    dfa.renameStates();

    logger.append("[*] Done\n");
    logger.append(dfa);
    System.out.println(logger.toString());
    return dfa;
  }

  public void printDebug(NFA nfa) {
    System.out.println("[*] " + this.getClass().getSimpleName() + " with L( " + this + " ):");
    System.out.println(nfa);
  }
}
