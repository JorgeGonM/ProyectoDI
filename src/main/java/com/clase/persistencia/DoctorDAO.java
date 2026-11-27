package com.clase.persistencia;
import java.util.List;

import com.clase.modelo.Doctor;

/**
 * Contrato de persistencia de doctores; DoctorDAOMySQL implementa estas
 * operaciones usando la base de datos MySQL.
 */
public interface DoctorDAO {
    // Operaciones de mantenimiento y filtrado por especialidad.
    void guardarDoctor(Doctor doctor);
    List<Doctor> cargarDoctores();
    Doctor buscarDoctor(String iddoc);
    void eliminarDoctor(String id);
    void modificarDoctor(String id, Doctor doctor);
    List<Doctor> buscaDocEspec(String especialidad);
    List<Doctor> buscarDoctoresPorApellidos(String apellidos);
    List<Doctor> buscarDoctoresPorNombre(String nombre);
    List<Doctor> buscarDoctoresPorMovil(String movil);
}
