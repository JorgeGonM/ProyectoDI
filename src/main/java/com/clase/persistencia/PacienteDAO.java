package com.clase.persistencia;
import java.util.List;
import java.sql.SQLException;

import com.clase.modelo.Paciente;

/**
 * Contrato de persistencia de pacientes: declara las operaciones disponibles
 * sin acoplar a quienes lo usan a una base de datos concreta.
 */
public interface PacienteDAO {
    // Alta, listado, busqueda, baja, modificacion y busqueda especifica por DNI.
    void guardarPaciente(Paciente paciente) throws SQLException;
    List<Paciente> cargarPacientes();
    // List<Paciente> buscarPacientesPorProvincia(String provincia) throws SQLException;
    Paciente buscarPaciente(String dni);
    void eliminarPaciente(String dni);
    void modificarPaciente(String dni, Paciente paciente);
    Paciente buscaPacDni(String dni);
    List<Paciente> buscarPacientesPorApellidos(String apellidos);
    List<Paciente> buscarPacientesPorNombre(String nombre);
    List<Paciente> buscarPacientesPorMovil(String movil);
}
