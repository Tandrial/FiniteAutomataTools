package de.mkrane.finiteAutomataTools.finiteAutomata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import de.mkrane.finiteAutomataTools.parser.Parser;
import de.mkrane.finiteAutomataTools.regularExpr.Expression;

public abstract class FiniteAutomata {

  protected static StringBuilder logger   = new StringBuilder();

  protected Set<String>          alphabet = new HashSet<>();
  private StateCollection        states   = new StateCollection();
  protected TransitionTable      lambda   = new TransitionTable();

  public abstract boolean simulate(String word);

  public void addToAlphabet(String s) {
    alphabet.add(s);
  }

  public void addToAlphabet(Collection<String> alpha) {
    alphabet.addAll(alpha);
  }

  public Set<String> getAlphabet() {
    return alphabet;
  }

  public void addState(State state) {
    this.getStates().add(state);
  }

  public void addStates(StateCollection states) {
    this.getStates().addAll(states);
  }

  public StateCollection getStates() {
    return states;
  }

  public State getStartState() {
    return getStates().getStartState();
  }

  public Set<State> getFinalStates() {
    return getStates().getFinalStates();
  }

  public void addTransition(State s, String c, Set<State> g) {
    Transition t = new Transition(s, c, g);
    lambda.addTransition(t);
  }

  public Map<String, Set<State>> getPossibleTransitions(State state) {
    return lambda.getPossibleTransitions(state);
  }

  protected Set<State> move(Set<State> currentState, String c) {
    Set<State> result = new HashSet<>();

    for (State state : currentState) {
      Map<String, Set<State>> possibleTransitions = lambda.getPossibleTransitions(state);
      if (possibleTransitions.containsKey(c))
        result.addAll(possibleTransitions.get(c));
    }
    return result;
  }

  protected boolean containsEndstate(Set<State> states) {
    return states.stream().anyMatch(s -> s.isFinal());
  }

  public void renameStates() {
    states.resetMarked();

    String fmtString = "q%d";
    if (states.size() >= 10)
      fmtString = "q%02d";
    else if (states.size() >= 100)
      fmtString = "q%03d";

    Queue<State> queue = new PriorityQueue<>(states.size(), (s1, s2) -> Long.compare(s1.getId(), s2.getId()));
    queue.add(this.getStartState());
    int cnt = 1;
    while (!queue.isEmpty()) {
      State state = queue.remove();
      if (state.isMarked())
        continue;
      state.setMarked(true);
      state.setName(String.format(fmtString, cnt++));

      for (Set<State> goals : this.getPossibleTransitions(state).values())
        queue.addAll(goals);
    }
  }

  public static DFA convertRegExTominDFA(String expr, String name, String[] testCases) {
    logger = new StringBuilder();
    logger.append("Builder minimal DFA from Expression: " + expr + "\n");
    logger.append("[+] Parsing expression\n");
    logger.append("    in  = " + expr + "\n");
    Expression e = null;
    try {
      e = Parser.parse(expr);
    } catch (Exception e1) {
      e1.printStackTrace();
      logger.append("[+] Parse failed\n");
      logger.append("\t P(in) = " + e.toString() + "\n");
    }
    logger.append("[+] Parse successful\n");
    logger.append("  P(in) = " + e.toString() + "\n");

    logger.append("[+] Building NFA\n");
    NFA nfa = e.toNFA(false);
    logger.append("[+] NFA done");
    logger.append(nfa);

    logger.append("[+] Converting to DFA\n");
    DFA dfa = nfa.convertToDFA();
    if (!sameLanguage(nfa, dfa, testCases)) {
      logger.append("[+] Conversion to DFA failed!\n");
      saveSBToFile(logger, "Log.txt");
      return null;
    }
    logger.append(dfa);

    logger.append("[+] Minimizing DFA\n");
    DFA dfa_min = dfa.minimize();

    if (!sameLanguage(dfa, dfa_min, testCases)) {
      logger.append("[+] Minimazation of DFA failed!\n");
      saveSBToFile(logger, "Log.txt");
      return null;
    }
    logger.append(dfa_min);

    DFA dfa_direkt = e.toDFA(false);

    if (!sameLanguage(dfa_direkt, dfa_min, testCases)) {
      logger.append("[+] Direkt creation from RegularExpression failed!\n");
      saveSBToFile(logger, "Log.txt");
      return null;
    }

    logger.append("[+] All tests passed! Conversion and minimization are correct!\n");
    logger.append("[+] Creating .dot and .png files of all FAs generated!\n");
    String[] graphNames = { "DFA_minimiert", "DFA", "DFA_direkt", "NFA" };
    FiniteAutomata[] fas = new FiniteAutomata[] { dfa_min, dfa, nfa };

    String dotLocation;
    if (System.getProperty("os.name").equals("Windows 7"))
      dotLocation = "C:\\grahpviz\\bin\\dot.exe";
    else
      dotLocation = "dot";

    try {
      FiniteAutomata.saveToDotFile(fas, graphNames, name, name + e.toString());
      Runtime.getRuntime().exec(dotLocation + String.format(" %1$s.dot -Tpng -o %1$s.png", name));
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    logger.append("[+] Done\n");

    saveSBToFile(logger, name + ".log");
    return dfa_min;
  }

  public static void saveToDotFile(FiniteAutomata[] fas, String[] clusterNames, String fileName, String graphName)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    sb.append("digraph fs {\n");
    sb.append("\trankdir=LR;\n");
    sb.append("\tfontsize=20;\n");
    sb.append("\tnewrank=true");
    sb.append("\tlabel = \"" + graphName + "\";\n");
    sb.append("\t\tnode [shape=circle];\n");
    String ranks = "{ rank=same; ";
    for (FiniteAutomata fa : fas) {
      if (fa == null)
        continue;
      sb.append("\n\tsubgraph cluster_" + clusterNames[i] + " {\n");
      sb.append("\t\tlabel = \"" + clusterNames[i] + "\";\n");
      sb.append("\t\tgraph [ dpi = 1200 ];\n");

      // List with all endStates
      for (State s : fa.getStates().getFinalStates())
        sb.append(String.format("\t\t{%1$s%2$d [label=\"%1$s\", shape=doublecircle]};\n", s, i));

      // hidden node to get an arrow to point to the Start-Node
      sb.append("\t\tsecret_node" + i + " [style=invis, shape=point, fixedsize=true, width=.6]\n");

      String startName = fa.getStates().getStartState().toString();
      sb.append(String.format("\t\tsecret_node%1$d -> {%2$s%1$d [label=\"%2$s\"]};%n", i, startName));
      ranks += String.format("%s%d; ", startName, i);
      // Transitions for all the nodes
      for (State s : fa.getStates()) {
        Map<String, Set<State>> moves = fa.lambda.getPossibleTransitions(s);
        for (Entry<String, Set<State>> entry : moves.entrySet()) {
          for (State g : entry.getValue()) {
            sb.append(
                String.format("\t\t{%1$s%2$d [label=\"%1$s\"]} -> {%3$s%2$d [label=\"%3$s\"]} [label = \"%4$s\"];%n", s,
                    i, g, entry.getKey()));
          }
        }
      }
      sb.append("\t}\n");
      i++;
    }
    sb.append(ranks);
    sb.append("}\n}\n");

    saveSBToFile(sb, fileName + ".dot");
  }

  public static void saveSBToFile(StringBuilder sb, String fileName) {
    File file = new File(fileName);

    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
      writer.write(sb.toString());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (writer != null)
        try {
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  public static boolean sameLanguage(FiniteAutomata f1, FiniteAutomata f2, String[] testCases) {
    for (String test : testCases) {
      boolean result_f1 = f1.simulate(test);
      boolean result_f2 = f2.simulate(test);
      if (result_f1 != result_f2)
        return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(S, Σ, δ, s0, F)\n");

    sb.append("S = ");
    List<State> states = new ArrayList<>(this.getStates());
    Collections.sort(states, (s1, s2) -> s1.getName().compareTo(s2.getName()));
    sb.append(Arrays.toString(states.toArray()));
    sb.append("\n");

    sb.append("Σ = ");
    sb.append(Arrays.toString(this.alphabet.toArray()));
    sb.append("\n");

    sb.append("δ = [");
    sb.append(this.lambda);
    sb.delete(sb.length() - 6, sb.length());
    sb.append("]\n");

    sb.append("s0 = ");
    sb.append(this.getStartState());
    sb.append("\n");

    sb.append("F = ");
    List<State> finals = new ArrayList<>(this.getFinalStates());
    Collections.sort(finals, (s1, s2) -> s1.getName().compareTo(s2.getName()));
    sb.append(Arrays.toString(finals.toArray()));
    sb.append("\n");

    return sb.toString();
  }
}
