package de.mkrane.finiteAutomataTools.regularExpr;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.State;

public class Concat extends Expression {

  Expression t1;
  Expression t2;

  public Concat(Expression t1, Expression t2) {
    this.t1 = t1;
    this.t2 = t2;
  }

  @Override
  public String toString() {
    String result = "";
    if (t1 instanceof Alternative)
      result += "(" + t1.toString() + ")";
    else
      result += t1.toString();
    if (t2 instanceof Alternative)
      result += "(" + t2.toString() + ")";
    else
      result += t2.toString();
    return result;
  }

  @Override
  public NFA toNFA(boolean debugOutput) {
    NFA n1 = t1.toNFA(debugOutput);
    NFA n2 = t2.toNFA(debugOutput);

    NFA result = new NFA();
    result.getAlphabet().add(NFA.eps);

    for (NFA n : new NFA[] { n1, n2 }) {
      result.getAlphabet().addAll(n.getAlphabet());
      result.addStates(n.getStates());

      for (State state : n.getStates()) {
        Map<String, Set<State>> bla = n.getPossibleTransitions(state);
        for (Entry<String, Set<State>> entry : bla.entrySet()) {
          result.addTransition(state, entry.getKey(), entry.getValue());
        }
      }
    }

    State n2OldStart = n2.getStartState();
    n2OldStart.setInitial(false);
    Set<State> n1OldFinal = n1.getFinalStates();

    for (State s : n1OldFinal) {
      s.setFinal(false);
      result.addTransition(s, NFA.eps, n2OldStart.asSet());
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
    return t1.isNullable() && t2.isNullable();
  }

  @Override
  public Set<Integer> getFirstPos() {
    if (firstPos != null)
      return firstPos;
    firstPos = new HashSet<>(t1.getFirstPos());
    if (t1.isNullable())
      firstPos.addAll(t2.getFirstPos());
    return firstPos;
  }

  @Override
  public Set<Integer> getLastPos() {
    if (lastPos != null)
      return lastPos;
    lastPos = new HashSet<>(t2.getFirstPos());
    if (t2.isNullable())
      lastPos.addAll(t1.getFirstPos());
    return lastPos;
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
    // If n is a cat-node with left child c1 and right child c2, then for every
    // position i in lastpos(c_1), all positions in firstpos(c_2) are in
    // followpos(i).

    // look at each cat-node, and put each position in firrstpos of its right
    // child in followpos for each position in lastpos of its left child.
    for (Integer follow : t2.getFirstPos()) {
      for (Integer last : t1.getLastPos()) {
        if (!followPosLookUp.containsKey(last)) {
          followPosLookUp.put(last, new HashSet<>());
        }
        followPosLookUp.get(last).add(follow);
      }
    }

  }
}
