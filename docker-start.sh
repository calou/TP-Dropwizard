/etc/init.d/postgresql start  &&\
java -jar /kanban-web.jar db migrate /pg.yml &&\
su postgres -c "psql kanban < /data.sql" &&\
java -jar /kanban-web.jar server /pg.yml