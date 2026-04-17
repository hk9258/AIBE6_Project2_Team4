package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleTest {

    @Test
    @DisplayName("게시글 생성 시 기본값이 올바르게 설정된다")
    void createArticle() {
        Article article = new Article(1, "제목", "내용", "2026-04-17");

        assertThat(article.getId()).isEqualTo(1);
        assertThat(article.getTitle()).isEqualTo("제목");
        assertThat(article.getContent()).isEqualTo("내용");
        assertThat(article.getRegDate()).isEqualTo("2026-04-17");
        assertThat(article.getViewCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("제목을 수정할 수 있다")
    void setTitle() {
        Article article = new Article(1, "기존 제목", "내용", "2026-04-17");

        article.setTitle("새 제목");

        assertThat(article.getTitle()).isEqualTo("새 제목");
    }

    @Test
    @DisplayName("내용을 수정할 수 있다")
    void setContent() {
        Article article = new Article(1, "제목", "기존 내용", "2026-04-17");

        article.setContent("새 내용");

        assertThat(article.getContent()).isEqualTo("새 내용");
    }

    @Test
    @DisplayName("상세 조회 시 조회수가 1 증가한다")
    void incrementViewCount() {
        Article article = new Article(1, "제목", "내용", "2026-04-17");

        article.incrementViewCount();

        assertThat(article.getViewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("조회수를 여러 번 증가시킬 수 있다")
    void incrementViewCountMultipleTimes() {
        Article article = new Article(1, "제목", "내용", "2026-04-17");

        article.incrementViewCount();
        article.incrementViewCount();
        article.incrementViewCount();

        assertThat(article.getViewCount()).isEqualTo(3);
    }
}
