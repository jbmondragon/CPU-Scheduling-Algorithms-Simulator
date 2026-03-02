package src;

import java.util.List;

public class ScheduleResult {
    public List<Job> jobs;
    public double averageWaitingTime;
    public double averageTurnaroundTime;

    public ScheduleResult(List<Job> jobs,
            double avgWT,
            double avgTAT) {
        this.jobs = jobs;
        this.averageWaitingTime = avgWT;
        this.averageTurnaroundTime = avgTAT;
    }
}