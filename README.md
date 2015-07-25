[![alt text](https://travis-ci.org/calou/TP-Dropwizard.svg "Build status")](https://travis-ci.org/calou/TP-Dropwizard)

# Lancement de l'application
Avant de lancer l'application et quelque soit la méthode choisie, il est nécessaire de créer un package :

    mvn clean install

## Avec une base de données H2
    java -jar web/target/kanban-web-1.0.0-SNAPSHOT.jar db migrate web/h2.yml && java -jar web/target/kanban-web-1.0.0-SNAPSHOT.jar server web/h2.yml
## Avec une base de données PostgreSQL
### Création de l'utilisateur et du schéma
    CREATE USER kanban with LOGIN CREATEDB PASSWORD 'kanban';
    CREATE DATABASE kanban with owner kanban;
### Démarrage de l'application
    java -jar web/target/kanban-web-1.0.0-SNAPSHOT.jar db migrate web/pg.yml && java -jar web/target/kanban-web-1.0.0-SNAPSHOT.jar server web/pg.yml
## Avec Docker
### A la main
#### Lancer l'application
    docker build -t acme/kanban . && docker run -d -t -p 9090:8080 --name kanban acme/kanban
Pour accéder à l'application [http://localhost:9090](http://localhost:9090)
#### Stopper l'application
    docker rm -f kanban
Cette commande stoppe et détruit le conteneur

### Avec docker-compose
#### Démarrage du conteneur
    mvn clean install
    docker-compose up -d
#### Arrêt du conteneur
    docker-compose stop
    docker-compose rm