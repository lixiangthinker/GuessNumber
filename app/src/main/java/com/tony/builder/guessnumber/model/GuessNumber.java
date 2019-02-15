package com.tony.builder.guessnumber.model;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class GuessNumber {
    private int[] realNumbers = {0,0,0,0};
    private int guessCount = 0;
    private static final int GUESS_COUNT_MAX = 7;
    private OnResultListener listener = null;

    public GuessNumber() {
    }

    private void generateRealNumber() {
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        while (integerArrayList.size() < 4) {
            int random = (int) (Math.random() * 10);
            if (!integerArrayList.contains(random)) {
                integerArrayList.add(random);
            }
        }
        for (int i=0; i<4; i++) {
            realNumbers[i] = integerArrayList.get(i);
        }
    }

    public int[] getRealNumbers() {
        return realNumbers;
    }

    public void guessNumbers(int[] numbers) {
        GuessNumberResult result = getResult(numbers);
        if (result != null && listener != null) {
            listener.onResult(result);
            guessCount++;

            if (result.a == 4) {
                listener.onGameFinished(true);
            } else if (guessCount == GUESS_COUNT_MAX) {
                listener.onGameFinished(false);
            }
        }
    }

    private GuessNumberResult getResult(final int[] numbers) {
        if (numbers == null || numbers.length != 4 ||
                realNumbers == null || realNumbers.length != 4) {
            return null;
        }

        int a = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] == realNumbers[i]) {
                a++;
            }
        }

        int b = 0;
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < realNumbers.length; j++) {
                if (numbers[i] == realNumbers[j] && i!=j) {
                    b++;
                }
            }
        }
        return new GuessNumberResult(a, b);
    }


    public void setResultListener(OnResultListener listener) {
        this.listener = listener;
    }

    public void onNewGame() {
        guessCount = 0;
        for (int i=0; i< realNumbers.length; i++) {
            realNumbers[i] = 0;
        }
        generateRealNumber();
    }

    public interface OnResultListener {
        void onResult(GuessNumberResult result);
        void onGameFinished(boolean isWining);
    }

    public class GuessNumberResult {
        public int a;
        public int b;
        GuessNumberResult(int a, int b){
            this.a = a;
            this.b = b;
        }

        @NonNull
        @Override
        public String toString() {
            return "["+a+","+b+"]";
        }

        public String toTextValue() {
            return a+"A"+b+"B";
        }
    }
}
