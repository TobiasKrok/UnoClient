package com.tobias.gui;

import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;
import com.tobias.utils.IPValidator;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;


public class LobbyController {

    @FXML
    private TabPane tabPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField portField;
    @FXML
    private TextField addressField;
    @FXML
    private Button connectButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;
    @FXML
    private Label connectionStatusLabel;

    private static final Logger LOGGER = LogManager.getLogger(LobbyController.class.getName());

    public void initialize() {

    }

    //Validates user input and returns the first error it finds.
    private boolean validateUserInput() {

        if(!IPValidator.isIpv4(addressField.getText())) {
            setErrorMessage("IP address is not valid");
            return false;
        } else if(!IPValidator.isValidPort(portField.getText())) {
            setErrorMessage("Port number must be between 1 and 65535");
            return false;
        }
        else if(usernameField.getText().isEmpty()) {
            setErrorMessage("Username field cannot be empty");
            return false;
        } else if(!(usernameField.getText().matches("[A-Za-z0-9_]+"))) {
            setErrorMessage("Username cannot contain spaces or special characters");
            return false;
        }
        else if (usernameField.getText().length() > 16) {
            setErrorMessage("Username cannot be longer than 16 characters");
            return false;
        }
        return true;
    }

    public void onConnectClick() {
        if(validateUserInput()) {
            ServerConnection serverConnection = connect(addressField.getText(), Integer.parseInt(portField.getText()));
            if(serverConnection == null) {
                setErrorMessage("Could not connect to server! Please try again later");
            } else {
                if(checkForId(serverConnection)) {
                    setSuccessLabel("Connected to server");
                    serverConnection.write(new Command(CommandType.CLIENT_CONNECT,usernameField.getText()));
                }
            }
        }
    }

    private void setErrorMessage(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
        hideLabelAfterSeconds(3,errorLabel);
    }
    private void setSuccessLabel(String successMessage) {
        successLabel.setText(successMessage);
        hideLabelAfterSeconds(4, successLabel);
    }
    private void hideLabelAfterSeconds(int seconds, Label label) {
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(seconds));
        visiblePause.setOnFinished(event -> label.setVisible(false));
        visiblePause.play();
    }

    private ServerConnection connect(String ip, int port) {
        ServerConnection serverConnection = null;
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        try {
            connectionStatusLabel.setVisible(true);
            connectionStatusLabel.setText("Connecting..");
            ses.schedule()
            Socket socket = new Socket(ip,port);
            LOGGER.info("Connected to server:" + socket.getRemoteSocketAddress().toString());
            serverConnection = new ServerConnection(socket);
            new Thread(serverConnection).start();

        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to server!",e);
            connectionStatusLabel.setVisible(false);
        }
        return serverConnection;
    }
    private boolean checkForId(ServerConnection serverConnection){
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture future = ses.schedule(serverConnection::idReceived, 10, TimeUnit.SECONDS);

       boolean recieved = false;
       try {
           recieved = (Boolean) future.get();
       } catch (InterruptedException e) {
           LOGGER.error("ID check was interrupted!", e);
       } catch (ExecutionException e) {
           LOGGER.error("Error during SES execution!", e);
       }
        return recieved;
    }
}