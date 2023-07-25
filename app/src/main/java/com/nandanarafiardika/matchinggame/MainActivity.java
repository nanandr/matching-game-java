package com.nandanarafiardika.matchinggame;

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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int COLS = 4;
    private static final int ROWS = 5;

    ViewFlipper[] selectedCard = new ViewFlipper[2];

    TextView player1Text, player2Text, displayTurn;

    int turn, attempt = 1;
    int player1, player2 = 0;

    //total: 10 blocks / max 4*5 grid
    //don't hardcode these blocks!!!
    private final int[] cards = {R.drawable.diamond_ore, R.drawable.cobblestone, R.drawable.furnace_front_on, R.drawable.grass_block_side, R.drawable.iron_block, R.drawable.netherrack, R.drawable.oak_log, R.drawable.oak_planks, R.drawable.obsidian, R.drawable.soul_sand, R.drawable.diamond_ore, R.drawable.cobblestone, R.drawable.furnace_front_on, R.drawable.grass_block_side, R.drawable.iron_block, R.drawable.netherrack, R.drawable.oak_log, R.drawable.oak_planks, R.drawable.obsidian, R.drawable.soul_sand};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1Text = findViewById(R.id.player_1);
        player2Text = findViewById(R.id.player_2);
        displayTurn = findViewById(R.id.turn);

        LinearLayout grid = findViewById(R.id.grid);
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
                viewFlipper.setTag(cards[row * COLS + col]);

                LinearLayout.LayoutParams matchParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                ImageView cover = new ImageView(this);
                cover.setImageResource(R.drawable.glass);
                cover.setLayoutParams(matchParent);
                viewFlipper.addView(cover);

                ImageView card = new ImageView(this);
                card.setImageResource(cards[row * COLS + col]);
                card.setLayoutParams(matchParent);
                viewFlipper.addView(card);

                viewFlipper.setOnClickListener((v) -> clickEvent(viewFlipper));

                rowLayout.addView(viewFlipper);
            }

            grid.addView(rowLayout);
        }
    }

    private void clickEvent(ViewFlipper card){
        if(attempt == 1){
            selectedCard[0] = card;
            attempt = 2;
        }
        else{
            selectedCard[1] = card;
            attempt = 1;

            Handler handler = new Handler();
            handler.postDelayed(this::check, 1000);
        }
        card.setDisplayedChild(1);
    }

    @SuppressLint("SetTextI18n")
    private void check(){
        //still need to find a way so that user cant click the previously chosen
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
            if(turn == 1){
                turn = 2;
            }
            else{
                turn = 1;
            }
        }
        player1Text.setText(Integer.toString(player1));
        player2Text.setText(Integer.toString(player2));
        displayTurn.setText("Player " + turn + " Turn");
        checkEnd();
    }

    private void checkEnd(){
        if(player1 + player2 == ROWS * COLS / 2){
            Toast.makeText(this, player1 == player2 ? "It's a Tie!" : (player1 > player2 ? "Player 1" : "Player 2") + " Wins!", Toast.LENGTH_SHORT).show();
        }
    }

    private void shuffle() {
        //BEFORE ITS SHUFFLED, CHECK HOW MANY CARDS ARE REQUIRED, example: 5x4 uses 20 etc.
        //i should also multiply the array here instead of hard code it
        // Convert the primitive int array to a List<Integer>

        List<Integer> list = new ArrayList<>();
        for (int resourceId : cards) {
            list.add(resourceId);
        }

        Collections.shuffle(list);

        for (int i = 0; i < list.size(); i++) {
            cards[i] = list.get(i);
        }
    }
}