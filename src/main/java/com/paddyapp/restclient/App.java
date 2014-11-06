package com.paddyapp.restclient;

import java.io.IOException;

import com.paddyapp.restclient.controller.MainController;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
 
public class App extends Application {
    
	private static Stage primaryStage;
    private Pane rootLayout;
    private FXMLLoader loader;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("OAuth REST Client");

        initRootLayout();
    }

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		MainController controller = (MainController)loader.getController();
		controller.onClose();
	}

	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/fxml/main.fxml"));
            rootLayout = (Pane) loader.load();

            // Show the scene containing the root layout.
            final Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            primaryStage.getIcons().add(new Image("/images/rest-client-icon.png"));
            
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
