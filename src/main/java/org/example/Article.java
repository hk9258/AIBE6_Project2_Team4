package org.example;

public class Article {
    private int id;
    private String title;
    private String content;
    private String regDate;
    private int viewCount;

    public Article(int id, String title, String content, String regDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.regDate = regDate;
        this.viewCount = 0;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getRegDate() {
        return this.regDate;
    }

    public int getViewCount() {
        return this.viewCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void incrementViewCount() {
        ++this.viewCount;
    }

    public String toString() {
        return String.format("Article{id=%d, title='%s', regDate='%s', viewCount=%d}", this.id, this.title, this.regDate, this.viewCount);
    }
}
