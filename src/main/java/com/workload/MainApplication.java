package com.workload;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.workload.model.*;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainApplication extends Application {
    private Staff currentStaff;
    private TextField nameField;
    private ComboBox<String> staffComboBox;
    private ComboBox<String> activityTypeCombo;
    private TextField descriptionField;
    private TextField durationField;
    private TextField instancesField;
    private TableView<ActivityWithUser> activitiesTable;
    private TableView<UserSummary> summaryTable;
    private Label totalWorkloadLabel;
    private Map<String, Staff> allStaff = new HashMap<>();
    private ObservableList<UserSummary> summaryData = FXCollections.observableArrayList();
    private ObservableList<String> staffNames = FXCollections.observableArrayList();

    // Inner class for activity with user information
    public static class ActivityWithUser extends Activity {
        private final SimpleStringProperty userName = new SimpleStringProperty();

        public ActivityWithUser(String userName, String type, String description, int duration, int instances) {
            super(type, description, duration, instances);
            this.userName.set(userName);
        }

        public String getUserName() { return userName.get(); }
        public SimpleStringProperty userNameProperty() { return userName; }
    }

    // Inner class for user summary data
    public static class UserSummary {
        private final SimpleStringProperty userName;
        private final SimpleDoubleProperty atsrHours;
        private final SimpleDoubleProperty tsHours;
        private final SimpleDoubleProperty saHours;
        private final SimpleDoubleProperty otherHours;
        private final SimpleDoubleProperty totalHours;

        public UserSummary(String userName, double atsrHours, double tsHours, double saHours, double otherHours) {
            this.userName = new SimpleStringProperty(userName);
            this.atsrHours = new SimpleDoubleProperty(atsrHours);
            this.tsHours = new SimpleDoubleProperty(tsHours);
            this.saHours = new SimpleDoubleProperty(saHours);
            this.otherHours = new SimpleDoubleProperty(otherHours);
            this.totalHours = new SimpleDoubleProperty(atsrHours + tsHours + saHours + otherHours);
        }

        // Getters and property methods
        public String getUserName() { return userName.get(); }
        public SimpleStringProperty userNameProperty() { return userName; }

        public double getAtsrHours() { return atsrHours.get(); }
        public SimpleDoubleProperty atsrHoursProperty() { return atsrHours; }

        public double getTsHours() { return tsHours.get(); }
        public SimpleDoubleProperty tsHoursProperty() { return tsHours; }

        public double getSaHours() { return saHours.get(); }
        public SimpleDoubleProperty saHoursProperty() { return saHours; }

        public double getOtherHours() { return otherHours.get(); }
        public SimpleDoubleProperty otherHoursProperty() { return otherHours; }

        public double getTotalHours() { return totalHours.get(); }
        public SimpleDoubleProperty totalHoursProperty() { return totalHours; }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Staff Workload Management System");

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));

        // Staff details section
        GridPane staffGrid = new GridPane();
        staffGrid.setHgap(10);
        staffGrid.setVgap(5);

        nameField = new TextField();
        Button addStaffButton = new Button("Add Staff Member");

        staffComboBox = new ComboBox<>(staffNames);
        staffComboBox.setPromptText("Select Staff Member");
        staffComboBox.setPrefWidth(200);
        staffComboBox.setOnAction(e -> handleStaffSelection());

        staffGrid.addRow(0, new Label("Staff Name:"), nameField, addStaffButton);
        staffGrid.addRow(1, new Label("Select Staff:"), staffComboBox);

        // Activity details section
        GridPane activityGrid = new GridPane();
        activityGrid.setHgap(10);
        activityGrid.setVgap(5);

        activityTypeCombo = new ComboBox<>();
        activityTypeCombo.getItems().addAll("ATSR", "TS", "SA", "OTHER");
        activityTypeCombo.setValue("ATSR");
        activityTypeCombo.setDisable(true);

        descriptionField = new TextField();
        descriptionField.setDisable(true);

        durationField = new TextField();
        durationField.setDisable(true);

        instancesField = new TextField();
        instancesField.setDisable(true);

        Button addActivityButton = new Button("Add Activity");
        addActivityButton.setDisable(true);

        activityGrid.addRow(0, new Label("Activity Type:"), activityTypeCombo);
        activityGrid.addRow(1, new Label("Description:"), descriptionField);
        activityGrid.addRow(2, new Label("Duration (hours):"), durationField);
        activityGrid.addRow(3, new Label("Instances:"), instancesField);
        activityGrid.add(addActivityButton, 1, 4);

        // Activities table
        activitiesTable = new TableView<>();
        activitiesTable.setMinHeight(200);

        TableColumn<ActivityWithUser, String> userNameCol = new TableColumn<>("Staff Name");
        userNameCol.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());
        userNameCol.setPrefWidth(150);

        TableColumn<ActivityWithUser, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        typeCol.setPrefWidth(100);

        TableColumn<ActivityWithUser, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descCol.setPrefWidth(200);

        TableColumn<ActivityWithUser, Number> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        durationCol.setPrefWidth(100);

        TableColumn<ActivityWithUser, Number> instancesCol = new TableColumn<>("Instances");
        instancesCol.setCellValueFactory(cellData -> cellData.getValue().instancesProperty());
        instancesCol.setPrefWidth(100);

        TableColumn<ActivityWithUser, Number> totalHoursCol = new TableColumn<>("Total Hours");
        totalHoursCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().calculateTotalHours()));
        totalHoursCol.setPrefWidth(100);

        activitiesTable.getColumns().addAll(userNameCol, typeCol, descCol, durationCol, instancesCol, totalHoursCol);

        // Summary table
        summaryTable = new TableView<>();
        summaryTable.setMinHeight(200);
        summaryTable.setItems(summaryData);

        TableColumn<UserSummary, String> summaryNameCol = new TableColumn<>("Staff Name");
        summaryNameCol.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());
        summaryNameCol.setPrefWidth(150);

        TableColumn<UserSummary, Number> atsrCol = new TableColumn<>("ATSR Hours");
        atsrCol.setCellValueFactory(cellData -> cellData.getValue().atsrHoursProperty());
        atsrCol.setPrefWidth(100);

        TableColumn<UserSummary, Number> tsCol = new TableColumn<>("TS Hours");
        tsCol.setCellValueFactory(cellData -> cellData.getValue().tsHoursProperty());
        tsCol.setPrefWidth(100);

        TableColumn<UserSummary, Number> saCol = new TableColumn<>("SA Hours");
        saCol.setCellValueFactory(cellData -> cellData.getValue().saHoursProperty());
        saCol.setPrefWidth(100);

        TableColumn<UserSummary, Number> otherCol = new TableColumn<>("Other Hours");
        otherCol.setCellValueFactory(cellData -> cellData.getValue().otherHoursProperty());
        otherCol.setPrefWidth(100);

        TableColumn<UserSummary, Number> totalCol = new TableColumn<>("Total Hours");
        totalCol.setCellValueFactory(cellData -> cellData.getValue().totalHoursProperty());
        totalCol.setPrefWidth(100);

        summaryTable.getColumns().addAll(summaryNameCol, atsrCol, tsCol, saCol, otherCol, totalCol);

        // Total workload label
        totalWorkloadLabel = new Label("Total Workload: 0 hours");
        totalWorkloadLabel.setWrapText(true);
        totalWorkloadLabel.setStyle("-fx-font-weight: bold");

        // Add everything to the main layout
        mainLayout.getChildren().addAll(
                new Label("Staff Details"),
                staffGrid,
                new Separator(),
                new Label("Activity Details"),
                activityGrid,
                new Separator(),
                new Label("Activities"),
                activitiesTable,
                new Separator(),
                new Label("Staff Summary"),
                summaryTable,
                totalWorkloadLabel
        );

        // Set up event handlers
        addStaffButton.setOnAction(e -> handleAddStaff());
        addActivityButton.setOnAction(e -> handleAddActivity());

        // Enable/disable activity button based on form completion
        activityTypeCombo.valueProperty().addListener((obs, oldVal, newVal) ->
                addActivityButton.setDisable(!isActivityFormComplete()));
        descriptionField.textProperty().addListener((obs, oldVal, newVal) ->
                addActivityButton.setDisable(!isActivityFormComplete()));
        durationField.textProperty().addListener((obs, oldVal, newVal) ->
                addActivityButton.setDisable(!isActivityFormComplete()));
        instancesField.textProperty().addListener((obs, oldVal, newVal) ->
                addActivityButton.setDisable(!isActivityFormComplete()));

        // Show the stage
        Scene scene = new Scene(mainLayout, 900, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean isActivityFormComplete() {
        return currentStaff != null &&
                activityTypeCombo.getValue() != null &&
                !descriptionField.getText().trim().isEmpty() &&
                !durationField.getText().trim().isEmpty() &&
                !instancesField.getText().trim().isEmpty();
    }

    private void handleStaffSelection() {
        String selectedName = staffComboBox.getValue();
        if (selectedName != null) {
            Optional<Staff> selectedStaff = allStaff.values().stream()
                    .filter(staff -> staff.getName().equals(selectedName))
                    .findFirst();

            if (selectedStaff.isPresent()) {
                currentStaff = selectedStaff.get();

                // Enable activity controls
                activityTypeCombo.setDisable(false);
                descriptionField.setDisable(false);
                durationField.setDisable(false);
                instancesField.setDisable(false);

                // Update the activities table to show only selected staff's activities
                updateActivitiesTableForStaff();

                // Update workload display for selected staff
                updateWorkloadDisplay();
            }
        }
    }

    private void updateActivitiesTableForStaff() {
        if (currentStaff != null) {
            ObservableList<ActivityWithUser> activities = FXCollections.observableArrayList();
            for (Activity activity : currentStaff.getActivities()) {
                activities.add(new ActivityWithUser(
                        currentStaff.getName(),
                        activity.getType(),
                        activity.getDescription(),
                        activity.getDuration(),
                        activity.getInstances()
                ));
            }
            activitiesTable.setItems(activities);
        }
    }

    private void handleAddStaff() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Error", "Please enter a staff name.", Alert.AlertType.ERROR);
            return;
        }

        // Check for duplicate names
        if (staffNames.contains(name)) {
            showAlert("Error", "A staff member with this name already exists.", Alert.AlertType.ERROR);
            return;
        }

        // Create new staff member
        currentStaff = new Staff(UUID.randomUUID().toString(), name);
        allStaff.put(currentStaff.getStaffId(), currentStaff);

        // Update UI elements
        staffNames.add(name);
        staffComboBox.setValue(name);
        nameField.clear();

        // Enable activity controls
        activityTypeCombo.setDisable(false);
        descriptionField.setDisable(false);
        durationField.setDisable(false);
        instancesField.setDisable(false);

        // Clear activities table
        activitiesTable.getItems().clear();

        showAlert("Success", "Staff member '" + name + "' added successfully!", Alert.AlertType.INFORMATION);
    }

    private void handleAddActivity() {
        if (currentStaff == null) {
            showAlert("Error", "Please select a staff member first.", Alert.AlertType.ERROR);
            return;
        }

        try {
            String type = activityTypeCombo.getValue();
            String description = descriptionField.getText().trim();

            if (description.isEmpty()) {
                showAlert("Error", "Please enter a description.", Alert.AlertType.ERROR);
                return;
            }

            int duration = Integer.parseInt(durationField.getText().trim());
            int instances = Integer.parseInt(instancesField.getText().trim());

            if (duration <= 0 || instances <= 0) {
                showAlert("Error", "Duration and instances must be positive numbers.", Alert.AlertType.ERROR);
                return;
            }

            Activity activity = new Activity(type, description, duration, instances);
            currentStaff.addActivity(activity);

            // Add to activities table
            ActivityWithUser activityWithUser = new ActivityWithUser(
                    currentStaff.getName(), type, description, duration, instances
            );
            activitiesTable.getItems().add(activityWithUser);

            // Update summary table
            updateSummaryTable();

            // Check workload validation
            String validationMessage = WorkloadCalculator.validateWorkload(currentStaff);
            if (!validationMessage.isEmpty()) {
                showAlert("Workload Warning", validationMessage, Alert.AlertType.WARNING);
            }

            // Update total workload
            updateWorkloadDisplay();

            // Clear the fields
            descriptionField.clear();
            durationField.clear();
            instancesField.clear();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for duration and instances.", Alert.AlertType.ERROR);
        }
    }

    private void updateSummaryTable() {
        summaryData.clear();

        for (Staff staff : allStaff.values()) {
            Map<String, Double> workloadByType = staff.getActivities().stream()
                    .collect(Collectors.groupingBy(
                            Activity::getType,
                            Collectors.summingDouble(Activity::calculateTotalHours)
                    ));

            UserSummary summary = new UserSummary(
                    staff.getName(),
                    workloadByType.getOrDefault("ATSR", 0.0),
                    workloadByType.getOrDefault("TS", 0.0),
                    workloadByType.getOrDefault("SA", 0.0),
                    workloadByType.getOrDefault("OTHER", 0.0)
            );

            summaryData.add(summary);
        }
    }

    private void updateWorkloadDisplay() {
        if (currentStaff != null) {
            double totalWorkload = currentStaff.calculateTotalWorkload();

            Map<String, Double> workloadByType = currentStaff.getActivities().stream()
                    .collect(Collectors.groupingBy(
                            Activity::getType,
                            Collectors.summingDouble(Activity::calculateTotalHours)
                    ));

            StringBuilder details = new StringBuilder();
            details.append(String.format("Current Staff: %s\\n", currentStaff.getName()));
            details.append(String.format("Total Workload: %.1f hours\\n\\n", totalWorkload));
            details.append("Breakdown:\\n");
            details.append(String.format("ATSR: %.1f hours (max 550)\\n", workloadByType.getOrDefault("ATSR", 0.0)));
            details.append(String.format("TS: %.1f hours (max 660)\\n", workloadByType.getOrDefault("TS", 0.0)));
            details.append(String.format("SA: %.1f hours (must be 188)\\n", workloadByType.getOrDefault("SA", 0.0)));
            details.append(String.format("OTHER: %.1f hours (min 172)", workloadByType.getOrDefault("OTHER", 0.0)));

            totalWorkloadLabel.setText(details.toString());
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}