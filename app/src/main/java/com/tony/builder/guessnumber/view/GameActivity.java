package com.tony.builder.guessnumber.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tony.builder.guessnumber.R;
import com.tony.builder.guessnumber.model.GuessNumber;
import com.tony.builder.guessnumber.viewmodel.GameViewModel;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private GameViewModel viewModel;
    private EditText[] editTexts;
    private Button btnGuess;

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
        initResultView();
        viewModel.onNewGame();
    }

    private void initResultView() {
        mResult = new ArrayList<>();
        rvResultContainer = findViewById(R.id.rvResultContainer);
        rvResultContainer.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GameResultsAdapter();
        rvResultContainer.setAdapter(mAdapter);
    }

    private void initButtonGuess() {
        btnGuess = findViewById(R.id.btnGuess);
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.onGuessNumber();
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

        }
    }

    private void subscribe(GameViewModel viewModel) {
        viewModel.getIsGameFinished().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isWining) {
                Log.d(TAG, "game finished, isWining = " + isWining);
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
            holder.tvItem.setText(""+position);
        }

        @Override
        public int getItemCount() {
            return mResult.size();
        }

        class ResultViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvItem;
            TextView tvResult;
            ResultViewHolder(View view)
            {
                super(view);
                tvItem = view.findViewById(R.id.tvItemId);
                tvResult = view.findViewById(R.id.tvResult);
            }
        }
    }
}
