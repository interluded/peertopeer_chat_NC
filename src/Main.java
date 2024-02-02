import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Are you hosting? (true/false): ");
        boolean hoster = scan.nextBoolean();
        // Consume the leftover newline
        scan.nextLine();

        String ip = "";
        String port = "";

        if (!hoster) {
            System.out.print("Type IP: ");
            ip = scan.nextLine();
            System.out.print("Type Port: ");
            port = scan.nextLine();
        }

        try {
            Process process;
            if (hoster) {
                process = Runtime.getRuntime().exec(new String[]{"nc", "-nvlp", "4444"}); // Hosting command
            } else {
                process = Runtime.getRuntime().exec(new String[]{"nc", ip, port}); // Connecting command
            }

            // Handle output and error streams in separate threads
            new Thread(() -> readStream(process.getInputStream())).start();
            new Thread(() -> readStream(process.getErrorStream())).start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Process exited with code " + exitCode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readStream(java.io.InputStream inputStream) {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(System.out::println);
    }
}
