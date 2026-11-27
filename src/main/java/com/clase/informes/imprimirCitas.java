package com.clase.informes;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;

import com.clase.persistencia.ConexionMySQL;

import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/** Compila la plantilla y muestra el listado de citas. */
public class imprimirCitas {

    public static void mostrar() {
        try {
            Connection conexion = ConexionMySQL.getConexion();
            if (conexion == null) {
                throw new Exception("No se pudo conectar con la base de datos.");
            }

            InputStream informe = imprimirCitas.class.getResourceAsStream("/informes/citas.jrxml");
            if (informe == null) {
                throw new Exception("No se encuentra citas.jrxml en resources.");
            }

            URL imagen = imprimirCitas.class.getResource("/img/logo.png");
            if (imagen == null) {
                throw new Exception("No se encuentra img/logo.png en resources.");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(informe);
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("LOGO", imagen);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parametros, conexion);
            JasperViewer.viewReport(jasperPrint, false);
            conexion.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al generar el informe");
            alert.setHeaderText("No se ha podido generar el informe de citas");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
