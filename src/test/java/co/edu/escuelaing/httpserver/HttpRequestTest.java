package co.edu.escuelaing.httpserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Pruebas unitarias para la clase HttpRequest
 * Verifica el correcto funcionamiento de la extracción de parámetros de consulta
 */
public class HttpRequestTest {

    private HttpRequest httpRequest;

    @Test
    public void testGetValueWithSingleParameter() throws URISyntaxException {
        // Prueba con un solo parámetro
        URI uri = new URI("/greeting?name=Pedro");
        httpRequest = new HttpRequest(uri);
        
        String result = httpRequest.getValue("name");
        assertEquals("Pedro", result, "Debe extraer correctamente el parámetro 'name'");
    }

    @Test
    public void testGetValueWithMultipleParameters() throws URISyntaxException {
        // Prueba con múltiples parámetros
        URI uri = new URI("/service?name=Juan&age=25&city=Bogota");
        httpRequest = new HttpRequest(uri);
        
        assertEquals("Juan", httpRequest.getValue("name"), "Debe extraer correctamente el primer parámetro");
        assertEquals("25", httpRequest.getValue("age"), "Debe extraer correctamente el segundo parámetro");
        assertEquals("Bogota", httpRequest.getValue("city"), "Debe extraer correctamente el tercer parámetro");
    }

    @Test
    public void testGetValueWithEmptyQuery() throws URISyntaxException {
        // Prueba sin query string
        URI uri = new URI("/greeting");
        httpRequest = new HttpRequest(uri);
        
        String result = httpRequest.getValue("name");
        assertEquals("", result, "Debe retornar cadena vacía cuando no hay query string");
    }

    @Test
    public void testGetValueWithNonExistentParameter() throws URISyntaxException {
        // Prueba con parámetro que no existe
        URI uri = new URI("/greeting?name=Pedro");
        httpRequest = new HttpRequest(uri);
        
        String result = httpRequest.getValue("nonexistent");
        assertEquals("", result, "Debe retornar cadena vacía para parámetros inexistentes");
    }

    @Test
    public void testGetValueWithEmptyParameterValue() throws URISyntaxException {
        // Prueba con parámetro sin valor
        URI uri = new URI("/greeting?name=&age=25");
        httpRequest = new HttpRequest(uri);
        
        assertEquals("", httpRequest.getValue("name"), "Debe manejar parámetros con valores vacíos");
        assertEquals("25", httpRequest.getValue("age"), "Debe extraer correctamente otros parámetros");
    }
}
