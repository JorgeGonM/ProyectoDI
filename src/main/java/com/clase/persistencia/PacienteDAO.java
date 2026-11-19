package com.clase.persistencia;
import java.util.List;
import java.sql.SQLException;

import com.clase.modelo.Paciente;

// Es la interfaz. Solo dice qué operaciones podemos hacer con un paciente, no cómo se hace.

public interface PacienteDAO {
    void guardarPaciente(Paciente paciente) throws SQLException;
    List<Paciente> cargarPacientes();
    Paciente buscarPaciente(String dni);
    void eliminarPaciente(String dni);
    void modificarPaciente(String dni, Paciente paciente);
    Paciente buscaPacDni(String dni);
}
