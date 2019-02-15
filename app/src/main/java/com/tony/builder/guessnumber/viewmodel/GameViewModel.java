package com.tony.builder.guessnumber.viewmodel;

import android.util.Log;

import com.tony.builder.guessnumber.model.GuessNumber;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {
    private static final String TAG = "GameViewModel";
    private GuessNumber guessNumber;
    private int[] inputNumber = {0,0,0,0};

    private MutableLiveData<Boolean> isGameFinished;
    private MutableLiveData<GuessNumber.GuessNumberResult> mResult;

    public void setGuessNumber(GuessNumber guessNumber) {
        this.guessNumber = guessNumber;
        guessNumber.setResultListener(new GuessNumber.OnResultListener() {
            @Override
            public void onResult(GuessNumber.GuessNumberResult result) {
                if (mResult != null) {
                    mResult.postValue(result);
                }
            }

            @Override
            public void onGameFinished(boolean isWining) {
                if (isGameFinished != null) {
                    isGameFinished.postValue(isWining);
                }
            }
        });
    }

    public void onNewGame() {
        guessNumber.onNewGame();
    }

    public void setInputNumber(int value, int index) {
        Log.d(TAG, "setInputNumber, numbers["+index+"]="+value);
        if (index > inputNumber.length - 1 || index < 0) {
            return;
        }
        inputNumber[index] = value;
    }

    public void onGuessNumber() {
        guessNumber.guessNumbers(inputNumber);
    }

    public LiveData<Boolean> getIsGameFinished() {
        if (isGameFinished == null) {
            isGameFinished = new MutableLiveData<>();
        }
        return isGameFinished;
    }

    public LiveData<GuessNumber.GuessNumberResult> getResult() {
        if (mResult == null) {
            mResult = new MutableLiveData<>();
        }
        return mResult;
    }
}
