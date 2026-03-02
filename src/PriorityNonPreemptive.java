package src;

import java.util.*;

public class PriorityNonPreemptive implements Scheduler {

    private boolean higherNumberHigherPriority;

    public PriorityNonPreemptive(boolean higherNumberHigherPriority) {
        this.higherNumberHigherPriority = higherNumberHigherPriority;
    }

    @Override
    public ScheduleResult schedule(List<Job> jobs) {

        // Create a copy to avoid modifying original list
        List<Job> jobList = new ArrayList<>(jobs);

        // Sort by arrival time
        jobList.sort(Comparator.comparingInt(j -> j.arrivalTime));

        int currentTime = 0;
        int completed = 0;

        double totalWT = 0;
        double totalTAT = 0;

        List<Job> readyQueue = new ArrayList<>();

        while (completed < jobList.size()) {

            // Add jobs that have arrived
            for (Job job : jobList) {
                if (job.arrivalTime <= currentTime && job.remainingTime > 0 && !readyQueue.contains(job)) {
                    readyQueue.add(job);
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Sort readyQueue by priority
            readyQueue.sort((j1, j2) -> {
                if (higherNumberHigherPriority) {
                    return Integer.compare(j2.priorityNumber, j1.priorityNumber);
                } else {
                    return Integer.compare(j1.priorityNumber, j2.priorityNumber);
                }
            });

            Job currentJob = readyQueue.get(0);

            // Run it to completion (non-preemptive)
            currentTime += currentJob.remainingTime;
            currentJob.remainingTime = 0;
            completed++;

            currentJob.turnaroundTime = currentTime - currentJob.arrivalTime;
            currentJob.waitingTime = currentJob.turnaroundTime - currentJob.burstTime;

            totalWT += currentJob.waitingTime;
            totalTAT += currentJob.turnaroundTime;

            readyQueue.remove(currentJob);
        }

        double avgWT = totalWT / jobList.size();
        double avgTAT = totalTAT / jobList.size();

        return new ScheduleResult(jobList, avgWT, avgTAT);
    }
}
