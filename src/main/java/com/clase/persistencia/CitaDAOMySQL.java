package com.clase.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.clase.modelo.Cita;

public class CitaDAOMySQL implements CitaDAO {

    @Override
    public void guardarCita(Cita cita) throws SQLException {
        String sql = "INSERT INTO citas "
                + "(citadnipac, citaiddoc, fechacita, horacita, motivo, "
                + "diagnostico, tratamiento, estadocita) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setString(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setTime(4, java.sql.Time.valueOf(cita.getHoracita()));
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getDiagnostico());
            ps.setString(7, cita.getTratamiento());
            ps.setString(8, cita.getEstadocita());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    cita.setIdcita(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Cita> cargarCitas(String dniPaciente, String idDoctor) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.idcita, c.citadnipac, c.citaiddoc, c.fechacita, "
                + "c.horacita, c.motivo, c.diagnostico, c.tratamiento, c.estadocita, "
                + "CONCAT(p.nompac, ' ', p.apelpac) AS nombrePaciente, "
                + "CONCAT(d.nomdoc, ' ', d.apeldoc) AS nombreDoctor, d.espedoc AS especialidad "
                + "FROM citas c "
                + "LEFT JOIN pacientes p ON c.citadnipac = p.dnipac "
                + "LEFT JOIN doctores d ON c.citaiddoc = d.iddoc "
                + "WHERE c.citadnipac = ? AND c.citaiddoc = ? "
                + "ORDER BY c.fechacita, c.horacita";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dniPaciente);
            ps.setString(2, idDoctor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cita cita = new Cita(
                            rs.getInt("idcita"),
                            rs.getString("citadnipac"),
                            rs.getString("citaiddoc"),
                            rs.getDate("fechacita").toLocalDate(),
                            rs.getTime("horacita").toLocalTime(),
                            rs.getString("motivo"),
                            rs.getString("diagnostico"),
                            rs.getString("tratamiento"),
                            rs.getString("estadocita"));
                    cita.setNombrePaciente(rs.getString("nombrePaciente"));
                    cita.setNombreDoctor(rs.getString("nombreDoctor"));
                    cita.setEspecialidad(rs.getString("especialidad"));
                    citas.add(cita);
                }
            }
        }

        return citas;
    }

    @Override
    public boolean modificarCita(Cita cita) throws SQLException {
        String sql = "UPDATE citas SET citadnipac = ?, citaiddoc = ?, fechacita = ?, "
                + "horacita = ?, motivo = ?, diagnostico = ?, tratamiento = ?, estadocita = ? "
                + "WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setString(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setTime(4, java.sql.Time.valueOf(cita.getHoracita()));
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getDiagnostico());
            ps.setString(7, cita.getTratamiento());
            ps.setString(8, cita.getEstadocita());
            ps.setInt(9, cita.getIdcita());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminarCita(int idcita) throws SQLException {
        String sql = "DELETE FROM citas WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idcita);
            return ps.executeUpdate() > 0;
        }
    }
}