package com.acme.kanban.repository;

import com.acme.kanban.dropwizard.KanbanApplication;
import com.acme.kanban.dropwizard.KanbanConfiguration;
import com.acme.kanban.model.Project;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectResourceTest {
    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("configuration-test.yml");

    private static IDatabaseTester DATABASE_TESTER;

    @ClassRule
    public static final DropwizardAppRule<KanbanConfiguration> RULE = new DropwizardAppRule<>(
            KanbanApplication.class, CONFIG_PATH);

    private Client client;

    @BeforeClass
    public static void migrateDb() throws Exception {
        RULE.getApplication().run("db", "migrate", CONFIG_PATH);
        io.dropwizard.db.DataSourceFactory databaseFactory = RULE.getConfiguration().getDataSourceFactory();
        DATABASE_TESTER = new JdbcDatabaseTester(databaseFactory.getDriverClass(), databaseFactory.getUrl(), databaseFactory.getUser(), databaseFactory.getPassword());
        IDataSet dataset = new FlatXmlDataSetBuilder().build(new FileInputStream("src/test/resources/datasets/default.xml"));
        DATABASE_TESTER.setDataSet(dataset);
    }

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();

        DATABASE_TESTER.onSetup();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
        DATABASE_TESTER.onTearDown();
    }

    @Test
    public void list() throws Exception {
        final List<Project> projects = client.target("http://localhost:" + RULE.getLocalPort() + "/api/projects").request().get().readEntity(List.class);
        assertThat(projects).hasSize(4);
    }

    @Test
    public void create() throws Exception {
        final Project project = Project.builder().title("This is the title").build();
        Entity<Project> entity = Entity.entity(project, MediaType.APPLICATION_JSON_TYPE);
        final Project persistedProject = client.target("http://localhost:" + RULE.getLocalPort() + "/api/projects").request().post(entity).readEntity(Project.class);
        assertThat(persistedProject).isEqualToIgnoringNullFields(project);
        assertThat(persistedProject.getId()).isNotNull();
    }

    @Test
    public void get() throws Exception {
        final Project p1 = client.target("http://localhost:" + RULE.getLocalPort() + "/api/projects/1234").request().get().readEntity(Project.class);
        assertThat(p1.getId()).isEqualTo(1234);
        assertThat(p1.getTitle()).isEqualTo("Project P1");
        assertThat(p1.getSteps()).extracting("id").containsExactly(11l, 10l, 12l, 13l);
        assertThat(p1.getSteps()).extracting("title").containsExactly("TODO", "DEVELOP", "VALIDATION", "DONE");

        final Project p2 = client.target("http://localhost:" + RULE.getLocalPort() + "/api/projects/1235").request().get().readEntity(Project.class);
        assertThat(p2.getId()).isEqualTo(1235);
        assertThat(p2.getTitle()).isEqualTo("Project F2");
        assertThat(p2.getSteps()).extracting("id").containsExactly(21l, 20l, 22l);
        assertThat(p2.getSteps()).extracting("title").containsExactly("TODO", "DEVELOP", "DONE");
    }

    @Test
    public void getUnavailable() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/api/projects/1").request().get();
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void update() throws Exception {
        final Project wish = Project.builder().id(1234l).title("This is the title").build();
        Entity<Project> entity = Entity.entity(wish, MediaType.APPLICATION_JSON_TYPE);
        final Project persistedProject = client.target("http://localhost:" + RULE.getLocalPort() + "/api/projects/1234").request().put(entity).readEntity(Project.class);
        assertThat(persistedProject.getId()).isEqualTo(1234l);
        assertThat(persistedProject.getTitle()).isEqualTo(wish.getTitle());
    }

    @Test
    public void updateUnavailable() throws Exception {
        final Project todo = Project.builder().id(9999l).title("This is the title").build();
        Entity<Project> entity = Entity.entity(todo, MediaType.APPLICATION_JSON_TYPE);
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/api/projects/9999").request().put(entity);
        assertThat(response.getStatus()).isEqualTo(404);
    }

}
