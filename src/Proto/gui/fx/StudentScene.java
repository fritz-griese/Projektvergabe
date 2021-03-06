package Proto.gui.fx;

import Proto.domain.Rating;
import Proto.domain.Student;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StudentScene {

    private Scene scene;
    private Student student;
    private MainWindow window;

    public StudentScene(int stageId, Student student, MainWindow window) {
        this.student = student;
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text sceneTitle = new Text("Student " + student.getName() + " " + student.getSurname());
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Button logout = new Button("Logout");
        HBox logoutBtn = new HBox(10);
        logoutBtn.getChildren().add(logout);
        grid.add(logoutBtn, 0, 1);

        TableView<Rating> table = getRatingPane(student);

        Button refresh = new Button("Aktualisieren");
        HBox refreshBtn = new HBox(10);
        refreshBtn.getChildren().add(refresh);
        grid.add(refreshBtn, 1, 1);

        grid.add(table, 0, 2, 2, 1);
        
        Text givenProject = new Text(getAssignedProject());
        grid.add(givenProject, 0, 3, 2, 1);
        
        logout.setOnAction(event -> window.logout(stageId));
        refresh.setOnAction(event -> givenProject.setText(getAssignedProject()));
        

        scene = new Scene(grid, 500, 500);
    }

    public TableView<Rating> getRatingPane(Student s){
        TableView<Rating> table = new TableView<>();
        table.setEditable(true);

        TableColumn<Rating, String> idCol = new TableColumn<>("Projekt");
        idCol.setCellValueFactory(new PropertyValueFactory<>("project"));

        TableColumn<Rating, Integer> ratingCol = new TableColumn<>("Bewertung (1 - 5)");
        ratingCol.setCellFactory((param) -> new RadioButtonCell<>(new Integer[]{1, 2, 3, 4, 5}));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        ratingCol.setOnEditCommit(
               t -> t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setGrade(t.getNewValue())
        );

        table.setItems(FXCollections.observableArrayList(s.getRatings()));
        table.getColumns().addAll(idCol, ratingCol);

        table.setPrefWidth(450);
        return table;
    }

    public String getAssignedProject() {
        String text = "Erhaltenes Projekt: ";
        if (student.getProject() == null) {
            text += "Die Projektvergaebe wurde noch nicht durchgeführt.";
        }
        else {
            text += student.getProject().toString();
        }
        return text;
    }

    public static class RadioButtonCell<S, T> extends TableCell<S, T> {
        private T[] choices;
        public RadioButtonCell(T[] choices){
            this.choices = choices;
        }
        @Override
        protected void updateItem(T item, boolean empty){
            super.updateItem(item, empty);
            if(!empty){
                HBox hb = new HBox(7);
                hb.setAlignment(Pos.CENTER);
                final ToggleGroup group = new ToggleGroup();

                for(T t : choices){
                    RadioButton radioButton = new RadioButton();
                    radioButton.setUserData(t);
                    radioButton.setToggleGroup(group);
                    hb.getChildren().add(radioButton);
                    if(t.equals(item)){
                        radioButton.setSelected(true);
                    }
                }

                group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void changed(ObservableValue<? extends Toggle> observable,
                                        Toggle oldValue, Toggle newValue) {
                        getTableView().edit(getIndex(), getTableColumn());
                        StudentScene.RadioButtonCell.this.commitEdit((T) newValue.getUserData());
                    }
                });
                setGraphic(hb);
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
}
