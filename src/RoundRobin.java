package src;

import java.util.*;

public class RoundRobin implements Scheduler {

    private int timeQuantum;

    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    @Override
    public ScheduleResult schedule(List<Job> jobs) {

        List<Job> jobList = new ArrayList<>(jobs);
        jobList.sort(Comparator.comparingInt(j -> j.arrivalTime));

        Queue<Job> queue = new LinkedList<>();
        List<Integer> ganttChart = new ArrayList<>();

        int currentTime = 0;
        int completed = 0;
        int index = 0;

        double totalWT = 0;
        double totalTAT = 0;

        while (completed < jobList.size()) {

            // Add newly arrived jobs
            while (index < jobList.size() &&
                    jobList.get(index).arrivalTime <= currentTime) {
                queue.add(jobList.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                ganttChart.add(-1); 
                currentTime++;
                continue;
            }

            Job currentJob = queue.poll();

            int executionTime = Math.min(timeQuantum, currentJob.remainingTime);

            // PID
            int processId = extractProcessId(currentJob.processID);
            for (int i = 0; i < executionTime; i++) {
                ganttChart.add(processId);
            }

            currentTime += executionTime;
            currentJob.remainingTime -= executionTime;

            // Add newly arrived jobs during execution
            while (index < jobList.size() &&
                    jobList.get(index).arrivalTime <= currentTime) {
                queue.add(jobList.get(index));
                index++;
            }

            if (currentJob.remainingTime > 0) {
                queue.add(currentJob);
            } else {
                completed++;
                currentJob.turnaroundTime = currentTime - currentJob.arrivalTime;
                currentJob.waitingTime = currentJob.turnaroundTime - currentJob.burstTime;

                totalWT += currentJob.waitingTime;
                totalTAT += currentJob.turnaroundTime;
            }
        }

        double avgWT = totalWT / jobList.size();
        double avgTAT = totalTAT / jobList.size();

        return new ScheduleResult(jobList, avgWT, avgTAT, ganttChart);
    }

    private int extractProcessId(String processID) {
        return Integer.parseInt(processID.replaceAll("[^0-9]", ""));
    }
}
