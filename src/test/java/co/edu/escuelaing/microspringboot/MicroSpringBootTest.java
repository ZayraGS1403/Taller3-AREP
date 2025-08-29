package co.edu.escuelaing.microspringboot;

import co.edu.escuelaing.httpserver.HttpServer;
import co.edu.escuelaing.microspringboot.annotations.GetMapping;
import co.edu.escuelaing.microspringboot.annotations.RequestParam;
import co.edu.escuelaing.microspringboot.annotations.RestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;


public class MicroSpringBootTest {

    @BeforeEach
    public void setUp() {
        // Limpiar servicios registrados antes de cada prueba
        HttpServer.services.clear();
    }

    @Test
    public void testGetMappingAnnotationDetection() throws NoSuchMethodException {
        // Verificar que la anotación @GetMapping se detecta correctamente
        Method method = TestController.class.getMethod("testEndpoint", String.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "El método debe tener la anotación @GetMapping");
        
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertEquals("/test", mapping.value(),
                "El valor de @GetMapping debe ser '/test'");
    }

    @Test
    public void testServiceRegistration() {
        // Simular el registro de un servicio
        try {
            Method method = TestController.class.getMethod("testEndpoint", String.class);
            HttpServer.services.put("/test", method);
            
            assertTrue(HttpServer.services.containsKey("/test"),
                    "El servicio debe estar registrado en el mapa de servicios");
            assertEquals(method, HttpServer.services.get("/test"),
                    "El método registrado debe ser el correcto");
        } catch (NoSuchMethodException e) {
            fail("No se pudo encontrar el método de prueba");
        }
    }

    @Test
    public void testMultipleServicesRegistration() {
        // Verificar el registro de múltiples servicios
        try {
            Method method1 = TestController.class.getMethod("testEndpoint", String.class);
            Method method2 = TestController.class.getMethod("anotherEndpoint", String.class);
            
            HttpServer.services.put("/test", method1);
            HttpServer.services.put("/another", method2);
            
            assertEquals(2, HttpServer.services.size(),
                    "Deben estar registrados exactamente 2 servicios");
            assertTrue(HttpServer.services.containsKey("/test"),
                    "Debe contener el primer servicio");
            assertTrue(HttpServer.services.containsKey("/another"),
                    "Debe contener el segundo servicio");
        } catch (NoSuchMethodException e) {
            fail("No se pudieron encontrar los métodos de prueba");
        }
    }

    // Clase de prueba para simular un controlador
    @RestController
    public static class TestController {
        
        @GetMapping("/test")
        public static String testEndpoint(@RequestParam(value = "name", defaultValue = "Default") String name) {
            return "Test response for " + name;
        }
        
        @GetMapping("/another")
        public static String anotherEndpoint(@RequestParam(value = "param", defaultValue = "Value") String param) {
            return "Another response for " + param;
        }
    }
}
