package javafxcrud;

import beans.Customer;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


public class Javafxcrud extends Application {
    
    private final Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
    //private final SessionFactory sf = cfg.buildSessionFactory();
    private final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
    private final SessionFactory sf = cfg.configure().buildSessionFactory(serviceRegistry);
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        startApp();
        primaryStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
        });
    }
    
    void startApp() {
        
        Button create = new Button();
        create.setText("Create New Customer");
        
        create.setOnAction((ActionEvent event) -> {
            create();
        });
        
        List<Customer> data = read();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        Label snHeader = new Label("SN");
        Label firstnameHeader = new Label("First Name");
        Label lastnameHeader = new Label("Last Name");
        Label emailHeader = new Label("Email");
        Label phoneHeader = new Label("Phone");
        
        snHeader.setFont(new Font("Arial", 15));
        firstnameHeader.setFont(new Font("Arial", 15));
        lastnameHeader.setFont(new Font("Arial", 15));
        emailHeader.setFont(new Font("Arial", 15));
        phoneHeader.setFont(new Font("Arial", 15));
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        
        gridPane.add(snHeader, 0, 0);
        gridPane.add(firstnameHeader, 1, 0);
        gridPane.add(lastnameHeader, 2, 0);
        gridPane.add(emailHeader, 3, 0);
        gridPane.add(phoneHeader, 4, 0);
        
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        
        gridPane.add(separator, 0,1,7,1);
        
        int i = 2;
        for(Customer customer : data) {
 
            Label sn = new Label(Integer.toString(i-1));
            Label firstname = new Label(customer.getFirstName());
            Label lastname = new Label(customer.getLastName());
            Label email = new Label(customer.getEmail());
            Label phone = new Label(customer.getPhone());
            Button edit = new Button("Edit");
            edit.setOnAction((ActionEvent event) -> {
                update(customer.getId());
            });
            Button delete = new Button("Delete");
            delete.setOnAction((ActionEvent event) -> {
                delete(customer.getId());
            });
            gridPane.add(sn, 0, i, 1, 1);
            gridPane.add(firstname, 1, i, 1, 1);
            gridPane.add(lastname, 2, i, 1, 1);
            gridPane.add(email, 3, i, 1, 1);
            gridPane.add(phone, 4, i, 1, 1);
            gridPane.add(edit, 5, i, 1, 1);
            gridPane.add(delete, 6, i, 1, 1);
            ++i;
            
        } 
        
        vbox.getChildren().addAll(create, gridPane);
 
        StackPane root = new StackPane(); 
        root.getChildren().addAll(vbox);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("JavaFX CRUD");
        primaryStage.setScene(scene);
        primaryStage.hide();
        primaryStage.show();
        
}
    
    public void create(){
        Stage createStage = new Stage();
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
		
        TextField FirstName, LastName, Email, Phone;
        FirstName = new TextField();
        FirstName.setTooltip(new Tooltip("Enter First Name"));
        FirstName.setPromptText("First Name");
        FirstName.setMaxWidth(200);

        LastName = new TextField();
        LastName.setTooltip(new Tooltip("Enter Last Name"));
        LastName.setPromptText("Last Name");
        LastName.setMaxWidth(200);

        Email = new TextField();
        Email.setTooltip(new Tooltip("Enter Email"));
        Email.setPromptText("Email");
        Email.setMaxWidth(200);

        Phone = new TextField();
        Phone.setTooltip(new Tooltip("Enter Phone"));
        Phone.setPromptText("Phone");
        Phone.setMaxWidth(200);
        
        Button savebtn = new Button("Save");
        savebtn.setTooltip(new Tooltip("Save"));

        savebtn.setOnAction(event ->{
            Session session = sf.openSession();
            session.beginTransaction();
            Customer customer = new Customer(FirstName.getText(), LastName.getText(), Email.getText(), Phone.getText());
            session.save(customer);

            session.getTransaction().commit();
            session.close();
            startApp();
            
            ((Node)(event.getSource())).getScene().getWindow().hide();
        });
        
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(FirstName, LastName, Email, Phone, savebtn);
        vbox.setPadding(new Insets(10));		
        root.getChildren().add(vbox);

        createStage.setTitle("New Customer");
        createStage.setScene(scene);
        createStage.show();   
    }
        
    public List<Customer> read(){
         Session session = sf.openSession();
         List<Customer> data = session.createCriteria(Customer.class).list();
         session.close();  
         return data;
    }    
    
    public void update(int id){
        Session session = sf.openSession();
        Customer customer = (Customer)session.get(Customer.class, id); 
        
        Stage updateStage = new Stage();
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
		
        TextField FirstName, LastName, Email, Phone;
        FirstName = new TextField(customer.getFirstName());
        FirstName.setTooltip(new Tooltip("Enter First Name"));
        FirstName.setPromptText("First Name");
        FirstName.setMaxWidth(200);

        LastName = new TextField(customer.getLastName());
        LastName.setTooltip(new Tooltip("Enter Last Name"));
        LastName.setPromptText("Last Name");
        LastName.setMaxWidth(200);

        Email = new TextField(customer.getEmail());
        Email.setTooltip(new Tooltip("Enter Email"));
        Email.setPromptText("Email");
        Email.setMaxWidth(200);

        Phone = new TextField(customer.getPhone());
        Phone.setTooltip(new Tooltip("Enter Mobile Number"));
        Phone.setPromptText("Phone");
        Phone.setMaxWidth(200);
        
        Button savebtn = new Button("Save");
        savebtn.setTooltip(new Tooltip("Save"));

        savebtn.setOnAction(event ->{
            
            session.beginTransaction();

            customer.setFirstName(FirstName.getText());
            customer.setLastName(LastName.getText());
            customer.setEmail(Email.getText());
            customer.setPhone(Phone.getText());
            session.update(customer); 

            session.getTransaction().commit();
            session.close();
            
            startApp();
            
                    
            ((Node)(event.getSource())).getScene().getWindow().hide();
        });
        
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(FirstName, LastName, Email, Phone, savebtn);
        vbox.setPadding(new Insets(10));		
        root.getChildren().add(vbox);

        updateStage.setTitle("Edit Customer");
        updateStage.setScene(scene);
        updateStage.show();
    }
    
    public void delete(int id){
        Session session = sf.openSession();
        session.beginTransaction();
        Customer customer = (Customer)session.get(Customer.class, id);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Deleting "+ customer.getFirstName()+ " "+customer.getLastName());
        alert.setHeaderText("Are you Sure, You want to delete "+ customer.getFirstName()+ " "+customer.getLastName());
        alert.setContentText("This action can't be undone!");
        Optional result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            session.delete(customer);
            session.getTransaction().commit();
            session.close();
            startApp();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}