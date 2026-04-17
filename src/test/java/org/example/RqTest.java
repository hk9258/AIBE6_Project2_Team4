package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RqTest {

    @Test
    @DisplayName("명령어만 입력하면 cmd만 추출되고 인자는 비어 있다")
    void parseCommandOnly() {
        Rq rq = new Rq("list");

        assertThat(rq.getCmd()).isEqualTo("list");
        assertThat(rq.getArgStr()).isEqualTo("");
        assertThat(rq.hasArg()).isFalse();
    }

    @Test
    @DisplayName("명령어와 인자를 분리할 수 있다")
    void parseCommandAndArgument() {
        Rq rq = new Rq("detail 3");

        assertThat(rq.getCmd()).isEqualTo("detail");
        assertThat(rq.getArgStr()).isEqualTo("3");
        assertThat(rq.getArgInt()).isEqualTo(3);
        assertThat(rq.hasArg()).isTrue();
    }

    @Test
    @DisplayName("입력 앞뒤 공백을 제거한다")
    void trimInput() {
        Rq rq = new Rq("   list   ");

        assertThat(rq.getRawInput()).isEqualTo("list");
        assertThat(rq.getCmd()).isEqualTo("list");
    }

    @Test
    @DisplayName("명령어는 소문자로 변환된다")
    void commandToLowerCase() {
        Rq rq = new Rq("DETAIL 10");

        assertThat(rq.getCmd()).isEqualTo("detail");
        assertThat(rq.getArgInt()).isEqualTo(10);
    }

    @Test
    @DisplayName("인자가 숫자가 아니면 -1을 반환한다")
    void returnMinusOneWhenArgumentIsNotNumber() {
        Rq rq = new Rq("detail abc");

        assertThat(rq.getArgStr()).isEqualTo("abc");
        assertThat(rq.getArgInt()).isEqualTo(-1);
    }

    @Test
    @DisplayName("공백이 여러 개여도 정상적으로 파싱된다")
    void parseMultipleSpaces() {
        Rq rq = new Rq("update     25");

        assertThat(rq.getCmd()).isEqualTo("update");
        assertThat(rq.getArgStr()).isEqualTo("25");
        assertThat(rq.getArgInt()).isEqualTo(25);
    }
}