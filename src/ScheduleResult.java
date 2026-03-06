package src;

import java.util.List;

public class ScheduleResult {
    public List<Job> jobs;
    public double averageWaitingTime;
    public double averageTurnaroundTime;
    public List<Integer> ganttChart;

    public ScheduleResult(List<Job> jobs,
            double avgWT,
            double avgTAT) {
        this.jobs = jobs;
        this.averageWaitingTime = avgWT;
        this.averageTurnaroundTime = avgTAT;
        this.ganttChart = null;
    }

    public ScheduleResult(List<Job> jobs,
            double avgWT,
            double avgTAT,
            List<Integer> ganttChart) {
        this.jobs = jobs;
        this.averageWaitingTime = avgWT;
        this.averageTurnaroundTime = avgTAT;
        this.ganttChart = ganttChart;
    }
}