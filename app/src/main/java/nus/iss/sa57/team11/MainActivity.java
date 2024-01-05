package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    private Button fetch_btn;
    private Button game_btn;
    private final String DEFAULT_URL = "https://www.wallpaperbetter.com/es/search?q=birds";
    //TODO: find a better website or add handling for DUPLICATED images!
    private List<String> allImgUrls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFetchButton();
        setupURLEditText();
        setupImagePlaceholders();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.fetch_btn) {
            EditText et = findViewById(R.id.edit_url);
            String URLString = String.valueOf(et.getText());
            startDownloadImage(URLString);
        }
        else if(id == R.id.game_btn){
            Intent intent = new Intent(this,GameActivity.class);
            startActivity(intent);
        }
    }

    private void setupFetchButton() {
        fetch_btn = findViewById(R.id.fetch_btn);
        fetch_btn.setOnClickListener(this);
        game_btn = findViewById(R.id.game_btn);
        game_btn.setOnClickListener(this);
    }

    private void setupURLEditText() {
        EditText et = findViewById(R.id.edit_url);
        et.setText(DEFAULT_URL);
    }

    private void setupImagePlaceholders() {
        /*
         * Init the 20 images place holders
         * */
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
    }

    protected void startDownloadImage(String imgURL) {
        // progress bar to zero
        ProgressBar pb = findViewById(R.id.download_bar);
        pb.setProgress(0);
        // clean folder
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = externalFilesDir.listFiles();
        if (files != null) {
            for (File f : files
            ) {
                f.delete();
            }
        }
        /*
         * ### Downloading Start ###
         * Here I used a AsyncTask task to do the downloading, because
         * we can't open up internet connections on the UI thread directly.
         * This is the stackoverflow answer I referred to:
         * https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
         * */
        GetIndividualImageUrlsTask task = new GetIndividualImageUrlsTask();
        task.delegate = this;
        task.execute(imgURL);
    }

    @Override
    public void processFinish(List<String> output) {
        /*
         * Save the output (a list of img urls) to the class variable allImgUrls.
         * For now it has no usage yet but later we might need it.
         * */
        allImgUrls = output;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetIndividualImageUrlsTask extends AsyncTask<String, Void, List<String>> {
        public AsyncResponse delegate = null;

        @Override
        protected List<String> doInBackground(String... urls) {
            ImageDownloader imgDL = new ImageDownloader();
            return imgDL.getIndividualImageUrls(urls[0]);
        }

        @Override
        protected void onPostExecute(List<String> result) {
            delegate.processFinish(result);
            File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            /*
             * Here we have gotten 20 img urls.
             * Now we loop around the 20 img, after downloading each one,
             * we update UI.
             * The 20 downloads are executed in parallel.
             * */
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    downloadImageAndUpdateUI(externalFilesDir, result, i, j);
                }
            }
        }

    }

    private void downloadImageAndUpdateUI(File externalFilesDir, List<String> allImageUrls, int i, int j) {
        ImageDownloader imgDL = new ImageDownloader();
        String url = allImageUrls.get(i * 4 + j);
        new Thread(() -> {
            File destFile = new File(externalFilesDir, url.substring(url.lastIndexOf('/') + 1));
            TableLayout imgTable = findViewById(R.id.img_table);
            if (imgDL.downloadImage(url, destFile)) {
                runOnUiThread(() -> {
                    TableRow tr = (TableRow) imgTable.getChildAt(i);
                    ImageView iv = (ImageView) tr.getChildAt(j);
                    Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
                    iv.setImageBitmap(bitmap);
                    // progress bar update
                    ProgressBar pb = findViewById(R.id.download_bar);
                    pb.incrementProgressBy(100/20); // 20 total imgs
                });
            }
        }).start();
    }

}