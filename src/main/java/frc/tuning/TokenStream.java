package frc.tuning;

import frc.csm.PackagePrivate;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@PackagePrivate
class TokenStream {
    private final Queue<String> tokenQueue = new LinkedList<>();

    public void insert(String... tokens) {
        tokenQueue.addAll(List.of(tokens));
    }

    public boolean hasNextMatching(String regex) {
        return !tokenQueue.isEmpty() && tokenQueue.peek().matches(regex);
    }

    public boolean consumeNextMatchingIfPresent(String regex) {
        if (!hasNextMatching(regex)) return false;
        tokenQueue.poll();
        return true;
    }

    public void skip() {
        tokenQueue.poll();
    }

    public String getNextMatching(String regex) {
        String op;
        while ((op = tokenQueue.poll()) != null) {
            if (op.matches("^" + regex + "$")) {
                return op;
            }

            System.out.println("Invalid token while parsing config file: \"" + op + "\"");
        }
        return null;
    }

    public boolean isExhausted() {
        return tokenQueue.isEmpty();
    }
}
