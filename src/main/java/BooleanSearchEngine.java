import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    Map<String, List<PageEntry>> wordList = new HashMap<>();
    File pdfsDir;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        this.pdfsDir = pdfsDir;

        for (File file : pdfsDir.listFiles()) {

            var doc = new PdfDocument(new PdfReader(file));
            int numberOfPages = doc.getNumberOfPages();

            for (int i = 1; i <= numberOfPages; i++) {

                PdfPage page = doc.getPage(i);
                String text = PdfTextExtractor.getTextFromPage(page);
                String[] words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>();
                for (String word : words) {//
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(),
                            freqs.getOrDefault(word, 0) + 1);
                }

                for (Map.Entry<String, Integer> kv : freqs.entrySet()) {
                    List<PageEntry> pageEntryList = new ArrayList<>();
                    if (wordList.containsKey(kv.getKey())) {
                        pageEntryList = wordList.get(kv.getKey());
                    }
                    PageEntry pageEntry = new PageEntry(file.getName(), i, kv.getValue());
                    pageEntryList.add(pageEntry);
                    wordList.put(kv.getKey(), pageEntryList);
                }
            }
        }
        System.out.println("Количество слов в исходных документах: " + wordList.size());
    }

    @Override
    public List<PageEntry> search(String word) {
        if (wordList.containsKey(word)) {
            return wordList.get(word);
        }
        return Collections.emptyList();
    }
}
