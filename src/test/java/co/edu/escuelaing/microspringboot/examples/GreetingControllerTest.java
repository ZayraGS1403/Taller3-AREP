package co.edu.escuelaing.microspringboot.examples;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import co.edu.escuelaing.microspringboot.annotations.GetMapping;
import co.edu.escuelaing.microspringboot.annotations.RequestParam;
import co.edu.escuelaing.microspringboot.annotations.RestController;

/**
 * Pruebas unitarias para GreetingController
 * Verifica el correcto funcionamiento del controlador de ejemplo
 */
public class GreetingControllerTest {

    @Test
    public void testControllerHasRestControllerAnnotation() {
        // Verificar que GreetingController tiene la anotación @RestController
        assertTrue(GreetingController.class.isAnnotationPresent(RestController.class),
                "GreetingController debe tener la anotación @RestController");
    }

    @Test
    public void testGreetingMethodWithDefaultValue() throws Exception {
        // Probar el método greeting con valor por defecto
        String result = GreetingController.greeting("World");
        assertEquals("Hola World", result,
                "El método greeting debe retornar 'Hola World' con el valor por defecto");
    }

    @Test
    public void testGreetingMethodWithCustomValue() throws Exception {
        // Probar el método greeting con valor personalizado
        String result = GreetingController.greeting("Pedro");
        assertEquals("Hola Pedro", result,
                "El método greeting debe retornar 'Hola Pedro' con valor personalizado");
    }

    @Test
    public void testHelloServiceMethod() throws Exception {
        // Probar el método helloService
        String result = GreetingController.helloService("Juan");
        assertEquals("Hola Juan", result,
                "El método helloService debe retornar 'Hola Juan'");
    }

    @Test
    public void testMethodAnnotations() throws NoSuchMethodException {
        // Verificar las anotaciones de los métodos
        Method greetingMethod = GreetingController.class.getMethod("greeting", String.class);
        Method helloMethod = GreetingController.class.getMethod("helloService", String.class);
        
        // Verificar @GetMapping en greeting
        assertTrue(greetingMethod.isAnnotationPresent(GetMapping.class),
                "El método greeting debe tener @GetMapping");
        GetMapping greetingMapping = greetingMethod.getAnnotation(GetMapping.class);
        assertEquals("/greeting", greetingMapping.value(),
                "El mapping del método greeting debe ser '/greeting'");
        
        // Verificar @GetMapping en helloService
        assertTrue(helloMethod.isAnnotationPresent(GetMapping.class),
                "El método helloService debe tener @GetMapping");
        GetMapping helloMapping = helloMethod.getAnnotation(GetMapping.class);
        assertEquals("/hello", helloMapping.value(),
                "El mapping del método helloService debe ser '/hello'");
    }

    @Test
    public void testRequestParamAnnotations() throws NoSuchMethodException {
        // Verificar las anotaciones @RequestParam
        Method greetingMethod = GreetingController.class.getMethod("greeting", String.class);
        RequestParam requestParam = (RequestParam) greetingMethod.getParameterAnnotations()[0][0];
        
        assertEquals("name", requestParam.value(),
                "El valor de @RequestParam debe ser 'name'");
        assertEquals("World", requestParam.defaultValue(),
                "El valor por defecto debe ser 'World'");
    }
}
