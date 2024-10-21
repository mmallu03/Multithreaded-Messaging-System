// Description: This program handles a server-client messaging app to allow for communication through the server.

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


public class GuiClient extends Application {

    TextField usernameField;
    Button submitButton;
    Client clientConnection;
    ListView<String> usersListView;
    ArrayList<String> existingUsernames = new ArrayList<>();
    ArrayList<String> sentMessages = new ArrayList<>();

    private void printUsersList() {
        System.out.println("List of users:");
        for (String username : existingUsernames) {
            System.out.println(username);
        }

    }
    @Override
    public void start(Stage primaryStage) {
        Label title = new Label("Welcome to Social ChitChat");
        // Username input screen
        VBox usernameBox = new VBox(10);
        usernameBox.setStyle("-fx-background-color: turquoise;");
        Label titleLabel = new Label("Enter Username:");
        usersListView = new ListView<>();
        usersListView.getItems().addAll(existingUsernames);
        usernameField = new TextField();
        usernameField.setStyle("-fx-background-color: pink;");
        submitButton = new Button("Submit");
        clientConnection = new Client(data -> {
            Platform.runLater(() -> {
                usersListView.getItems().addAll(existingUsernames);
                // Handle received data
                System.out.println("Received data: " + data);
            });
        });
        clientConnection.start();
        submitButton.setOnAction(e -> {
            Message usernameMsg = new Message(usernameField.getText().trim());
            String username = usernameMsg.getUsername();

            if (!username.isEmpty()) {

                if (!existingUsernames.contains(username)) {
                    existingUsernames.add(username);
                    printUsersList();
                    primaryStage.setScene(createMainScreen(username));
                    clientConnection.clients.add(username);
                    System.out.println(clientConnection.clients);
                }
                else {
                    Alert invalidUser = new Alert(AlertType.ERROR);
                    invalidUser.setTitle("Invalid Username");
                    invalidUser.setHeaderText(null);
                    invalidUser.setContentText("Please enter a username:");
                    invalidUser.showAndWait();
                }
            }
            else {
                Alert emptyUser = new Alert(AlertType.ERROR);
                emptyUser.setTitle("Empty Username");
                emptyUser.setHeaderText(null);
                emptyUser.setContentText("Please enter a username:");
                emptyUser.showAndWait();
            }
        });

        usernameBox.getChildren().addAll(title,titleLabel, usernameField, submitButton);

        Scene usernameScene = new Scene(usernameBox, 400, 300);

        primaryStage.setScene(usernameScene);
        primaryStage.setTitle("Enter Username");
        primaryStage.show();
    }

    private Scene createMainScreen(String username) {
        // Create the main screen displaying all users and groups
        VBox mainScreen = new VBox(10);
        // Populate mainScreen with UI components to display users and groups
        Label usersLabel = new Label("Connected Users:");
        usersListView.getItems().setAll(existingUsernames);
        mainScreen.setStyle("-fx-background-color: turquoise;");
        Label groupsLabel = new Label("Groups:");
        ListView<String> groupsListView = new ListView<>();

        // Add groups to groupsListView
        CheckBox messageAllUsersCheckBox = new CheckBox("Message All Users");


        // Buttons for additional functionalities
        Button createGroupButton = new Button("Create Group");
        createGroupButton.setOnAction(e -> {
            Stage createGroup = new Stage();
            VBox createGroupBox = new VBox(10);
            Label createGroupLabel = new Label("Create Group");
            createGroupBox.getChildren().addAll(createGroupLabel);
            Scene createGroupScene = new Scene(createGroupBox, 600, 600);
            createGroup.setScene(createGroupScene);
            createGroup.setTitle("Create Group");
            createGroup.show();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Group");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter group name:");
            dialog.showAndWait().ifPresent(groupName -> {
                System.out.println("New group created: " + groupName);
                groupsListView.getItems().add(groupName);
            });
        });

        Button sendMessageButton = new Button("Send Message");
        sendMessageButton.setOnAction(e -> {
            if(messageAllUsersCheckBox.isSelected()){
                TextInputDialog sendDialog = new TextInputDialog();
                sendDialog.setTitle("Send Message");
                sendDialog.setHeaderText(null);
                sendDialog.setContentText("Enter message: ");
                sendDialog.showAndWait().ifPresent(message -> {
                    System.out.println("Message sent to all users: " + message);
                    clientConnection.send(message);
                    Stage primaryStage = (Stage) sendMessageButton.getScene().getWindow();
                    //primaryStage.setScene();
                    primaryStage.setScene(ConvoScreen(primaryStage, message, username));
                });
            }
            else {
                String recipient = usersListView.getSelectionModel().getSelectedItem();
                if (recipient != null) {
                    TextInputDialog sendDialog = new TextInputDialog();
                    sendDialog.setTitle("Send Message");
                    sendDialog.setHeaderText(null);
                    sendDialog.setContentText("Enter message: ");
                    sendDialog.showAndWait().ifPresent(message -> {
                        System.out.println("Message sent to " + recipient + ": " + message);
                        clientConnection.send(message);
                        //may need to call a screen to display textbox to send and a displayer of your conversation with other person
                        Stage primaryStage = (Stage) sendMessageButton.getScene().getWindow();
                        //primaryStage.setScene();
                        primaryStage.setScene(ConvoScreen(primaryStage, message, username));
                    });
                } else {
                    Alert sendAlert = new Alert(Alert.AlertType.ERROR);
                    sendAlert.setTitle("Error");
                    sendAlert.setHeaderText(null);
                    sendAlert.setContentText("Please select a user to send a message to.");
                    sendAlert.showAndWait();
                }
            }
        });
        Button allMessagesButton = new Button("View Messages");
        allMessagesButton.setOnAction(e -> viewMessages());

        mainScreen.getChildren().addAll(usersLabel, usersListView, groupsLabel, groupsListView, messageAllUsersCheckBox, createGroupButton, sendMessageButton, allMessagesButton);

        return new Scene(mainScreen, 600, 400);
    }
    private Scene ConvoScreen(Stage primaryStage, String message, String username){
        VBox CreateMessageScreen = new VBox(10);
        CreateMessageScreen.setStyle("-fx-background-color: turquoise;");
        Label MessageToLabel = new Label("Conversation:");
        ListView<String> convo = new ListView<>();
        convo.getItems().add(username+ " said: " + message);
        Label CreateMessageLabel = new Label("Send Message:");
        TextField messageField = new TextField();
        Button send = new Button("Send Message");
        sentMessages.add(username+ " said: " +message);
        send.setOnAction(e -> {
            String message1 = messageField.getText().trim();
            if (!message1.isEmpty()) {
                String sentMessage = username + " said: " + message1;
                convo.getItems().add(sentMessage);
                sentMessages.add(sentMessage);
                // Clear the messageField for the next message
                messageField.clear();
            } else {
                // Show an alert if the message is empty
                Alert emptyMessageAlert = new Alert(AlertType.ERROR);
                emptyMessageAlert.setTitle("Empty Message");
                emptyMessageAlert.setHeaderText(null);
                emptyMessageAlert.setContentText("Please enter a message.");
                emptyMessageAlert.showAndWait();
            }
        });
        Button returnHome = new Button("Return");
        returnHome.setOnAction(e -> primaryStage.setScene(createMainScreen(usernameField.getText())));
        CreateMessageScreen.getChildren().addAll(MessageToLabel, convo, CreateMessageLabel, messageField, send, returnHome);
        return new Scene(CreateMessageScreen, 600, 400);
    }


    private void viewMessages() {
        Stage messagesStage = new Stage();
        VBox messagesBox = new VBox(10);
        ListView<String> messagesListView = new ListView<>();
        messagesListView.getItems().addAll(sentMessages);
        messagesBox.getChildren().addAll(new Label("Sent Messages:"), messagesListView);
        Scene messagesScene = new Scene(messagesBox, 400, 300);
        messagesStage.setScene(messagesScene);
        messagesStage.setTitle("Sent Messages");
        messagesStage.show();
    }

    private Scene createMessageScreen(Stage primaryStage){
        VBox CreateMessageScreen = new VBox(10);
        Label MessageToLabel = new Label("To: ");
        Label CreateMessageLabel = new Label("Send Message:");
        TextField messageField = new TextField();
        ChoiceBox<String> messageTo = new ChoiceBox<>();
        messageTo.getItems().addAll("All Users", "Group", "Certain User");
        //need to figure out how to display list of all users, all groups so can message a group, a user, or everyone
        messageTo.setValue("All Users");
        Button send = new Button("Send Message");
        send.setOnAction(e -> {

        });
        Button returnHome = new Button("Return");
        returnHome.setOnAction(e -> primaryStage.setScene(createMainScreen(usernameField.getText())));
        CreateMessageScreen.getChildren().addAll(MessageToLabel, messageTo, CreateMessageLabel, messageField, send, returnHome);
        return new Scene(CreateMessageScreen, 600, 400);
    }







    public static void main(String[] args) {
        launch(args);
    }
}

