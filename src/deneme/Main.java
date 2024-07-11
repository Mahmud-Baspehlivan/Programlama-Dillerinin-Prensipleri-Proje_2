package deneme;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("GitHub depo linkini girin: ");
            String repoUrl = reader.readLine();

            // Git deposunu klonla
            File klonlananDepoKlasoru = new File("klonlananDepo");
            FileUtils.deleteDirectory(klonlananDepoKlasoru);
            klonlananDepoKlasoru.mkdir();
            ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", repoUrl, klonlananDepoKlasoru.getAbsolutePath());
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Git klonlama işlemi başarısız oldu.");
                return;
            }

            // Java sınıflarını bul
            var javaFiles = FileUtils.findJavaFiles(klonlananDepoKlasoru);

            for (File javaFile : javaFiles) {
                CodeAnalyzer.analyzeJavaFile(javaFile);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}