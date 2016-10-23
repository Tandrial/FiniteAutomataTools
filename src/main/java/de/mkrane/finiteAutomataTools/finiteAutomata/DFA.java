package de.mkrane.finiteAutomataTools.finiteAutomata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFA extends FiniteAutomata {

  private DFA dfa_min = null;

  @Override
  public String toString() {
    return "DFA = " + super.toString();
  }

  @Override
  public boolean simulate(String word) {
    Set<State> s = new HashSet<>();
    s.add(this.getStates().getStartState());
    for (char c : word.toCharArray()) {
      s = move(s, String.valueOf(c));
      if (s.isEmpty())
        break;
    }
    return !s.isEmpty() && containsEndstate(s);
  }

  public DFA minimize() {
    if (dfa_min != null)
      return dfa_min;
    logger.append("[*] Building groups\n");

    List<Set<State>> gruppen = buildGroups();

    logger.append("[*] Groups built\n");
    logger.append("[*] Building DFA_min\n");
    dfa_min = new DFA();
    dfa_min.addToAlphabet(this.alphabet);

    logger.append("[*] Creating S_min\n");
    for (Set<State> gruppe : gruppen)
      dfa_min.addState(State.mergeStates(gruppe));

    logger.append("[*] Creating δ_min\n");
    dfa_min.lambda = createLambda(dfa_min.getStates());

    logger.append("[*] Renaming States\n");
    dfa_min.renameStates();

    logger.append("[*] Done\n");
    return dfa_min;
  }

  private List<Set<State>> buildGroups() {
    List<Set<State>> gruppen = new ArrayList<>();
    Set<State> endStates = this.getStates().getFinalStates();
    Set<State> nonEndStates = new HashSet<>(this.getStates());
    nonEndStates.removeAll(endStates);
    gruppen.add(endStates);
    if (nonEndStates.size() > 0)
      gruppen.add(nonEndStates);

    boolean groupWasSplit = false;
    int i = 0;
    do {
      logger.append("\tΠ" + i++ + " = " + gruppen);
      List<Set<State>> gruppenNeu = new ArrayList<>();
      groupWasSplit = false;
      for (Set<State> gruppe : gruppen) {
        if (gruppe.size() == 1) {
          gruppenNeu.add(gruppe);
          continue;
        }
        Set<State> alt = new HashSet<>(gruppe);
        Set<State> neu = new HashSet<>();

        boolean groupNeedstoBeSplit = false;
        for (String c : this.alphabet) {
          Set<Set<State>> allGoals = new HashSet<>();

          for (State s : gruppe) {
            Set<State> goal = getGroupWhichIncludes(s, c, gruppen);
            assert (goal != null);
            if (allGoals.size() == 0) {
              allGoals.add(goal);
            } else if (!allGoals.contains(goal)) {
              if (!groupNeedstoBeSplit)
                logger.append("\n\t\tSplitting " + gruppe);
              groupNeedstoBeSplit = true;

              neu.add(s);
              alt.remove(s);
            }
          }
        }

        if (groupNeedstoBeSplit) {
          logger.append(" into " + alt + " and " + neu + "\n");
          groupWasSplit = true;
          gruppenNeu.add(neu);
        }
        gruppenNeu.add(alt);
      }
      logger.append('\n');
      gruppen = gruppenNeu;

    } while (groupWasSplit);
    return gruppen;
  }

  private TransitionTable createLambda(StateCollection states) {
    TransitionTable result = new TransitionTable();
    for (State s : states) {
      State start = s.getIncludedStates().iterator().next();
      for (String c : this.alphabet) {
        State goal = findStateWithIncludedState(lambda.getGoalFromTransition(start, c), states);
        if (goal != null)
          result.addTransition(new Transition(s, c, goal.asSet()));
      }
    }
    return result;
  }

  private Set<State> getGroupWhichIncludes(State state, String c, List<Set<State>> gruppen) {
    Set<State> result = null;
    state = lambda.getGoalFromTransition(state, c);
    for (Set<State> gruppe : gruppen) {
      if (gruppe.contains(state)) {
        result = gruppe;
        break;
      }
    }
    return result;
  }

  private State findStateWithIncludedState(State s, Set<State> states) {
    State result = null;
    for (State curr : states) {
      if (curr.getIncludedStates().contains(s)) {
        result = curr;
        break;
      }
    }
    return result;
  }
}
