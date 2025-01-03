// WorkloadCalculator.java
package com.workload.model;

import java.util.Map;
import java.util.stream.Collectors;

public class WorkloadCalculator {
    public static final int ATSR_MAX_HOURS = 550;
    public static final int TS_MAX_HOURS = 660;
    public static final int SA_HOURS = 188;
    public static final int OTHER_MIN_HOURS = 172;

    public static String validateWorkload(Staff staff) {
        Map<String, Double> workloadByType = calculateWorkloadByType(staff);

        // Get hours for each type, defaulting to 0 if not present
        double atsrHours = workloadByType.getOrDefault("ATSR", 0.0);
        double tsHours = workloadByType.getOrDefault("TS", 0.0);
        double saHours = workloadByType.getOrDefault("SA", 0.0);
        double otherHours = workloadByType.getOrDefault("OTHER", 0.0);

        // Build validation message
        StringBuilder message = new StringBuilder();

        if (atsrHours > ATSR_MAX_HOURS) {
            message.append(String.format("ATSR hours (%.1f) exceed maximum of %d\n", atsrHours, ATSR_MAX_HOURS));
        }
        if (tsHours > TS_MAX_HOURS) {
            message.append(String.format("TS hours (%.1f) exceed maximum of %d\n", tsHours, TS_MAX_HOURS));
        }
        if (saHours != SA_HOURS && workloadByType.containsKey("SA")) {
            message.append(String.format("SA hours (%.1f) must be exactly %d\n", saHours, SA_HOURS));
        }
        if (otherHours < OTHER_MIN_HOURS && workloadByType.containsKey("OTHER")) {
            message.append(String.format("OTHER hours (%.1f) must be at least %d\n", otherHours, OTHER_MIN_HOURS));
        }

        return message.toString();
    }

    private static Map<String, Double> calculateWorkloadByType(Staff staff) {
        return staff.getActivities().stream()
                .collect(Collectors.groupingBy(
                        Activity::getType,
                        Collectors.summingDouble(Activity::calculateTotalHours)
                ));
    }
}