package src;

import java.util.List;

public interface Scheduler {
    ScheduleResult schedule(List<Job> jobs);
}
