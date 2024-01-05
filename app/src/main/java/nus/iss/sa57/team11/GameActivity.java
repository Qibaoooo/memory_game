package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

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
    List<String> imgPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        imgPaths = getImgFilesDir();
        setImgHolders();
        //setPictures();
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(!pairedValues.contains(id)) {
            if (isFirstClick) {
                setPicture(imgPaths, id);
                firstClickId = id;
                isFirstClick = false;
            } else {
                setPicture(imgPaths, id);
                if (id == firstClickId) {
                    return;
                } else if (imgPaths.get(id).equalsIgnoreCase(imgPaths.get(firstClickId))) {
                    isFirstClick = true;
                    pairedValues.add(id);
                    pairedValues.add(firstClickId);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setBackground(id);
                            setBackground(firstClickId);
                            isFirstClick = true;
                        }
                    }, 400);
                }
            }
        }
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
        int id = this.getResources().getIdentifier(imgPath.get(index),"drawable",this.getPackageName());
        iv.setImageResource(id);
    }

    private void setBackground(int index){
        TableLayout imgTable = findViewById(R.id.game_img_table);
        TableRow tr = (TableRow) imgTable.getChildAt(index / 3);
        ImageView iv = (ImageView) tr.getChildAt(index % 3);
        iv.setImageResource(R.drawable.q_mark);
    }

    private void setPictures(List<String> imgPath) {
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
    }

    private List<String> getImgFilesDir(){
        //String imgPath = "app/src/main/res/drawable";
        //String[] imgList = new String[]{"t0", "t1", "t2", "t3", "t4", "t5"};
        //List<String> modifiedimgList = Arrays.stream(imgList)
        //        .map(e -> imgPath + e + ".png")
        //        .collect(Collectors.toList());
        List<String> originalName = Arrays.asList("t0", "t1", "t2", "t3", "t4", "t5");
        List<String> res = new ArrayList<>();
        res.addAll(originalName);
        res.addAll(originalName);
        Collections.shuffle(res);

        return res;
    }
}