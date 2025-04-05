package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.CourseDao;
import com.gbs.gbsproject.model.Admin;
import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.model.Student;
import com.gbs.gbsproject.model.Tutor;
import com.gbs.gbsproject.dao.StudentDao;
import com.gbs.gbsproject.dao.TutorDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminPageViewController {
    Admin admin;
    private static final Logger LOGGER = Logger.getLogger(AdminPageViewController.class.getName());
    public AnchorPane accountPane;
    public Button buttonMenuAccount;
    public ScrollPane userScrollPane;

    @FXML
    protected void formClicked() {
        accountPane.setVisible(false);
    }

    @FXML
    protected void accountClicked() {
        if(accountPane.isVisible()){
            accountPane.setVisible(false);
        } else {
            accountPane.setVisible(true);
            accountPane.toFront();
        }
    }

    public void logOut() {
        try {
            // Load the FXML file for the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/login-page-view.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the list of all windows
            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No window found"));

            double width = stage.getWidth();
            double height = stage.getHeight();

            // Set the new scene with the same window size
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
        }
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void homeButtonClick(MouseEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/admin-page-view.fxml"));
            Parent root = loader.load();


            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Set the new scene
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);

            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            // Log the exception using a logger instead of printStackTrace()
            LOGGER.log(Level.SEVERE, "An error occurred while loading the next FXML", e);
        }
    }

    public void viewUsersClicked() {
        List<Tutor> tutors = TutorDao.getAllTutors();
        List<Student> students = StudentDao.getAllStudents();

        TableView<Tutor> tutorTable = new TableView<>();
        TableView<Student> studentTable = new TableView<>();

        // Tutor Table Columns
        TableColumn<Tutor, Integer> tutorId = new TableColumn<>("ID");
        tutorId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Tutor, String> tutorName = new TableColumn<>("Name");
        tutorName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Tutor, String> tutorSurname = new TableColumn<>("Surname");
        tutorSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Tutor, String> tutorUsername = new TableColumn<>("Username");
        tutorUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Tutor, String> tutorEmail = new TableColumn<>("Email");
        tutorEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Tutor, String> tutorField = new TableColumn<>("Field");
        tutorField.setCellValueFactory(new PropertyValueFactory<>("field"));

        tutorTable.getColumns().add(tutorId);
        tutorTable.getColumns().add(tutorName);
        tutorTable.getColumns().add(tutorSurname);
        tutorTable.getColumns().add(tutorUsername);
        tutorTable.getColumns().add(tutorEmail);
        tutorTable.getColumns().add(tutorField);
        tutorTable.getItems().addAll(tutors);

        // Student Table Columns
        TableColumn<Student, Integer> studentId = new TableColumn<>("ID");
        studentId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> studentName = new TableColumn<>("Name");
        studentName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> studentSurname = new TableColumn<>("Surname");
        studentSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Student, String> studentUsername = new TableColumn<>("Username");
        studentUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Student, String> studentEmail = new TableColumn<>("Email");
        studentEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        studentTable.getColumns().add(studentId);
        studentTable.getColumns().add(studentName);
        studentTable.getColumns().add(studentSurname);
        studentTable.getColumns().add(studentUsername);
        studentTable.getColumns().add(studentEmail);

        studentTable.getItems().addAll(students);

        tutorTable.setPrefHeight(200);
        studentTable.setPrefHeight(200);

        VBox tablesContainer = new VBox(20, new Label("Tutors"), tutorTable, new Label("Students"), studentTable);
        tablesContainer.setPadding(new Insets(10));
        userScrollPane.setContent(tablesContainer);
    }

    public void viewCoursesClick() {
        List<Course> courses = CourseDao.getAllCourses();
        TableView<Course> courseTable = new TableView<>();

        TableColumn<Course, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Course, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Course, Integer> tutorIdCol = new TableColumn<>("Tutor ID");
        tutorIdCol.setCellValueFactory(new PropertyValueFactory<>("tutorId"));

        courseTable.getColumns().add(idCol);
        courseTable.getColumns().add(nameCol);
        courseTable.getColumns().add(descCol);
        courseTable.getColumns().add(tutorIdCol);
        courseTable.getItems().addAll(courses);

        VBox tablesContainer = new VBox(20, new Label("Courses"), courseTable);
        tablesContainer.setPadding(new Insets(10));
        userScrollPane.setContent(tablesContainer);
    }
}