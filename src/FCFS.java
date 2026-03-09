package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFS implements Scheduler {

    @Override
    public ScheduleResult schedule(List<Job> jobs) {

        List<Job> sortedJobs = new ArrayList<>(jobs);
        sortedJobs.sort(Comparator.comparingInt(j -> j.arrivalTime));

        int currentTime = 0;
        double totalWT = 0;
        double totalTAT = 0;
        List<Integer> ganttChart = new ArrayList<>();

        for (Job job : sortedJobs) {

            if (currentTime < job.arrivalTime) {
                currentTime = job.arrivalTime;
            }

            // PID
            int processId = extractProcessId(job.processID);
            for (int i = 0; i < job.burstTime; i++) {
                ganttChart.add(processId);
            }

            // waitinng time
            job.waitingTime = currentTime - job.arrivalTime;
            // turnaround time
            job.turnaroundTime = job.waitingTime + job.burstTime;

            currentTime += job.burstTime;

            totalWT += job.waitingTime;
            totalTAT += job.turnaroundTime;
        }

        // Calculate averages
        double avgWT = totalWT / sortedJobs.size();
        double avgTAT = totalTAT / sortedJobs.size();

        return new ScheduleResult(sortedJobs, avgWT, avgTAT, ganttChart);
    }

    private int extractProcessId(String processID) {
        return Integer.parseInt(processID.replaceAll("[^0-9]", ""));
    }
}