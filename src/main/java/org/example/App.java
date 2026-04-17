package org.example;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 게시판 애플리케이션 실행 클래스
 * 사용자 입력을 받아 명령어를 분기하는 역할만 담당합니다.
 * 데이터 접근은 ArticleRepository에 위임합니다.
 */
public class App {

    private final ArticleRepository repo = new ArticleRepository();
    private final Scanner scanner = new Scanner(System.in);

    // 메인 루프

    public void run() {
        printWelcome();

        int loaded = repo.loadFromFile();
        if (loaded > 0) System.out.printf("📂 저장된 게시글 %d개를 불러왔습니다.%n", loaded);

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
                case "exit"   -> { repo.saveToFile(); System.out.println("프로그램을 종료합니다."); return; }
                default -> System.out.println("⚠ 알 수 없는 명령어입니다. 'help'를 입력해 보세요.");
            }
        }
    }

    // 명령어 처리

    private void writeArticle() {
        String title = prompt("제목");
        if (title.isEmpty()) { System.out.println("⚠ 제목을 입력해 주세요."); return; }

        String content = prompt("내용");
        if (content.isEmpty()) { System.out.println("⚠ 내용을 입력해 주세요."); return; }

        Article article = repo.save(title, content, today());
        System.out.printf("✅ 게시글이 등록되었습니다. (번호: %d)%n", article.getId());
    }

    private void listArticles() {
        List<Article> list = repo.findAll();
        if (list.isEmpty()) { System.out.println("등록된 게시글이 없습니다."); return; }

        System.out.println();
        System.out.printf("%-6s | %-25s | %-12s | %s%n", "번호", "제목", "등록일", "조회수");
        System.out.println("-".repeat(60));
        list.forEach(a -> System.out.printf("%-6d | %-25s | %-12s | %d%n",
                a.getId(), truncate(a.getTitle()), a.getRegDate(), a.getViewCount()));
        System.out.printf("%n총 %d개의 게시글%n", repo.size());
    }

    private void showDetail(Rq rq) {
        Article article = findOrWarn(rq);
        if (article == null) return;

        article.incrementViewCount();
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

    private void updateArticle(Rq rq) {
        Article article = findOrWarn(rq);
        if (article == null) return;

        String newTitle   = prompt("제목 (현재: " + article.getTitle() + ")");
        String newContent = prompt("내용 (현재: " + article.getContent() + ")");

        if (!newTitle.isEmpty())   article.setTitle(newTitle);
        if (!newContent.isEmpty()) article.setContent(newContent);
        System.out.println("✅ 게시글이 수정되었습니다.");
    }

    private void deleteArticle(Rq rq) {
        Article article = findOrWarn(rq);
        if (article == null) return;

        System.out.printf("'%s' 게시글을 삭제하시겠습니까? (y/n): ", article.getTitle());
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            repo.delete(article.getId());
            System.out.println("✅ 게시글이 삭제되었습니다.");
        } else {
            System.out.println("삭제를 취소했습니다.");
        }
    }

    private void searchArticles(Rq rq) {
        if (!rq.hasArg()) { System.out.println("⚠ 사용법: search [키워드]"); return; }

        List<Article> results = repo.search(rq.getArgStr());
        if (results.isEmpty()) {
            System.out.printf("'%s'에 대한 검색 결과가 없습니다.%n", rq.getArgStr());
            return;
        }

        System.out.printf("%n'%s' 검색 결과 (%d건)%n", rq.getArgStr(), results.size());
        System.out.printf("%-6s | %-25s | %s%n", "번호", "제목", "등록일");
        System.out.println("-".repeat(50));
        results.forEach(a -> System.out.printf("%-6d | %-25s | %s%n",
                a.getId(), truncate(a.getTitle()), a.getRegDate()));
    }

    // 유틸 메서드

    /** 인자 유효성 확인 후 게시글 반환, 문제 있으면 경고 출력 후 null */
    private Article findOrWarn(Rq rq) {
        String cmd = rq.getCmd();
        if (!rq.hasArg()) { System.out.printf("⚠ 사용법: %s [번호]%n", cmd); return null; }

        Article article = repo.findById(rq.getArgInt());
        if (article == null) System.out.printf("⚠ 번호 %d에 해당하는 게시글이 없습니다.%n", rq.getArgInt());
        return article;
    }

    /** 레이블을 출력하고 입력값을 trim해서 반환 */
    private String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    /** 제목이 23자를 초과하면 잘라서 '…' 추가 */
    private String truncate(String title) {
        return title.length() > 23 ? title.substring(0, 22) + "…" : title;
    }

    /** 현재 날짜를 yyyy-MM-dd 형식으로 반환 */
    private String today() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private void printWelcome() {
        System.out.println("=".repeat(45));
        System.out.println("   📋  Java CLI 텍스트 게시판 v2.0");
        System.out.println("=".repeat(45));
        System.out.println("'help'를 입력하면 명령어 목록을 볼 수 있습니다.");
    }

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