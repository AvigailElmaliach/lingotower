package com.lingotower.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LingotowerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // יצירת ממשק גרפי
        Label label = new Label("JavaFX עובד!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 300, 200);

        // הגדרת כותרת והצגת חלון
        primaryStage.setTitle("לקוח לינגו טאוור");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
}
