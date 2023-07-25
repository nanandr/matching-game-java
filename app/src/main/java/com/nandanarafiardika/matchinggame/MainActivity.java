package com.nandanarafiardika.matchinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int COLS = 4;
    private static final int ROWS = 5;

    int[] selectedCard = new int[2];

    TextView player1, player2, displayTurn;

    int turn, attempt = 1;

    //total: 10 blocks / max 4*5 grid
    private int[] cards = {R.drawable.diamond_ore, R.drawable.cobblestone, R.drawable.furnace_front_on, R.drawable.grass_block_side, R.drawable.iron_block, R.drawable.netherrack, R.drawable.oak_log, R.drawable.oak_planks, R.drawable.obsidian, R.drawable.soul_sand, R.drawable.diamond_ore, R.drawable.cobblestone, R.drawable.furnace_front_on, R.drawable.grass_block_side, R.drawable.iron_block, R.drawable.netherrack, R.drawable.oak_log, R.drawable.oak_planks, R.drawable.obsidian, R.drawable.soul_sand};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = findViewById(R.id.player_1);
        player2 = findViewById(R.id.player_2);
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
//                ImageView imageView = new ImageView(this);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 140,1);
//                imageView.setLayoutParams(params);
////                imageView.setImageResource(R.drawable.glass);
//                imageView.setImageResource(cards[row * COLS + col]);
//
//                imageView.setTag(cards[row * COLS + col]);
//
//                imageView.setOnClickListener(v -> clickEvent(imageView, (int) v.getTag()));
//                rowLayout.addView(imageView);

                ViewFlipper viewFlipper = new ViewFlipper(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 140, 1);
                viewFlipper.setLayoutParams(params);
                viewFlipper.setTag(cards[row * COLS + col]);

            }

            grid.addView(rowLayout);
        }
    }

    private void clickEvent(ImageView imageView, int tag){
        if(attempt == 1){
            //CHANGE IT TO UNIQUE these shouldnt use tag!
            selectedCard[0] = tag;
            attempt = 2;
        }
        else{
            selectedCard[1] = tag;
            attempt = 1;

            Handler handler = new Handler();
            handler.postDelayed(() -> check(imageView, tag), 1000);
        }
        imageView.setImageResource(R.drawable.diamond_ore);
        //Toast.makeText(this, "you clicked " + tag, Toast.LENGTH_SHORT).show(); FOR DEBUG

        //stuff about first or second card
    }

    private void check(ImageView imageView, int tag){
        if(selectedCard[0] == selectedCard[1]){
            //card match, set visibility to invisible
            //turn stay
            imageView.setVisibility(View.INVISIBLE);
        }
        else{
            imageView.setImageResource(R.drawable.glass);
//            LinearLayout grid = findViewById(R.id.grid);
//            for (int i = 0; i < grid.getChildCount(); i++) {
//                View view = grid.getChildAt(i);
//                if (view instanceof ImageView) {
//                    ImageView imageView = (ImageView) view;
//                    imageView.setImageResource(R.drawable.glass);
//                }
//            }
            if(turn == 1){
                turn = 2;
            }
            else{
                turn = 1;
            }
            displayTurn.setText("Player " + turn + " Turn");
        }
        checkEnd();
    }

    private void checkEnd(){
        //if every imageview is invisible, game over
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