package com.tobias;


import com.tobias.gui.LobbyController;
import com.tobias.gui.UnoController;
import com.tobias.server.ServerConnection;
import com.tobias.server.handlers.AbstractCommandHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
private static Logger LOGGER = LogManager.getLogger(Main.class.getName());
private static UnoController unoController;
private static LobbyController lobbyController;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        loadLobbyWindow(stage);
        stage.show();
    }

    private void loadLobbyWindow(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/LobbyGui.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root,600,400));
        stage.setTitle("Uno Lobby");
        lobbyController = fxmlLoader.getController();

    }
    private void loadGameWindow(Stage stage, Map<String, AbstractCommandHandler> handlers) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/ClientGui.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root,1100,600));
        stage.setTitle("Uno");
        root.getStylesheets().addAll(this.getClass().getClassLoader().getResource("css/style.css").toExternalForm());
        unoController = fxmlLoader.getController();
        unoController.newWorker(handlers);
        setStageSizeChangedEvents(stage);
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
    public static LobbyController getLobbyController() {
        return lobbyController;}
}
