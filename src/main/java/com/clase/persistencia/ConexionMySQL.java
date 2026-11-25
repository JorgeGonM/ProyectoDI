package com.clase.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fabrica conexiones MySQL. La configuracion admite propiedades JVM, variables
 * de entorno y, por ultimo, valores por defecto.
 */
public class ConexionMySQL {
    
    // Orden de prioridad: -Ddb.* > DB_* del entorno > valor por defecto.
    private static final String URL = System.getProperty(
            "db.url",
            System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/bbdd"));
    private static final String USUARIO = System.getProperty(
            "db.user",
            System.getenv().getOrDefault("DB_USER", "user"));
    private static final String PASSWORD = System.getProperty(
            "db.password",
            System.getenv().getOrDefault("DB_PASSWORD", "abc123"));

    public static Connection getConexion() throws SQLException{
        // Cada DAO solicita su conexion a traves de este punto central.
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }


}
