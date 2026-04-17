package org.example;

import java.util.*;
import java.io.*;

/**
 * 게시글 데이터 접근 클래스
 * ArrayList 관리와 파일 I/O를 담당합니다.
 * App은 이 클래스를 통해서만 데이터에 접근합니다.
 */
public class ArticleRepository {

    private final List<Article> articles = new ArrayList<>();
    private int nextId = 1;
    private static final String DATA_FILE = "articles.dat";

    // CRUD

    public Article save(String title, String content, String regDate) {
        Article article = new Article(nextId++, title, content, regDate);
        articles.add(article);
        return article;
    }

    /** 전체 목록 반환 (최신순) */
    public List<Article> findAll() {
        List<Article> reversed = new ArrayList<>(articles);
        Collections.reverse(reversed);
        return reversed;
    }

    /** ID로 단건 조회, 없으면 null */
    public Article findById(int id) {
        return articles.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /** 키워드로 제목+내용 검색 (최신순) */
    public List<Article> search(String keyword) {
        String lower = keyword.toLowerCase();
        List<Article> results = new ArrayList<>();
        for (Article a : articles) {
            if (a.getTitle().toLowerCase().contains(lower)
                    || a.getContent().toLowerCase().contains(lower)) {
                results.add(a);
            }
        }
        Collections.reverse(results);
        return results;
    }

    public boolean delete(int id) {
        Article article = findById(id);
        if (article == null) return false;
        articles.remove(article);
        return true;
    }

    public int size() {
        return articles.size();
    }

    // 파일 I/O

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            bw.write(String.valueOf(nextId));
            bw.newLine();
            for (Article a : articles) {
                bw.write(a.getId() + "|" + a.getTitle() + "|"
                        + a.getContent() + "|" + a.getRegDate()
                        + "|" + a.getViewCount());
                bw.newLine();
            }
            System.out.println("💾 데이터가 저장되었습니다.");
        } catch (IOException e) {
            System.out.println("⚠ 파일 저장 오류: " + e.getMessage());
        }
    }

    public int loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) nextId = Integer.parseInt(line.trim());

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 5);
                if (parts.length < 5) continue;

                Article a = new Article(
                        Integer.parseInt(parts[0]),
                        parts[1], parts[2], parts[3]
                );
                int viewCount = Integer.parseInt(parts[4]);
                for (int i = 0; i < viewCount; i++) a.incrementViewCount();
                articles.add(a);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("⚠ 파일 불러오기 오류: " + e.getMessage());
        }

        return articles.size();
    }
}