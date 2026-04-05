package com.example.sro_4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount;
    private boolean gameActive = true;
    private SharedPreferences prefs;
    private static final String GAME_PREFS = "tictactoe_settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);

        prefs = getSharedPreferences(GAME_PREFS, MODE_PRIVATE);
        GridLayout gridLayout = findViewById(R.id.gridGame);
        Button btnReset = findViewById(R.id.btnReset);
        Button btnBack = findViewById(R.id.btnBackGame);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int col = j;
                Button btn = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 250;
                params.height = 250;
                params.setMargins(5, 5, 5, 5);
                btn.setLayoutParams(params);
                btn.setTextSize(30);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCellClick(row, col);
                    }
                });
                buttons[i][j] = btn;
                gridLayout.addView(btn);
            }
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPrefs();
                resetGame();
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadGame();
    }

    private void onCellClick(int row, int col) {
        if (!gameActive || buttons[row][col].getText().length() > 0) return;
        String symbol = player1Turn ? "X" : "O";
        buttons[row][col].setText(symbol);
        roundCount++;
        if (checkForWin()) {
            gameActive = false;
            Toast.makeText(this, (player1Turn ? "X" : "O") + " победил!", Toast.LENGTH_SHORT).show();
        } else if (roundCount == 9) {
            gameActive = false;
            Toast.makeText(this, "Ничья", Toast.LENGTH_SHORT).show();
        } else {
            player1Turn = !player1Turn;
        }
        updateStatus();
        saveGame();
    }

    private void updateStatus() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        if (gameActive) {
            tvStatus.setText("Ход: " + (player1Turn ? "X" : "O"));
        } else {
            tvStatus.setText("Игра окончена");
        }
    }

    private boolean checkForWin() {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                buttons[i][0].getText().equals(buttons[i][2].getText()) &&
                !buttons[i][0].getText().equals("")) return true;
            if (buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                buttons[0][i].getText().equals(buttons[2][i].getText()) &&
                !buttons[0][i].getText().equals("")) return true;
        }
        if (buttons[0][0].getText().equals(buttons[1][1].getText()) &&
            buttons[0][0].getText().equals(buttons[2][2].getText()) &&
            !buttons[0][0].getText().equals("")) return true;
        if (buttons[0][2].getText().equals(buttons[1][1].getText()) &&
            buttons[0][2].getText().equals(buttons[2][0].getText()) &&
            !buttons[0][2].getText().equals("")) return true;
        return false;
    }

    private void saveGame() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("p1Turn", player1Turn);
        editor.putInt("round", roundCount);
        editor.putBoolean("active", gameActive);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                editor.putString("cell_" + i + "_" + j, buttons[i][j].getText().toString());
            }
        }
        editor.apply();
    }

    private void loadGame() {
        if (prefs.getAll().isEmpty()) {
            resetGame();
            return;
        }
        player1Turn = prefs.getBoolean("p1Turn", true);
        roundCount = prefs.getInt("round", 0);
        gameActive = prefs.getBoolean("active", true);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String val = prefs.getString("cell_" + i + "_" + j, "");
                buttons[i][j].setText(val);
            }
        }
        updateStatus();
    }

    private void resetGame() {
        player1Turn = true;
        roundCount = 0;
        gameActive = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        updateStatus();
    }
    
    private void clearPrefs() {
        prefs.edit().clear().apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGame();
    }
}
