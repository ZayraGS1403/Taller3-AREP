
package co.edu.escuelaing.httpserver;

import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import co.edu.escuelaing.microspringboot.annotations.GetMapping;
import co.edu.escuelaing.microspringboot.annotations.RequestParam;
import co.edu.escuelaing.microspringboot.annotations.RestController;

public class HttpServer {

    private static String principalPath = "src/main/java/resorces/";
    public static Map<String, Method> services = new HashMap<String, Method>();

    public static void startServer(String[] args) throws IOException, URISyntaxException {

        loadControllers(args);

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;

        boolean running = true;

        while (running) {

            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine;

            boolean isFirstLine = true;
            URI requestUri = null;

            while ((inputLine = in.readLine()) != null) {

                if (isFirstLine) {
                    requestUri = new URI(inputLine.split(" ")[1]);
                    System.out.println("Path: " + requestUri.getPath());
                    isFirstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            typeRequest(requestUri, out, clientSocket.getOutputStream());
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static void loadControllers(String[] args){
       if (args != null && args.length > 0) {
            loadOneController(args[0]);
        } else {
            loadAllControllers();
        }
    }

    private static void loadOneController(String className){
        try {
            Class c = Class.forName(className);
            loadControllersRestMethods(c);
        } catch (ClassNotFoundException ex) {
            System.getLogger(HttpServer.class.getName()).log(System.Logger.Level.ERROR, "Controller not found: " + className, ex);
        }
    }

    private static void loadAllControllers(){
        Set<Class<?>> restControllers = findControllers();
        for (Class<?> controller : restControllers) {
            loadControllersRestMethods(controller);
        }
        System.out.println(restControllers.size() + " REST controllers");
    }

    private static void loadControllersRestMethods(Class<?> c){
        if (c.isAnnotationPresent(RestController.class)) {
            Method[] methods = c.getDeclaredMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(GetMapping.class)) {
                    String mapping = m.getAnnotation(GetMapping.class).value();
                    services.put(mapping, m);
                    System.out.println("New endpoint: " + mapping + c.getSimpleName() + "." + m.getName());
                }
            }
        }
    }

    private static Set<Class<?>> findControllers(){
        Set<Class<?>> controllers = new HashSet<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String packageName = "co.edu.escuelaing.microspringboot";
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.toURI());
                    controllers.addAll(findDirectory(directory, packageName));
                } else if (resource.getProtocol().equals("jar")) {
                    controllers.addAll(findClassJar(resource, packageName));
                }
            }
        } catch (Exception e) {
            System.err.println("Error en classpath: " + e.getMessage());
            e.printStackTrace();
        }
        return controllers;
    }

    private static Set<Class<?>> findDirectory(File directory, String packageName) {
        Set<Class<?>> controllers = new HashSet<>();
        if (!directory.exists()) {
            return controllers;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    controllers.addAll(findDirectory(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(RestController.class)) {
                            controllers.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        // Ignore
                    }
                }
            }
        }
        return controllers;
    }

    private static Set<Class<?>> findClassJar(URL jarURI, String packageName){
        Set<Class<?>> controllers = new HashSet<>();
        try {
            String jarPath = jarURI.getPath().substring(5, jarURI.getPath().indexOf("!"));
            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.endsWith(".class") && entryName.startsWith(packageName.replace('.', '/'))) {
                    String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(RestController.class)) {
                            controllers.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        // Ignore
                    }
                }
            }
            jarFile.close();
        } catch (Exception e) {
            System.err.println("Error en JAR: " + e.getMessage());
        }

        return controllers;
    }

    private static void typeRequest(URI requestUri, PrintWriter out, OutputStream imageOut) {
        if (requestUri.getPath().endsWith(".js")) {
            handleJS(requestUri, out);
        } else if (requestUri.getPath().endsWith(".css")) {
            handleCSS(requestUri, out);
        } else if (requestUri.getPath().endsWith(".html") || requestUri.getPath().equalsIgnoreCase("/")) {
            handleHTML(requestUri, out);
        } else if (requestUri.getPath().startsWith("/app")) {
            processAppRequest(requestUri, out);
        } else if (requestUri.getPath().endsWith(".png")
                || requestUri.getPath().endsWith(".jpg") || requestUri.getPath().endsWith(".ico")) {
            try {
                handleImage(requestUri, imageOut);
            } catch (IOException ex) {
                System.getLogger(HttpServer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        } else {
            handleNotFound(out);
        }
    }

    private static void handleNotFound(PrintWriter out) {
        String response = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n"
                + "404 Not Found";
        out.write(response);
    }

    private static void handleJS(URI requestUri, PrintWriter out) {

        String filePath = principalPath + requestUri.getPath();
        File file = new File(filePath);
        if (!file.exists()) {
            handleNotFound(out);
            return;
        }

        String output = "HTTP/1.1 200 OK\n\r"
                + "contente-type: text/javascript\n\r"
                + "\n\r";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.write(output);
    }

    private static void handleCSS(URI requestUri, PrintWriter out) {

        String filePath = principalPath + requestUri.getPath();
        File file = new File(filePath);
        if (!file.exists()) {
            handleNotFound(out);
            return;
        }

        String output = "HTTP/1.1 200 OK\n\r"
                + "contente-type: text/css\n\r"
                + "\n\r";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.write(output);
    }

    private static void handleHTML(URI requestUri, PrintWriter out) {

        String filePath = requestUri.getPath().equalsIgnoreCase("/") ? principalPath + "index.html" : principalPath + requestUri.getPath();
        File file = new File(filePath);
        if (!file.exists()) {
            handleNotFound(out);
            return;
        }

        String output = "HTTP/1.1 200 OK\n\r"
                + "contente-type: text/html\n\r"
                + "\n\r";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.write(output);
    }

    private static void processAppRequest(URI requestUri, PrintWriter out) {
        String serRoute = requestUri.getPath().substring(4);
        Method m = services.get(serRoute);

        if (m == null) {
            handleNotFound(out);
            return;
        }

        HttpRequest req = new HttpRequest(requestUri);
        HttpResponse res = new HttpResponse();

        String responseHeader = "HTTP/1.1 200 OK\n\r"
                + "contente-type: application/json\n\r"
                + "\n\r";
        try {
            RequestParam rp = (RequestParam) m.getParameterAnnotations()[0][0];
            String queryParamName = rp.value();
            String paramName = req.getValue(queryParamName);
            if (paramName.equals("")){
                paramName = rp.defaultValue();
            }
            String[] argValues = new String[]{paramName};
            out.write(responseHeader + m.invoke(null, argValues));
        } catch (IllegalAccessException ex){
            System.getLogger(HttpServer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (InvocationTargetException ex) {
            System.getLogger(HttpServer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }

    /**
     *
     * @param requestUri
     * @param out
     * @throws IOException
     */
    private static void handleImage(URI requestUri, OutputStream out) throws IOException {

        String filePath = requestUri.getPath().startsWith("/images/") ? principalPath + requestUri.getPath() : principalPath + "images/" + requestUri.getPath();

        File file = new File(filePath);

        if (!file.exists()) {
            PrintWriter outPrinter = new PrintWriter(out, true);
            handleNotFound(outPrinter);
            return;
        }

        String extension = requestUri.getPath().substring(requestUri.getPath().lastIndexOf(".") + 1).toLowerCase();

        String output = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: image/" + extension + "\r\n"
                + "Content-Length: " + file.length() + "\r\n"
                + "\r\n";

        try {
            out.write(output.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            System.getLogger(HttpServer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        try (FileInputStream fileInputStream = new FileInputStream(file); BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        out.flush();
    }


    public static void staticfiles(String staticfile) {
        if (staticfile.startsWith("/")) {
            principalPath = "target/classes" + staticfile + "/";
        } else {
            principalPath = "target/classes/" + staticfile + "/";
        }

        System.out.println("Static files will save in " + principalPath);
        createDirectory();
        copyFiles(principalPath, "src/main/java/resorces");
    }

    private static void createDirectory() {
        Path staticPath = Paths.get(principalPath);
        if (!staticPath.toFile().exists()) {
            try {
                staticPath.toFile().mkdirs();
                System.out.println("Directory created in " + principalPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyFiles(String dirDes, String dirOrigin) {
        Path desPath = Paths.get(dirDes);
        Path originPath = Paths.get(dirOrigin);

        try {
            java.nio.file.Files.walk(originPath).forEach(source -> {
                Path destination = desPath.resolve(originPath.relativize(source));
                try {
                    if (source.toFile().isDirectory()) {
                        if (!destination.toFile().exists()) {
                            destination.toFile().mkdirs();
                        }
                    } else {
                        java.nio.file.Files.copy(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
