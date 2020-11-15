package com.tobias;


import com.tobias.gui.LobbyController;
import com.tobias.gui.UnoController;
import com.tobias.server.ServerConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
private static Logger LOGGER = LogManager.getLogger(Main.class.getName());
private static UnoController unoController;
private static LobbyController lobbyController;
private static Stage guiStage;


    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        guiStage = stage;
        loadLobbyWindow();
        stage.show();
    }

    public static void loadLobbyWindow() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("/LobbyGui.fxml"));
        Parent root = fxmlLoader.load();
        guiStage.setScene(new Scene(root,600,400));
        guiStage.setTitle("Uno Lobby");
        lobbyController = fxmlLoader.getController();

    }
    public static void loadGameWindow() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("/ClientGui.fxml"));
        Parent root = fxmlLoader.load();
        Platform.runLater(() -> guiStage.setScene(new Scene(root,1100,600)));
        Platform.runLater(() -> guiStage.setTitle("Uno"));
        root.getStylesheets().addAll(Main.class.getClassLoader().getResource("css/style.css").toExternalForm());
        unoController = fxmlLoader.getController();
        unoController.setCardImages(loadCardImages());
        unoController.newWorker(lobbyController.getCommandHandlers());
        setStageSizeChangedEvents(guiStage);
    }
    private static void setStageSizeChangedEvents(Stage stage) {
        stage.widthProperty().addListener((obv, oldVal, newVal) -> {
            unoController.adjustComponentXPosition(stage.getWidth());
        });
        stage.heightProperty().addListener((obv, oldVal, newVal) -> {
            unoController.adjustComponentYPosition(stage.getHeight());
        });
    }

    private static ServerConnection startServerConnection(String ip, int port) {
        ServerConnection serverConnection = null;
        try {
            Socket socket = new Socket(ip,port);
            LOGGER.info("Connected to server:" + socket.getRemoteSocketAddress().toString());
            serverConnection = new ServerConnection(socket);
            new Thread(serverConnection).start();

        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to server!",e);
        }
        return serverConnection;
    }
    private static void checkForId(ServerConnection serverConnection) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.schedule(() -> {
            if(!serverConnection.idReceived()){
                LOGGER.fatal("Server did not accept connection, no ID was received. Disconnecting..");
                serverConnection.close();
            }
        },10, TimeUnit.SECONDS);
    }

    public static UnoController getUnoController() {
        return unoController;
    }
    public static LobbyController getLobbyController() { return lobbyController;}

    private static Map<String, Image> loadCardImages() {
        Map<String, Image> cardImages = new HashMap<>();
        File imageDir = new File(Main.class.getResource("/images/cards").getFile());
        File[] files = imageDir.listFiles();
        if(files != null) {
            for (File f : files) {
                String cardName = f.getName().substring(0, f.getName().indexOf("."));
                Image cardImage = new Image(f.toURI().toString(), 200, 250, false, false);
                cardImages.put(cardName, cardImage);
            }
        }

        return cardImages;
    }
}
