package com.clase.persistencia;

import java.sql.SQLException;
import java.util.List;

import com.clase.modelo.Cita;

public interface CitaDAO {
    void guardarCita(Cita cita) throws SQLException;
    List<Cita> cargarCitas(String dniPaciente, String idDoctor) throws SQLException;
    boolean modificarCita(Cita cita) throws SQLException;
    boolean eliminarCita(int idcita) throws SQLException;
}
