package mccormick.cmccorm8.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    DrawFrag dFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            dFrag = new DrawFrag();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, dFrag).commit();
        }
    }
}