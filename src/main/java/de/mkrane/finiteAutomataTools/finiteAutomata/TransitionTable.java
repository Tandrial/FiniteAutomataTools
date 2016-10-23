package de.mkrane.finiteAutomataTools.finiteAutomata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransitionTable {

  private Map<State, Transition> table = new HashMap<>();

  protected void addTransition(Transition t) {
    if (table.containsKey(t.getState())) {
      table.get(t.getState()).addTransition(t);
    } else {
      table.put(t.getState(), t);
    }
  }

  protected Map<String, Set<State>> getPossibleTransitions(State state) {
    if (table.containsKey(state))
      return table.get(state).moves;
    else
      return new HashMap<>();
  }

  protected State getGoalFromTransition(State start, String c) {
    return getPossibleTransitions(start).get(c).iterator().next();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    List<State> states = new ArrayList<>(table.keySet());
    Collections.sort(states, (s1, s2) -> s1.getName().compareTo(s2.getName()));
    for (State s : states) {
      sb.append(table.get(s) + "\n     ");
    }
    return sb.toString();
  }
}
