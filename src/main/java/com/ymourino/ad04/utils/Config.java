package com.ymourino.ad04.utils;

import java.io.Serializable;

public class Config implements Serializable {
    private static final long serialVersionUID = -2532903982561985934L;

    private DbConnection dbConnection;
    private Hibernate hibernate;

    public DbConnection getDbConnection() {
        return dbConnection;
    }

    public Hibernate getHibernate() {
        return hibernate;
    }

    public static class DbConnection implements Serializable {
        private static final long serialVersionUID = 7838625292183744130L;

        private String address;
        private String port;
        private String name;
        private String user;
        private String password;

        public DbConnection() {}

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Hibernate implements Serializable {
        private static final long serialVersionUID = 1907062053068318501L;

        private String driver;
        private String dialect;
        private String HBM2DDL_AUTO;
        private boolean SHOW_SQL;

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getDialect() {
            return dialect;
        }

        public void setDialect(String dialect) {
            this.dialect = dialect;
        }

        public String getHBM2DDL_AUTO() {
            return HBM2DDL_AUTO;
        }

        public void setHBM2DDL_AUTO(String HBM2DDL_AUTO) {
            this.HBM2DDL_AUTO = HBM2DDL_AUTO;
        }

        public boolean getSHOW_SQL() {
            return SHOW_SQL;
        }

        public void setSHOW_SQL(boolean SHOW_SQL) {
            this.SHOW_SQL = SHOW_SQL;
        }
    }
}
