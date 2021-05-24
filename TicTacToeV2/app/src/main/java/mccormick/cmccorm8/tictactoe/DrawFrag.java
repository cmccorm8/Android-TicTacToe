package mccormick.cmccorm8.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


public class DrawFrag extends Fragment {

    ImageView thegbField;
    TextView currTurn;
    //EditText payerNum;

    //Resources res = this.getResources();

    Bitmap gameBoard;
    Bitmap oImage, reSizedO;
    Bitmap xImage, reSizedX;

    public Canvas canvas;
    public int gamePosition[][];
    int currPlayer, playerNum, computerNum;
    Paint lineColor, o_XPiece;
    int leftX, midX, rightX, topY, midY, bottomY;
    public boolean gameOver = false;
    public boolean playerVPlayer = false;
    public boolean playerVComputer = false;
    public String Player1;
    //HashMap<String, Integer> decisionScores = new HashMap<String, Integer>();
    //public boolean isMaximizing = false;



    public DrawFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_draw, container, false);
        currTurn = view.findViewById(R.id.textView2);
        thegbField = view.findViewById(R.id.gbField);

        drawUtils();

        gamePosition = new int[3][3];
        for(int yi = 0; yi<3; yi++)
        {
            for(int xi = 0; xi<3; xi++)
            {
                gamePosition[xi][yi]=-1;
                Log.d("gamePosition: ", String.valueOf(gamePosition[xi][yi]));
            }
        }

        gameModeSelect();
        Log.d("Player1", "string is" + Player1);
        //Log.wtf("PlayerVPlayer:", String.valueOf(playerVPlayer));
        currPlayer=1;

        return view;
    }

    /**
     * gameModeSelect() utilizes a dialog box to prompt the user for which type of game they
     * would like to play. Player Vs Player or Player Vs Computer
     * calls startGame() after a selection has been made
     */
    public void gameModeSelect()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View gameMode = inflater.inflate(R.layout.gamemode_dialog, null);

        final RadioGroup rg = gameMode.findViewById(R.id.radioGroup);
        final RadioButton rB1 = gameMode.findViewById(R.id.radioButton);
        final RadioButton rB2 = gameMode.findViewById(R.id.radioButton2);

        final RadioGroup rg2 = gameMode.findViewById(R.id.radioGroup2);
        final RadioButton rB3 = gameMode.findViewById(R.id.radioButton3);
        final RadioButton rB4 = gameMode.findViewById(R.id.radioButton4);


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ThemeOverlay_AppCompat_Dialog));
        builder.setView(gameMode).setTitle("Choose Your Game Mode!");
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id)
                {
                    case R.id.radioButton:
                        Log.d("RadioButton: ", "Player v Player pressed");
                        playerVPlayer = true;
                        playerVComputer = false;
                        //setPlayerVPlayer();
                        break;
                    case R.id.radioButton2:
                        Log.d("RadioButton: ", "Player v computer pressed");
                        playerVComputer = true;
                        playerVPlayer = false;
                        //setPlayerVComputer();
                        break;
                }

            }
        });
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id)
                {
                    case R.id.radioButton3:
                        Log.d("RadioButton: ", "Player 2 (O) selected.");
                        playerNum = 2;
                        computerNum = 1;
                        String temp = "Computer";
                        setPlayer1(temp);
                        //Player1 = "Computer";

                        Log.d("RadioButton", "Player1 is " + Player1);
                        break;
                    case R.id.radioButton4:
                        Log.d("RadioButton: ", "Player 1 (X) selected.");
                        playerNum = 1;
                        computerNum = 2;
                        temp = "Player";
                        setPlayer1(temp);
                        //Player1 = "Player";

                        Log.d("RadioButton", "Player1 is " + Player1);
                        break;
                }
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                Context context = getContext();
                String gmError = "Please select a Game Mode.";
                String gpError = "Please choose Player 1 or Player 2.";
                int duration = Toast.LENGTH_SHORT;


                //The player must make a selection on game mode and game piece if not display error and recall gameModeSelect
                //else start the game.
                Log.d("Save:", "Save Pressed");
                if(!rB1.isChecked() && !rB2.isChecked())
                {
                    Toast toast = Toast.makeText(context, gmError, duration);
                    toast.show();
                    gameModeSelect();
                }
                else if(!rB3.isChecked() && !rB4.isChecked())
                {
                    Toast toast = Toast.makeText(context, gpError, duration);
                    toast.show();
                    gameModeSelect();
                }
                else
                {
                    startGame();
                }

            }
        })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Objects.requireNonNull(getActivity()).finish();
                        System.exit(0);
                    }
                });
        builder.show();


    }

    /**
     * startGame() is a function that determines which gameMode has been selected
     * and starts the game.
     */
    public void startGame()
    {
        //FIXME
        if(playerVPlayer)
        {
            Log.d("starGame()", "playerVPlayer made it");
            currTurn.setText("Player " + String.valueOf(currPlayer) + " turn!");
            thegbField.setOnTouchListener(new myTouchListener());

        }
        if(playerVComputer)
        {
            Log.d("startGame()", "playerVComputer made it");
            currTurn.setText("Player " + String.valueOf(currPlayer) + " turn!");
            playerVComputerDriver();
        }

    }

    /**
     *  Class that handles any touch action on the gameBoard
     */
   class myTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            int xPos = (int) event.getX();
            int yPos = (int) event.getY();

            if (action == MotionEvent.ACTION_UP) { //fake it for tap.
                gameDriver(xPos,yPos);
                if(playerVPlayer)
                {
                    startGame();
                }
                else if(playerVComputer)
                {
                    aiRoutine();
                }

                return true;
            }
            return false;
        }
    }

    /**
     * playerVComputerDriver() drives the game for Player Vs Computer action.
     * Takes appropriate action based on who player 1 is (person or computer).
     */
    void playerVComputerDriver()
    {
        Log.d("playerVComputerDriver()", "Player1 == " + Player1);

        if(getPlayer1().equals("Computer"))
        {
            //computer is player 1
            switch (currPlayer)
            {
                case 1:
                    //computer AI
                    thegbField.setOnTouchListener(null);  //disable touchListener if computer turn
                    aiRoutine();
                    thegbField.setOnTouchListener(new myTouchListener());
                    break;
                case 2:
                    thegbField.setOnTouchListener(new myTouchListener());
                    aiRoutine();
                    break;
            }
        }
        else if(getPlayer1().equals("Player")) {
            //player is player 1
            switch (currPlayer) {
                case 1:
                    //player
                    thegbField.setOnTouchListener(new myTouchListener());
                    break;
                case 2:
                    //computer AI
                    thegbField.setOnTouchListener(null);
                    aiRoutine();
                    break;
            }
        }
    }


    /**
     * gameDriver()-used to determine where the current player touched the screen and draw an appropriate
     * gamePiece on the board. Drives the game for Player Vs Player action
     * @param x-touch x-coordinate for the gameBoard
     * @param y-touch y-coordinate for the gameBoard
     */
    void gameDriver(int x, int y)
    {

        switch(currPlayer)
        {
            //leftX,midX,rightX,topY,midY,bottomY for positions
/********************************** Player 1 ******************************************************/
            case 1:
                if(x < leftX && y < topY) // position 0,0
                {
                    drawBMP(0,0,0,0,currPlayer);
                    break;
                }
                else if (x > leftX && x < midX && y < topY) // position 0,1
                {
                    drawBMP(0,1, (int) (333+lineColor.getStrokeWidth()),0,currPlayer);
                    break;
                }
                else if(x > midX && x < rightX && y < topY) //position 0,2
                {
                    drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
                    break;
                }
                else if (x < leftX && y < midY && y > topY) //position 1,0
                {
                    drawBMP(1,0, (int) 0,333,currPlayer);
                    break;
                }
                else if(x > leftX && x < midX && y < midY && y > topY) //position 1,1
                {
                    drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
                    break;
                }
                else if(x > midX && x < rightX && y < midY && y > topY) //position 1,2
                {
                    drawBMP(1,2, (int) (666+lineColor.getStrokeWidth()), 333, currPlayer);
                    break;
                }
                else if(x < leftX && y > midY && y < bottomY) //position 2,0
                {
                    drawBMP(2,0, 0, 666,currPlayer);
                    break;
                }
                else if(x > leftX && x < midX && y > midY && y < bottomY) //position 2,1
                {
                    drawBMP(2, 1, (int) (333+lineColor.getStrokeWidth()), 666, currPlayer);
                    break;
                }
                else if(x > midX && x < rightX && y > midY && y < bottomY) //position 2,2
                {
                    drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
                    break;
                }



/***************************** Player 2 **********************************************************/
            case 2:
                if(x < leftX && y < topY) // position 0,0
                {
                    drawBMP(0,0,0,0,currPlayer);
                    break;
                }
                else if (x > leftX && x < midX && y < topY) // position 0,1
                {
                    drawBMP(0,1, (int) (333+lineColor.getStrokeWidth()),0,currPlayer);
                    break;
                }
                else if(x > midX && x < rightX && y < topY) //position 0,2
                {
                    drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
                    break;
                }
                else if (x < leftX && y < midY && y > topY) //position 1,0
                {
                    drawBMP(1,0, 0,333,currPlayer);
                    break;
                }
                else if(x > leftX && x < midX && y < midY && y > topY) //position 1, 1
                {
                    drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
                    break;
                }
                else if(x > midX && x < rightX && y < midY && y > topY) //position 1,2
                {
                    drawBMP(1,2, (int) (666+lineColor.getStrokeWidth()), 333, currPlayer);
                    break;
                }
                else if(x < leftX && y > midY && y < bottomY) //position 2,0
                {
                    drawBMP(2,0, 0, 666,currPlayer);
                    break;
                }
                else if(x > leftX && x < midX && y > midY && y < bottomY) //position 2,1
                {
                    drawBMP(2, 1, (int) (333+lineColor.getStrokeWidth()), 666, currPlayer);
                    break;
                }
                else if(x > midX && x < rightX && y > midY && y < bottomY)
                {
                    drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
                    break;
                }


        }
        currTurn.setText("Player " + String.valueOf(currPlayer) + " turn!");

    }

    /**
     * aiRoutine() is a function that determines the AI's routine in a game of Tic Tac Toe
     */
    void aiRoutine()
    {
        int xCoord;
        int yCoord;
        int x, y;
        /*************************For The Win********************************************/

        //horizontal 1 check, gamePosition[0][0] empty
        if((gamePosition[0][0]==-1) && (gamePosition[0][1]==computerNum) && (gamePosition[0][2]==computerNum))
        {
            drawBMP(0,0,0,0,currPlayer);
            //startGame();
        }
        //horizontal 1 check, gamePosition[0][1] empty
        else if((gamePosition[0][0]==computerNum) && (gamePosition[0][1]==-1) && (gamePosition[0][2]==computerNum))
        {
            drawBMP(0,1, (int) (333+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //horizontal 1 check, gamePosition[0][2] empty
        else if((gamePosition[0][0]==computerNum) && (gamePosition[0][1]==computerNum) && (gamePosition[0][2]==-1))
        {
            drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();

        }
        //horizontal 2 check, gamePosition[1][0] empty
        else if((gamePosition[1][0]==-1) && (gamePosition[1][1]==computerNum) && (gamePosition[1][2]==computerNum))
        {
            drawBMP(1,0, (int) 0,333,currPlayer);
            //startGame();

        }
        //horizontal 2 check, gamePosition[1][1] empty
        else if((gamePosition[1][0]==computerNum) && (gamePosition[1][1]==-1) && (gamePosition[1][2]==computerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //horizontal 2 check, gamePosition[1][2] empty
        else if((gamePosition[1][0]==computerNum) && (gamePosition[1][1]==computerNum) && (gamePosition[1][2]==-1))
        {
            drawBMP(1,2, (int) (666+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //horizontal 3 check, gamePosition[2][0] empty
        else if((gamePosition[2][0]==-1) && (gamePosition[2][1]==computerNum) && (gamePosition[2][2]==computerNum))
        {
            drawBMP(2,0, 0, 666,currPlayer);
            //startGame();
        }
        //horizontal 3 check, gamePosition[2][1] empty
        else if((gamePosition[2][0]==computerNum) && (gamePosition[2][1]==-1) && (gamePosition[2][2]==computerNum))
        {
            drawBMP(2, 1, (int) (333+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //horizontal 3 check, gamePosition[2][2] empty
        else if((gamePosition[2][0]==computerNum) && (gamePosition[2][1]==computerNum) && (gamePosition[2][2]==-1))
        {
            drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //vertical 1 check, gamePosition[0][0] empty
        else if((gamePosition[0][0]==-1) && (gamePosition[1][0]==computerNum) && (gamePosition[2][0]==computerNum))
        {
            drawBMP(0,0,0,0,currPlayer);
            //startGame();
        }
        //vertical 1 check, gamePosition[1][0] empty
        else if((gamePosition[0][0]==computerNum) && (gamePosition[1][0]==-1) && (gamePosition[2][0]==computerNum))
        {
            drawBMP(1,0, (int) 0,333,currPlayer);
            //startGame();
        }
        //vertical 1 check, gamePosition[2][0] empty
        else if((gamePosition[0][0]==computerNum) && (gamePosition[1][0]==computerNum) && (gamePosition[2][0]==-1))
        {
            drawBMP(2,0, 0, 666,currPlayer);
            //startGame();
        }
        //vertical 2 check, gamePosition[0][1] empty
        else if((gamePosition[0][1]==-1) && (gamePosition[1][1]==computerNum) && (gamePosition[2][1]==computerNum))
        {
            drawBMP(0,1, (int) (333+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //vertical 2 check, gamePosition[1][1] empty
        else if((gamePosition[0][1]==computerNum) && (gamePosition[1][1]==-1) && (gamePosition[2][1]==computerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //vertical 2 check, gamePosition[2][1] empty
        else if((gamePosition[0][1]==computerNum) && (gamePosition[1][1]==computerNum) && (gamePosition[2][1]==-1))
        {
            drawBMP(2, 1, (int) (333+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //vertical 3 check, gamePosition[0][2] empty
        else if((gamePosition[0][2]==-1) && (gamePosition[1][2]==computerNum) && (gamePosition[2][2]==computerNum))
        {
            drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //vertical 3 check, gamePosition[1][2] empty
        else if ((gamePosition[0][2]==computerNum) && (gamePosition[1][2]==-1) && (gamePosition[2][2]==computerNum))
        {
            drawBMP(1,2, (int) (666+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //vertical 3 check, gamePosition[2][2] empty
        else if((gamePosition[0][2]==computerNum) && (gamePosition[1][2]==computerNum) && (gamePosition[2][2]==-1))
        {
            drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //diagonal 1 check, gamePosition[0][0] empty
        else if((gamePosition[0][0]==-1) && (gamePosition[1][1]==computerNum) && (gamePosition[2][2]==computerNum))
        {
            drawBMP(0,0,0,0,currPlayer);
            //startGame();
        }
        //diagonal 1 check, gamePosition[1][1] empty
        else if((gamePosition[0][0]==computerNum) && (gamePosition[1][1]==-1) && (gamePosition[2][2]==computerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //diagonal 1 check, gamePosition[2][2] empty
        else if((gamePosition[0][0]==computerNum) && (gamePosition[1][1]==computerNum) && (gamePosition[2][2]==-1))
        {
            drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //diagonal 2 check, gamePosition[0][2] empty
        else if((gamePosition[0][2]==-1) && (gamePosition[1][1]==computerNum) && (gamePosition[2][0]==computerNum))
        {
            drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //diagonal 2 check, gamePosition[1][1] empty
        else if((gamePosition[0][2]==computerNum) && (gamePosition[1][1]==-1) && (gamePosition[2][0]==computerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //diagonal 2 check, gamePosition[2][0] empty
        else if((gamePosition[0][2]==computerNum) && (gamePosition[1][1]==computerNum) && (gamePosition[2][0]==-1))
        {
            drawBMP(2,0, 0, 666,currPlayer);
            //startGame();
        }



        /************************For The Block*******************************************/

        //horizontal 1 check, gamePosition[0][0] empty
        else if((gamePosition[0][0]==-1) && (gamePosition[0][1]==playerNum) && (gamePosition[0][2]==playerNum))
        {
            drawBMP(0,0,0,0,currPlayer);
            //startGame();
        }
        //horizontal 1 check, gamePosition[0][1] empty
        else if((gamePosition[0][0]==playerNum) && (gamePosition[0][1]==-1) && (gamePosition[0][2]==playerNum))
        {
            drawBMP(0,1, (int) (333+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //horizontal 1 check, gamePosition[0][2] empty
        else if((gamePosition[0][0]==playerNum) && (gamePosition[0][1]==playerNum) && (gamePosition[0][2]==-1))
        {
            drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //horizontal 2 check, gamePosition[1][0] empty
        else if((gamePosition[1][0]==-1) && (gamePosition[1][1]==playerNum) && (gamePosition[1][2]==playerNum))
        {
            drawBMP(1,0, (int) 0,333,currPlayer);
            //startGame();
        }
        //horizontal 2 check, gamePosition[1][1] empty
        else if((gamePosition[1][0]==playerNum) && (gamePosition[1][1]==-1) && (gamePosition[1][2]==playerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //horizontal 2 check, gamePosition[1][2] empty
        else if((gamePosition[1][0]==playerNum) && (gamePosition[1][1]==playerNum) && (gamePosition[1][2]==-1))
        {
            drawBMP(1,2, (int) (666+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //horizontal 3 check, gamePosition[2][0] empty
        else if((gamePosition[2][0]==-1) && (gamePosition[2][1]==playerNum) && (gamePosition[2][2]==playerNum))
        {
            drawBMP(2,0, 0, 666,currPlayer);
            //startGame();
        }
        //horizontal 3 check, gamePosition[2][1] empty
        else if((gamePosition[2][0]==playerNum) && (gamePosition[2][1]==-1) && (gamePosition[2][2]==playerNum))
        {
            drawBMP(2, 1, (int) (333+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //horizontal 3 check, gamePosition[2][2] empty
        else if((gamePosition[2][0]==playerNum) && (gamePosition[2][1]==playerNum) && (gamePosition[2][2]==-1))
        {
            drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //vertical 1 check, gamePosition[0][0] empty
        else if((gamePosition[0][0]==-1) && (gamePosition[1][0]==playerNum) && (gamePosition[2][0]==playerNum))
        {
            drawBMP(0,0,0,0,currPlayer);
            //startGame();
        }
        //vertical 1 check, gamePosition[1][0] empty
        else if((gamePosition[0][0]==playerNum) && (gamePosition[1][0]==-1) && (gamePosition[2][0]==playerNum))
        {
            drawBMP(1,0, (int) 0,333,currPlayer);
            //startGame();
        }
        //vertical 1 check, gamePosition[2][0] empty
        else if((gamePosition[0][0]==playerNum) && (gamePosition[1][0]==playerNum) && (gamePosition[2][0]==-1))
        {
            drawBMP(2,0, 0, 666,currPlayer);
            //startGame();
        }
        //vertical 2 check, gamePosition[0][1] empty
        else if((gamePosition[0][1]==-1) && (gamePosition[1][1]==playerNum) && (gamePosition[2][1]==playerNum))
        {
            drawBMP(0,1, (int) (333+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //vertical 2 check, gamePosition[1][1] empty
        else if((gamePosition[0][1]==playerNum) && (gamePosition[1][1]==-1) && (gamePosition[2][1]==playerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //vertical 2 check, gamePosition[2][1] empty
        else if((gamePosition[0][1]==playerNum) && (gamePosition[1][1]==playerNum) && (gamePosition[2][1]==-1))
        {
            drawBMP(2, 1, (int) (333+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //vertical 3 check, gamePosition[0][2] empty
        else if((gamePosition[0][2]==-1) && (gamePosition[1][2]==playerNum) && (gamePosition[2][2]==playerNum))
        {
            drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //vertical 3 check, gamePosition[1][2] empty
        else if ((gamePosition[0][2]==playerNum) && (gamePosition[1][2]==-1) && (gamePosition[2][2]==playerNum))
        {
            drawBMP(1,2, (int) (666+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //vertical 3 check, gamePosition[2][2] empty
        else if((gamePosition[0][2]==playerNum) && (gamePosition[1][2]==playerNum) && (gamePosition[2][2]==-1))
        {
            drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //diagonal 1 check, gamePosition[0][0] empty
        else if((gamePosition[0][0]==-1) && (gamePosition[1][1]==playerNum) && (gamePosition[2][2]==playerNum))
        {
            drawBMP(0,0,0,0,currPlayer);
            //startGame();
        }
        //diagonal 1 check, gamePosition[1][1] empty
        else if((gamePosition[0][0]==playerNum) && (gamePosition[1][1]==-1) && (gamePosition[2][2]==playerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //diagonal 1 check, gamePosition[2][2] empty
        else if((gamePosition[0][0]==playerNum) && (gamePosition[1][1]==playerNum) && (gamePosition[2][2]==-1))
        {
            drawBMP(2, 2, (int) (666+lineColor.getStrokeWidth()), 666, currPlayer);
            //startGame();
        }
        //diagonal 2 check, gamePosition[0][2] empty
        else if((gamePosition[0][2]==-1) && (gamePosition[1][1]==playerNum) && (gamePosition[2][0]==playerNum))
        {
            drawBMP(0,2, (int) (666+lineColor.getStrokeWidth()),0,currPlayer);
            //startGame();
        }
        //diagonal 2 check, gamePosition[1][1] empty
        else if((gamePosition[0][2]==playerNum) && (gamePosition[1][1]==-1) && (gamePosition[2][0]==playerNum))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //diagonal 2 check, gamePosition[2][0] empty
        else if((gamePosition[0][2]==playerNum) && (gamePosition[1][1]==playerNum) && (gamePosition[2][0]==-1))
        {
            drawBMP(2,0, 0, 666,currPlayer);
            //startGame();
        }
        //Select middle position if all spaces are empty
        else if((gamePosition[0][0]==-1) && (gamePosition[0][1]==-1) && (gamePosition[0][2]==-1) &&
                (gamePosition[1][0]==-1) && (gamePosition[1][1]==-1) && (gamePosition[1][2]==-1) &&
                (gamePosition[2][0]==-1) && (gamePosition[2][1]==-1) && (gamePosition[2][2]==-1))
        {
            drawBMP(1,1, (int) (333+lineColor.getStrokeWidth()), 333, currPlayer);
            //startGame();
        }
        //if all spaces have been selected
        else if((gamePosition[0][0]!=-1) && (gamePosition[0][1]!=-1) && (gamePosition[0][2]!=-1) &&
                (gamePosition[1][0]!=-1) && (gamePosition[1][1]!=-1) && (gamePosition[1][2]!=-1) &&
                (gamePosition[2][0]!=-1) && (gamePosition[2][1]!=-1) && (gamePosition[2][2]!=-1))
        {
            winCheck();
        }
        else
        {
            do {
                x = (int) (Math.random() * 3);
                Log.d("Ranodm x", String.valueOf(x));
                y = (int) (Math.random() * 3);
                Log.d("Ranodm y", String.valueOf(y));
            } while(gamePosition[y][x] != -1);



            switch(x)
            {
                case 0:
                    xCoord = 0;
                    break;
                case 1:
                    xCoord = (int) (333+lineColor.getStrokeWidth());
                    break;
                case 2:
                    xCoord = (int) (666+lineColor.getStrokeWidth());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + x);
            }
            switch(y)
            {
                case 0:
                    yCoord = 0;
                    break;
                case 1:
                    yCoord = 333;
                    break;
                case 2:
                    yCoord = 666;
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + y);
            }

            drawBMP(y, x, xCoord, yCoord, currPlayer);
            //startGame();
            //playerVComputerDriver();
        }

    }

    /**
     * setPlayer1() sets the Player1 string. Used to determine who is player 1
     * @param temp- temp storage of Player1 String
     */
    void setPlayer1(String temp)
    {
        Player1 = temp;
    }

    /**
     * getPlayer1() gets the player1 string. Used to determine who is player 1
     * @return Player1
     */
    public String getPlayer1()
    {
        return Player1;
    }

    /**
     * drawBMP() used to draw the gamepiece (X or O) to the gameBoard
     * @param posY y position on the gameBoard
     * @param posX x position on the gameBoard
     * @param xCoord x-coordinate used for drawing to the screen
     * @param yCoord y-coordinated used for drawing to the screen
     * @param currPlayer the current player
     */
    void drawBMP(int posY, int posX, int xCoord, int yCoord, int currPlayer)
    {
        Toast toast = Toast.makeText(getContext(), "Must select an empty position!", Toast.LENGTH_SHORT);

        switch (currPlayer)
        {
            case 1:
                if(gamePosition[posY][posX] == -1)
                {
                    canvas.drawBitmap(reSizedX, xCoord, yCoord+lineColor.getStrokeWidth(), lineColor);
                    thegbField.invalidate();
                    gamePosition[posY][posX] = 1;
                    winCheck();
                }
                else
                {
                    toast.show();
                }
                //startGame();


                break;

            case 2:
                if(gamePosition[posY][posX] == -1)
                {
                    canvas.drawBitmap(reSizedO, xCoord, yCoord+lineColor.getStrokeWidth(), lineColor);
                    thegbField.invalidate();
                    gamePosition[posY][posX] = 2;
                    winCheck();
                }
                else
                {
                    toast.show();
                }
                //startGame();
                break;
        }
    }

    /**
     * ClearBMP() resets the game board
     */
    void clearBMP()
    {

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                gamePosition[i][j]=-1;
            }
        }
        drawUtils();
        thegbField.invalidate();
        currPlayer = 1;
        currTurn.setText("Player " + String.valueOf(currPlayer) + " turn!");

    }

    /**
     * drawUtils() is a utility function that sets up the drawable objects and draws them
     */
    void drawUtils()
    {

        gameBoard = Bitmap.createBitmap(999, 999, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(gameBoard);
        canvas.drawColor(Color.rgb(0,39,76)); //canvas is a blue color
        thegbField.setImageBitmap(gameBoard);

        leftX = 999/3;
        midX = leftX*2;
        rightX = 999;
        topY = 999/3;
        midY = topY*2;
        bottomY = 999;

        lineColor = new Paint();
        lineColor.setColor(Color.WHITE);
        lineColor.setStrokeWidth(10);

        canvas.drawLine(0,333,999,333,lineColor);
        canvas.drawLine(0,666,999,666,lineColor);
        canvas.drawLine(333,0,333,999,lineColor);
        canvas.drawLine(666,0,666,999,lineColor);
        o_XPiece = new Paint();
        o_XPiece.setColor(Color.rgb(255,203,5));

        oImage = BitmapFactory.decodeResource(getResources(),R.drawable.o_img);
        xImage = BitmapFactory.decodeResource(getResources(),R.drawable.x_img);
        reSizedO = Bitmap.createScaledBitmap(oImage, (int) (333-lineColor.getStrokeWidth()),(int) (333-lineColor.getStrokeWidth()),false);
        reSizedX = Bitmap.createScaledBitmap(xImage, (int) (333-lineColor.getStrokeWidth()),(int) (333-lineColor.getStrokeWidth()),false);

    }

    /**
     * winCheck() is a function that is used to determine a winner or potentially a tie
     */
    void winCheck()
    {
        if(currPlayer == 1)
        {
            currPlayer = 2;
        }
        else
        {
            currPlayer=1;
        }

        // horizontal 1 check
        if((gamePosition[0][0]==1) && (gamePosition[0][1]==1) && (gamePosition[0][2]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[0][0]==2) && (gamePosition[0][1]==2) && (gamePosition[0][2]==2))
        {

            customDialogWin();
        }

        // horizontal 2 check
        else if((gamePosition[1][0]==1) && (gamePosition[1][1]==1) && (gamePosition[1][2]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[1][0]==2) && (gamePosition[1][1]==2) && (gamePosition[1][2]==2))
        {

            customDialogWin();
        }

        //horizontal 3 check
        else if((gamePosition[2][0]==1) && (gamePosition[2][1]==1) && (gamePosition[2][2]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[2][0]==2) && (gamePosition[2][1]==2) && (gamePosition[2][2]==2))
        {

            customDialogWin();
        }

        //vertical 1 check
        else if((gamePosition[0][0]==1) && (gamePosition[1][0]==1) && (gamePosition[2][0]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[0][0]==2) && (gamePosition[1][0]==2) && (gamePosition[2][0]==2))
        {

            customDialogWin();
        }

        // vertical 2  check
        else if((gamePosition[0][1]==1) && (gamePosition[1][1]==1) && (gamePosition[2][1]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[0][1]==2) && (gamePosition[1][1]==2) && (gamePosition[2][1]==2))
        {

            customDialogWin();
        }

        // vertical 3 check
        else if((gamePosition[0][2]==1) && (gamePosition[1][2]==1) && (gamePosition[2][2]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[0][2]==2) && (gamePosition[1][2]==2) && (gamePosition[2][2]==2))
        {

            customDialogWin();
        }

        //Diagonal 1 check
        else if((gamePosition[0][0]==1) && (gamePosition[1][1]==1) && (gamePosition[2][2]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[0][0]==2) && (gamePosition[1][1]==2) && (gamePosition[2][2]==2))
        {

            customDialogWin();
        }

        //Diagonal 2 check
        else if((gamePosition[0][2]==1) && (gamePosition[1][1]==1) && (gamePosition[2][0]==1))
        {

            customDialogWin();
        }
        else if((gamePosition[0][2]==2) && (gamePosition[1][1]==2) && (gamePosition[2][0]==2))
        {

            customDialogWin();
        }

        //Tie Check-very ugly
        else if((gamePosition[0][0]!=-1) && (gamePosition[0][1]!=-1) && (gamePosition[0][2]!=-1) &&
                (gamePosition[1][0]!=-1) && (gamePosition[1][1]!=-1) && (gamePosition[1][2]!=-1) &&
                (gamePosition[2][0]!=-1) && (gamePosition[2][1]!=-1) && (gamePosition[2][2]!=-1))
        {
            customDialogTie();
        }
    }

    /**
     * customDialogWin() display a dialog box declaring the the winner when a win has occurred
     */
    void customDialogWin()
    {
        if(currPlayer == 2)
        {
            currPlayer -=1;
        }
        else
        {
            currPlayer +=1;
        }
        Dialog dialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Player "+ String.valueOf(currPlayer) + " Is the WINNER!")
                .setCancelable(false)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Dialog Win: ", "Winner!");
                        clearBMP();
                        gameModeSelect();
                    }
                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Not sure if there is a better way to exit an app
                Objects.requireNonNull(getActivity()).finish();
                System.exit(0);

            }
        });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * customeDialogTie() displays a dialog box declaring a Tie has occurred
     */
    void customDialogTie()
    {
        Dialog dialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setMessage("TIE!")
                .setCancelable(false)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Dialog Tie: ", "Tie!");
                        clearBMP();
                        gameModeSelect();
                    }
                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Not sure if there is a better way to exit an app
                Objects.requireNonNull(getActivity()).finish();
                System.exit(0);

            }
        });
        dialog = builder.create();
        dialog.show();
    }
}