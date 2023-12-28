package com.example.knightshift.ocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The OCR thread should be used to send pictures of PGN score sheets to the Python microservice.
 * The thread waits for a response which can be queried after the thread has run. Using a thread
 * prevents the UI from hanging.
 */
public class OcrThread extends Thread {

    private final String url;
    private final String filePath; // TODO, use data directory
    private final InputStream in;
    private URLConnection connection;
    private String response;

    private volatile boolean done;

    public OcrThread(String url, InputStream in, String filePath) throws IOException {
        this.url = url;
        this.in = in;
        this.filePath = filePath;
        this.connection = new URL(this.url).openConnection();
        done = false;
    }

    // Uses input stream to create a File object
    private File getFile() throws IOException {
        File file = new File(filePath);
//        OutputStream out = new FileOutputStream(file);
//
//        byte[] buffer = new byte[1024];
//        while ((in.read(buffer)) > 0) {
//            out.write(buffer);
//        }
//
//        out.close();

        return file;
    }

    public String getResponse() {
        return this.response;
    }

    public boolean done() {
        return done;
    }

    public void run() {
        File file = null;
        try {
            file = getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String charset = "UTF-8";
        String boundary = "*****";
        String CTRLF = "\r\n";

        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.setDoOutput(true); // POST request
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                InputStream in = new FileInputStream(file);
        ) {
            writer.append("--" + boundary).append(CTRLF);
            writer.append("Content-Disposition: form-data; name=\"scoresheet\"; filename=\"" + file.getName() + "\"").append(CTRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(CTRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CTRLF);
            writer.append("Connection: close").append(CTRLF);
            writer.append(CTRLF).flush();

            // TODO copy all bytes
//            Files.copy(in, output);
            byte[] arr = new byte[1024];
            while (in.read(arr) != -1) {
                output.write(arr);
            }

            output.flush();
            writer.append(CTRLF).flush();

            // end of multipart/form-data.
            writer.append("--" + boundary + "--").append(CTRLF).flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line);
            }

            this.response = sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        done = true;
    }

    /**
     * Unit tests the OCR thread data type.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/001_0.png");
        OcrThread thread = new OcrThread("http://127.0.0.1:5000/scoresheet", null, "TODO");
        thread.start();
        while (thread.isAlive());
        System.out.println(thread.response);
    }
}
