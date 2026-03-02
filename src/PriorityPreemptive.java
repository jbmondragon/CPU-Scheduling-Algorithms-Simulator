package src;

import java.util.*;

public class PriorityPreemptive implements Scheduler {

    private boolean higherNumberHigherPriority;

    public PriorityPreemptive(boolean higherNumberHigherPriority) {
        this.higherNumberHigherPriority = higherNumberHigherPriority;
    }

    @Override
    public ScheduleResult schedule(List<Job> jobs) {

        List<Job> jobList = new ArrayList<>(jobs);
        jobList.sort(Comparator.comparingInt(j -> j.arrivalTime));

        int currentTime = 0;
        int completed = 0;

        double totalWT = 0;
        double totalTAT = 0;

        while (completed < jobList.size()) {

            List<Job> available = new ArrayList<>();

            for (Job job : jobList) {
                if (job.arrivalTime <= currentTime && job.remainingTime > 0) {
                    available.add(job);
                }
            }

            if (available.isEmpty()) {
                currentTime++;
                continue;
            }

            // Sort by priority depending on toggle
            available.sort((j1, j2) -> {
                if (higherNumberHigherPriority) {
                    return Integer.compare(j2.priorityNumber, j1.priorityNumber);
                } else {
                    return Integer.compare(j1.priorityNumber, j2.priorityNumber);
                }
            });

            Job currentJob = available.get(0);

            currentJob.remainingTime--;
            currentTime++;

            if (currentJob.remainingTime == 0) {
                completed++;
                currentJob.turnaroundTime = currentTime - currentJob.arrivalTime;
                currentJob.waitingTime = currentJob.turnaroundTime - currentJob.burstTime;

                totalWT += currentJob.waitingTime;
                totalTAT += currentJob.turnaroundTime;
            }
        }

        double avgWT = totalWT / jobList.size();
        double avgTAT = totalTAT / jobList.size();

        return new ScheduleResult(jobList, avgWT, avgTAT);
    }
}
