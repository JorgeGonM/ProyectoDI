package com.clase.persistencia;

import java.sql.SQLException;
import java.util.List;

import com.clase.modelo.Cita;

/** Operaciones de persistencia que necesita el controlador de citas. */
public interface CitaDAO {
    // El listado acepta uno o dos filtros: DNI del paciente e ID del doctor.
    void guardarCita(Cita cita) throws SQLException;
    List<Cita> cargarCitas(String dniPaciente, String idDoctor) throws SQLException;
    boolean modificarCita(Cita cita) throws SQLException;
    boolean eliminarCita(int idcita) throws SQLException;
}
