package org.example;

/**
 * 게시글 데이터 클래스
 * 게시글의 기본 정보와 조회수를 관리합니다.
 */
public class Article {
    private int id;
    private String title;
    private String content;
    private String regDate;
    private int viewCount; // 추가 기능: 조회수

    public Article(int id, String title, String content, String regDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.regDate = regDate;
        this.viewCount = 0;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getRegDate() { return regDate; }
    public int getViewCount() { return viewCount; }

    // Setters (수정 기능에 사용)
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }

    // 조회수 증가
    public void incrementViewCount() { this.viewCount++; }

    @Override
    public String toString() {
        return String.format("Article{id=%d, title='%s', regDate='%s', viewCount=%d}",
                id, title, regDate, viewCount);
    }
}