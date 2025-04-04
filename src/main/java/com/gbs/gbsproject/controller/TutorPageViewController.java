package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.SectionContentDao;
import com.gbs.gbsproject.dao.SectionDao;
import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.dao.CourseDao;
import com.gbs.gbsproject.model.Section;
import com.gbs.gbsproject.model.SectionContent;
import com.gbs.gbsproject.model.Tutor;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TutorPageViewController {
    public TextField nameTextField;
    public TextArea textAreaDescription;
    public AnchorPane coursePane;
    public ScrollPane scrollPane;
    public AnchorPane vboxCourses;
    public Label courseName;
    public AnchorPane modifyPane;
    public TextField titleField;
    public ChoiceBox<String> choice;
    public Label sectionLabel;
    public TextArea contentArea;
    Tutor tutor;
    Course currentCourse;
    private final List<Section> sections = new ArrayList<>();
    private Section currentSection; // Set this when selecting the section
    private int contentOrder = 1; // You can count from DB or increase locally

    private static final Logger LOGGER = Logger.getLogger(TutorPageViewController.class.getName());

    public AnchorPane accountPane;

    private final CourseDao courseDAO = new CourseDao();

    public void initialize() {
        scrollPane.setFitToWidth(true);
        // Adding items/choices to the ChoiceBox
        choice.getItems().addAll("text", "video", "image");

        // You can also set a default selection
        choice.setValue("Option 1");
    }

    public void formClicked() {
        accountPane.setVisible(false);
    }

    public void accountClicked() {
        if(accountPane.isVisible()){
            accountPane.setVisible(false);
        } else {
            accountPane.setVisible(true);
            accountPane.toFront();
        }
    }
    public void setTutor(Tutor tutor){
        this.tutor = tutor;
    }

    public void homeButtonClick(MouseEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/tutor-page-view.fxml"));
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

    public void addCourse(){
        String name = nameTextField.getText();
        String description = textAreaDescription.getText();
        int tutorId = tutor.getId();

        // Generate a unique ID for the new course (or handle the ID assignment logic)
        int id = generateCourseId(); // You would implement this based on your logic (e.g., auto-increment, or next available ID)

        // Create the course object
        Course course = new Course(id, name, description, tutorId);

        // Try to insert the course using the DAO
        boolean isInserted = courseDAO.insertCourse(course);

        // Show alert based on the result
        if (isInserted) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the course.");
        }
    }

    public void addCourseClicked() {
        coursePane.setVisible(true);
    }

    // You might want to implement a method to generate the course ID.
    public int generateCourseId() {
        Random random = new Random();
        int courseId;
        boolean isUnique;

        do {
            // Generate a random 3-digit number (between 100 and 999)
            courseId = 100 + random.nextInt(900);
            isUnique = courseDAO.checkIfCourseIdExists(courseId);
        } while (!isUnique);

        return courseId;
    }

    // Utility method for showing alerts
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void displayTutorCourses() {
        CourseDao courseDao = new CourseDao(); // Create CourseDao instance
        List<Course> courses = courseDao.getCoursesByTutorId(tutor.getId()); // Get courses by tutor ID

        // Clear existing content in the AnchorPane before adding new courses
        vboxCourses.getChildren().clear(); // or AnchorPane, depending on your setup

        // Loop through each course and create a button for it
        for (int i = 0; i < courses.size(); i++) {
            Button courseButton = getCourseButton(courses, i);

            // Add the button to the AnchorPane
            vboxCourses.getChildren().add(courseButton); // Replace with your AnchorPane variable
        }

        // Refresh the layout of the AnchorPane
        vboxCourses.layout();
    }

    @NotNull
    private Button getCourseButton(List<Course> courses, int i) {
        Course course = courses.get(i);
        Button courseButton = new Button(course.getName()); // Create a button with the course name
        courseButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px; -fx-padding: 10px;"); // Optional styling

        // Manually position the button inside AnchorPane
        courseButton.setLayoutX(20); // Set horizontal position
        courseButton.setLayoutY(i * 50 + 20); // Set vertical position with spacing between buttons

        // Add an event handler to handle clicks on the button
        courseButton.setOnAction(_ -> modifyCourse(course));
        return courseButton;
    }

    @NotNull
    private Button getCourseViewButton(List<Course> courses, int i) {
        Course course = courses.get(i);
        Button courseButton = new Button(course.getName()); // Create a button with the course name
        courseButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px; -fx-padding: 10px;"); // Optional styling

        // Manually position the button inside AnchorPane
        courseButton.setLayoutX(20); // Set horizontal position
        courseButton.setLayoutY(i * 50 + 20); // Set vertical position with spacing between buttons
        class YPosition {
            double value;

            public YPosition(double initialValue) {
                this.value = initialValue;
            }

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }
        }
        // Add an event handler to handle clicks on the button
        courseButton.setOnAction(_ -> {
            // Get the course name from the button's text
            String courseName = courseButton.getText();

            // Query for sections that belong to this course
            // Assuming you have a method in your SectionDao to fetch sections by course name
            List<Section> sectionsForCourse = SectionDao.getSectionsByCourseName(courseName);

            // Clear any existing buttons in the display area (AnchorPane)
            modifyPane.getChildren().clear(); // Assuming you're displaying sections in 'modifyPane'

            // Initialize a YPosition object to hold the Y value
            YPosition currentY = new YPosition(10); // Starting Y position (can be adjusted as needed)
            double spacing = 20; // Space between buttons (in pixels)

            // Iterate through the sections and display them as buttons
            for (Section section : sectionsForCourse) {
                // Create a button for each section
                Button sectionButton = new Button("Section ID: " + section.getId() + ", Title: " + section.getTitle());

                // Set properties for the button (you can customize the appearance)
                sectionButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

                // Set the position for the button
                sectionButton.setLayoutX(10); // X position (adjust as needed)
                sectionButton.setLayoutY(currentY.getValue()); // Y position for stacking

                // Add an action for when the button is clicked
                sectionButton.setOnAction(_ -> {
                    // Fetch content related to the clicked section from the database
                    List<SectionContent> sectionContents;
                    try {
                        sectionContents = SectionContentDao.getContentBySectionId(section.getId());
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    // Assuming you have a TextArea for displaying the section content
                    TextArea contentArea = new TextArea();
                    contentArea.setEditable(false);  // Make the TextArea read-only
                    contentArea.setPrefHeight(200);  // Set height to fit content
                    contentArea.setPrefWidth(400);   // Set width for display purposes

                    // Display the section title and content in the TextArea
                    StringBuilder contentText = new StringBuilder("Title: " + section.getTitle() + "\n\n");

                    // Loop through the content for this section and display it
                    for (SectionContent content : sectionContents) {
                        contentText.append("Content Type: ").append(content.contentType()).append("\n");
                        contentText.append("Content: ").append(content.content()).append("\n\n");
                    }

                    // Set the content of the TextArea
                    contentArea.setText(contentText.toString());

                    // Clear any previous content and display the new TextArea
                    modifyPane.getChildren().clear();  // Optional: clear existing buttons and content
                    modifyPane.getChildren().add(contentArea);  // Add the new content area
                });


                // Add the button to the AnchorPane
                modifyPane.getChildren().add(sectionButton);

                // Update the Y position for the next button, ensuring a gap between them
                currentY.setValue(currentY.getValue() + sectionButton.getHeight() + spacing); // Adding spacing between buttons
            }
        });


        return courseButton;
    }

    public void modifyCourse(Course course){
        courseName.setText(course.getName());
        currentCourse = course;
    }

    public void addNewSection(MouseEvent event) {
        // Get the clicked button (the "Add New Section" button)
        Button clickedButton = (Button) event.getSource();

        // Change the text of the clicked button to the title from the text field
        clickedButton.setText(titleField.getText()); // Set button text to the title from the text field

        // Get the course ID (you will need to pass this information, or get it from somewhere)
        int courseId = currentCourse.getId(); // Replace with the actual course ID (use the right variable or context)

        // Get the section order (you can use the size of the sections list to determine the order)
        int sectionOrder = sections.size() + 1;

        // Create a new section
        Section newSection = new Section(courseId, titleField.getText(), "Description", sectionOrder);
        try {
            newSection = SectionDao.insertSection(newSection); // Save the section in the database
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        titleField.setText(""); // Clear the text field after setting the title
        sections.add(newSection); // Add the section to the list

        // Create a new "Add New Section" button for the next section
        Button newSectionButton = getNewSectionButton(clickedButton);

        // Set the event handler for the current button to display the section info when clicked
        Section finalNewSection = newSection;
        clickedButton.setOnMouseClicked(_ -> {
            // Display the section info, here we are just printing the section ID
            sectionLabel.setText("Section ID: " + finalNewSection.getId() + ", Title: " + finalNewSection.getTitle());
            currentSection = finalNewSection;
        });

        // Add the new button to the layout (e.g., AnchorPane or any layout you're using)
        modifyPane.getChildren().add(newSectionButton); // 'modifyPane' is your layout, like AnchorPane or VBox
    }

    @NotNull
    private Button getNewSectionButton(Button clickedButton) {
        Button newSectionButton = new Button("Add New Section");

        // Set the same size for the new button as the clicked button
        newSectionButton.setPrefWidth(clickedButton.getPrefWidth());
        newSectionButton.setPrefHeight(clickedButton.getPrefHeight());

        // Optional: Style the new button (if needed)
        newSectionButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

        // Position the new button below the clicked button
        double clickedButtonY = clickedButton.getLayoutY(); // Get the Y position of the clicked button
        newSectionButton.setLayoutX(clickedButton.getLayoutX()); // Set X position same as clicked button
        newSectionButton.setLayoutY(clickedButtonY + clickedButton.getHeight() + 10); // Y is below the clicked button with some padding

        // Set the event handler for the new "Add New Section" button
        newSectionButton.setOnMouseClicked(this::addNewSection); // The same handler as the original button
        return newSectionButton;
    }


    public void addContent() {
        String type = choice.getValue();
        String content = contentArea.getText();
        String title = titleField.getText();

        if (type == null || content.isEmpty() || title.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        SectionContentDao dao = new SectionContentDao();
        dao.insertSectionContent(currentSection.getId(), title, type, content, contentOrder++);

        titleField.clear();
        contentArea.clear();
        choice.getSelectionModel().clearSelection();
    }

    public void viewCourses() {
        CourseDao courseDao = new CourseDao(); // Create CourseDao instance
        List<Course> courses = courseDao.getCoursesByTutorId(tutor.getId()); // Get courses by tutor ID

        // Clear existing content in the AnchorPane before adding new courses
        vboxCourses.getChildren().clear(); // or AnchorPane, depending on your setup

        // Loop through each course and create a button for it
        for (int i = 0; i < courses.size(); i++) {
            Button courseButton = getCourseViewButton(courses, i);

            // Add the button to the AnchorPane
            vboxCourses.getChildren().add(courseButton); // Replace with your AnchorPane variable
        }

        // Refresh the layout of the AnchorPane
        vboxCourses.layout();
    }
}
