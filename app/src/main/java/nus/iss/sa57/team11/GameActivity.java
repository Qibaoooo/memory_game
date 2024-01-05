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
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    //private File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    private boolean isFirstClick = true;
    int firstClickId = 99;
    List<Integer> pairedValues = new ArrayList<>();
    int pairedCount;
    List<String> imgPaths;
    private TextView timerTextView;
    private long startTime;
    private final Handler handler = new Handler();
    int attempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        imgPaths = getImgFilesDir();
        setImgHolders();
        pairedCount = 0;
        attempt = 0;
        setMatchesText();
        setAttemptsText();
        timerTextView = findViewById(R.id.timer);
        startTime = SystemClock.elapsedRealtime();
        handler.postDelayed(updateTimerRunnable, 1000);
        Button restartBtn = findViewById(R.id.reset111);
        restartBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                restart();
            }
        });
        //setPictures();
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(!pairedValues.contains(id)) {
            if (isFirstClick) {
                setPicture(imgPaths, id);
                attempt++;
                firstClickId = id;
                isFirstClick = false;
            } else {
                setPicture(imgPaths, id);
                if (id == firstClickId) {
                    //do nothing
                } else if (imgPaths.get(id).equalsIgnoreCase(imgPaths.get(firstClickId))) {
                    isFirstClick = true;
                    pairedValues.add(id);
                    pairedValues.add(firstClickId);
                    pairedCount++;
                    attempt++;
                    setMatchesText();
                    if(pairedCount == 6){
                        pauseTimer();
                    }
                } else {
                    attempt++;
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        setBackground(id);
                        setBackground(firstClickId);
                        isFirstClick = true;
                    }, 400);
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

    /*private void resetTimer() {
        // 重置计时器
        startTime = SystemClock.elapsedRealtime();
        timerTextView.setText("00:00:00");
    }*/

    private void pauseTimer(){
        handler.removeCallbacks(updateTimerRunnable);
    }

    private void restart(){
        finish();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void setMatchesText(){
        TextView matches = findViewById(R.id.matches);
        matches.setText(String.format(getString(R.string.matches_text), pairedCount));
    }

    private void setAttemptsText(){
        TextView attempts = findViewById(R.id.attempts);
        attempts.setText(String.format(getString(R.string.attempts), attempt));
    }

    /*private void setPictures(List<String> imgPath) {
        TableLayout imgTable = findViewById(R.id.game_img_table);
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                TableRow tr = (TableRow) imgTable.getChildAt(i);
                ImageView iv = (ImageView) tr.getChildAt(j);
                //Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
                //iv.setImageBitmap(bitmap);
                int id = this.getResources().getIdentifier(imgPath.get(index++),"drawable",this.getPackageName());
                iv.setImageResource(id);
            }
        }
    }*/
}