# FiniteAutomata Tools

Tool created for the "Übersetzerbau" class of my Master "[Internet-Sicherheit]" at the Westfälische Hochschule Gelsenkirchen.

The tool is able to transform a given regular Expression into a minimized DFA. The algorithms used are described in __Compilers: Principles, Techniques, and Tools__, Chapter 3.

Usage:
```java -jar  <OutputFileName> "<RegExpression>" [<TestCases>]*```

__OutputFilename__ is the name of the files (.dot, .png, .log) which will be created

__RegExpression__ can be any Regular Expression composed of the following Syntax:
* Concatination (ab)
* Alternative (a|b)
* Iteration (a*)
* empty word (ε)
 
__TestCases__ are strings used to test if the result of the steps (NFA to DFA, minimazation DFA, RegEx to DFA) are correct.

The tools creates .dot files, which are rendered into .png using graphviz

[Internet-Sicherheit]:https://www.w-hs.de/internet-sicherheit-ge/
