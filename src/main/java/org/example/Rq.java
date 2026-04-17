package org.example;

/**
 * 커맨드 명령어 요청 유틸 클래스
 * 사용자 입력을 파싱하여 명령어와 인자를 분리합니다.
 *
 * 예시 입력: "detail 3"  →  cmd = "detail", arg = 3
 */
public class Rq {
    private final String rawInput; // 사용자 원본 입력
    private final String cmd;      // 명령어 (첫 번째 토큰)
    private final String argStr;   // 인자 문자열 (두 번째 토큰, 없으면 "")

    public Rq(String rawInput) {
        this.rawInput = rawInput.trim();

        String[] tokens = this.rawInput.split("\\s+", 2);
        this.cmd = tokens[0].toLowerCase();
        this.argStr = (tokens.length > 1) ? tokens[1].trim() : "";
    }

    /** 명령어 반환 (소문자) */
    public String getCmd() { return cmd; }

    /** 인자 문자열 반환 */
    public String getArgStr() { return argStr; }

    /**
     * 인자를 정수로 변환하여 반환
     * 인자가 없거나 숫자가 아니면 -1 반환
     */
    public int getArgInt() {
        try {
            return Integer.parseInt(argStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** 인자가 존재하는지 확인 */
    public boolean hasArg() { return !argStr.isEmpty(); }

    /** 원본 입력 반환 */
    public String getRawInput() { return rawInput; }
}
