package com.clase.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.clase.modelo.Cita;

/** Implementacion MySQL del contrato CitaDAO, incluidas las relaciones de consulta. */
public class CitaDAOMySQL implements CitaDAO {

    // La base de datos almacena la hora con segundos; la vista trabaja con hora:minuto.
    private static final DateTimeFormatter HORA_BD =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    // Convierte el valor del modelo al formato de hora de MySQL.
    private static String formatearHoraBD(LocalTime hora) {
        return hora.format(HORA_BD);
    }

    // Acepta tanto horas mostradas en la interfaz (9:00) como el formato SQL.
    private static LocalTime leerHoraBD(String hora) {
        if (hora == null || hora.trim().isEmpty()) {
            return null;
        }

        String valor = hora.trim();
        if (valor.length() == 4 || valor.length() == 5) {
            return LocalTime.parse(valor, DateTimeFormatter.ofPattern("H:mm"));
        }
        return LocalTime.parse(valor, HORA_BD);
    }

    @Override
    public void guardarCita(Cita cita) throws SQLException {
        // RETURN_GENERATED_KEYS permite recuperar el ID autogenerado por MySQL.
        String sql = "INSERT INTO citas "
                + "(citadnipac, citaiddoc, fechacita, horacita, motivo, "
                + "diagnostico, tratamiento, estadocita) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setString(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setString(4, formatearHoraBD(cita.getHoracita()));
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
        // Los JOIN agregan nombres y especialidad para mostrar la cita en la tabla.
        String sql = "SELECT c.idcita, c.citadnipac, c.citaiddoc, c.fechacita, "
                + "c.horacita, c.motivo, c.diagnostico, c.tratamiento, c.estadocita, "
                + "CONCAT(p.nompac, ' ', p.apelpac) AS nombrePaciente, "
                + "CONCAT(d.nomdoc, ' ', d.apeldoc) AS nombreDoctor, d.espedoc AS especialidad "
                + "FROM citas c "
                + "LEFT JOIN pacientes p ON c.citadnipac = p.dnipac "
                + "LEFT JOIN doctores d ON c.citaiddoc = d.iddoc "
                + "WHERE 1 = 1 ";

        // Se incorporan solo los filtros que tienen valor; sus parametros se enlazan abajo.
        if (dniPaciente != null && !dniPaciente.trim().isEmpty()) {
            sql += "AND c.citadnipac = ? ";
        }
        if (idDoctor != null && !idDoctor.trim().isEmpty()) {
            sql += "AND c.citaiddoc = ? ";
        }
        sql += "ORDER BY c.fechacita, c.horacita";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            int posicion = 1;
            if (dniPaciente != null && !dniPaciente.trim().isEmpty()) {
                ps.setString(posicion++, dniPaciente.trim());
            }
            if (idDoctor != null && !idDoctor.trim().isEmpty()) {
                ps.setString(posicion, idDoctor.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cita cita = new Cita(
                            rs.getInt("idcita"),
                            rs.getString("citadnipac"),
                            rs.getString("citaiddoc"),
                            rs.getDate("fechacita").toLocalDate(),
                            leerHoraBD(rs.getString("horacita")),
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
        // Actualiza todos los campos por la clave primaria idcita.
        String sql = "UPDATE citas SET citadnipac = ?, citaiddoc = ?, fechacita = ?, "
                + "horacita = ?, motivo = ?, diagnostico = ?, tratamiento = ?, estadocita = ? "
                + "WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cita.getCitadnipac());
            ps.setString(2, cita.getCitaiddoc());
            ps.setDate(3, java.sql.Date.valueOf(cita.getFechacita()));
            ps.setString(4, formatearHoraBD(cita.getHoracita()));
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
        // El resultado indica si se encontro y elimino alguna fila.
        String sql = "DELETE FROM citas WHERE idcita = ?";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idcita);
            return ps.executeUpdate() > 0;
        }
    }
}