package de.mkrane.finiteAutomataTools.finiteAutomata;

import java.util.HashSet;
import java.util.Set;

public class State {

  public static <T> State mergeStates(Set<T> states) {
    StringBuilder sb = new StringBuilder();
    boolean neuIsStart = false;
    boolean neuIsFinal = false;
    Set<State> includedStates = new HashSet<>();
    Set<Integer> includedIds = new HashSet<>();
    for (T o : states) {
      if (o instanceof State) {
        State state = (State) o;
        if (state.isInitial())
          neuIsStart = true;
        if (state.isFinal())
          neuIsFinal = true;
        sb.append(state);
        includedStates.add(state);
      } else if (o instanceof Integer) {
        Integer id = (Integer) o;
        includedIds.add(id);
        sb.append(id);
      }
    }

    State neu;
    if (includedStates.size() > 0)
      neu = new State(sb.toString(), neuIsStart, neuIsFinal, includedStates);
    else
      neu = new State(sb.toString(), includedIds);
    return neu;
  }

  public static State fromPosSet(Set<Integer> firstPos) {
    State neu = new State(firstPos.toString());
    return neu;
  }

  public static String getMergedName(Set<State> states) {
    StringBuilder sb = new StringBuilder();
    for (State state : states) {
      sb.append(state);
    }
    return sb.toString();
  }

  private static long nextID = 1;

  private static long getNextID() {
    return nextID++;
  }

  private String       name;
  private long         id;
  private boolean      isInitial      = false;
  private boolean      isFinal        = false;
  private boolean      marked         = false;

  private Set<State>   includedStates = new HashSet<>();
  private Set<Integer> includedIds    = new HashSet<>();

  public State(String name, boolean isInitial, boolean isFinal, Set<State> includedStates) {
    this(name);
    this.isInitial = isInitial;
    this.isFinal = isFinal;
    this.includedStates.addAll(includedStates);
  }

  public State(String name, Set<Integer> includedIds) {
    this(name);
    this.includedIds = includedIds;
  }

  public State(String name) {
    this.name = name;
    this.id = getNextID();
  }

  public boolean isInitial() {
    return isInitial;
  }

  public void setInitial(boolean isInitial) {
    this.isInitial = isInitial;
  }

  public boolean isFinal() {
    return isFinal;
  }

  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  public boolean isMarked() {
    return marked;
  }

  public void setMarked(boolean marked) {
    this.marked = marked;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public Set<State> getIncludedStates() {
    return includedStates;
  }

  public Set<Integer> getIncludedIds() {
    return includedIds;
  }

  public Set<State> asSet() {
    Set<State> result = new HashSet<>();
    result.add(this);
    return result;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof State)) {
      return false;
    }
    State other = (State) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }
}
