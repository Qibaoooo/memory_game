package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button fetch_btn;
    private final String DEFAULT_URL = "https://www.wallpaperbetter.com/es/search?q=cat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFetchButton();
        setupImages();
    }

    @Override
    public void onClick(View v) {
        EditText et = findViewById(R.id.edit_url);
        String URLString = String.valueOf(et.getText());
        Log.d("debug", URLString);
    }

    private void setupFetchButton() {
        fetch_btn = findViewById(R.id.fetch_btn);
        fetch_btn.setOnClickListener(this);
    }

    private void setupImages() {
        EditText et = findViewById(R.id.edit_url);
        et.setText(DEFAULT_URL);

        TableLayout imgTable = findViewById(R.id.img_table);
        for (int i = 0; i < 5; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
            for (int j = 0; j < 4; j++) {
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

        startDownloadImage(DEFAULT_URL);
    }

    protected void startDownloadImage(String imgURL) {
        // creating a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                TableLayout imgTable = findViewById(R.id.img_table);
                ImageDownloader imgDL = new ImageDownloader();
                File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                for (File f:externalFilesDir.listFiles()
                     ) {
                    f.delete();
                }
                if (imgDL.downloadAllImages(imgURL, externalFilesDir)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            File[] files = externalFilesDir.listFiles();
                            for (int i = 0; i < 5; i++) {
                                for (int j = 0; j < 4; j++) {
                                    TableRow tr = (TableRow) imgTable.getChildAt(i);
                                    ImageView iv = (ImageView) tr.getChildAt(j);
                                    Bitmap bitmap = BitmapFactory.decodeFile(files[i * 4 + j].getAbsolutePath());
                                    iv.setImageBitmap(bitmap);
                                }
                            }
                            Log.d("debug", "here");
                        }
                    });
                }
            }
        }).start();
    }

}