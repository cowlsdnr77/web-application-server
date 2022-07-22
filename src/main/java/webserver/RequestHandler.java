package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

import static util.HttpRequestUtils.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            log.debug("request line : {}", line);

            String url = getUrl(line);
            HashMap<String, String> headers = new HashMap<>();

            while (!line.equals("")) {
                log.debug("header: {}", line);
                line = br.readLine();
                String[] headerTokens = line.split(": ");
                if (headerTokens.length == 2) {
                    headers.put(headerTokens[0], headerTokens[1]);
                }
            }

            log.debug("Content-Length : {}", headers.get("Content-Length"));

            DataOutputStream dos = new DataOutputStream(out);

            if (url.startsWith("/user/create")) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                log.debug("requestBody : {}", requestBody);
                Map<String, String> requestParams = parseQueryString(requestBody);
                User user = new User(requestParams.get("userId"), requestParams.get("password"), requestParams.get("name"), requestParams.get("email"));
                log.debug("user = {}", user);
                DataBase.addUser(user);
                response302Header(dos);
            } else if (url.equals("/user/login")) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                log.debug("requestBody : {}", requestBody);
                Map<String, String> requestParams = parseQueryString(requestBody);
                log.debug("userId: {}, password: {}", requestParams.get("userId"), requestParams.get("password"));
                User user = DataBase.findUserById(requestParams.get("userId"));

                if (user == null) {
                    log.debug("User Not Found");
                    responseResource(out, "/user/login_failed.html");
                    return;
                }

                if (user.getPassword().equals(requestParams.get("password"))) {
                    log.debug("Login Success");
                    response302LoginSuccessHeader(dos);
                } else {
                    log.debug("Password Mismatch");
                    responseResource(dos, "/user/login_failed.html");
                }
            } else {
                responseResource(dos, url);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
