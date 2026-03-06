package src;

public class Job {

    // ===== Static counter for automatic IDs =====
    private static int counter = 1;

    // ===== Basic process info =====
    public String processID;
    public int burstTime;
    public int arrivalTime;
    public int priorityNumber;

    public int waitingTime;
    public int turnaroundTime;
    public int remainingTime;

    // ===== Constructor =====
    public Job(int burst, int arrival, int priorityNumber) {

        this.processID = "P" + counter++; // Auto increment ID
        this.burstTime = burst;
        this.arrivalTime = arrival;
        this.priorityNumber = priorityNumber;
        this.remainingTime = burst;
    }
}