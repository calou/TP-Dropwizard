package com.acme.kanban.repository;

import com.acme.kanban.dropwizard.KanbanApplication;
import com.acme.kanban.dropwizard.KanbanConfiguration;
import com.acme.kanban.model.Project;
import com.acme.kanban.model.Step;
import com.acme.kanban.model.Story;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class StoryResourceTest {
    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("configuration-test.yml");

    private static IDatabaseTester DATABASE_TESTER;

    private final ObjectMapper mapper = new ObjectMapper();

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
    public void create() throws Exception {
        final Story story = Story.builder().title("This is the new story")
                .project(Project.builder().id(1234l).build())
                .step(Step.builder().id(10l).build())
                .build();
        Entity<Story> entity = Entity.entity(story, MediaType.APPLICATION_JSON_TYPE);
        final Story persistedStory = client.target("http://localhost:" + RULE.getLocalPort() + "/api/stories").request().post(entity).readEntity(Story.class);
        assertThat(persistedStory).isEqualToIgnoringNullFields(story);
        assertThat(persistedStory.getId()).isNotNull();
    }


    @Test
    public void get() throws Exception {
        final Story persistedStory = client.target("http://localhost:" + RULE.getLocalPort() + "/api/stories/101").request().get().readEntity(Story.class);
        assertThat(persistedStory.getId()).isEqualTo(101l);
        assertThat(persistedStory.getTitle()).isEqualTo("An awesome feature");
    }

    @Test
    public void getUnavailable() throws Exception {
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/api/stories/9999").request().get();
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void update() throws Exception {
        final Story story = mapper.readValue(fixture("fixtures/story101.json"), Story.class);

        Entity<Story> entity = Entity.entity(story, MediaType.APPLICATION_JSON_TYPE);
        final Story persistedStory = client.target("http://localhost:" + RULE.getLocalPort() + "/api/stories/101").request().put(entity).readEntity(Story.class);
        assertThat(persistedStory.getId()).isEqualTo(101l);
        assertThat(persistedStory.getTitle()).isEqualTo(story.getTitle());
    }

    @Test
    public void updateUnavailable() throws Exception {
        final Story story = Story.builder().id(9999l).title("This is the title").build();
        Entity<Story> entity = Entity.entity(story, MediaType.APPLICATION_JSON_TYPE);
        final Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/api/stories/9999").request().put(entity);
        assertThat(response.getStatus()).isEqualTo(404);
    }
}
