package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button fetch_btn;
    private final String DEFAULT_URL = "https://stocksnap.io/";

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
                if (imgDL.downloadAllImages(imgURL, externalFilesDir)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            File[] files = externalFilesDir.listFiles();

                            Log.d("debug", "here");
                        }
                    });
                }
            }
        }).start();
    }

}