# Customer Experience Platform 

## Stack
* **Backend:** Java 21, Spring Boot 3.5, Gradle.
* **Persistencia:** MySQL 8.0 (vía Hibernate/JPA).
* **Mensajería:** RabbitMQ (para comunicación asíncrona).
* **Integración:** EspoCRM (vía WebClient/REST).
* **Infraestructura:** Docker & Docker Compose.

## Arquitectura del Proyecto
El sistema sigue un patrón de **Arquitectura en Capas (N-Tier)**:
* **Controllers:** Endpoints REST para autenticación y gestión.
* **Services:** Lógica de negocio e integración con APIs externas (EspoCRM).
* **Repositories:** Capa de persistencia utilizando Spring Data JPA.
* **Security:** Configuración de seguridad base para implementación de JWT.

## Inicio Rápido

### 1. Requisitos Previos
* Tener instalado **Docker** y **Docker Compose**.
* JDK 21 (opcional si usas el wrapper de Gradle).

### 2. Configuración de Variables de Entorno
El proyecto utiliza un sistema de configuración basado en archivos `.env`. 
1. Copia el archivo de ejemplo:
   ```(En terminal) 
   cp .env.example .env
2. Edita el archivo .env con tus credenciales locales (MySQL, puertos, etc.)
3. Levantar contenedores
   docker-compose up -d
4. Ejecutar aplicación (En terminal)
   Linux:
     ./gradlew bootRun
   Win:
     gradlew bootRun o .\gradlew.bat bootRun
