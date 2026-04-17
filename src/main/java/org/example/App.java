package org.example;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.*;

/**
 * 게시판 애플리케이션 핵심 로직 클래스
 * 명령어를 받아 게시글 CRUD 및 추가 기능을 처리합니다.
 */
public class App {

    private final List<Article> articles;  // 게시글 목록
    private int nextId;                    // 다음 게시글 ID
    private final Scanner scanner;
    private final String DATA_FILE = "articles.dat"; // 파일 저장 경로

    public App() {
        this.articles = new ArrayList<>();
        this.nextId = 1;
        this.scanner = new Scanner(System.in);
    }

    // 메인 실행 루프

    /**
     * 앱 실행 루프: 입력 대기 및 명령어 처리
     */
    public void run() {
        printWelcome();
        loadFromFile(); // 저장된 데이터 불러오기

        while (true) {
            System.out.print("\n명령어: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            Rq rq = new Rq(input);

            switch (rq.getCmd()) {
                case "write"  -> writeArticle();
                case "list"   -> listArticles();
                case "detail" -> showDetail(rq);
                case "update" -> updateArticle(rq);
                case "delete" -> deleteArticle(rq);
                case "search" -> searchArticles(rq);
                case "help"   -> printHelp();
                case "exit"   -> {
                    saveToFile();
                    System.out.println("프로그램을 종료합니다.");
                    return;
                }
                default -> System.out.println("⚠ 알 수 없는 명령어입니다. 'help'를 입력하면 명령어 목록을 확인할 수 있습니다.");
            }
        }
    }

    // 게시글 CRUD

    /**
     * 게시글 작성 처리
     */
    private void writeArticle() {
        System.out.print("제목: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("⚠ 제목을 입력해 주세요.");
            return;
        }

        System.out.print("내용: ");
        String content = scanner.nextLine().trim();

        if (content.isEmpty()) {
            System.out.println("⚠ 내용을 입력해 주세요.");
            return;
        }

        Article article = new Article(nextId++, title, content, getCurrentDate());
        articles.add(article);

        System.out.printf("✅ 게시글이 등록되었습니다. (번호: %d)%n", article.getId());
    }

    /**
     * 게시글 목록 출력 (최신순 역순)
     */
    private void listArticles() {
        if (articles.isEmpty()) {
            System.out.println("등록된 게시글이 없습니다.");
            return;
        }

        System.out.println();
        System.out.printf("%-6s | %-25s | %-12s | %s%n", "번호", "제목", "등록일", "조회수");
        System.out.println("-".repeat(60));

        // 최신 글이 위로 오도록 역순 출력
        List<Article> reversed = new ArrayList<>(articles);
        Collections.reverse(reversed);

        for (Article a : reversed) {
            String shortTitle = a.getTitle().length() > 23
                    ? a.getTitle().substring(0, 22) + "…"
                    : a.getTitle();
            System.out.printf("%-6d | %-25s | %-12s | %d%n",
                    a.getId(), shortTitle, a.getRegDate(), a.getViewCount());
        }

        System.out.printf("%n총 %d개의 게시글%n", articles.size());
    }

    /**
     * 특정 글 상세 내용 출력
     */
    private void showDetail(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("⚠ 사용법: detail [번호]");
            return;
        }

        Article article = findById(rq.getArgInt());
        if (article == null) {
            System.out.printf("⚠ 번호 %d에 해당하는 게시글이 없습니다.%n", rq.getArgInt());
            return;
        }

        article.incrementViewCount(); // 조회수 증가

        System.out.println();
        System.out.println("=".repeat(40));
        System.out.printf("번호  : %d%n", article.getId());
        System.out.printf("제목  : %s%n", article.getTitle());
        System.out.printf("등록일: %s%n", article.getRegDate());
        System.out.printf("조회수: %d%n", article.getViewCount());
        System.out.println("-".repeat(40));
        System.out.println(article.getContent());
        System.out.println("=".repeat(40));
    }

    /**
     * 게시글 수정 처리
     */
    private void updateArticle(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("⚠ 사용법: update [번호]");
            return;
        }

        Article article = findById(rq.getArgInt());
        if (article == null) {
            System.out.printf("⚠ 번호 %d에 해당하는 게시글이 없습니다.%n", rq.getArgInt());
            return;
        }

        System.out.printf("제목 (현재: %s): ", article.getTitle());
        String newTitle = scanner.nextLine().trim();

        System.out.printf("내용 (현재: %s): ", article.getContent());
        String newContent = scanner.nextLine().trim();

        if (!newTitle.isEmpty())   article.setTitle(newTitle);
        if (!newContent.isEmpty()) article.setContent(newContent);

        System.out.println("✅ 게시글이 수정되었습니다.");
    }

    /**
     * 게시글 삭제 처리
     */
    private void deleteArticle(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("⚠ 사용법: delete [번호]");
            return;
        }

        Article article = findById(rq.getArgInt());
        if (article == null) {
            System.out.printf("⚠ 번호 %d에 해당하는 게시글이 없습니다.%n", rq.getArgInt());
            return;
        }

        System.out.printf("'%s' 게시글을 삭제하시겠습니까? (y/n): ", article.getTitle());
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            articles.remove(article);
            System.out.println("✅ 게시글이 삭제되었습니다.");
        } else {
            System.out.println("삭제를 취소했습니다.");
        }
    }

    // 추가 기능

    /**
     * 키워드로 게시글 검색 (제목 + 내용 포함 검색)
     */
    private void searchArticles(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("⚠ 사용법: search [키워드]");
            return;
        }

        String keyword = rq.getArgStr().toLowerCase();
        List<Article> results = new ArrayList<>();

        for (Article a : articles) {
            if (a.getTitle().toLowerCase().contains(keyword)
                    || a.getContent().toLowerCase().contains(keyword)) {
                results.add(a);
            }
        }

        if (results.isEmpty()) {
            System.out.printf("'%s'에 대한 검색 결과가 없습니다.%n", rq.getArgStr());
            return;
        }

        System.out.printf("%n'%s' 검색 결과 (%d건)%n", rq.getArgStr(), results.size());
        System.out.printf("%-6s | %-25s | %s%n", "번호", "제목", "등록일");
        System.out.println("-".repeat(50));

        Collections.reverse(results);
        for (Article a : results) {
            String shortTitle = a.getTitle().length() > 23
                    ? a.getTitle().substring(0, 22) + "…"
                    : a.getTitle();
            System.out.printf("%-6d | %-25s | %s%n",
                    a.getId(), shortTitle, a.getRegDate());
        }
    }

    /**
     * 파일에 게시글 저장 (BufferedWriter)
     */
    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            bw.write(String.valueOf(nextId));
            bw.newLine();
            for (Article a : articles) {
                // 구분자: "|"
                bw.write(a.getId() + "|" + a.getTitle() + "|"
                        + a.getContent() + "|" + a.getRegDate()
                        + "|" + a.getViewCount());
                bw.newLine();
            }
            System.out.println("💾 데이터가 저장되었습니다.");
        } catch (IOException e) {
            System.out.println("⚠ 파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일에서 게시글 불러오기 (BufferedReader)
     */
    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) nextId = Integer.parseInt(line.trim());

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 5);
                if (parts.length < 5) continue;

                int id          = Integer.parseInt(parts[0]);
                String title    = parts[1];
                String content  = parts[2];
                String regDate  = parts[3];
                int viewCount   = Integer.parseInt(parts[4]);

                Article a = new Article(id, title, content, regDate);
                for (int i = 0; i < viewCount; i++) a.incrementViewCount();
                articles.add(a);
            }

            if (!articles.isEmpty()) {
                System.out.printf("📂 저장된 게시글 %d개를 불러왔습니다.%n", articles.size());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("⚠ 파일 불러오기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 유틸 메서드

    /**
     * ID로 게시글 검색, 없으면 null 반환
     */
    private Article findById(int id) {
        for (Article a : articles) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    /**
     * 현재 날짜를 yyyy-MM-dd 형식으로 반환
     */
    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 환영 메시지 출력
     */
    private void printWelcome() {
        System.out.println("=".repeat(45));
        System.out.println("   📋  Java CLI 텍스트 게시판 v1.0");
        System.out.println("=".repeat(45));
        System.out.println("'help'를 입력하면 명령어 목록을 볼 수 있습니다.");
    }

    /**
     * 명령어 도움말 출력
     */
    private void printHelp() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│              명령어 목록                │");
        System.out.println("├──────────────────┬──────────────────────┤");
        System.out.println("│ write            │ 게시글 작성          │");
        System.out.println("│ list             │ 게시글 목록 보기     │");
        System.out.println("│ detail [번호]    │ 게시글 상세 보기     │");
        System.out.println("│ update [번호]    │ 게시글 수정          │");
        System.out.println("│ delete [번호]    │ 게시글 삭제          │");
        System.out.println("│ search [키워드]  │ 게시글 검색          │");
        System.out.println("│ help             │ 명령어 도움말        │");
        System.out.println("│ exit             │ 프로그램 종료 + 저장 │");
        System.out.println("└──────────────────┴──────────────────────┘");
    }
}
