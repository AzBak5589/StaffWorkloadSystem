module com.workload {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.workload to javafx.fxml;
    opens com.workload.model to javafx.base;
    exports com.workload;
}