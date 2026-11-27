package com.clase;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import com.clase.modelo.Nota;
import com.clase.persistencia.NotaDAOMySQL;

 

/** Controla las acciones de la ventana secundaria acercade.fxml. */
public class NotasAdministrativasController {

    @FXML
    private TextArea nota;

    private final NotaDAOMySQL notaDAO = new NotaDAOMySQL();


    @FXML
    public void guardarNota(){

        if (nota.getText().trim().isEmpty()) {
            mostrarAviso("Completa la nota para poder guardar.");
            return;
        }

        Nota notaNueva = new Nota(
            nota.getText().trim());

        try {

            notaDAO.guardarNota(notaNueva);

            Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
            alert3.setTitle("Información");
            alert3.setHeaderText(null);
            alert3.setContentText("Nota guardada correctamente");
            alert3.showAndWait();
            limpiarCampos();

        } catch (SQLException e) {
            mostrarError("No se pudo eliminar la cita", e);
        }



    }

    @FXML 
    private void cerrarNotasAdministrativas(ActionEvent event) {

        // Cierra la ventana actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        stage.close();

        

    }


    @FXML
    private void limpiarCampos() {
        nota.clear();
    } 


    private void mostrarAviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}