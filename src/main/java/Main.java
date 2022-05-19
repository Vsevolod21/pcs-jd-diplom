import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String pathName = "pdfs";

        BooleanSearchEngine engine = new BooleanSearchEngine(new File(pathName));

        int port = 8989;
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(clientSocket.getInputStream()));
            ) {
                System.out.printf("Соединение установлено, порт: %d%n",
                        clientSocket.getPort());

                out.println("Введите слово для поиска");
                out.flush();
                String word = in.readLine();

                List<PageEntry> list = engine.search(word);
//                Collections.sort(list);
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();

                out.println("Результат поиска по слову - " + word + ":");

                for (PageEntry pageEntry : list) {
                    out.println(gson.toJson(pageEntry));
                }
                break;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}