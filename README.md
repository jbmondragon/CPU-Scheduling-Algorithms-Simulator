# CPU-Scheduling-Algorithms-Simulator

_This project is developed by **Jake Mondragon**, **Benedict Pagba**, **Justin Magne Agaton**, and **Niel Frias**_

## Overview

This application is an implementation in java of 6 CPU algorithms: First Come First Serve (FCFS), Round Robin, Shortest Job Scheduler (Preemptive), Shortest Job Scheduler (Non-Preemptive), PriorityPreemptive, and PriorityNonPreemptive.

## Appendix A

- Processes should be between 3-20
- Burst time should be between 1-30
- Arrival time should be between 0-30
- Priority number that can be assigned should be between 1-20 without duplications
- Time quantum is between 1 to 10
- User has 3 choices how to generate the data needed by CPU scheduler (See appendix B)

## Appendix B

Generated data can be obtained through random, user-defined input, and user-defined input from a file

- Random: the program will randomly generate the data needed by the CPU scheduler.
- User-defined input: the user will input the data through an input screen in the simulator.
- User-defined input from a text file: the user will input the data by reading a text file.

---

## Installation & Setup Guide

### 1. Clone the Repository

```bash
git clone https://github.com/jbmondragon/CPU-Scheduling-Algorithms-Simulator.git
```

## 2. Open the Project in VS Code

- Make sure you have the **Java Extension Pack** installed and use java 21.
- Open the `Main.java` file.

To compile and run the project via terminal:

```bash
find . -name "*.class" -type f -delete && javac --release 21 -d out $(find . -name "*.java") && java -cp "out;." Main
```

### 3️. Run the Application

You can run the game by:

- Clicking the Run Java button in the top-right of VS Code

### 4. Create and Run a jar file

```bash
jar cfe CPUSchedulingSimulator.jar Main -C out .
java -jar CPUSchedulingSimulator.jar
```

### 5. Run the .exe by double clicking the file
