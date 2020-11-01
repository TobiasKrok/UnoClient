package com.tobias.gui;

import com.tobias.Main;
import com.tobias.game.ClientPlayer;
import com.tobias.game.Player;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;
import com.tobias.utils.IPValidator;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class LobbyController extends AbstractController {

    @FXML
    private TabPane tabPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField portField;
    @FXML
    private TextField addressField;
    @FXML
    private ToggleButton readyToggleButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;
    @FXML
    private Label connectionStatusLabel;
    @FXML
    private TableView<Player> playerListView;
    private List<Player> connectedPlayers;
    private AtomicBoolean connected = new AtomicBoolean();
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(3);
    private static final Logger LOGGER = LogManager.getLogger(LobbyController.class.getName());

    public LobbyController() {
        connectedPlayers = new ArrayList<>();
    }

    public void initialize() {
        // Disable when started
        readyToggleButton.setDisable(true);

        //todo lobby disconnect
    }

    //Validates user input and returns the first error it finds.
    private boolean validateUserInput() {

        if (!IPValidator.isIpv4(addressField.getText().trim())) {
            setErrorMessage("IP address is not valid");
            return false;
        } else if (!IPValidator.isValidPort(portField.getText().trim())) {
            setErrorMessage("Port number must be between 1 and 65535");
            return false;
        } else if (usernameField.getText().isEmpty()) {
            setErrorMessage("Username field cannot be empty");
            return false;
        } else if (!(usernameField.getText().trim().matches("[A-Za-z0-9_]+"))) {
            setErrorMessage("Username cannot contain spaces or special characters");
            return false;
        } else if (usernameField.getText().trim().length() > 16) {
            setErrorMessage("Username cannot be longer than 16 characters");
            return false;
        }
        return true;
    }

    public void onConnectClick() {
        if (validateUserInput() && !connected.get()) {
            connect(addressField.getText().trim(), Integer.parseInt(portField.getText().trim()));
            ses.schedule(() -> {
                if (connected.get()) {
                    setSuccessLabel("Connected to server");
                    worker.process(new Command(CommandType.CLIENT_CONNECT, usernameField.getText()));
                    readyToggleButton.setDisable(false);
                } else {
                    // if (checkForId(serverConnection)) {
                    setErrorMessage("Could not connect to server! Please try again later");
                    //   }
                }
                connectionStatusLabel.setVisible(false);
            }, 6, TimeUnit.SECONDS);

        }
    }

    public void onReadyClick() {
        boolean ready = readyToggleButton.isSelected();
        if (ready) {
            worker.process(new Command(CommandType.CLIENT_READY));
        } else {
            worker.process(new Command(CommandType.CLIENT_NOTREADY));
        }
        // There can only be one ClientPlayer
        connectedPlayers.stream()
                .filter(p -> p instanceof ClientPlayer)
                .forEach(c -> setPlayerStatus(c, ready));
    }

    public void setPlayerStatus(Player player, boolean ready) {
        player.setReady(ready);
        playerListView.getItems().clear();
        playerListView.getItems().setAll(connectedPlayers);
    }

    private void setErrorMessage(String error) {
        Platform.runLater(() -> errorLabel.setText(error));
        errorLabel.setVisible(true);
        hideLabelAfterSeconds(3, errorLabel);
    }

    private void setSuccessLabel(String successMessage) {
        Platform.runLater(() -> successLabel.setText(successMessage));
        successLabel.setVisible(true);
        hideLabelAfterSeconds(4, successLabel);
    }

    private void hideLabelAfterSeconds(int seconds, Label label) {
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(seconds));
        visiblePause.setOnFinished(event -> label.setVisible(false));
        visiblePause.play();
    }

    private void connect(String ip, int port) {
        connectionStatusLabel.setVisible(true);
        connectionStatusLabel.setText("Connecting..");
        ses.schedule(() -> {
            try {
                Socket socket = new Socket(ip, port);
                LOGGER.info("Connected to server:" + socket.getRemoteSocketAddress().toString());
                connected.set(true);
                ServerConnection serverConnection = new ServerConnection(socket);
                new Thread(serverConnection).start();
                // Initialize workers once the server connection has been made
                newWorker(serverConnection.getHandlers());
                Main.getUnoController().newWorker(serverConnection.getHandlers());
            } catch (IOException e) {
                LOGGER.fatal("Failed to connect to server!", e);
                connected.set(false);
            }
        }, 0, TimeUnit.SECONDS);

    }

    public List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    public void addPlayerToView(Player p) {
        connectedPlayers.add(p);
        playerListView.getItems().add(p);
    }

    private boolean checkForId(ServerConnection serverConnection) {
        ScheduledFuture<Boolean> future = ses.schedule(serverConnection::idReceived, 10, TimeUnit.SECONDS);

        boolean recieved = false;
        try {
            recieved = future.get();
        } catch (InterruptedException e) {
            LOGGER.error("ID check was interrupted!", e);
        } catch (ExecutionException e) {
            LOGGER.error("Error during SES execution!", e);
        }
        ses.shutdown();
        return recieved;
    }


}
