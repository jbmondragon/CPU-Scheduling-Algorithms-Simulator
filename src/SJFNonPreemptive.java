package src;

import java.util.*;

public class SJFNonPreemptive implements Scheduler {

    @Override
    public ScheduleResult schedule(List<Job> jobs) {

        // Copy list so original jobs are not modified
        List<Job> jobList = new ArrayList<>(jobs);

        // Sort by arrival time initially
        jobList.sort(Comparator.comparingInt(j -> j.arrivalTime));

        int currentTime = 0;
        int completed = 0;

        double totalWT = 0;
        double totalTAT = 0;

        List<Job> readyQueue = new ArrayList<>();

        while (completed < jobList.size()) {

            // Add arrived jobs to readyQueue
            for (Job job : jobList) {
                if (job.arrivalTime <= currentTime && job.remainingTime > 0 && !readyQueue.contains(job)) {
                    readyQueue.add(job);
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Sort readyQueue by burst time (shortest job first)
            readyQueue.sort(Comparator.comparingInt(j -> j.burstTime));

            Job currentJob = readyQueue.get(0);

            // Run to completion (non-preemptive)
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
