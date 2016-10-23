package de.mkrane.finiteAutomataTools.regularExpr;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.State;

public class Alternative extends Expression {

  Expression t1;
  Expression t2;

  public Alternative(Expression t1, Expression t2) {
    this.t1 = t1;
    this.t2 = t2;
  }

  @Override
  public String toString() {
    return t1.toString() + "|" + t2.toString();
  }

  @Override
  public NFA toNFA(boolean debugOutput) {
    NFA n1 = t1.toNFA(debugOutput);
    NFA n2 = t2.toNFA(debugOutput);

    NFA result = new NFA();
    result.addToAlphabet(NFA.eps);

    State neuStart = new State("neuStart");
    neuStart.setInitial(true);
    result.addState(neuStart);

    State neuFinal = new State("neuFinal");
    neuFinal.setFinal(true);
    result.addState(neuFinal);

    for (NFA n : new NFA[] { n1, n2 }) {
      State oldStart = n.getStartState();
      oldStart.setInitial(false);

      result.addToAlphabet(n.getAlphabet());
      result.addStates(n.getStates());

      result.addTransition(neuStart, NFA.eps, oldStart.asSet());

      for (State state : n.getStates()) {
        Map<String, Set<State>> bla = n.getPossibleTransitions(state);
        for (Entry<String, Set<State>> entry : bla.entrySet()) {
          result.addTransition(state, entry.getKey(), entry.getValue());
        }
      }

      Set<State> oldFinal = n.getFinalStates();
      for (State s : oldFinal) {
        s.setFinal(false);
        result.addTransition(s, NFA.eps, neuFinal.asSet());
      }
    }

    result.renameStates();

    if (debugOutput)
      printDebug(result);
    return result;
  }

  @Override
  public Set<String> getAlphabet() {
    Set<String> result = new HashSet<>(t1.getAlphabet());
    result.addAll(t2.getAlphabet());
    return result;
  }

  @Override
  public boolean isNullable() {
    return t1.isNullable() || t2.isNullable();
  }

  @Override
  public Set<Integer> getFirstPos() {
    if (firstPos != null)
      return firstPos;
    firstPos = new HashSet<>(t1.getFirstPos());
    firstPos.addAll(t2.getFirstPos());
    return firstPos;
  }

  @Override
  public Set<Integer> getLastPos() {
    return getFirstPos();
  }

  @Override
  protected void setId() {
    t1.setId();
    t2.setId();
  }

  @Override
  protected void calcFollowPos() {
    t1.calcFollowPos();
    t2.calcFollowPos();
  }
}
