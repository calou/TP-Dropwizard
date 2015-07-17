package com.acme.kanban.repository;

import org.assertj.core.util.Strings;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public abstract class BaseRepositoryTest {

    protected String datasetFilename = "src/test/resources/datasets/default.xml";
    protected SessionFactory sessionFactory;
    protected Transaction tx;
    private IDatabaseTester databaseTester;

    public BaseRepositoryTest(){}

    @Before
    public void setUp() throws Exception {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(ssrb.build());
        onSetup();

        // Chargement du dataset
        if(!Strings.isNullOrEmpty(datasetFilename)) {
            createDatabaseTester(configuration);
            databaseTester.onSetup();
        }
        tx = sessionFactory.getCurrentSession().beginTransaction();
    }

    public abstract void onSetup();

    private void createDatabaseTester(Configuration configuration) throws ClassNotFoundException, DataSetException, FileNotFoundException {
        String driverClassName = configuration.getProperty("hibernate.connection.driver_class");
        String databaseUrl = configuration.getProperty("hibernate.connection.url");
        String username = configuration.getProperty("hibernate.connection.username");
        String password = configuration.getProperty("hibernate.connection.password");
        databaseTester = new JdbcDatabaseTester(driverClassName, databaseUrl, username, password);

        IDataSet dataset = new FlatXmlDataSetBuilder().build(new FileInputStream(datasetFilename));
        databaseTester.setDataSet(dataset);
    }

    @After
    public void tearDown() throws Exception {
        databaseTester.onTearDown();
        if(tx.isActive()) {
            tx.commit();
        }
        sessionFactory.close();
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected Transaction beginTransaction(){
        return getSession().beginTransaction();
    }
}
