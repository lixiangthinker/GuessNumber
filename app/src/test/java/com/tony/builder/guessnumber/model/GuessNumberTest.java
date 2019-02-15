package com.tony.builder.guessnumber.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class GuessNumberTest {

    @Test
    public void guessNumbers() {
        final GuessNumber game = new GuessNumber();
        int[] realNumber = game.getRealNumbers();
        assertNotNull(realNumber);
        assertEquals(4,realNumber.length);

        boolean isInRange = true;
        for (int i =0; i<realNumber.length; i++) {
            if (realNumber[i] >=10 || realNumber[i] < 0) {
                isInRange = false;
                break;
            }
        }
        assertTrue(isInRange);

        final int[] guess = {1,2,3,4};
        game.setResultListener(new GuessNumber.OnResultListener() {
            @Override
            public void onResult(GuessNumber.GuessNumberResult result) {
                printArray(guess);
                System.out.println();
                printArray(game.getRealNumbers());
                System.out.println();
                System.out.println(result);
            }

            @Override
            public void onGameFinished(boolean isWining) {

            }
        });
        game.guessNumbers(guess);
    }

    private void printArray(int[] array) {
        for (int i=0; i<array.length; i++) {
            System.out.print(array[i]);
            if (i+1 != array.length) {
                System.out.print(", ");
            }
        }
    }
}