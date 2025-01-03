package com.workload.model;
import java.util.ArrayList;
import java.util.List;

public class Staff {
    private String staffId;
    private String name;
    private List<Activity> activities;

    public Staff(String staffId, String name) {
        this.staffId = staffId;
        this.name = name;
        this.activities = new ArrayList<>();
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public double calculateTotalWorkload() {
        return activities.stream()
                .mapToDouble(Activity::calculateTotalHours)
                .sum();
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

}

