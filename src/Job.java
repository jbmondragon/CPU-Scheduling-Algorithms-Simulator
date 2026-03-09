package src;

public class Job {

    private static int counter = 1;
    public String processID;
    public int burstTime;
    public int arrivalTime;
    public int priorityNumber;

    public int waitingTime;
    public int turnaroundTime;
    public int remainingTime;

    public Job(int burst, int arrival, int priorityNumber) {

        this.processID = "P" + counter++;
        this.burstTime = burst;
        this.arrivalTime = arrival;
        this.priorityNumber = priorityNumber;
        this.remainingTime = burst;
    }
}