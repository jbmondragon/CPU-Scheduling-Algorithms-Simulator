package src;

import java.util.*;

public class SJFPreemptive implements Scheduler {

    @Override
    public ScheduleResult schedule(List<Job> jobs) {

        // Copy jobs so original list is not modified
        List<Job> jobList = new ArrayList<>(jobs);

        // Sort by arrival time initially
        jobList.sort(Comparator.comparingInt(j -> j.arrivalTime));

        int currentTime = 0;
        int completed = 0;

        double totalWT = 0;
        double totalTAT = 0;
        List<Integer> ganttChart = new ArrayList<>();

        int n = jobList.size();

        // Track which jobs are completed
        boolean[] isCompleted = new boolean[n];

        while (completed < n) {

            List<Integer> availableIndices = new ArrayList<>();

            // Find all jobs that have arrived and are not completed
            for (int i = 0; i < n; i++) {
                Job job = jobList.get(i);
                if (job.arrivalTime <= currentTime && !isCompleted[i]) {
                    availableIndices.add(i);
                }
            }

            if (availableIndices.isEmpty()) {
                currentTime++;
                continue;
            }

            // Select the job with shortest remaining time
            int shortestIndex = availableIndices.get(0);
            for (int idx : availableIndices) {
                if (jobList.get(idx).remainingTime < jobList.get(shortestIndex).remainingTime) {
                    shortestIndex = idx;
                }
            }

            Job currentJob = jobList.get(shortestIndex);

            // PID
            int processId = extractProcessId(currentJob.processID);
            ganttChart.add(processId);

            // Execute for 1 time unit
            currentJob.remainingTime--;
            currentTime++;

            // If job finished, update metrics
            if (currentJob.remainingTime == 0) {
                completed++;
                isCompleted[shortestIndex] = true;

                currentJob.turnaroundTime = currentTime - currentJob.arrivalTime;
                currentJob.waitingTime = currentJob.turnaroundTime - currentJob.burstTime;

                totalWT += currentJob.waitingTime;
                totalTAT += currentJob.turnaroundTime;
            }
        }

        double avgWT = totalWT / n;
        double avgTAT = totalTAT / n;

        return new ScheduleResult(jobList, avgWT, avgTAT, ganttChart);
    }

    private int extractProcessId(String processID) {
        return Integer.parseInt(processID.replaceAll("[^0-9]", ""));
    }
}
