package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity {
    //private File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        setPictures(getImgFilesDir());
    }
    //going to work on game activity

    private void setPictures(List<String> imgPath) {
        TableLayout imgTable = findViewById(R.id.game_img_table);
        for (int i = 0; i < 3; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
            for (int j = 0; j < 2; j++) {
                ImageView holder = new ImageView(this);
                holder.setImageResource(R.drawable.ic_launcher_background);
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                holder.setLayoutParams(params);

                holder.getLayoutParams().height = 400; //can change the size according to you requirements
                holder.requestLayout();
                holder.setScaleType(ImageView.ScaleType.FIT_XY);

                tr.addView(holder);
            }
            imgTable.addView(tr);
        }
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
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
        return Arrays.asList("t0", "t1", "t2", "t3", "t4", "t5");
    }
}