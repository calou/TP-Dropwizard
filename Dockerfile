FROM debian:8.1

MAINTAINER Sébastien Gruchet

RUN apt-get update -q && apt-get install -y -q openjdk-7-jre postgresql

RUN echo "host all  all    0.0.0.0/0  trust" >> /etc/postgresql/9.4/main/pg_hba.conf
#RUN echo "local all  all    0.0.0.0/0  trust" >> /etc/postgresql/9.4/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.4/main/postgresql.conf

RUN echo "127.0.0.1  localhost" >> /etc/hosts

USER postgres
ADD data.sql /data.sql
RUN /etc/init.d/postgresql start &&\
    psql -c "CREATE USER kanban WITH LOGIN CREATEDB PASSWORD 'kanban';" &&\
    psql -c "CREATE DATABASE kanban WITH OWNER kanban;"
USER root

EXPOSE 8080
EXPOSE 5432
ADD docker-start.sh /docker-start.sh
ADD web/pg.yml /pg.yml
ADD web/target/kanban-web-1.0.0-SNAPSHOT.jar /kanban-web.jar


RUN chmod +x /docker-start.sh
CMD /docker-start.sh
