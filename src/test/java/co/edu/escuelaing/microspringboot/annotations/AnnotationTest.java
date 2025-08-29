package co.edu.escuelaing.microspringboot.annotations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Pruebas unitarias para las anotaciones del framework
 * Verifica el correcto funcionamiento de @RestController, @GetMapping y @RequestParam
 */
public class AnnotationTest {

    @Test
    public void testRestControllerAnnotation() {
        // Verificar que la anotación @RestController funciona correctamente
        assertTrue(TestAnnotatedClass.class.isAnnotationPresent(RestController.class),
                "La clase debe estar anotada con @RestController");
        
        RestController annotation = TestAnnotatedClass.class.getAnnotation(RestController.class);
        assertNotNull(annotation, "La anotación @RestController no debe ser null");
    }

    @Test
    public void testGetMappingAnnotation() throws NoSuchMethodException {
        // Verificar que la anotación @GetMapping funciona correctamente
        Method method = TestAnnotatedClass.class.getMethod("mappedMethod", String.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
                "El método debe estar anotado con @GetMapping");
        
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertEquals("/test-mapping", mapping.value(),
                "El valor de @GetMapping debe ser '/test-mapping'");
    }

    @Test
    public void testRequestParamAnnotation() throws NoSuchMethodException {
        // Verificar que la anotación @RequestParam funciona correctamente
        Method method = TestAnnotatedClass.class.getMethod("mappedMethod", String.class);
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        
        assertTrue(paramAnnotations.length > 0,
                "Debe haber anotaciones en los parámetros");
        assertTrue(paramAnnotations[0].length > 0,
                "El primer parámetro debe tener anotaciones");
        assertTrue(paramAnnotations[0][0] instanceof RequestParam,
                "La anotación debe ser de tipo RequestParam");
        
        RequestParam requestParam = (RequestParam) paramAnnotations[0][0];
        assertEquals("testParam", requestParam.value(),
                "El valor de @RequestParam debe ser 'testParam'");
        assertEquals("DefaultValue", requestParam.defaultValue(),
                "El valor por defecto debe ser 'DefaultValue'");
    }

    @Test
    public void testAnnotationRetentionPolicy() {
        // Verificar que las anotaciones tienen la política de retención correcta (RUNTIME)
        RestController restController = TestAnnotatedClass.class.getAnnotation(RestController.class);
        assertNotNull(restController,
                "La anotación @RestController debe estar disponible en tiempo de ejecución");
    }

    // Clase de prueba para verificar las anotaciones
    @RestController
    public static class TestAnnotatedClass {
        
        @GetMapping("/test-mapping")
        public static String mappedMethod(@RequestParam(value = "testParam", defaultValue = "DefaultValue") String param) {
            return "Response: " + param;
        }
        
        public static String nonAnnotatedMethod() {
            return "No annotations here";
        }
    }
}
