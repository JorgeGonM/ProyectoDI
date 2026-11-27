package com.clase.persistencia;

import java.sql.SQLException;
import java.util.List;

import com.clase.modelo.Nota;

public interface NotaDAO {


    void guardarNota(Nota nota) throws SQLException;
    
}