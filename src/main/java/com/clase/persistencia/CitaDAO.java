package com.clase.persistencia;
import java.util.List;

import com.clase.modelo.Cita;

// Es la interfaz. Solo dice qué operaciones podemos hacer con un doctor, no cómo se hace.

public interface CitaDAO {
    void guardarCita(Cita cita);
    List<Doctor> cargarDoctores();
    Doctor buscarDoctor(String iddoc);
    void eliminarDoctor(String id);
    void modificarDoctor(String id, Doctor doctor);
    List<Doctor> buscaDocEspec(String especialidad);
}