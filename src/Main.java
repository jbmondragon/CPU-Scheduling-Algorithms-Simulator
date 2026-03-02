package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        List<Job> jobs = new ArrayList<>();

        askUserInput(jobs, sc);

        System.out.println("\nChoose Scheduling Algorithm:");
        System.out.println("1 - First Come First Serve (FCFS)");
        System.out.println("2 - Round Robin");
        System.out.println("3 - Priority Preemptive");
        System.out.println("4 - Priority NonPreemptive");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();

        Scheduler scheduler = null;
        int quantum = 0;
        boolean higherNumberHigherPriority;

        switch (choice) {
            case 1:
                scheduler = new FCFS();
                break;

            case 2:
                System.out.print("Enter Quantum Time: ");
                quantum = sc.nextInt();
                scheduler = new RoundRobin(quantum);
                break;
            case 3:
                System.out.print("Higher number = higher priority? (true/false): ");
                higherNumberHigherPriority = sc.nextBoolean();
                scheduler = new PriorityPreemptive(higherNumberHigherPriority);
                break;
            case 4:
                System.out.print("Higher number = higher priority? (true/false): ");
                higherNumberHigherPriority = sc.nextBoolean();
                scheduler = new PriorityNonPreemptive(higherNumberHigherPriority);
                break;
            default:
                throw new AssertionError();
        }

        ScheduleResult result = scheduler.schedule(jobs);

        printResults(result.jobs);
        if (choice == 2) {
            System.out.printf("Quantum Time: %d\n", quantum);
        }
        System.out.printf("Average Waiting Time: %.2f\n", result.averageWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", result.averageTurnaroundTime);

        sc.close();
    }

    /* Print Results */
    public static void printResults(List<Job> jobs) {
        System.out.println("\n==============================================================");
        System.out.println("Process ID | Burst | Arrival | Priority | Waiting | Turnaround");
        System.out.println("==============================================================");

        for (Job j : jobs) {
            System.out.printf("%-10s | %-5d | %-7d | %-8d | %-7d | %-10d\n",
                    j.processID,
                    j.burstTime,
                    j.arrivalTime,
                    j.priorityNumber,
                    j.waitingTime,
                    j.turnaroundTime);
        }
        System.out.println("==============================================================");
    }

    /* Ask user input */
    public static void askUserInput(List<Job> jobs, Scanner sc) {
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("\nProcess " + (i + 1));
            System.out.print("Process ID: ");
            String id = sc.next();

            System.out.print("Burst Time: ");
            int burst = sc.nextInt();

            System.out.print("Arrival Time: ");
            int arrival = sc.nextInt();

            System.out.print("Priority Number: ");
            int priority = sc.nextInt();

            jobs.add(new Job(id, burst, arrival, priority));
        }
    }
}
