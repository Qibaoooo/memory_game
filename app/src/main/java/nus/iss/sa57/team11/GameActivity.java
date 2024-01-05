package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    //private File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    private boolean isFirstClick = true;
    int firstClickId;
    List<Integer> matchedId = new ArrayList<>();//this is for checking click
    int matches;
    private TextView timerTextView;
    private long startTime;
    private final Handler handler = new Handler();
    int attempts;
    List<String> imgPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        imgPaths = getImgFilesDir();//get the list on create because the list is random
        setImgHolders();
        matches = 0;
        setMatchesText();
        attempts = 0;
        setAttemptsText();
        timerTextView = findViewById(R.id.timer);
        startTime = SystemClock.elapsedRealtime();
        handler.postDelayed(updateTimerRunnable, 1000);
        Button restartBtn = findViewById(R.id.reset_btn);
        restartBtn.setOnClickListener(v -> restart());
        //setPictures();
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(!matchedId.contains(id)) { //avoid first click on revealed img
            setPicture(imgPaths, id);
            if (isFirstClick) {
                attempts++;
                firstClickId = id;
                isFirstClick = false;
            } else {
                if (id != firstClickId) { //avoid click on same img
                    if (imgPaths.get(id).equalsIgnoreCase(imgPaths.get(firstClickId))) {
                        isFirstClick = true;
                        matchedId.add(id);
                        matchedId.add(firstClickId);
                        matches++;
                        attempts++;
                        setMatchesText();
                        if (matches == 6) {
                            handler.removeCallbacks(updateTimerRunnable);
                        }
                    } else {
                        attempts++;
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            setBackground(id);
                            setBackground(firstClickId);
                            isFirstClick = true;
                        }, 400);
                    }
                }
            }
        }
        setAttemptsText();
    }

    private void setImgHolders(){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        int index = 0;
        for (int i = 0; i < 4; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
            for (int j = 0; j < 3; j++) {
                ImageView holder = new ImageView(this);
                holder.setImageResource(R.drawable.q_mark);
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                holder.setLayoutParams(params);

                holder.getLayoutParams().height = 400; //can change the size according to you requirements
                holder.requestLayout();
                holder.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.setId(index++);
                holder.setOnClickListener(this);

                tr.addView(holder);
            }
            imgTable.addView(tr);
        }
    }

    private void setPicture(List<String> imgPath,int index){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        TableRow tr = (TableRow) imgTable.getChildAt(index / 3);
        ImageView iv = (ImageView) tr.getChildAt(index % 3);
        int id = getResources().getIdentifier(imgPath.get(index),"drawable",getPackageName());
        iv.setImageResource(id);
    }

    private void setBackground(int index){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        TableRow tr = (TableRow) imgTable.getChildAt(index / 3);
        ImageView iv = (ImageView) tr.getChildAt(index % 3);
        iv.setImageResource(R.drawable.q_mark);
    }

    private List<String> getImgFilesDir(){
        List<String> originalName = Arrays.asList("t0", "t1", "t2", "t3", "t4", "t5");
        //File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File[] files = externalFilesDir.listFiles();
        List<String> res = new ArrayList<>();
        res.addAll(originalName);
        res.addAll(originalName);
        Collections.shuffle(res);

        return res;
    }

    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = SystemClock.elapsedRealtime() - startTime;

            int seconds = (int) (elapsedTime / 1000) % 60;
            int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
            int hours = (int) ((elapsedTime / (1000 * 60 * 60)));

            timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            handler.postDelayed(this, 1000);
        }
    };

    private void restart(){
        finish();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void setMatchesText(){
        TextView matches = findViewById(R.id.matches);
        matches.setText(String.format(getString(R.string.matches_text), this.matches));
    }

    private void setAttemptsText(){
        TextView attempts = findViewById(R.id.attempts);
        attempts.setText(String.format(getString(R.string.attempts), this.attempts));
    }

}