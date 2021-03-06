package com.javastik.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    public abstract Connection getConnection() throws SQLException;

}
