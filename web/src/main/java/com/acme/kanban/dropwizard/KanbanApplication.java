package com.acme.kanban.dropwizard;

import com.acme.kanban.resource.ProjectRepository;
import com.acme.kanban.resource.ProjectResource;
import com.acme.kanban.resource.StoryRepository;
import com.acme.kanban.resource.StoryResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.hibernate.SessionFactory;


public class KanbanApplication extends Application<KanbanConfiguration>{

    private final HibernateBundle<KanbanConfiguration> hibernate = new ScanningHibernateBundle<KanbanConfiguration>("com.acme.kanban.model") {
        @Override
        public DataSourceFactory getDataSourceFactory(KanbanConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<KanbanConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new MigrationsBundle<KanbanConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(KanbanConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
        bootstrap.addBundle(new ViewBundle<KanbanConfiguration>());
    }

    @Override
    public void run(KanbanConfiguration kanbanConfiguration, Environment environment) throws Exception {
        final SessionFactory sessionFactory = hibernate.getSessionFactory();

        final ProjectRepository projectRepository = new ProjectRepository(sessionFactory);
        environment.jersey().register(new ProjectResource(projectRepository));

        final StoryRepository storyRepository = new StoryRepository(sessionFactory);
        environment.jersey().register(new StoryResource(storyRepository));
    }

    public static void main(String[] args) throws Exception {
        new KanbanApplication().run(args);
    }
}
