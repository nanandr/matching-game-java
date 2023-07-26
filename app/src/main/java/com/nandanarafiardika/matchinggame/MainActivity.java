package com.nandanarafiardika.matchinggame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

    private static final int COLS = 4;
    private static final int ROWS = 6;

    int cardRequired = ROWS * COLS / 2;

    ViewFlipper[] selectedCard = new ViewFlipper[2];

    TextView player1Text, player2Text, displayTurn;

    int turn, attempt = 1;
    int player1, player2 = 0;

    //total: 12 blocks / max 4*6 grid
    private final int[] cards = {R.drawable.diamond_ore, R.drawable.cobblestone, R.drawable.furnace_front_on, R.drawable.grass_block_side, R.drawable.iron_block, R.drawable.netherrack, R.drawable.oak_log, R.drawable.oak_planks, R.drawable.obsidian, R.drawable.soul_sand, R.drawable.blue_ice, R.drawable.bookshelf};

    //store cards used in game
    int[] shuffledCards = new int[cardRequired * 2];

    private boolean checking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1Text = findViewById(R.id.player_1);
        player2Text = findViewById(R.id.player_2);
        displayTurn = findViewById(R.id.turn);

        //prompt asking player the grid size

        startGame();
    }

    private void startGame(){
        LinearLayout grid = findViewById(R.id.grid);
        grid.removeAllViews();
        createGrid(grid);
    }

    private void createGrid(LinearLayout grid){
        shuffle();

        for(int row = 0; row < ROWS; row++){
            LinearLayout rowLayout = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 20);
            rowLayout.setLayoutParams(rowParams);

            for(int col = 0; col < COLS; col++){

                ViewFlipper viewFlipper = new ViewFlipper(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 140, 1);
                viewFlipper.setLayoutParams(params);
                viewFlipper.setTag(shuffledCards[row * COLS + col]);

                LinearLayout.LayoutParams matchParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                ImageView cover = new ImageView(this);
                cover.setImageResource(R.drawable.glass);
                cover.setLayoutParams(matchParent);
                viewFlipper.addView(cover);

                ImageView card = new ImageView(this);
                card.setImageResource(shuffledCards[row * COLS + col]);
                card.setLayoutParams(matchParent);
                viewFlipper.addView(card);

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
        }
        attempt++;
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
            Toast.makeText(this, player1 == player2 ? "It's a Tie!" : (player1 > player2 ? "Player 1" : "Player 2") + " Wins!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Replay?");
            builder.setPositiveButton("Yes", (dialog, which) -> resetGame());
            builder.show();
        }
    }

    private void resetGame(){
        player1 = player2 = 0;
        turn = attempt = 1;
        updateText();
        startGame();
    }

    private void shuffle() {
        int[] temp = Arrays.copyOf(cards, cards.length);

        for(int i = 0; i < cardRequired; i++){
            int randomIndex = (int) (Math.random() * temp.length);
            shuffledCards[i] = temp[randomIndex];
            shuffledCards[i + cardRequired] = temp[randomIndex];

            //avoid duplicate
            temp = removeCards(temp, randomIndex);
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

    private int[] removeCards(int[] array, int index){
        int[] result = new int[array.length - 1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    @SuppressLint("SetTextI18n")
    private void updateText(){
        player1Text.setText(Integer.toString(player1));
        player2Text.setText(Integer.toString(player2));
        displayTurn.setText("Player " + turn + " Turn");
    }
}