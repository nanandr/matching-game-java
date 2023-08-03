package com.nandanarafiardika.matchinggame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int COLS = 0; //MAX: 4
    private static int ROWS = 0; //MAX: 6

    ViewFlipper[] selectedCard = new ViewFlipper[2];

    LinearLayout grid, background;
    TextView player1DisplayName, player2DisplayName, player1Text, player2Text, displayTurn;
    ImageView icon, replay, history, info;

    int turn = 1;
    int attempt = 1;
    int player1, player2;

    String player1Name, player2Name;

    //total: 17 blocks / max 4*6 grid
    private static final int[] cards = {R.drawable.grass_block_side, R.drawable.diamond_ore, R.drawable.emerald_block, R.drawable.hay_block_side, R.drawable.gravel, R.drawable.dirt, R.drawable.stone_bricks, R.drawable.cobblestone, R.drawable.furnace_front_on, R.drawable.iron_block, R.drawable.netherrack, R.drawable.oak_log, R.drawable.oak_planks, R.drawable.obsidian, R.drawable.soul_sand, R.drawable.blue_ice, R.drawable.bookshelf};

    //store cards used in game
    private int[] shuffledCards;

    private boolean checking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.background);
        icon = findViewById(R.id.icon);
        replay = findViewById(R.id.replay);
        history = findViewById(R.id.history);
        info = findViewById(R.id.info);
        grid = findViewById(R.id.grid);

        player1DisplayName = findViewById(R.id.player_1_name);
        player2DisplayName = findViewById(R.id.player_2_name);
        player1Text = findViewById(R.id.player_1);
        player2Text = findViewById(R.id.player_2);
        displayTurn = findViewById(R.id.turn);

        showInput();

        replay.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Replay");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", (dialog, which) -> resetGame());
            builder.show();
        });
        history.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("History");
            builder.setView(showHistory());
            builder.show();
        });
        info.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("About Me");
            builder.setMessage("Name: Nandana Rafi Ardika\nClass: XII RPL B\nAbsen: 16");

            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setPadding(0, 0, 0, 42);
            layout.setLayoutParams(params);

            ImageView profile = new ImageView(this);
            LinearLayout.LayoutParams matchParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 420);
            profile.setLayoutParams(matchParent);
            profile.setImageResource(R.drawable.profile);
            layout.addView(profile);
            builder.setView(layout);

            builder.show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void showInput(){
        player1Text.setText("");
        player2Text.setText("");
        player1DisplayName.setText("");
        player2DisplayName.setText("");
        grid.removeAllViews();

        LinearLayout inputName = createLinearLayout("Input Players Name");
        EditText player1Text = createEditText("Player 1");
        inputName.addView(player1Text);
        EditText player2Text = createEditText("Player 2");
        inputName.addView(player2Text);
        grid.addView(inputName);

        LinearLayout inputGrid = createLinearLayout("Input Grid Size");
        EditText rowsText = createEditText("Rows");
        rowsText.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputGrid.addView(rowsText);
        EditText colsText = createEditText("Columns");
        colsText.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputGrid.addView(colsText);
        grid.addView(inputGrid);

        Button button = new Button(this);
        button.setText("Start Game");
        button.setOnClickListener(v -> {
            String player1Input = player1Text.getText().toString();
            String player2Input = player2Text.getText().toString();
            String rowsInput = rowsText.getText().toString();
            String colsInput = colsText.getText().toString();

            if(player1Input.isEmpty() || player2Input.isEmpty()){
                showToast("Please fill the players name");
                return;
            }
            if(rowsInput.isEmpty() || colsInput.isEmpty()){
                showToast("Please fill the grid size");
                return;
            }

            int gridSize = Integer.parseInt(rowsInput) * Integer.parseInt(colsInput);

            if(gridSize > (cards.length * 2)){
                showToast("Grid total must be below " + (cards.length * 2) + " \n(You filled " + gridSize + ")");
                return;
            }
            if(Integer.parseInt(rowsInput) > 4 || Integer.parseInt(colsInput) > 6){
                showToast("Grid total must be below " + (cards.length * 2) + " \n(You filled " + gridSize + ")");
                return;
            }

            if(gridSize % 2 == 0){
                ROWS = Integer.parseInt(rowsInput);
                COLS = Integer.parseInt(colsInput);
                player1Name = player1Input;
                player2Name = player2Input;
                startGame();
            }
            else{
                showToast("Grid must have an even number of cells \n (You filled " + gridSize + ")");
            }
        });
        grid.addView(button);
    }

    private void createGrid(){
        grid.removeAllViews();
        shuffle();

        for(int col = 0; col < COLS; col++){
            LinearLayout rowLayout = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 20);
            rowLayout.setGravity(Gravity.CENTER);
            rowLayout.setLayoutParams(rowParams);

            for(int row = 0; row < ROWS; row++){
                ViewFlipper viewFlipper = new ViewFlipper(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160, 140);
                viewFlipper.setLayoutParams(params);
                viewFlipper.setTag(shuffledCards[row * COLS + col]);

                viewFlipper.addView(drawCard(R.drawable.glass));
                viewFlipper.addView(drawCard(shuffledCards[row * COLS + col]));

                viewFlipper.setOnClickListener((v) -> clickEvent(viewFlipper));

                rowLayout.addView(viewFlipper);
            }

            grid.addView(rowLayout);
        }
    }

    private void clickEvent(ViewFlipper card){
        if(!checking && card.getDisplayedChild() == 0){
            card.setDisplayedChild(1);
            if(attempt == 1){
                selectedCard[0] = card;
            }
            else if(attempt == 2){
                selectedCard[1] = card;

                Handler handler = new Handler();
                handler.postDelayed(this::check, 1000);
                checking = true;
            }
            attempt++;
        }
    }

    private void check(){
        attempt = 1;
        if(selectedCard[0].getTag().equals(selectedCard[1].getTag())){
            selectedCard[0].setVisibility(View.INVISIBLE);
            selectedCard[1].setVisibility(View.INVISIBLE);
            if(turn == 1){
                player1++;
            }
            else{
                player2++;
            }
            icon.setImageResource((int) selectedCard[1].getTag());
        }
        else{
            selectedCard[0].setDisplayedChild(0);
            selectedCard[1].setDisplayedChild(0);
            turn = (turn == 1) ? 2 : 1;
        }
        updateText();
        checkGameOver();
        checking = false;
    }

    private void checkGameOver(){
        if(player1 + player2 == ROWS * COLS / 2){
            showToast(player1 == player2 ? "It's a Tie!" : (player1 > player2 ? player1Name : player2Name) + " Wins!");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Replay?");
            builder.setPositiveButton("Yes", (dialog, which) -> resetGame());
            builder.show();

            //save player1, player1Name, player2, player2Name to db
        }
    }

    private void startGame(){
        createGrid();
        updateText();
    }

    private void resetGame(){
        player1 = player2 = 0;
        turn = attempt = 1;
        updateText();
        showInput();
    }

    private void shuffle() {
        int[] temp = Arrays.copyOf(cards, cards.length);
        shuffledCards = new int[ROWS * COLS];

        for(int i = 0; i < (ROWS * COLS / 2); i++){
            int randomIndex = (int) (Math.random() * temp.length);
            shuffledCards[i] = temp[randomIndex];
            shuffledCards[i + (ROWS * COLS / 2)] = temp[randomIndex];

            //avoid duplicate
            temp = removeCard(temp, randomIndex);
        }

        //shuffle
        List<Integer> list = new ArrayList<>();
        for (int resourceId : shuffledCards) {
            list.add(resourceId);
        }
        Collections.shuffle(list);

        //turn list to array
        for (int i = 0; i < list.size(); i++) {
            shuffledCards[i] = list.get(i);
        }
    }

    private ImageView drawCard(int drawable){
        ImageView card = new ImageView(this);
        card.setImageResource(drawable);
        LinearLayout.LayoutParams matchParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        card.setLayoutParams(matchParent);
        return card;
    }

    private int[] removeCard(int[] array, int index){
        int[] result = new int[array.length - 1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    @SuppressLint("SetTextI18n")
    private void updateText(){
        background.setBackgroundResource((turn == 1) ? R.drawable.blue_background : R.drawable.red_background);
        player1DisplayName.setText(player1Name);
        player2DisplayName.setText(player2Name);
        player1Text.setText(Integer.toString(player1));
        player2Text.setText(Integer.toString(player2));
        displayTurn.setText("Player " + turn + " Turn");
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private EditText createEditText(String hint){
        EditText editText = new EditText(this);
        editText.setHint(hint);
        return editText;
    }

    private TextView createTextView(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
        return textView;
    }

    private LinearLayout createLinearLayout(String title){
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(16, 16, 16, 16);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTypeface(Typeface.MONOSPACE);
        textView.setTextSize(16);
        layout.addView(textView);

        return layout;
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout showHistory(){
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(32, 16, 32, 16);
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(margin);

        //loop this layout
        LinearLayout list = new LinearLayout(this);
        list.setLayoutParams(margin);
        list.setGravity(Gravity.CENTER);
        list.setOrientation(LinearLayout.HORIZONTAL);

        TextView listPlayer1 = createTextView("Jim\n6");
        listPlayer1.setGravity(Gravity.CENTER);
        list.addView(listPlayer1);

        TextView vs = createTextView("vs");
        vs.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        vs.setLayoutParams(params);
        list.addView(vs);

        TextView listPlayer2 = createTextView("Mike\n10");
        listPlayer2.setGravity(Gravity.CENTER);
        list.addView(listPlayer2);

        layout.addView(list);
        //end loop here

        return layout;
    }
}