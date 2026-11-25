package com.clase;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.clase.modelo.Paciente;
import com.clase.persistencia.PacienteDAOMySQL;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Gestiona el formulario de pacientes: validacion, carga de datos de recursos,
 * operaciones CRUD mediante PacienteDAOMySQL y comunicacion con las citas.
 */
public class PacientesController implements Initializable{
   
    // Estos nombres deben coincidir con los fx:id de pacientes.fxml.
    @FXML 
    private TextField dnipac, apelpac, nompac, movilpac, emailpac, dirpac;

    @FXML 
    private DatePicker nacpac;
    
    @FXML 
    private ComboBox<String> propac, munipac;

    @FXML 
    private Button btnguardarpac, btndelpac, btnLimpiarPac, btnBuscarPac;

    @FXML
    private TableView<Paciente> tablaPacientes;

    @FXML 
    private TableColumn<Paciente, String> coldnipac, colapelpac, colnompac, colmovilpac, colpropac, colmunipac;
   
    boolean pacienteExiste = false;
    private CitasController citasController;

    // Se establece desde VentanaController despues de cargar los fx:include.
    public void setCitasController(CitasController citasController) {
        this.citasController = citasController;
    }

    // JavaFX llama a initialize cuando ya ha inyectado los controles del FXML.
    @Override 
    public void initialize(URL url, ResourceBundle rb){

        dnipac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                comprobarDNI();
            }
        });

        nompac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                String nombre = letrasCapitales(nompac.getText());
                nompac.setText(nombre);
            }
        });

        apelpac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                String apellidos = letrasCapitales(apelpac.getText());
                apelpac.setText(apellidos);
            }
        });

        movilpac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                comprobarMovil();
            }
        });

        emailpac.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                comprobarEmail();
            }
        });

        cargarProvincias();
        propac.setOnAction(e -> {
            cargarMunicipios();
            // cargarPacientesPorProvincia(); // Filtro desactivado temporalmente.
        });

        //Vinculamos las columnas de la tabla con los atributos del modelo Paciente

        coldnipac.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colapelpac.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colnompac.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colmovilpac.setCellValueFactory(new PropertyValueFactory<>("movil"));
        colpropac.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        colmunipac.setCellValueFactory(new PropertyValueFactory<>("municipio")); 
        


        cargarPacientes();

        //Carga un paciente al seleccionarlo en la tabla en el formaulario para poder modificarlo o eliminarlo

        tablaPacientes.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> {
            if (nuevo != null) {
                cargarPaciente();
                if (citasController != null) {
                    citasController.cargarDatosPaciente(nuevo);
                }
            }
        });



    }

    // Valida el texto del campo al perder el foco y normaliza las letras a mayusculas.
    @FXML 
    private void comprobarDNI(){
        String dni = dnipac.getText().trim().toUpperCase();
        if(dni.isEmpty())
            return;

        if(validarDNI(dni)){
            dnipac.setStyle("");
            dnipac.setText(dni);
        }else{
            dnipac.setStyle("-fx-border-color: red;");
            dnipac.setText("");
        }
        
    }

    // Comprueba formato y letra de control de un DNI o NIE.
    private boolean validarDNI(String documento){
        if (documento.matches("\\d{8}[A-Z]")) {

        int numero = Integer.parseInt(documento.substring(0, 8));
        char letra = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(numero % 23);

        return letra == documento.charAt(8);
        }

        if (documento.matches("[XYZ]\\d{7}[A-Z]")) {

            String nie = documento
                    .replace("X", "0")
                    .replace("Y", "1")
                    .replace("Z", "2");

            int numero = Integer.parseInt(nie.substring(0, 8));
            char letra = "TRWAGMYFPDXBNJZSQVHLCKE".charAt(numero % 23);

            return letra == documento.charAt(8);
        }

        return false;
    }

    // Normaliza cada palabra para mostrar nombres y apellidos con inicial mayuscula.
    private String letrasCapitales(String texto){

        String[] palabras = texto.toLowerCase().trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for(String palabra: palabras){
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0))).append(palabra.substring(1)).append(" ");
            }
        }

        return resultado.toString().trim();

    }

    // Comprueba el movil cuando el campo pierde el foco.
    @FXML
    private void comprobarMovil(){
        String movil = movilpac.getText().trim().toUpperCase();
        if(movil.isEmpty())
            return;

        if(validarMovil(movil)){
            movilpac.setStyle("");
            movilpac.setText(movil);
        }else{
            movilpac.setStyle("-fx-border-color: red;");
            movilpac.setText("");
        }
        
    }

    @FXML
    private boolean validarMovil(String movil){
        
        return movil.matches("[67][0-9]{8}");
    }
     
    // Comprueba el email cuando el campo pierde el foco.
    @FXML
    private void comprobarEmail(){
        String email = emailpac.getText().trim();
        if(email.isEmpty())
            return;

        if(validarEmail(email)){
            emailpac.setStyle("");
            emailpac.setText(email);
        }else{
            emailpac.setStyle("-fx-border-color: red;");
            emailpac.setText("");
        }
        
    }

    @FXML
    private boolean validarEmail(String email){
        
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");    
    
    }

    @FXML 
    private void cargarProvincias(){
        
        // Abrimos el fichero JSON que está dentro de resources
        // usamos la clase de java InputStrem que lee datos en este caso de un fichero

        InputStream is = getClass().getResourceAsStream("/com/clase/data/municipios.json");

 
        // Leemos el JSON y lo convertimos en un objeto JsonObject
        JsonObject json = JsonParser.parseReader(
                new InputStreamReader(is)
        ).getAsJsonObject();

 
        // Obtenemos el array "provincias" del JSON
        JsonArray provincias = json.getAsJsonArray("provincias");

        // Recorremos todas las provincias
        for (var provincia : provincias) {

            // Cada elemento del array es un objeto JSON
            JsonObject p = provincia.getAsJsonObject();

            // Obtenemos el nombre de la provincia
            // y lo añadimos al ComboBox
            propac.getItems().add(
                    p.get("nm").getAsString()
            );
        }
    }

    @FXML
    private void cargarMunicipios() {

        // Abrimos de nuevo el fichero JSON
        InputStream is = getClass()
                .getResourceAsStream("/com/clase/data/municipios.json");

        // Convertimos el contenido del fichero en un JsonObject
        JsonObject json = JsonParser.parseReader(
                new InputStreamReader(is)
        ).getAsJsonObject();

        // Obtenemos los dos arrays que necesitamos
        JsonArray provincias = json.getAsJsonArray("provincias");
        JsonArray municipios = json.getAsJsonArray("municipios");

        // Obtenemos el nombre de la provincia seleccionada
        String nombreProvincia = propac.getValue();

        // Variable donde guardaremos el código de la provincia
        String idProvincia = "";

        // Recorremos las provincias
        for (var provincia : provincias) {

            JsonObject p = provincia.getAsJsonObject();

            // Comprobamos si es la provincia seleccionada
            if (p.get("nm").getAsString().equals(nombreProvincia)) {

                // Obtenemos su código
                idProvincia = p.get("id").getAsString();

                // Ya hemos encontrado la provincia
                break;
            }
        }

        // Eliminamos los municipios que pudiera haber
        // de una selección anterior muy importante sino agrega municipios
        munipac.getItems().clear();

        // Recorremos todos los municipios
        for (var municipio : municipios) {

            JsonObject m = municipio.getAsJsonObject();

            // Comprobamos los dos primeros caracteres del código
            if (m.get("id").getAsString().startsWith(idProvincia)) {

                // Si pertenecen a la provincia,
                // añadimos su nombre al ComboBox
                munipac.getItems().add(
                        m.get("nm").getAsString()
                );
            }
        }

    }


    // Convierte el formulario en un Paciente y decide entre alta o modificacion
    // usando pacienteExiste; al terminar actualiza tabla y formulario.
    @FXML
    private void guardarPaciente(){

        // Comprobamos que se haya intriducido la fecha
        if(nacpac.getValue() == null){
            System.out.println("Deber introducir la fecha de nacimiento: ");
            return;
        }

        /*
        if (nacpac.getValue().isAfter(LocalDate.now())) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Fecha de nacimiento no válida");
            alerta.setHeaderText(null);
            alerta.setContentText("La fecha de nacimiento no puede ser posterior a hoy.");
            alerta.showAndWait();
            return;
        }
        */

    
        String dni = dnipac.getText();
        String apellidos = apelpac.getText();
        String nombre = nompac.getText();

        LocalDate fechaNacimiento = nacpac.getValue();

        String telefono = movilpac.getText();
        String email = emailpac.getText();
        String direcion = dirpac.getText();

        String provincia = propac.getValue();
        String municipio = munipac.getValue();

        //Creamos el objeto paciente
        Paciente paciente = new Paciente(dni, apellidos, nombre, telefono, email, fechaNacimiento, direcion, provincia, municipio);


        if(pacienteExiste){
            

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText(null);
            alert.setContentText("¿Desea modificar el paciente?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                //Si el paciente ya existe, lo actualizamos en la BBDD
                PacienteDAOMySQL dao = new PacienteDAOMySQL();
                dao.modificarPaciente(paciente.getDni(), paciente);

                //Alerta
                Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
                alert3.setTitle("Información");
                alert3.setHeaderText(null);
                alert3.setContentText("Paciente modificado guardado");
                alert3.showAndWait();

            }else{
                return;
            }



            pacienteExiste = false;
            buscaPacDni();


        }else{
            

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText(null);
            alert.setContentText("¿Desea guardar el paciente?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
            
                //Si el paciente no existe, lo guardamos en la BBDD
                PacienteDAOMySQL dao = new PacienteDAOMySQL();
                try {
                    dao.guardarPaciente(paciente);
                } catch (SQLException e) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo guardar el paciente");
                    error.setContentText(e.getMessage());
                    error.showAndWait();
                    return;
                }

                //Alerta
                Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
                alert3.setTitle("Información");
                alert3.setHeaderText(null);
                alert3.setContentText("Paciente guardado correctamente");
                alert3.showAndWait();
            }else{
                return;
            }

        }

        //Recargamos la tabla de pacientes
        cargarPacientes();

        limpiarCampos();

        System.out.println("======== PACIENTE =========");
        System.out.println("DNI: " + dni);        
        System.out.println("Apellidos: " + apellidos);        
        System.out.println("Nombre: " + nombre);        
        System.out.println("Fecha nacimiento: " + fechaNacimiento);        
        System.out.println("Teléfono: " + telefono);        
        System.out.println("Email: " + email);        
        System.out.println("Dirección: " + direcion);        
        System.out.println("Provincia: " + provincia);        
        System.out.println("Municipio: " + municipio);        
        System.out.println("===========================");        


    }


    // Consulta el DAO y reemplaza las filas visibles de la tabla.
    @FXML 
    private void cargarPacientes(){

        //Creamos el DAO
        PacienteDAOMySQL dao = new PacienteDAOMySQL();

        //Obetenemos los pacientes de la base de datos
        List<Paciente> pacientes = dao.cargarPacientes();

        //Los mostramos en la tabla
        tablaPacientes.getItems().setAll(pacientes);

    }

    // Filtro de provincia desactivado temporalmente; se conserva como referencia.
    /*
    private void cargarPacientesPorProvincia() {
        String provincia = propac.getValue();
        if (provincia == null || provincia.trim().isEmpty()) {
            cargarPacientes();
            return;
        }

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        try {
            List<Paciente> pacientes = dao.buscarPacientesPorProvincia(provincia);
            tablaPacientes.getItems().setAll(pacientes);
        } catch (SQLException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudieron filtrar los pacientes");
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }
    */

    // Elimina el paciente seleccionado tras pedir confirmacion y recarga la tabla.
    @FXML 
    private void eliminarPaciente(){

       Paciente seleccionado = tablaPacientes.getSelectionModel().getSelectedItem();

        if(seleccionado == null) {
            return;
        }


        //Alerta
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea eliminar el paciente?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            PacienteDAOMySQL dao = new PacienteDAOMySQL();
            dao.eliminarPaciente(seleccionado.getDni());

            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Aviso");
            alert2.setHeaderText(null);
            alert2.setContentText("Paciente dado de baja correctamente");
            alert2.showAndWait();

        }else{
            return;
        }


        //Recargamos la tabla de pacientes
        cargarPacientes();

    }


    // Busca el registro completo del paciente seleccionado y rellena el formulario.
    @FXML 
    private void cargarPaciente(){
        Paciente pacienteSelect = tablaPacientes.getSelectionModel().getSelectedItem();

        if(pacienteSelect == null){
            return;
        } else {
            pacienteExiste = true;
        }

        PacienteDAOMySQL dao = new PacienteDAOMySQL();

        Paciente paciente = dao.buscarPaciente(pacienteSelect.getDni());

        if(paciente ==null){
            return;
        }

        dnipac.setText(paciente.getDni());
        apelpac.setText(paciente.getApellidos());
        nompac.setText(paciente.getNombre());
        movilpac.setText(paciente.getMovil());
        emailpac.setText(paciente.getEmail());
        nacpac.setValue(paciente.getNacimiento());
        dirpac.setText(paciente.getDireccion());
        propac.setValue(paciente.getProvincia());
        cargarMunicipios();
        munipac.setValue(paciente.getMunicipio());
        
    }


    // Busca por DNI y, si existe, rellena el formulario y sincroniza la vista de citas.
    @FXML 
    private void buscaPacDni(){

        PacienteDAOMySQL dao = new PacienteDAOMySQL();
        Paciente paciente = dao.buscaPacDni(dnipac.getText());

        if(paciente ==null){
            Alert alert2 = new Alert(Alert.AlertType.WARNING);
            alert2.setTitle("Aviso");
            alert2.setHeaderText(null);
            alert2.setContentText("Paciente no existe en la BBDD");
            alert2.showAndWait();
            return;
        }else {
            pacienteExiste = true;
        }


        dnipac.setText(paciente.getDni());
        apelpac.setText(paciente.getApellidos());
        nompac.setText(paciente.getNombre());
        movilpac.setText(paciente.getMovil());
        emailpac.setText(paciente.getEmail());
        nacpac.setValue(paciente.getNacimiento());
        dirpac.setText(paciente.getDireccion());
        propac.setValue(paciente.getProvincia());
        cargarMunicipios();
        munipac.setValue(paciente.getMunicipio());

        if (citasController != null) {
            citasController.cargarDatosPaciente(paciente);
        }
        
    }



    // Limpia los controles del formulario; se reutiliza tras guardar y desde el FXML.
    @FXML
    private void limpiarCampos() {
        dnipac.clear();
        apelpac.clear();
        nompac.clear();
        movilpac.clear();
        emailpac.clear();
        nacpac.setValue(null);
        dirpac.clear();
        propac.getSelectionModel().clearSelection();
        munipac.getSelectionModel().clearSelection();
    }


    
    


}
