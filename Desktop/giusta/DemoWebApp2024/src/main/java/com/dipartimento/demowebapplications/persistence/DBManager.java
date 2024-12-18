package com.dipartimento.demowebapplications.persistence;


import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;
import com.dipartimento.demowebapplications.persistence.dao.RistoranteDao;
import com.dipartimento.demowebapplications.persistence.dao.impljdbc.PiattoDaoJDBC;
import com.dipartimento.demowebapplications.persistence.dao.impljdbc.RistoranteDaoJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static DBManager instance = null;
    private static RistoranteDao ristoranteDaoInstance = null;
    private static PiattoDao piattoDaoInstance = null;


    private static Connection connection = null;
    private static final String dbPosition = "jdbc:postgresql://localhost:5432/Unical";


    private DBManager() {}

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }

        return instance;
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(dbPosition, "postgres", "123456");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return connection;
    }

    public RistoranteDao getRistoranteDao() {
        if (ristoranteDaoInstance == null) {
            ristoranteDaoInstance = new RistoranteDaoJDBC(getConnection());
        }

        return ristoranteDaoInstance;
    }

    public PiattoDao getPiattoDao() {
        if (piattoDaoInstance == null) {
            piattoDaoInstance = new PiattoDaoJDBC(getConnection());
        }

        return piattoDaoInstance;
    }
}
