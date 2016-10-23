package de.mkrane.finiteAutomataTools.finiteAutomata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Transition {
  private State                     state;
  protected Map<String, Set<State>> moves = new HashMap<>();

  public State getState() {
    return state;
  }

  public Transition(State state, String c, Set<State> states) {
    this.state = state;
    if (!moves.containsKey(c)) {
      moves.put(c, states);
    } else {
      moves.get(c).addAll(states);
    }
  }

  public void addMove(String c, Set<State> states) {
    if (!moves.containsKey(c))
      moves.put(c, states);
    else
      moves.get(c).addAll(states);
  }

  public void addTransition(Transition t) {
    for (Entry<String, Set<State>> entry : t.moves.entrySet()) {
      if (!moves.containsKey(entry.getKey())) {
        moves.put(entry.getKey(), entry.getValue());
      } else {
        moves.get(entry.getKey()).addAll(entry.getValue());
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Entry<String, Set<State>> move : moves.entrySet())
      sb.append(String.format("[%s] x '%s' → %s\t", state, move.getKey(), Arrays.toString(move.getValue().toArray())));
    return sb.toString().trim();
  }
}
