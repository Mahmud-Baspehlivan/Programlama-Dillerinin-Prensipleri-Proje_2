package deneme;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzer {
    public static void analyzeJavaFile(File javaFile) {
        try {
            List<String> lines = Files.readAllLines(javaFile.toPath());
            boolean isClass = false;

            // Dosyanın sınıf mı yoksa arayüz mü olduğunu kontrol et
            for (String line : lines) {
                if (line.contains("class ") && !line.contains("interface ")) {
                    isClass = true;
                    break;
                }
            }

            // Sadece sınıf dosyalarının içeriğini bastır
            if (isClass) {
                int javadocLines = countJavadocLines(lines);
                int commentLines = countCommentLines(lines);
                double codeLines = countCodeLines(lines);
                int totalLines = lines.size();
                double functionCount = countFunctions(String.join("\n", lines));

                double yg = ((javadocLines + commentLines) * 0.8) / functionCount;
                double yh = (codeLines / functionCount) * 0.3;
                double commentDeviationPercentage = ((100.0 * yg) / yh) - 100.0;

                System.out.println("Sınıf: " + javaFile.getName());
                System.out.println("Javadoc Satır Sayısı: " + javadocLines);
                System.out.println("Yorum Satır Sayısı: " + commentLines);
                System.out.println("Kod Satır Sayısı: " + (int) codeLines);
                System.out.println("LOC: " + totalLines);
                System.out.println("Fonksiyon Sayısı: " + (int) functionCount);
                System.out.printf("Yorum Sapma Yüzdesi: %.2f%%\n", commentDeviationPercentage);
                System.out.println("-----------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int countJavadocLines(List<String> lines) {
        int javadocLines = 0;
        boolean inJavadoc = false;

        for (String line : lines) {
            String trimmedLine = line.trim();
            
            if (trimmedLine.equals("/**")) { // Tam eşleşme kontrolü
                inJavadoc = true;
            } else if (trimmedLine.startsWith("*/")) { // Javadoc sonu kontrolü
                inJavadoc = false;
            } else if (inJavadoc && trimmedLine.startsWith("*")) { // İçinde veya yorum satırı kontrolü
                javadocLines++;
            }
          
        }
        
        return javadocLines;
    }
    
    private static int countCommentLines(List<String> lines) {
        int commentLines = 0;
        boolean inMultiLineComment = false;
        boolean multiLineCommentStarted = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Çok satırlı yorum kontrolü
            if (trimmedLine.equals("/*")) {
                inMultiLineComment = true;
                multiLineCommentStarted = true;
            } else if (trimmedLine.endsWith("*/") && multiLineCommentStarted) {
                inMultiLineComment = false;
                multiLineCommentStarted = false;
            } else if (inMultiLineComment && !trimmedLine.isEmpty()) {
                commentLines++;
            }

            // Tek satırlık yorum kontrolü
            if (!inMultiLineComment && trimmedLine.contains("//")) {
                commentLines++;
            }
        }
        return commentLines;
    }

    private static int countCodeLines(List<String> lines) {
        int codeLines = 0;
        boolean inMultiLineComment = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Çok satırlı yorum kontrolü
            if (trimmedLine.startsWith("/*")) {
                inMultiLineComment = true;
            }

            // Tek satırlık yorum kontrolü
            if (!inMultiLineComment && !trimmedLine.isEmpty() && !trimmedLine.startsWith("//")) {
                codeLines++;
            }

            // Çok satırlı yorum sonu kontrolü
            if (trimmedLine.endsWith("*/")) {
                inMultiLineComment = false;
            }
        }

        return codeLines;
    }

    private static double countFunctions(String codeContent) {
        Pattern pattern = Pattern.compile("\\b\\w+\\s+\\w+\\s*\\([^\\)]*\\)\\s*\\{");
        Matcher matcher = pattern.matcher(codeContent);
        double functionCount = 0;
        while (matcher.find()) {
            functionCount++;
        }
        return functionCount;
    }
}