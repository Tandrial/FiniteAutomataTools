package de.mkrane.finiteAutomataTools.regularExpr;

import java.util.HashSet;
import java.util.Set;

import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.State;

public class Iteration extends Expression {

  Expression t1;

  public Iteration(Expression t1) {
    this.t1 = t1;
  }

  @Override
  public String toString() {
    if (t1 instanceof Literal)
      return t1.toString() + "*";
    else
      return "(" + t1 + ")*";
  }

  @Override
  public NFA toNFA(boolean debugOutput) {
    NFA result = t1.toNFA(debugOutput);
    State oldStart = result.getStartState();
    oldStart.setInitial(false);
    Set<State> oldFinal = result.getFinalStates();

    State neuStart = new State("neuStart");
    neuStart.setInitial(true);
    State neuFinal = new State("neuFinal");
    neuFinal.setFinal(true);

    result.addState(neuStart);
    result.addState(neuFinal);
    result.getAlphabet().add(NFA.eps);

    for (State s : oldFinal) {
      s.setFinal(false);
      result.addTransition(s, NFA.eps, oldStart.asSet());
      result.addTransition(s, NFA.eps, neuFinal.asSet());
    }

    result.addTransition(neuStart, NFA.eps, neuFinal.asSet());
    result.addTransition(neuStart, NFA.eps, oldStart.asSet());

    result.renameStates();

    if (debugOutput)
      printDebug(result);
    return result;
  }

  @Override
  public Set<String> getAlphabet() {
    return new HashSet<>(t1.getAlphabet());
  }

  @Override
  public boolean isNullable() {
    return true;
  }

  @Override
  public Set<Integer> getFirstPos() {
    if (firstPos != null)
      return firstPos;
    firstPos = t1.getFirstPos();
    return firstPos;
  }

  @Override
  public Set<Integer> getLastPos() {
    return getFirstPos();
  }

  @Override
  protected void setId() {
    t1.setId();
  }

  @Override
  protected void calcFollowPos() {
    t1.calcFollowPos();
    // If n is a star-node, and i is a position in lastpos(n), then all
    // positions in firstpos(n) are in followpos(i).
    for (Integer last : t1.getLastPos()) {
      if (!followPosLookUp.containsKey(last)) {
        followPosLookUp.put(last, new HashSet<>());
      }
      followPosLookUp.get(last).addAll(t1.getFirstPos());
    }
  }
}
