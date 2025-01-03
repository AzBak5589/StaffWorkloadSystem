package com.workload.model;

import javafx.beans.property.*;

public class Activity {
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty duration = new SimpleIntegerProperty();
    private final IntegerProperty instances = new SimpleIntegerProperty();

    public Activity(String type, String description, int duration, int instances) {
        this.type.set(type);
        this.description.set(description);
        this.duration.set(duration);
        this.instances.set(instances);
    }

    public double calculateTotalHours() {
        return getDuration() * getInstances();
    }

    // Type property methods
    public String getType() { return type.get(); }
    public void setType(String value) { type.set(value); }
    public StringProperty typeProperty() { return type; }

    // Description property methods
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    // Duration property methods
    public int getDuration() { return duration.get(); }
    public void setDuration(int value) { duration.set(value); }
    public IntegerProperty durationProperty() { return duration; }

    // Instances property methods
    public int getInstances() { return instances.get(); }
    public void setInstances(int value) { instances.set(value); }
    public IntegerProperty instancesProperty() { return instances; }

    @Override
    public String toString() {
        return "Activity{" +
                "type='" + getType() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", duration=" + getDuration() +
                ", instances=" + getInstances() +
                '}';
    }
}