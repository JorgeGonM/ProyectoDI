package com.clase;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada JavaFX: carga la ventana principal definida en ventana.fxml
 * y aplica la hoja de estilos comun a la aplicacion.
 */
public class App extends Application {

    @Override 
    public void start(Stage stage) throws IOException{

        // FXMLLoader crea la vista y su VentanaController a partir de fx:controller.
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/clase/ventana.fxml")
        );

        Scene scene = new Scene(loader.load(), 1280, 800);
        stage.setTitle("Sanitarios Teis");

        // El CSS se aplica a la escena completa, incluidas las vistas incluidas por FXML.
        scene.getStylesheets().add(
            getClass().getResource("/com/clase/css/estilos.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

    }

    public static void main(String[] args) {
        // Inicia el ciclo de vida de JavaFX y acaba invocando start().
        launch(args);
    }


    /*
    dnipac
    apelpac
    nompac
    nacpac
    movilpac
    
    emailpac
    dirpac
    propac
    munipac
    
    */

}