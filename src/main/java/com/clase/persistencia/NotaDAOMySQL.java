package com.clase.persistencia;
import com.clase.modelo.Nota;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class NotaDAOMySQL implements NotaDAO {

    @Override
    public void guardarNota(Nota nota) throws SQLException {
        
        String sql = "INSERT INTO notas "
                + "(notas) "
                + "VALUES (?)";

        try (Connection conexion = ConexionMySQL.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            
            ps.setString(1, nota.getNotas());
            
            ps.executeUpdate();

        }        



    }



}