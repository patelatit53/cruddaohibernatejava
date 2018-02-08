package crudtableview;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import beans.User;
import com.sun.prism.impl.Disposer.Record;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class crudtableview extends Application {

    private TextField firstName, lastName, email;
    private Stage primaryStage;
    private final Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
//    private final SessionFactory sf = cfg.buildSessionFactory();
    private final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
    private final SessionFactory sf = cfg.configure().buildSessionFactory(serviceRegistry);

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        startApp();
    }

    private void startApp() {
        Session session1 = sf.openSession();
        List<User> users = session1.createCriteria(User.class).list();
        session1.close();

        TableView<User> table = new TableView<>();
        ObservableList<User> data = FXCollections.observableArrayList(users);

        table.setEditable(true);

        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setMinWidth(200);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setOnEditCommit(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> t) {
                int id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                Session session = sf.openSession();
                session.beginTransaction();
                User user = (User) session.get(User.class, id);
                user.setFirstName(t.getNewValue());
                session.update(user);
                session.getTransaction().commit();
                session.close();
            }
        }
        );

        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setMinWidth(200);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> t) {

                int id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                Session session = sf.openSession();
                session.beginTransaction();
                User user = (User) session.get(User.class, id);
                user.setLastName(t.getNewValue());
                session.update(user);
                session.getTransaction().commit();
                session.close();

            }
        }
        );

        TableColumn emailCol = new TableColumn("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setOnEditCommit(new EventHandler<CellEditEvent<User, String>>() {
            @Override
            public void handle(CellEditEvent<User, String> t) {

                int id = t.getTableView().getItems().get(t.getTablePosition().getRow()).getId();
                Session session = sf.openSession();
                session.beginTransaction();
                User user = (User) session.get(User.class, id);
                user.setEmail(t.getNewValue());
                session.update(user);
                session.getTransaction().commit();
                session.close();
            }
        }
        );

        TableColumn deleteCol = new TableColumn<>("Action");
        deleteCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Record, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });
        deleteCol.setCellFactory(new Callback<TableColumn<Record, Boolean>, TableCell<Record, Boolean>>() {
            @Override
            public TableCell<Record, Boolean> call(TableColumn<Record, Boolean> p) {
                return new ActionButton();
            }
        });

        table.setItems(data);
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol, deleteCol);

        firstName = new TextField();
        firstName.setPromptText("First Name");
        firstName.setMinWidth(firstNameCol.getPrefWidth());
        lastName = new TextField();
        lastName.setMinWidth(lastNameCol.getPrefWidth());
        lastName.setPromptText("Last Name");
        email = new TextField();
        email.setMinWidth(emailCol.getPrefWidth());
        email.setPromptText("Email");

        final Button addButton = new Button("Add");
        addButton.setOnAction((ActionEvent e) -> {
            User user = new User();
            user.setFirstName(firstName.getText());
            user.setLastName(lastName.getText());
            user.setEmail(email.getText());
            Session session = sf.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            session.close();
            startApp();
        });
 final Button viewButton = new Button("View");
 viewButton.setOnAction((ActionEvent e) -> {
     startApp();
 });
        HBox hb = new HBox();
        hb.getChildren().addAll(firstName, lastName, email, addButton,viewButton);
        hb.setSpacing(3);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(table, hb);

        StackPane root = new StackPane();
        root.getChildren().addAll(vbox);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Table View Sample");

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class ActionButton extends TableCell<Record, Boolean> {

        final Button deleteButton = new Button("Delete");

        ActionButton() {

            deleteButton.setOnAction((ActionEvent t) -> {
                User currentUser = (User) ActionButton.this.getTableView().getItems().get(ActionButton.this.getIndex());
                Session session = sf.openSession();
                session.beginTransaction();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Deleting " + currentUser.getFirstName() + " " + currentUser.getLastName());
                alert.setHeaderText("Are you Sure, You want to delete " + currentUser.getFirstName() + " " + currentUser.getLastName());
                alert.setContentText("This action can't be undone!");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    session.delete(currentUser);
                    session.getTransaction().commit();
                    session.close();
                    startApp();
                }
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(deleteButton);
            }
        }
    }
}