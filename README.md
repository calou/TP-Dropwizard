# Installation
    
    mvn clean install

## Utilisation d'une base de données H2

    cd web
    mvn clean install -DskipTests && java -jar target/kanban-web-1.0.0-SNAPSHOT.jar db migrate h2.yml && java -jar target/kanban-web-1.0.0-SNAPSHOT.jar server h2.yml

## Utilisation d'une base de données PostgreSQL
### Création de l'utilisateur et du schéma

    CREATE USER kanban with LOGIN CREATEDB PASSWORD 'kanban';
    CREATE DATABASE kanban with owner kanban;

### Démarrage du service
    cd web
    mvn clean install -DskipTests && java -jar target/kanban-web-1.0.0-SNAPSHOT.jar db migrate pg.yml && java -jar target/kanban-web-1.0.0-SNAPSHOT.jar server pg.yml