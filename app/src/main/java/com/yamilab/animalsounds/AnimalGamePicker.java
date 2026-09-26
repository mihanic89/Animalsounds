package com.yamilab.animalsounds;

import java.util.ArrayList;
import java.util.Random;

/**
 * Picks a correct answer and three distinct wrong answers from an animal list for the
 * quiz game fragments (Game/Game2/Game3). Shared so the index bounds live in exactly
 * one place instead of being copy-pasted across three fragments.
 */
final class AnimalGamePicker {

    private static final Random RANDOM = new Random();

    private AnimalGamePicker() {
    }

    /**
     * @param recentlyUsed indices used as a correct answer recently; mutated in place,
     *                      same role as the original per-fragment "numbers" list.
     * @return {correctAnswer, wrong1, wrong2, wrong3}
     */
    static int[] pickNext(ArrayList<Animal> animals, ArrayList<Integer> recentlyUsed) {
        int size = animals.size();
        if (size == 0) {
            return new int[]{0, 0, 0, 0};
        }

        int correctAnswer = nextExcluding(size, recentlyUsed);
        recentlyUsed.add(correctAnswer);
        if (recentlyUsed.size() > Math.max(size - 5, 0)) {
            recentlyUsed.clear();
        }

        int wrong1 = nextExcluding(size, correctAnswer);
        int wrong2 = nextExcluding(size, correctAnswer, wrong1);
        int wrong3 = nextExcluding(size, correctAnswer, wrong1, wrong2);

        return new int[]{correctAnswer, wrong1, wrong2, wrong3};
    }

    private static int nextExcluding(int size, ArrayList<Integer> exclude) {
        if (exclude.size() >= size) {
            return RANDOM.nextInt(size);
        }
        int candidate = RANDOM.nextInt(size);
        while (exclude.contains(candidate)) {
            candidate = RANDOM.nextInt(size);
        }
        return candidate;
    }

    static int nextExcluding(int size, int... exclude) {
        if (exclude.length >= size) {
            return RANDOM.nextInt(size);
        }
        int candidate = RANDOM.nextInt(size);
        while (contains(exclude, candidate)) {
            candidate = RANDOM.nextInt(size);
        }
        return candidate;
    }

    private static boolean contains(int[] values, int value) {
        for (int v : values) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }
}
