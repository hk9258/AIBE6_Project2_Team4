package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class App {
    private final List<Article> articles = new ArrayList();
    private int nextId = 1;
    private final Scanner scanner;

    public App() {
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("== 게시판 앱 ==");

        while(true) {
            System.out.print("\n명령어: ");
            String input = this.scanner.nextLine().trim();
            if (!input.isEmpty()) {
                Rq rq = new Rq(input);
                switch (rq.getCmd()) {
                    case "write":
                        this.writeArticle();
                        break;
                    case "list":
                        this.listArticles();
                        break;
                    case "detail":
                        this.showDetail(rq);
                        break;
                    case "update":
                        this.updateArticle(rq);
                        break;
                    case "delete":
                        this.deleteArticle(rq);
                        break;
                    case "exit":
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    default:
                        System.out.println("알 수 없는 명령어입니다.");
                }
            }
        }
    }

    private void writeArticle() {
        System.out.print("제목: ");
        String title = this.scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("제목을 입력해 주세요.");
        } else {
            System.out.print("내용: ");
            String content = this.scanner.nextLine().trim();
            if (content.isEmpty()) {
                System.out.println("내용을 입력해 주세요.");
            } else {
                String regDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Article article = new Article(this.nextId++, title, content, regDate);
                this.articles.add(article);
                System.out.printf("%d번 게시글이 등록되었습니다.%n", article.getId());
            }
        }
    }

    private void listArticles() {
        if (this.articles.isEmpty()) {
            System.out.println("등록된 게시글이 없습니다.");
        } else {
            System.out.println("\n번호 / 제목 / 등록일 / 조회수");
            System.out.println("---------------------------------------");
            List<Article> reversed = new ArrayList(this.articles);
            Collections.reverse(reversed);

            for(Article article : reversed) {
                System.out.printf("%d / %s / %s / %d%n", article.getId(), article.getTitle(), article.getRegDate(), article.getViewCount());
            }

        }
    }

    private void showDetail(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("사용법: detail [번호]");
        } else {
            Article article = this.findById(rq.getArgInt());
            if (article == null) {
                System.out.println("해당 번호의 게시글이 없습니다.");
            } else {
                article.incrementViewCount();
                System.out.println("\n번호: " + article.getId());
                System.out.println("제목: " + article.getTitle());
                System.out.println("내용: " + article.getContent());
                System.out.println("등록일: " + article.getRegDate());
                System.out.println("조회수: " + article.getViewCount());
            }
        }
    }

    private void updateArticle(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("사용법: update [번호]");
        } else {
            Article article = this.findById(rq.getArgInt());
            if (article == null) {
                System.out.println("해당 번호의 게시글이 없습니다.");
            } else {
                System.out.printf("새 제목 (현재: %s): ", article.getTitle());
                String newTitle = this.scanner.nextLine().trim();
                System.out.printf("새 내용 (현재: %s): ", article.getContent());
                String newContent = this.scanner.nextLine().trim();
                if (!newTitle.isEmpty()) {
                    article.setTitle(newTitle);
                }

                if (!newContent.isEmpty()) {
                    article.setContent(newContent);
                }

                System.out.println("게시글이 수정되었습니다.");
            }
        }
    }

    private void deleteArticle(Rq rq) {
        if (!rq.hasArg()) {
            System.out.println("사용법: delete [번호]");
        } else {
            Article article = this.findById(rq.getArgInt());
            if (article == null) {
                System.out.println("해당 번호의 게시글이 없습니다.");
            } else {
                this.articles.remove(article);
                System.out.println("게시글이 삭제되었습니다.");
            }
        }
    }

    private Article findById(int id) {
        for(Article article : this.articles) {
            if (article.getId() == id) {
                return article;
            }
        }

        return null;
    }
}
