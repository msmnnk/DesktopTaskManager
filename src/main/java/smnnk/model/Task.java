package smnnk.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private enum taskStatus {DONE, EXPIRED, WAITING};
    private String title;
    private LocalDateTime time;
    private taskStatus status;

    public Task(String title, LocalDateTime time) throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException("Time must not be null.");
        }
        this.title = title;
        this.time = time;
        if (LocalDateTime.now().isAfter(time)) {
            this.status = taskStatus.EXPIRED;
        } else {
            this.status = taskStatus.WAITING;
        }
    }

    public Task(String title, LocalDateTime time, String status) throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException("Time must not be null.");
        }
        this.title = title;
        this.time = time;
        this.status = taskStatus.valueOf(status);
    }

    public static List<String> getStatuses() {
        List<String> statuses = new ArrayList<>();
        for (taskStatus temp : taskStatus.values()) {
            statuses.add(temp.toString());
        }
        return statuses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(String status) {
        this.status = taskStatus.valueOf(status);
    }
}
