package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * 게시판 애플리케이션 핵심 로직 클래스
 * 명령어를 받아 게시글 CRUD 및 추가 기능을 처리합니다.
 */
public class App {

    private final List<Article> articles;
    private int nextId;
    private final Scanner scanner;

    public App() {
        this.articles = new ArrayList<>();
        this.nextId = 1;
        this.scanner = new Scanner(System.in);
    }

    /**
     * 앱 실행 루프
     */
    public void run() {
        System.out.println("== 게시판 앱 ==");

        while (true) {
            System.out.print("\n명령어: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            Rq rq = new Rq(input);

            switch (rq.getCmd()) {
                case "write" -> writeArticle();
                case "list" -> listArticles();
                case "detail" -> showDetail(rq);
                case "update" -> updateArticle(rq);
                case "delete" -> deleteArticle(rq);
                case "exit" -> {
                    System.out.println("프로그램을 종료합니다.");
                    return;
                }
                default -> System.out.println("알 수 없는 명령어입니다.");
            }
        }
    }

    /**
     * 게시글 작성
     */
    private void writeArticle() {
        System.out.print("제목: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("제목을 입력해 주세요.");
            return;
        }

        System.out.print("내용: ");
        String content = scanner.nextLine().trim();

        if (content.isEmpty()) {
            System.out.println("내용을 입력해 주세요.");
            return;
        }

        String regDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Article article = new Article(nextId++, title, content, regDate);
        articles.add(article);

        System.out.printf("%d번 게시글이 등록되었습니다.%n", article.getId());
    }

    /**
     * 게시글 목록 조회
     */
    private void listArticles() {
        if (articles.isEmpty()) {
            System.out.println("등록된 게시글이 없습니다.");
            return;
        }

        System.out.println("\n번호 / 제목 / 등록일 / 조회수");
        System.out.println("---------------------------------------");

        List<Article> reversed = new ArrayList<>(articles);
        Collections.reverse(reversed);

        for (Article article : reversed) {
            System.out.printf("%d / %s / %s / %d%n",
                    article.getId(),
                    article.getTitle(),
                    article.getRegDate(),
                    article.getViewCount());
        }
    }

    /**
     * 게시글 상세 조회
     */
    private void showDetail(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("사용법: detail [번호]");
            return;
        }

        Article article = findById(rq.getArgInt());

        if (article == null) {
            System.out.println("해당 번호의 게시글이 없습니다.");
            return;
        }

        article.incrementViewCount();

        System.out.println("\n번호: " + article.getId());
        System.out.println("제목: " + article.getTitle());
        System.out.println("내용: " + article.getContent());
        System.out.println("등록일: " + article.getRegDate());
        System.out.println("조회수: " + article.getViewCount());
    }

    /**
     * 게시글 수정
     */
    private void updateArticle(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("사용법: update [번호]");
            return;
        }

        Article article = findById(rq.getArgInt());

        if (article == null) {
            System.out.println("해당 번호의 게시글이 없습니다.");
            return;
        }

        System.out.printf("새 제목 (현재: %s): ", article.getTitle());
        String newTitle = scanner.nextLine().trim();

        System.out.printf("새 내용 (현재: %s): ", article.getContent());
        String newContent = scanner.nextLine().trim();

        if (!newTitle.isEmpty()) {
            article.setTitle(newTitle);
        }

        if (!newContent.isEmpty()) {
            article.setContent(newContent);
        }

        System.out.println("게시글이 수정되었습니다.");
    }

    /**
     * 게시글 삭제
     */
    private void deleteArticle(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("사용법: delete [번호]");
            return;
        }

        Article article = findById(rq.getArgInt());

        if (article == null) {
            System.out.println("해당 번호의 게시글이 없습니다.");
            return;
        }

        articles.remove(article);
        System.out.println("게시글이 삭제되었습니다.");
    }

    /**
     * ID로 게시글 찾기
     */
    private Article findById(int id) {
        for (Article article : articles) {
            if (article.getId() == id) {
                return article;
            }
        }
        return null;
    }
}