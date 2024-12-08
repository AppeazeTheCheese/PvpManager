package dev.appeazethecheese.pvpmanager;

import java.util.HashSet;
import java.util.Set;

public class UnorderedPair<T> {
    private final Set<T> set;

    public UnorderedPair(T a, T b) {
        set = new HashSet<>();
        set.add(a);
        set.add(b);
    }

    public boolean equals(Object b) {
        return b instanceof UnorderedPair && b.hashCode() == hashCode();
    }

    public int hashCode() {
        return set.hashCode();
    }
}