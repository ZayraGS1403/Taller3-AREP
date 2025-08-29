# Servidor Web con Framework IoC (Taller 3 AREP)

Un servidor web implementado en Java que incluye un framework de Inversión de Control (IoC) para la construcción de aplicaciones web a partir de POJOs. El servidor es capaz de entregar páginas HTML e imágenes PNG, además de proveer servicios REST utilizando anotaciones similares a Spring Boot.

## Descripción

 El servidor debe ser capaz de entregar páginas html e imágenes tipo PNG. Igualmente el servidor debe proveer un framework IoC para la construcción de aplicaciones web a partir de POJOS. Usando el servidor se debe construir una aplicación Web de ejemplo. El servidor debe atender múltiples solicitudes no concurrentes.

Para este taller desarrolle un prototipo mínimo que demuestre las capacidades reflexivas de JAVA y permita por lo menos cargar un bean (POJO) y derivar una aplicación Web a partir de él.

## Características Principales

- **Servidor HTTP**: Implementación desde cero usando sockets Java
- **Framework IoC**: Sistema de inversión de control para cargar y gestionar POJOs
- **Capacidades reflexivas**: Uso de reflexión para descubrir y cargar componentes automáticamente
- **Anotaciones**: Soporte para `@RestController`, `@GetMapping` y `@RequestParam`
- **Servicio de archivos estáticos**: HTML, CSS, JavaScript, imágenes PNG
- **Arquitectura modular**: Separación de responsabilidades en diferentes clases

## Funcionalidades Implementadas

### 1. Framework MicroSpringBoot

El framework principal `MicroSpringBoot` implementa:
- Carga de POJOs desde línea de comandos
- Exploración automática del classpath para encontrar clases con `@RestController`
- Procesamiento de anotaciones `@GetMapping` y `@RequestParam`
- Gestión de servicios REST con tipos de retorno String

### 2. Anotaciones Soportadas

#### @RestController
Marca una clase como controlador REST que debe ser gestionado por el framework:

```java
@RestController
public class HelloController {
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
```

#### @GetMapping
Define un endpoint HTTP GET en la URI especificada:

```java
@GetMapping("/greeting")
public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    return "Hola " + name;
}
```

#### @RequestParam
Permite extraer parámetros de la URL con valores por defecto:

```java
@RequestParam(value = "name", defaultValue = "World") String name
```

### 3. Ejemplo de Controlador Completo

```java
@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
```

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── co/edu/escuelaing/
│   │       ├── httpserver/
│   │       │   ├── HttpServer.java      # Servidor HTTP principal
│   │       │   ├── HttpRequest.java     # Manejo de peticiones HTTP
│   │       │   └── HttpResponse.java    # Manejo de respuestas HTTP
│   │       └── microspringboot/
│   │           ├── MicroSpringBoot.java # Framework IoC principal
│   │           ├── annotations/
│   │           │   ├── GetMapping.java      # Anotación para mapeo GET
│   │           │   ├── RequestParam.java    # Anotación para parámetros
│   │           │   └── RestController.java  # Anotación para controladores
│   │           └── examples/
│   │               └── GreetingController.java # Ejemplo de controlador
│   └── resources/
│       ├── index.html               # Página principal
│       ├── images/                  # Imágenes PNG
│       ├── scripts/                 # JavaScript
│       └── styles/                  # CSS
└── test/
    └── java/                        # Pruebas unitarias
```

## Cómo Ejecutar el Proyecto

### 1. Compilar el proyecto
```bash
mvn clean compile
```

### 2. Ejecutar con POJO específico (Versión inicial)
```bash
java -cp target/classes co.edu.escuelaing.microspringboot.MicroSpringBoot co.edu.escuelaing.microspringboot.examples.GreetingController
```

### 3. Ejecutar con exploración automática (Versión final)
```bash
java -cp target/classes co.edu.escuelaing.microspringboot.MicroSpringBoot
```

## Ejemplos de Uso

### URLs de Prueba:

1. **Servicio con parámetro:**
   ```
   http://localhost:35000/greeting?name=Pedro
   ```
   Respuesta: "Hola Pedro"

2. **Servicio con valor por defecto:**
   ```
   http://localhost:35000/greeting
   ```
   Respuesta: "Hola World"

3. **Archivos estáticos:**
   ```
   http://localhost:35000/index.html
   http://localhost:35000/images/favicon.ico
   ```
## Authors

* **Zayra Gutierrez**
