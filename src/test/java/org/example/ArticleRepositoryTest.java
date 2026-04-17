package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleRepositoryTest {

    @Test
    @DisplayName("게시글 저장 시 ID가 1부터 순차적으로 증가한다")
    void saveArticleWithIncrementId() {
        ArticleRepository repo = new ArticleRepository();

        Article article1 = repo.save("제목1", "내용1", "2026-04-17");
        Article article2 = repo.save("제목2", "내용2", "2026-04-17");
        Article article3 = repo.save("제목3", "내용3", "2026-04-17");

        assertThat(article1.getId()).isEqualTo(1);
        assertThat(article2.getId()).isEqualTo(2);
        assertThat(article3.getId()).isEqualTo(3);
        assertThat(repo.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("ID로 게시글을 조회할 수 있다")
    void findById() {
        ArticleRepository repo = new ArticleRepository();
        Article saved = repo.save("제목", "내용", "2026-04-17");

        Article found = repo.findById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 null을 반환한다")
    void findByIdReturnsNull() {
        ArticleRepository repo = new ArticleRepository();

        Article found = repo.findById(999);

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("findAll은 최신순으로 게시글 목록을 반환한다")
    void findAllReturnsLatestFirst() {
        ArticleRepository repo = new ArticleRepository();
        repo.save("제목1", "내용1", "2026-04-17");
        repo.save("제목2", "내용2", "2026-04-17");
        repo.save("제목3", "내용3", "2026-04-17");

        List<Article> result = repo.findAll();

        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(Article::getTitle)
                .containsExactly("제목3", "제목2", "제목1");
    }

    @Test
    @DisplayName("키워드가 제목에 포함되면 검색된다")
    void searchByTitle() {
        ArticleRepository repo = new ArticleRepository();
        repo.save("자바 공부", "컬렉션 학습", "2026-04-17");
        repo.save("스프링 입문", "DI와 IoC", "2026-04-17");

        List<Article> result = repo.search("자바");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("자바 공부");
    }

    @Test
    @DisplayName("키워드가 내용에 포함되면 검색된다")
    void searchByContent() {
        ArticleRepository repo = new ArticleRepository();
        repo.save("제목1", "자바는 객체지향 언어다", "2026-04-17");
        repo.save("제목2", "스프링 학습", "2026-04-17");

        List<Article> result = repo.search("객체지향");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).contains("객체지향");
    }

    @Test
    @DisplayName("검색은 제목과 내용을 모두 대상으로 하며 최신순으로 반환한다")
    void searchReturnsLatestFirst() {
        ArticleRepository repo = new ArticleRepository();
        repo.save("Java 기초", "문법", "2026-04-17");
        repo.save("Spring", "JAVA 심화", "2026-04-17");
        repo.save("Python", "자료형", "2026-04-17");

        List<Article> result = repo.search("java");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Article::getTitle)
                .containsExactly("Spring", "Java 기초");
    }

    @Test
    @DisplayName("검색은 대소문자를 구분하지 않는다")
    void searchIgnoreCase() {
        ArticleRepository repo = new ArticleRepository();
        repo.save("JAVA", "기초", "2026-04-17");
        repo.save("spring", "framework", "2026-04-17");

        List<Article> result = repo.search("java");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("JAVA");
    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 리스트를 반환한다")
    void searchReturnsEmptyList() {
        ArticleRepository repo = new ArticleRepository();
        repo.save("자바", "기초", "2026-04-17");

        List<Article> result = repo.search("파이썬");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하는 게시글은 삭제할 수 있다")
    void deleteArticle() {
        ArticleRepository repo = new ArticleRepository();
        Article saved = repo.save("제목", "내용", "2026-04-17");

        boolean deleted = repo.delete(saved.getId());

        assertThat(deleted).isTrue();
        assertThat(repo.size()).isEqualTo(0);
        assertThat(repo.findById(saved.getId())).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 시 false를 반환한다")
    void deleteNonExistingArticle() {
        ArticleRepository repo = new ArticleRepository();
        repo.save("제목", "내용", "2026-04-17");

        boolean deleted = repo.delete(999);

        assertThat(deleted).isFalse();
        assertThat(repo.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("저장된 게시글 수를 반환한다")
    void size() {
        ArticleRepository repo = new ArticleRepository();

        assertThat(repo.size()).isEqualTo(0);

        repo.save("제목1", "내용1", "2026-04-17");
        repo.save("제목2", "내용2", "2026-04-17");

        assertThat(repo.size()).isEqualTo(2);
    }
}