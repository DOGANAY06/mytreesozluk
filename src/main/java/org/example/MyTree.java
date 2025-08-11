package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

// ==============================
// 1. Trie Node Sınıfı
// ==============================
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord; // Bu düğüm bir kelimenin sonunu gösteriyor mu?
}

// ==============================
// 2. Trie Veri Yapısı
// ==============================
class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Kelimeyi Trie'ye ekle
    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        node.isEndOfWord = true;
    }

    // Verilen prefix'e uygun kelimeleri bul
    public List<String> searchPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = root;

        // Prefix'in son düğümüne git
        for (char ch : prefix.toCharArray()) {
            node = node.children.get(ch);
            if (node == null) {
                return results; // Prefix yoksa boş liste dön
            }
        }

        // O noktadan itibaren tüm kelimeleri bul
        dfs(node, prefix, results);
        return results;
    }

    // Derinlik öncelikli arama
    // Her dalın sonuna kadar gidip kelimeleri tamamlıyor. Bu yüzden  DFS
    private void dfs(TrieNode node, String currentWord, List<String> results) {
        if (node.isEndOfWord) {
            results.add(currentWord);
        }
        // Tüm yolları gezip results listesine ekler.
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            dfs(entry.getValue(), currentWord + entry.getKey(), results);
        }
    }
}

// ==============================
// 3. Dosya Yükleme Yardımcı Sınıfı
// ==============================
class DictionaryLoader {
    public static void loadDictionary(String resourcePath, Trie trie) throws IOException {
        try (InputStream is = MyTree.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String word;
            while ((word = br.readLine()) != null) {
                trie.insert(word.trim().toUpperCase());
            }
        }
    }
}

// ==============================
// 4. Main Sınıf (Ana Sınıf)
// ==============================
public class MyTree {
    public static void main(String[] args) {
        Trie trie = new Trie();

        try {
            // Sözlük dosya adını sabit verdik
            String dictionaryFile = "sozluk.txt";
            System.out.println("> Java MyTree sözlük.txt");
            System.out.println("Sözlük Yükleniyor. Lütfen Bekleyin...");
            DictionaryLoader.loadDictionary(dictionaryFile, trie);
            System.out.println("Sözlük Yüklendi.");

            Scanner scanner = new Scanner(System.in);

            // Direkt kelime girişine geçiyoruz
            while (true) {
                System.out.println("Bir Kelime Yazıp Enter Tuşuna Basınız (Çıkmak için EXIT yazın):");
                String input = scanner.nextLine().trim().toUpperCase();

                if (input.equals("EXIT")) break;

                List<String> results = trie.searchPrefix(input);
                if (results.isEmpty()) {
                    System.out.println("Sonuç bulunamadı.");
                } else {
                    System.out.println("Olası Kelimeler:");
                    for (String word : results) {
                        System.out.println(word);
                    }
                }
            }

            scanner.close();

        } catch (IOException e) {
            System.err.println("Sözlük yüklenirken hata oluştu: " + e.getMessage());
        }
    }
}

