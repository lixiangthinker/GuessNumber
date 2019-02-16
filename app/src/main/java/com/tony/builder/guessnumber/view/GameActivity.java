package com.tony.builder.guessnumber.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.builder.guessnumber.R;
import com.tony.builder.guessnumber.model.GuessNumber;
import com.tony.builder.guessnumber.viewmodel.GameViewModel;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private GameViewModel viewModel;
    private EditText[] editTexts;

    private RecyclerView rvResultContainer;
    private List<GuessNumber.GuessNumberResult> mResult;
    private GameResultsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        viewModel.setGuessNumber(new GuessNumber());
        subscribe(viewModel);

        initEditTexts(viewModel);
        initButtonGuess();
        initButtonClear();
        initResultView();
        viewModel.onNewGame();
    }

    private void initResultView() {
        mResult = new ArrayList<>();
        rvResultContainer = findViewById(R.id.rvResultContainer);
        rvResultContainer.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new GameResultsAdapter();
        rvResultContainer.setAdapter(mAdapter);
    }

    private void initButtonGuess() {
        Button btnGuess = findViewById(R.id.btnGuess);
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.onGuessNumber();
                for (int i = 0; i < 4; i++) {
                    editTexts[i].setText("");
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                try {
                    imm.hideSoftInputFromWindow(editTexts[3].getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initButtonClear() {
        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 4; i++) {
                    editTexts[i].setText("");
                }
            }
        });
    }

    private void initEditTexts(GameViewModel viewModel) {
        editTexts = new EditText[4];
        editTexts[0] = findViewById(R.id.editText0);
        editTexts[1] = findViewById(R.id.editText1);
        editTexts[2] = findViewById(R.id.editText2);
        editTexts[3] = findViewById(R.id.editText3);

        for (int i = 0; i < 4; i++) {
            editTexts[i].addTextChangedListener(new NumberWatcher(i, viewModel));
        }
    }

    private class NumberWatcher implements TextWatcher {
        private int position;
        private GameViewModel viewModel;

        public NumberWatcher(int position, GameViewModel viewModel) {
            this.position = position;
            this.viewModel = viewModel;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "s = " + s);
            Integer value = 0;
            try {
                value = Integer.parseInt(""+s);
            } catch (NumberFormatException e) {
                Log.d(TAG, "could not parse input string to int");
            }
            viewModel.setInputNumber(value, position);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length() == 1) {
                transferFocus(editTexts);
            } else if(s.toString().length() == 0){
                reverseTransFocus(editTexts);
            }
        }

        private void reverseTransFocus(EditText[] editTexts) {
            for (int i = 0; i < editTexts.length - 1 ; i++) {
                if (editTexts[i].isFocused()) {
                    editTexts[i].clearFocus();
                    editTexts[i+1].requestFocus();
                    return;
                }
            }
        }

        private void transferFocus(@NonNull EditText[] editTexts) {
            for (int i = editTexts.length - 1; i > 0 ; i--) {
                if (editTexts[i].isFocused()) {
                    editTexts[i].clearFocus();
                    editTexts[i-1].requestFocus();
                    return;
                }
            }
        }
    }

    private void subscribe(final GameViewModel viewModel) {
        viewModel.getNewGameFlag().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (editTexts != null) {
                    for (int i = 0; i<editTexts.length; i++) {
                        editTexts[i].setText("");
                    }
                }
                mResult = new ArrayList<>();
                mAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getIsGameFinished().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isWining) {
                Log.d(TAG, "game finished, isWining = " + isWining);
                if (isWining) {
                    Toast.makeText(GameActivity.this, "Congratulations!", Toast.LENGTH_LONG).show();
                } else {
                    int[] realNumber = viewModel.getRealNumber();
                    StringBuilder sb = new StringBuilder();
                    for (int i=realNumber.length-1; i>=0; i--) {
                        sb.append(String.valueOf(realNumber[i]));
                    }
                    Toast.makeText(GameActivity.this, "Sorry, the answer is " + sb.toString(), Toast.LENGTH_LONG).show();
                }

                viewModel.onNewGame();
            }
        });

        viewModel.getResult().observe(this, new Observer<GuessNumber.GuessNumberResult>() {
            @Override
            public void onChanged(GuessNumber.GuessNumberResult guessNumberResult) {
                Log.d(TAG, "result = " + guessNumberResult);
                if (mResult == null) {
                    mResult = new ArrayList<>();
                }
                mResult.add(guessNumberResult);
                mAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getError().observe(this, new Observer<GuessNumber.ErrorInput>() {
            @Override
            public void onChanged(GuessNumber.ErrorInput errorInput) {
                if (errorInput == GuessNumber.ErrorInput.DUPLICATED_INPUT) {
                    Toast.makeText(GameActivity.this, "duplicated numbers", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class GameResultsAdapter extends RecyclerView.Adapter<GameResultsAdapter.ResultViewHolder> {
        @NonNull
        @Override
        public GameResultsAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GameActivity.this).inflate(R.layout.item_result,parent, false);
            return new ResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GameResultsAdapter.ResultViewHolder holder, int position) {
            holder.tvResult.setText(mResult.get(position).toTextValue());
            holder.tvItem.setText(String.valueOf(position+1));
            holder.tvGuessNumber.setText(mResult.get(position).getGuessNumberString());
        }

        @Override
        public int getItemCount() {
            return mResult.size();
        }

        class ResultViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvItem;
            TextView tvGuessNumber;
            TextView tvResult;
            ResultViewHolder(View view)
            {
                super(view);
                tvItem = view.findViewById(R.id.tvItemId);
                tvResult = view.findViewById(R.id.tvResult);
                tvGuessNumber = view.findViewById(R.id.tvGuessNumber);
            }
        }
    }
}
