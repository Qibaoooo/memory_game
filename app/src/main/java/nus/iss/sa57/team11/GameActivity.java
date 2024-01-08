package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    //private File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    private int matches;
    private int attempts;
    private TextView timerTextView;
    private long startTime;
    private final Handler handler = new Handler();
    private boolean isDefault;
    private List<String> imgPaths;
    private ArrayList<String> imgNames;
    private boolean isFirstClick;
    private int firstClickId;
    private List<Integer> matchedId;//this is for checking click
    private View firstClickedView;
    private View pausedView;
    private Button btn_resume;
    private boolean isDouble;
    private boolean isFirstPlayer;
    private View scoreTable;
    private View turn;
    private boolean isClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initGame();
        pausedView = findViewById(R.id.pauseView);
        pausedView.setVisibility(View.VISIBLE);
        btn_resume = findViewById(R.id.resumeButton);
        btn_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausedView.setVisibility(View.GONE);
                if(isDouble && isFirstPlayer){
                    TextView timeScore1 = findViewById(R.id.time_1);
                    timeScore1.setText("0");
                    TextView attemptScore1 = findViewById(R.id.attempts_1);
                    attemptScore1.setText("0");
                    TextView timeScore2 = findViewById(R.id.time_2);
                    timeScore2.setText("0");
                    TextView attemptScore2 = findViewById(R.id.attempts_2);
                    attemptScore2.setText("0");
                }
                startGame();
            }
        });
    }

    @Override
    public void onClick(View v){
        if (!isClickable) {
            return;
        }

        int id = v.getId();
        if(!matchedId.contains(id)) { //avoid first click on revealed img
            if(!isDefault) {
                setPicture(imgPaths, id);
            }else {
                setDefaultPicture(imgPaths, id);
            }
            if (isFirstClick) {
                attempts++;
                firstClickId = id;
                isFirstClick = false;
                firstClickedView = v;
            } else {
                if (id != firstClickId) { //avoid click on same img
                    if (imgPaths.get(id).equalsIgnoreCase(imgPaths.get(firstClickId))) {
                        isFirstClick = true;
                        matchedId.add(id);
                        matchedId.add(firstClickId);
                        matches++;
                        attempts++;
                        setMatchesText();
                        Animation emphasis = AnimationUtils.loadAnimation(this, R.anim.emphasis);
                        v.startAnimation(emphasis);
                        firstClickedView.startAnimation(emphasis);
                        if (matches == 6) {
                            pauseTimer();
                            if(isDouble){
                                setScore();
                                isFirstPlayer = !isFirstPlayer;
                                restart();
                            }
                        }
                    } else {
                        isClickable = false;
                        attempts++;
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            setBackground(id);
                            setBackground(firstClickId);
                            isFirstClick = true;
                            isClickable = true;
                        }, 400);
                    }
                }
            }
        }
        setAttemptsText();
    }

    private void initGame(){
        scoreTable = findViewById(R.id.scoreTable);
        turn = findViewById(R.id.turn);
        Intent intent = getIntent();
        isDefault = intent.getBooleanExtra("isDefault",false);
        if(!isDefault) {
            imgNames = intent.getStringArrayListExtra("imgList");
            imgPaths = getImgFilesDir(imgNames);//get the list on create because the list is random
        }else {
            imgPaths = getDefaultImgFilesDir();
        }
        isDouble = intent.getBooleanExtra("isDouble",false);
        if(!isDouble){
            scoreTable.setVisibility(View.GONE);
            turn.setVisibility(View.GONE);
        } else {
            scoreTable.setVisibility(View.VISIBLE);
            isFirstPlayer = true;
        }
        setImgHolders();
        matches = 0;
        setMatchesText();
        attempts = 0;
        setAttemptsText();
    }

    private void startGame(){
        if(!isDefault) {
            imgPaths = getImgFilesDir(imgNames);//get the list on create because the list is random
        }else {
            imgPaths = getDefaultImgFilesDir();
        }
        resetImgHolders();
        isFirstClick = true;
        matchedId = new ArrayList<>();
        matches = 0;
        setMatchesText();
        attempts = 0;
        setAttemptsText();
        initTimer();
        Button restartBtn = findViewById(R.id.reset_btn);
        restartBtn.setOnClickListener(v -> restart());
        Button backBtn = findViewById((R.id.back_btn));
        backBtn.setOnClickListener(v -> finish());
    }

    private void setImgHolders(){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        int index = 0;
        for (int i = 0; i < 4; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            for (int j = 0; j < 3; j++) {
                ImageView holder = new ImageView(this);
                holder.setImageResource(R.drawable.q_mark);
                holder.setPadding(8, 10, 8, 10);
                holder.setLayoutParams(params);

                holder.getLayoutParams().height = 338;
                holder.getLayoutParams().width = 338;
                holder.requestLayout();
                holder.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.setId(index++);
                holder.setOnClickListener(this);

                tr.addView(holder);
            }
            imgTable.addView(tr);
        }
    }

    private void resetImgHolders() {
        TableLayout imgTable = findViewById(R.id.game_img_table);
        for (int i = 0; i < imgTable.getChildCount(); i++) {
            View view = imgTable.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow tr = (TableRow) view;
                for (int j = 0; j < tr.getChildCount(); j++) {
                    View childView = tr.getChildAt(j);
                    if (childView instanceof ImageView) {
                        ImageView holder = (ImageView) childView;
                        holder.setImageResource(R.drawable.q_mark);
                    }
                }
            }
        }
    }

    private void setDefaultPicture(List<String> imgPath, int index){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        TableRow tr = (TableRow) imgTable.getChildAt(index / 3);
        ImageView iv = (ImageView) tr.getChildAt(index % 3);
        int id = getResources().getIdentifier(imgPath.get(index),"drawable",getPackageName());
        iv.setImageResource(id);
    }

    private void setPicture(List<String> imgPath,int index){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        TableRow tr = (TableRow) imgTable.getChildAt(index / 3);
        ImageView iv = (ImageView) tr.getChildAt(index % 3);
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File destFile = new File(externalFilesDir, imgPath.get(index));
        Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
        iv.setImageBitmap(bitmap);
    }

    private void setBackground(int index){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        TableRow tr = (TableRow) imgTable.getChildAt(index / 3);
        ImageView iv = (ImageView) tr.getChildAt(index % 3);
        iv.setImageResource(R.drawable.q_mark);
    }

    private List<String> getDefaultImgFilesDir(){
        List<String> originalName = Arrays.asList("t0", "t1", "t2", "t3", "t4", "t5");
        List<String> res = new ArrayList<>();
        res.addAll(originalName);
        res.addAll(originalName);
        Collections.shuffle(res);
        return res;
    }

    private List<String> getImgFilesDir(ArrayList<String> names){
        List<String> res = new ArrayList<>();
        res.addAll(names);
        res.addAll(names);
        Collections.shuffle(res);
        return res;
    }

    private void initTimer(){
        timerTextView = findViewById(R.id.timer);
        timerTextView.setText(String.format("%02d:%02d:%02d", 0, 0, 0));
        startTime = SystemClock.elapsedRealtime();
        handler.postDelayed(updateTimerRunnable, 1000);
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

    private void pauseTimer(){
        handler.removeCallbacks(updateTimerRunnable);
    }

    private void restart(){
        pauseTimer();
        pausedView.setVisibility(View.VISIBLE);
        if(isDouble && isFirstPlayer){
            TextView turn = findViewById(R.id.turn);
            turn.setText("Player 1's Turn");
            btn_resume.setText("Restart Combat");
        }
        if(isDouble && !isFirstPlayer){
            TextView turn = findViewById(R.id.turn);
            turn.setText("Player 2's Turn");
            btn_resume.setText(R.string.start);
        }
    }

    private void setMatchesText(){
        TextView matches = findViewById(R.id.matches);
        matches.setText(String.format(getString(R.string.matches_text), this.matches));
    }

    private void setAttemptsText(){
        TextView attempts = findViewById(R.id.attempts);
        attempts.setText(String.format(getString(R.string.attempts), this.attempts));
    }

    private void setScore(){
        if(isFirstPlayer){
            TextView timeScore = findViewById(R.id.time_1);
            timeScore.setText(timerTextView.getText());
            TextView attemptScore = findViewById(R.id.attempts_1);
            TextView attempts = findViewById(R.id.attempts);
            attemptScore.setText(attempts.getText());
        } else{
            TextView timeScore = findViewById(R.id.time_2);
            timeScore.setText(timerTextView.getText());
            TextView attemptScore = findViewById(R.id.attempts_2);
            TextView attempts = findViewById(R.id.attempts);
            attemptScore.setText(attempts.getText());
        }
    }
}