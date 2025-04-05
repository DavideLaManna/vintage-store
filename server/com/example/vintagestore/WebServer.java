package com.example.vintagestore;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class WebServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 5000), 0);

        server.createContext("/", new FileHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port 5000");
        System.out.println("Visit: http://0.0.0.0:5000/");
    }

    static class FileHandler implements HttpHandler {
        private final Map<String, String> mimeTypes = new HashMap<>();
        private final String BASE_PATH = "../app/src/main";

        public FileHandler() {
            mimeTypes.put("html", "text/html");
            mimeTypes.put("css", "text/css");
            mimeTypes.put("js", "application/javascript");
            mimeTypes.put("jpg", "image/jpeg");
            mimeTypes.put("jpeg", "image/jpeg");
            mimeTypes.put("png", "image/png");
            mimeTypes.put("svg", "image/svg+xml");
            mimeTypes.put("json", "application/json");
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();

            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

            String filePath = BASE_PATH + requestPath;
            File file = new File(filePath);

            if (!file.exists() && !requestPath.equals("/login.html")) {
                filePath = BASE_PATH + "/login.html";
                file = new File(filePath);
            }

            if (file.exists()) {
                String fileExtension = getFileExtension(filePath);
                String contentType = mimeTypes.getOrDefault(fileExtension, "text/plain");

                byte[] fileData = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileData.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileData);
                }
            } else {
                String response = "404 (Not Found)\n";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }

        private String getFileExtension(String filePath) {
            int lastDotIndex = filePath.lastIndexOf(".");
            if (lastDotIndex > 0) {
                return filePath.substring(lastDotIndex + 1).toLowerCase();
            }
            return "";
        }
    }
}