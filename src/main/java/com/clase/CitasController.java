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

import com.clase.modelo.Cita;
import com.clase.modelo.Doctor;
import com.clase.modelo.Paciente;
import com.clase.persistencia.CitaDAOMySQL;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

/**
 * Gestiona citas y su tabla. Recibe paciente y doctor desde sus controladores,
 * y delega las consultas y cambios persistentes en CitaDAOMySQL.
 */
public class CitasController implements Initializable {

    // Controles enlazados por fx:id en citas.fxml.
    @FXML
    private TextField idcita, citaiddoc, citadnipac, motivo;

    @FXML
    private Label lblNombrePaciente;

    @FXML
    private TextArea diagnostico, tratamiento;

    @FXML
    private DatePicker fechacita;

    @FXML
    private ComboBox<String> horacita;

    @FXML
    private CheckBox chkatendida, chkpendiente, chkanulada;

    @FXML
    private TableView<Cita> tablaCitas;

    @FXML
    private TableColumn<Cita, Integer> colidcita;

    @FXML
    private TableColumn<Cita, String> colcitaiddoc, colespec, colestadocita;

    @FXML
    private TableColumn<Cita, LocalDate> colfechacita;

    @FXML
    private TableColumn<Cita, LocalTime> colhoracita;

    private final CitaDAOMySQL citaDAO = new CitaDAOMySQL();
    private final List<Cita> citasCargadas = new ArrayList<>();
    private Cita citaSeleccionada;

    // Llamado al seleccionar/buscar un paciente en la pestaña Pacientes.
    public void cargarDatosPaciente(Paciente paciente) {
        if (paciente != null) {
            if (!paciente.getDni().equals(citadnipac.getText())) {
                limpiarDatosCita();
            }
            citaiddoc.clear();
            citadnipac.setText(paciente.getDni());
            lblNombrePaciente.setText(paciente.getNombre() + " " + paciente.getApellidos());
            cargarCitas();
        }
    }

    // Llamado al seleccionar un doctor; ambos filtros pueden combinarse.
    public void cargarDatosDoctor(Doctor doctor) {
        if (doctor != null) {
            if (!doctor.getIddoc().equals(citaiddoc.getText())) {
                limpiarDatosCita();
            }
            citaiddoc.setText(doctor.getIddoc());
            cargarCitas();
        }
    }

    // Enlaza columnas con propiedades del modelo y carga los datos iniciales.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colidcita.setCellValueFactory(new PropertyValueFactory<>("idcita"));
        colcitaiddoc.setCellValueFactory(new PropertyValueFactory<>("nombreDoctor"));
        colespec.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colfechacita.setCellValueFactory(new PropertyValueFactory<>("fechacita"));
        colhoracita.setCellValueFactory(new PropertyValueFactory<>("horacita"));
        colestadocita.setCellValueFactory(new PropertyValueFactory<>("estadocita"));

        cargarHorarios();
        chkpendiente.setSelected(true);
        cargarCitas();
        tablaCitas.getSelectionModel().selectedItemProperty().addListener((observable, anterior, nuevo) -> {
            if (nuevo != null) {
                cargarCita(nuevo);
            }
        });
    }

    // Carga las horas predefinidas desde horario.json en el selector del formulario.
    private void cargarHorarios() {
        try (InputStream is = getClass().getResourceAsStream("/com/clase/data/horario.json")) {
            if (is == null) {
                throw new IllegalStateException("No se encontró el recurso horario.json");
            }
            JsonObject json = JsonParser.parseReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray horarios = json.getAsJsonArray("horario");
            for (int i = 0; i < horarios.size(); i++) {
                horacita.getItems().add(horarios.get(i).getAsString());
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron cargar los horarios", e);
        }
    }

    // Valida el formulario, crea el modelo y delega el alta o modificacion al DAO.
    @FXML
    private void guardarCita() {
        String dniPaciente = citadnipac.getText().trim();
        String idDoctor = citaiddoc.getText().trim();
        String hora = horacita.getValue();
        String estado = obtenerEstadoSeleccionado();

        if (dniPaciente.isEmpty() || idDoctor.isEmpty()) {
            mostrarAviso("Selecciona primero un paciente y un doctor en sus pestañas.");
            return;
        }
        if (fechacita.getValue() == null || hora == null || motivo.getText().trim().isEmpty()) {
            mostrarAviso("Completa la fecha, la hora y el motivo de la cita.");
            return;
        }
        if (estado == null) {
            mostrarAviso("Selecciona el estado de la cita.");
            return;
        }

        LocalTime horaCita = LocalTime.parse(hora, DateTimeFormatter.ofPattern("H:mm"));
        Cita cita = new Cita(
                citaSeleccionada == null ? 0 : citaSeleccionada.getIdcita(),
                dniPaciente,
                idDoctor,
                fechacita.getValue(),
                horaCita,
                motivo.getText().trim(),
                diagnostico.getText().trim(),
                tratamiento.getText().trim(),
                estado);
        boolean modificando = citaSeleccionada != null;

        try {
            if (!modificando) {
                citaDAO.guardarCita(cita);
            } else {
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Confirmación");
                confirmacion.setHeaderText(null);
                confirmacion.setContentText("¿Desea modificar la cita?");
                if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return;
                }
                if (!citaDAO.modificarCita(cita)) {
                    mostrarAviso("No se encontró la cita que se desea modificar.");
                    return;
                }
            }
            cargarCitas();
            limpiarDatosCita();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(modificando
                    ? "Cita modificada correctamente."
                    : "Cita guardada correctamente.");
            alert.showAndWait();
        } catch (SQLException e) {
            mostrarError("No se pudo guardar o modificar la cita", e);
        }
    }

    // Copia a los controles los datos de la fila seleccionada para editarla.
    private void cargarCita(Cita cita) {
        citaSeleccionada = cita;
        idcita.setText(String.valueOf(cita.getIdcita()));
        citadnipac.setText(cita.getCitadnipac());
        lblNombrePaciente.setText(cita.getNombrePaciente() == null ? "" : cita.getNombrePaciente());
        citaiddoc.setText(cita.getCitaiddoc());
        motivo.setText(cita.getMotivo());
        fechacita.setValue(cita.getFechacita());
        horacita.setValue(cita.getHoracita().format(DateTimeFormatter.ofPattern("H:mm")));
        diagnostico.setText(cita.getDiagnostico() == null ? "" : cita.getDiagnostico());
        tratamiento.setText(cita.getTratamiento() == null ? "" : cita.getTratamiento());
        chkpendiente.setSelected("Pendiente".equals(cita.getEstadocita()));
        chkatendida.setSelected("Atendida".equals(cita.getEstadocita()));
        chkanulada.setSelected("Anulada".equals(cita.getEstadocita()));
    }

    // Elimina la cita actualmente seleccionada despues de pedir confirmacion.
    @FXML
    private void eliminarCita() {
        if (citaSeleccionada == null) {
            mostrarAviso("Selecciona una cita de la tabla para eliminarla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Desea eliminar la cita seleccionada?");
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            if (!citaDAO.eliminarCita(citaSeleccionada.getIdcita())) {
                mostrarAviso("No se encontró la cita que se desea eliminar.");
                return;
            }
            cargarCitas();
            limpiarDatosCita();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Cita eliminada correctamente.");
            alert.showAndWait();
        } catch (SQLException e) {
            mostrarError("No se pudo eliminar la cita", e);
        }
    }

    // Filtra por paciente, doctor o ambos; sin filtros deja la tabla vacia.
    @FXML
    private void cargarCitas() {
        String dniPaciente = citadnipac.getText().trim();
        String idDoctor = citaiddoc.getText().trim();
        if (dniPaciente.isEmpty() && idDoctor.isEmpty()) {
            citasCargadas.clear();
            tablaCitas.getItems().clear();
            return;
        }

        try {
            List<Cita> citas = citaDAO.cargarCitas(dniPaciente, idDoctor);
            citasCargadas.clear();
            citasCargadas.addAll(citas);
            // Muestra todas las citas cargadas, sin aplicar el filtro de estado.
            tablaCitas.getItems().setAll(citasCargadas);
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar las citas", e);
        }
    }

    // Filtra en memoria las citas ya cargadas, sin consultar de nuevo la base de datos.
    @FXML
    private void buscarCitasPorEstado() {
        String estado = obtenerEstadoSeleccionado();
        if (estado == null) {
            //mostrarAviso("Selecciona un estado antes de buscar.");
            cargarCitas();
            return;
        } 
        if (citasCargadas.isEmpty()) {
            mostrarAviso("No hay citas cargadas en la tabla para filtrar.");
            return;
        }

        limpiarDatosCita();
        chkpendiente.setSelected("Pendiente".equals(estado));
        chkatendida.setSelected("Atendida".equals(estado));
        chkanulada.setSelected("Anulada".equals(estado));

        List<Cita> citasFiltradas = new ArrayList<>();
        for (Cita cita : citasCargadas) {
            if (estado.equals(cita.getEstadocita())) {
                citasFiltradas.add(cita);
            }
        }
        tablaCitas.getItems().setAll(citasFiltradas);
    }

    // Traduce las casillas excluyentes a la etiqueta que se guarda en la BD.
    private String obtenerEstadoSeleccionado() {
        if (chkpendiente.isSelected()) {
            return "Pendiente";
        }
        if (chkatendida.isSelected()) {
            return "Atendida";
        }
        if (chkanulada.isSelected()) {
            return "Anulada";
        }
        return null;
    }

    // Limpia formulario, filtros y resultados de la tabla de citas.
    @FXML
    private void limpiarCamposCita() {
        limpiarDatosCita();
        citaiddoc.clear();
        citadnipac.clear();
        lblNombrePaciente.setText("");
        citasCargadas.clear();
        tablaCitas.getItems().clear();
    }

    // Limpia solo los campos propios de la cita, preservando paciente y doctor.
    private void limpiarDatosCita() {
        citaSeleccionada = null;
        tablaCitas.getSelectionModel().clearSelection();
        idcita.clear();
        motivo.clear();
        fechacita.setValue(null);
        horacita.getSelectionModel().clearSelection();
        diagnostico.clear();
        tratamiento.clear();
        chkpendiente.setSelected(true);
        chkatendida.setSelected(false);
        chkanulada.setSelected(false);
    }

    // Centraliza los avisos de validacion que no requieren mostrar una excepcion.
    private void mostrarAviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Presenta el error SQL al usuario, conservando el mensaje de la excepcion.
    private void mostrarError(String titulo, SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    // Hace mutuamente excluyentes los tres estados de cita del FXML.
    @FXML
    private void gestionarExclusion(ActionEvent event) {
        CheckBox seleccionado = (CheckBox) event.getSource();
        if (seleccionado == chkpendiente && chkpendiente.isSelected()) {
            chkatendida.setSelected(false);
            chkanulada.setSelected(false);
        } else if (seleccionado == chkatendida && chkatendida.isSelected()) {
            chkpendiente.setSelected(false);
            chkanulada.setSelected(false);
        } else if (seleccionado == chkanulada && chkanulada.isSelected()) {
            chkpendiente.setSelected(false);
            chkatendida.setSelected(false);
        }
    }
}
