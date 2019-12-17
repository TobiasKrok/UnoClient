package com.tobias;


import com.tobias.gui.UnoController;
import com.tobias.gui.components.OpponentPlayerView;
import com.tobias.server.ServerConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
private static Logger LOGGER = LogManager.getLogger(Main.class.getName());
private static UnoController unoController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
       List<String> params = getParameters().getRaw();
        ServerConnection serverConnection =  startServerConnection(params.get(0), Integer.parseInt(params.get(1)));
        checkForId(serverConnection);
        if(serverConnection != null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/ClientGui.fxml"));
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root,1100,600));
            stage.setTitle("Uno ALPHA");
            root.getStylesheets().addAll(this.getClass().getResource("/css/style.css").toExternalForm());
            unoController = fxmlLoader.getController();
            unoController.newWorker(serverConnection.getHandlers());
            stage.show();
        }
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
        ses.schedule(new Runnable() {
            @Override
            public void run() {
                if(!serverConnection.idReceived()){
                    LOGGER.fatal("Server did not accept connection, no ID was received. Disconnecting..");
                    serverConnection.close();
                }
            }
        },10, TimeUnit.SECONDS);
    }

    public static UnoController getUnoController() {
        return unoController;
    }
}
