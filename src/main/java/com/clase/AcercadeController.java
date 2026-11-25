package com.clase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

/** Controla las acciones de la ventana secundaria acercade.fxml. */
public class AcercadeController {

    // Obtiene la ventana desde el control que disparo el evento y la cierra.
    @FXML 
    private void cerrarAcercade(ActionEvent event) {

        // Cierra la ventana actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        stage.close();

        

    }
}
