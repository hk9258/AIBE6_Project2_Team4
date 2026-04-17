package org.example;
/**
 * Java CLI 텍스트 게시판
 * 진입점 클래스
 * 실행 방법:
 *   javac src/*.java -d out
 *   java -cp out Main
 */
public class Main {
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}
