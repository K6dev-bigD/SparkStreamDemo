package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class DataServer {

    /**
     * Run .
     */
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            while (true) {
                Socket socket = listener.accept();

                InputStreamReader input = new InputStreamReader(socket.getInputStream());
                BufferedReader bread = new BufferedReader(input);

                BufferedReader brCli = new BufferedReader(new InputStreamReader(System.in));
                String strCli = brCli.readLine();


                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(strCli);
                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
    }
}