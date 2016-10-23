package de.mkrane.finiteAutomataTools.regularExpr;

import java.util.HashSet;
import java.util.Set;

import de.mkrane.finiteAutomataTools.finiteAutomata.NFA;
import de.mkrane.finiteAutomataTools.finiteAutomata.State;

public class Literal extends Expression {

  String c;

  public Literal(String s) {
    this.c = s;
  }

  @Override
  public String toString() {
    return c;
  }

  @Override
  public NFA toNFA(boolean debugOutput) {

    State neuStart = new State("neuStart");
    neuStart.setInitial(true);
    State neuFinal = new State("neuFinal");
    neuFinal.setFinal(true);

    NFA result = new NFA();

    result.addToAlphabet(c);
    result.addState(neuStart);
    result.addState(neuFinal);

    result.addTransition(neuStart, c, neuFinal.asSet());

    result.renameStates();

    return result;
  }

  @Override
  public Set<String> getAlphabet() {
    Set<String> result = new HashSet<>();
    result.add(c);
    return result;
  }

  @Override
  public boolean isNullable() {
    return c.equals("\u03b5");
  }

  @Override
  public Set<Integer> getFirstPos() {
    Set<Integer> result = new HashSet<>();
    if (!c.equals("\u03b5"))
      result.add(Integer.valueOf(id));
    return result;
  }

  @Override
  public Set<Integer> getLastPos() {
    return getFirstPos();
  }

  @Override
  protected void setId() {
    if (!c.equals("\u03b5"))
      this.id = getNextID();
  }

  @Override
  protected void calcFollowPos() {
    if (!literalLookUp.containsKey(c)) {
      literalLookUp.put(c, new HashSet<>());
    }
    literalLookUp.get(c).add(Integer.valueOf(this.id));
  }
}
