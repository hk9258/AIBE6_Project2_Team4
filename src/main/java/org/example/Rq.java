package org.example;

public class Rq {
    private final String rawInput;
    private final String cmd;
    private final String argStr;

    public Rq(String rawInput) {
        this.rawInput = rawInput.trim();
        String[] tokens = this.rawInput.split("\\s+", 2);
        this.cmd = tokens[0].toLowerCase();
        this.argStr = tokens.length > 1 ? tokens[1].trim() : "";
    }

    public String getCmd() {
        return this.cmd;
    }

    public String getArgStr() {
        return this.argStr;
    }

    public int getArgInt() {
        try {
            return Integer.parseInt(this.argStr);
        } catch (NumberFormatException var2) {
            return -1;
        }
    }

    public boolean hasArg() {
        return !this.argStr.isEmpty();
    }

    public String getRawInput() {
        return this.rawInput;
    }
}
