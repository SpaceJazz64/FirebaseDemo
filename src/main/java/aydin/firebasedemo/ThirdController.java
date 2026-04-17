package aydin.firebasedemo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ThirdController {
    @FXML
    private TextField emailTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Button readButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button switchSecondaryViewButton;

    @FXML
    private Button writeButton;

    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize() {

        AccessDataView accessDataViewModel = new AccessDataView();
        nameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }


    @FXML
    void readButtonClicked(ActionEvent event) {
        readFirebase();
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }


    @FXML
    void writeButtonClicked(ActionEvent event) {
        addData();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        DemoApp.setRoot("secondary");
    }
    public boolean readFirebase()
    {
        key = false;

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("Persons").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                System.out.println("Getting (reading) data from firabase database....");
                listOfUsers.clear();
                for (QueryDocumentSnapshot document : documents)
                {
                    outputTextArea.setText(outputTextArea.getText()+ document.getData().get("Name")+ " , Age: "+
                            document.getData().get("Age")+ " \n ");
                    System.out.println(document.getId() + " => " + document.getData().get("Name"));
                    person  = new Person(String.valueOf(document.getData().get("Name")),
                            Integer.parseInt(document.getData().get("Age").toString()));
                    listOfUsers.add(person);
                }
            }
            else
            {
                System.out.println("No data");
            }
            key=true;

        }
        catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        return key;
    }

    public boolean registerUser() {

        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String name = nameTextField.getText().trim();

        // PHONE VALIDATION
        if (!phone.startsWith("+")) {
            System.out.println("Phone number must start with + and country code (e.g., +1234567890)");
            return false;
        }

        String digitsOnly = phone.substring(1);

        if (!digitsOnly.matches("\\d+")) {
            System.out.println("Phone number must contain only digits after +");
            return false;
        }

        if (digitsOnly.length() < 10 || digitsOnly.length() > 15) {
            System.out.println("Phone number must be between 10 and 15 digits");
            return false;
        }

        // (Optional but smart) EMAIL CHECK
        if (!email.contains("@")) {
            System.out.println("Invalid email format");
            return false;
        }

        // (Optional) PASSWORD CHECK
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters");
            return false;
        }

        // ONLY CREATE REQUEST AFTER VALIDATION PASSES
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setPhoneNumber(phone)
                .setDisplayName(name)
                .setDisabled(false);

        try {
            UserRecord userRecord = DemoApp.fauth.createUser(request);

            System.out.println("Successfully created new user with Firebase Uid: "
                    + userRecord.getUid());

            return true;

        } catch (FirebaseAuthException ex) {
            System.out.println("Error creating user:");
            outputTextArea.setText("Phone number must be between 10 and 15 digits");
            ex.printStackTrace();
            return false;
        }
    }

    public void addData() {

        DocumentReference docRef = DemoApp.fstore.collection("Persons").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameTextField.getText());
        data.put("Age", 0);
        data.put("Phone", phoneTextField.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }
}
