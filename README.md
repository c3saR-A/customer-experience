# Customer Experience Platform 

## Stack
* **Backend:** Java 21, Spring Boot 3.5, Gradle.
* **Persistencia:** MySQL 8.0 (vía Hibernate/JPA).
* **Mensajería:** RabbitMQ (para comunicación asíncrona).
* **Integración:** EspoCRM (vía WebClient/REST).
* **Infraestructura:** Docker & Docker Compose.
* **Documentacion API:** Swagger UI

## Arquitectura del Proyecto
El sistema sigue un patrón de **Arquitectura en Capas (N-Tier)**:
* **Config:** Configuraciones y seguridad, base para implementación de JWT, WebClient.
* **Controllers:** Endpoints REST para autenticación y gestión.
* **DTOs:** Objetos para intercambio de datos entre capas, evitando exponer entidades.
* **Entitites:** Modelos de datos para MySQL con JPA/Hibernate
* **Repositories:** Capa de persistencia utilizando Spring Data JPA.
* **Services:** Lógica de negocio e integración con APIs externas (EspoCRM).

## Inicio Rápido

### 1. Requisitos Previos
* Tener instalado **Docker** y **Docker Compose**.
* JDK 21 (opcional si usas el wrapper de Gradle).

### 2. Configuración de Variables de Entorno
El proyecto utiliza un sistema de configuración basado en archivos `.env`.
Si usas un IDE se recomienda usar un plugin que facilite leer el archivo `.env` ya sea **EnvFile**, **.env files**,
el de tu preferencia o que conozcas
1. Copia el archivo de ejemplo:
   * Linux:
      ```bash
      tu/ruta/de/archivo
      cp .env.example .env
   * Win:
     ```bash
     tu/ruta/de/archivo
     copy .env.example .env
3. Edita el archivo .env con tus credenciales locales (MySQL, EspoCRM, etc.)
4. Levantar contenedores (editar puertos de ser necesario)
   ``` bash
      tu/ruta/de/archivo
      docker-compose up -d
5. Ejecutar aplicación desde terminal
   * Linux:
      ``` bash
      tu/ruta/de/archivo
      ./gradlew bootRun
   * Win:
      ``` bash
      tu/ruta/de/archivo
      gradlew bootRun o .\gradlew.bat bootRun
6. Ejecutar desde Intellij u otro IDE con boton Run/Ejecutar

## Endpoints de la API

| Método   | Endpoint                     | Descripción                                 | Acceso         |
| :---     | :---                         | :---                                        | :---           |
| `POST`   | `/api/auth/register`         | Registro de nuevos usuarios en MySQL.       | Público        |
| `POST`   | `/api/auth/login`            | Autenticación y generación de JWT.          | Público        |
| `GET`    | `/api/profile`               | Perfil del cliente autenticado              | Privado (USER) |  
| `POST`   | `/api/crm/sync`              | Sincronización manual con EspoCRM.          | Privado (USER) |
| `POST`   | `/api/cart/items`            | Agregar producto a carrito.                 | Privado (USER) |
| `GET`    | `api/cart `                  | Obtención de todos los productos en carrito.| Privado (USER) |
| `PUT`    | `api/cart/items/{productId}` | Actualización de producto en carrito.       | Privado (USER) |
| `DELETE` | `api/cart/items/{productId}` | Eliminación de producto en carrito.         | Privado (USER) |


**Nota sobre Seguridad:** 
Los endpoints protegidos requieren el encabezado Authorization: Bearer <JWT>
