package src;

public class Job {

    // Basic process info
    public String processID;
    public int burstTime;
    public int arrivalTime;
    public int priorityNumber;

    // Calculated during scheduling
    public int waitingTime;
    public int turnaroundTime;

    // Needed for preemptive algorithms (RR, SJF-P, Priority-P)
    public int remainingTime;

    public Job(String id, int burst, int arrival, int priorityNumber) {
        this.processID = id;
        this.burstTime = burst;
        this.arrivalTime = arrival;
        this.priorityNumber = priorityNumber;
        this.remainingTime = burst; // important for RR & preemptive
    }
}