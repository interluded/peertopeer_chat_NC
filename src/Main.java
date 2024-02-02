import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Are you hosting? (true/false): ");
        boolean hoster = scan.nextBoolean();
        scan.nextLine();

        String ip = "";
        String port = "";

        if (!hoster) {
            System.out.print("Type IP: ");
            ip = scan.nextLine();
            System.out.print("Type Port: ");
            port = scan.nextLine();
        }

        Process process = null;
        try {
            if (hoster) {
                process = Runtime.getRuntime().exec(new String[]{"nc", "-nvlp", "4444"});
            } else {
                process = Runtime.getRuntime().exec(new String[]{"nc", ip, port});
            }

            Process finalProcess = process;
            new Thread(() -> readStream(finalProcess.getInputStream())).start();
            Process finalProcess1 = process;
            new Thread(() -> readStream(finalProcess1.getErrorStream())).start();

            try (OutputStream os = process.getOutputStream(); PrintWriter pw = new PrintWriter(os)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while ((line = br.readLine()) != null) {
                    pw.println(line);
                    pw.flush();
                    if ("exit".equals(line)) {
                        break;
                    }
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Process exited with code " + exitCode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static void readStream(java.io.InputStream inputStream) {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(System.out::println);
    }
}
