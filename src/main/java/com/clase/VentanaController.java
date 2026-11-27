package com.clase;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.clase.informes.imprimirPacientes;
import com.clase.informes.imprimirDoctores;
import com.clase.informes.imprimirCitas;


/**
 * Controlador de la ventana contenedora: coordina las vistas incluidas en las
 * pestanas y gestiona las acciones del menu y de la barra de herramientas.
 */
public class VentanaController {

    // Los fx:include de ventana.fxml inyectan aqui sus controladores asociados.
    @FXML
    private PacientesController pacientesController;

    @FXML
    private DoctoresController doctoresController;

    @FXML
    private CitasController citasController;

    // Comparte el controlador de citas para que las selecciones de paciente/doctor
    // actualicen el formulario y la tabla de citas.
    @FXML
    private void initialize() {
        pacientesController.setCitasController(citasController);
        doctoresController.setCitasController(citasController);
    }
    
    // Accion del menu Informes: delega la generacion y visualizacion del informe.
    @FXML
    private void informePacientes() {
        System.out.println("PULSADO INFORME PACIENTES");
        imprimirPacientes.mostrar();    
    }

    @FXML
    private void informeDoctores() {
        imprimirDoctores.mostrar();
    }

    @FXML
    private void informeCitas() {
        imprimirCitas.mostrar();
    }

    // Accion compartida por la opcion Salir y el boton de la barra.
    @FXML 
    private void salirApp(){
        Platform.exit();
    }

    // Carga una ventana independiente; su propio FXML crea AcercadeController.
    @FXML
    private void mostrarAcercade() throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clase/acercade.fxml"));
        
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Acerca de");
        stage.setScene(new Scene(root));
        stage.show();

        

    }


    @FXML
    private void mostrarNotas() throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/clase/notasadministrativas.fxml"));

        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Nueva Nota");
        stage.setScene(new Scene(root));
        stage.show();


    }


    // Abre el explorador de archivos de Windows; lo invocan menu y barra.
    @FXML 
    
    private void abrirDir(){
        try{
            new ProcessBuilder("explorer.exe").start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


}
