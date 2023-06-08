package server;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import keys.AirportKeys;

public class Test {
    static String token;
    static String oldToken;
    static String refresh;
    static PublicKey serverPubKey;
    public static void main(String[] args) throws IOException {

        for (String s : args)
            System.out.println(s);
        String host;
        if (args.length == 0)
            host = "84.246.85.148";
        else
            host = args[0];

        // Получить публичный ключ сервера
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write("getkey\n"); // ->
            writer.flush();
            String spk = reader.readLine();  // <-
            serverPubKey = AirportKeys.readPublicKey(spk);
            if (serverPubKey != null)
                System.out.println("Test 1 passed (Successfully recieved server public key)");
        }

        // Зарегистрировать admin
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            byte[] bytes = AirportKeys.cipher(serverPubKey, AirportKeys.ENCRYPT, "register admin adminpwd");
            writer.write(new String(Base64.getEncoder().encode(bytes)) + "\n"); // ->
            writer.flush();
            String answer = reader.readLine(); // <-
            if (answer.equals("Already registered") || answer.equals("Successfully registered"))
                System.out.println("Test 2 passed (Registration)");
            else
                System.out.println("Test 3 failed (Couldn't register)");
        }


        // Залогинить admin
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            byte[] bytes = AirportKeys.cipher(serverPubKey, AirportKeys.ENCRYPT, "login admin adminpwd");
            writer.write(new String(Base64.getEncoder().encode(bytes)) + "\n"); // ->
            writer.flush(); // ->
            String[] answer = reader.readLine().split(" ");
            token = answer[0];
            refresh = answer[1];
            if (token.charAt(0) == 'e')
                System.out.println("Test 3 passed (Recieved JWT tokens)");
            else System.out.println("Test 3 failed (Couldn't recieve JWT)");
        }
        int id = 0;

        // Получить Automatic как admin
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write(token + " getall\n"); // ->
            writer.flush();
            String answer = reader.readLine(); // <-
            if (answer.startsWith("<response>")) {
                System.out.println("Test 4 passed (Recieved valid XML)");
                Pattern pattern = Pattern.compile("id=\"(\\d+)\"");
                Matcher matcher = pattern.matcher(answer);
                matcher.find();
                id = Integer.parseInt(matcher.group(1));
            } else
                System.out.println("Test 4 failed (Didn't recieve XML)");
        }


        // Получить Automatic c невалидным JWT
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write("A.B.C getall\n");
            writer.flush();
            String answer = reader.readLine(); // <-
            if (answer.equals("Invalid JWT"))
                System.out.println("Test 5 passed (Invalid JWT)");
            else
                System.out.println("Test 5 falied (Couldn't recognize invalid JWT)");
        }


        // Получить лайки как admin
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write(token + " getfavs\n"); // ->
            writer.flush();
            String answer = reader.readLine(); // <-
            if (answer.equals("<response></response>"))
                System.out.println("Test 6 passed (No favorites)");
            else
                System.out.println("Test 6 failed (Favorites???)");
            writer.close();
        }


        // Получить инит как admin
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write(token + " mark " + id + "\n"); // ->
            writer.flush();
            String answer = reader.readLine();  // <-
            if (answer.equals("Fav added"))
                System.out.println("Test 7 passed (Has favorites)");
            else
                System.out.println("Test 7 failed (No favorites)");
        }

        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write(token + " getfavs\n"); // ->
            writer.flush();
            String answer = reader.readLine();  // <-
            if(answer.contains("id=\"" + id + "\""))
                System.out.println("Test 7.5 passed (Has favorites)");
            else
                System.out.println("Test 7.5 failed (No favorites)");
        }

        String oldToken = null;

        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write("refresh " + token + " " + refresh + "\n");
            writer.flush(); // ->
            String[] answer = reader.readLine().split(" ");
            oldToken = token;
            token = answer[0];
            refresh = answer[1];
            if (token.charAt(0) == 'e')
                System.out.println("Test 8 passed (Refresh successfull)");
            else System.out.println("Test 8 failed (Refresh unsuccessfull)");
        }

        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write(oldToken + " getall\n");
            writer.flush(); // ->
            String[] answer = reader.readLine().split(" ");
            oldToken = token;
            token = answer[0];
            refresh = answer[1];
            if (token.equals("Invalid"))
                System.out.println("Test 9 passed (Old token doesn't work)");
            else System.out.println("Test 9 failed (Old token works)");
        }


        // Залогинить несуществующего пользователя
        try (Socket socket = new Socket(host, 42000)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            byte[] bytes = AirportKeys.cipher(serverPubKey, AirportKeys.ENCRYPT, "login nonexist adminpwd");
            writer.write(new String(Base64.getEncoder().encode(bytes)) + "\n");
            writer.flush(); // ->
            String answer = reader.readLine(); // <-
            if (answer.equals("No such user"))
                System.out.println("Test 10 passed (No such user)");
            else
                System.out.println("Test 10 failed (There is such user)");
        }
    }
}