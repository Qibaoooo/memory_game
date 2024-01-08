package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    private Button fetch_btn;
    private Button game_btn;
    private Button default_game_btn;
    private Button double_game_btn;
    private final String DEFAULT_URL = "https://www.wallpaperbetter.com/es/search?q=tom";
    private List<String> allImgUrls;
    private List<ImageView> imageViews;
    private List<ImageView> selectedImageViews;
    private boolean isDefault = false;
    private boolean isDouble = false;
    private GameSound gameSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init fields
        this.imageViews = new ArrayList<ImageView>();
        this.selectedImageViews = new ArrayList<ImageView>();

        // init UI
        setupButtons();
        setupURLEditText();
        setupImagePlaceholders();

        gameSound = new GameSound();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fetch_btn) {
            onClickFetchButton();
        } else if (v.getId() == R.id.game_btn) {
            Intent intent = new Intent(this, GameActivity.class);
            if(!isDefault) {
                if (this.selectedImageViews.size() == 6) {
                    List<String> selected = new ArrayList<>();
                    for (ImageView i : selectedImageViews) {
                        selected.add("img-" + i.getId());
                    }
                    intent.putStringArrayListExtra("imgList", new ArrayList<>(selected));
                    intent.putExtra("isDouble", isDouble);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please select 6 images or start with default images!", Toast.LENGTH_SHORT).show();
                }
            }else {
                intent.putExtra("isDefault", true);
                intent.putExtra("isDouble", isDouble);
                gameSound.play(this, GameSounds.START);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.default_game_btn){
            if(!isDefault){
                isDefault = true;
                default_game_btn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_pressed));
            } else{
                isDefault = false;
                default_game_btn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_default));

            }
        } else if (v.getId() == R.id.double_game_btn) {
            if (!isDouble) {
                isDouble = true;
                double_game_btn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_pressed));
            } else {
                isDouble = false;
                double_game_btn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_default));
            }
        } else {
            onClickImage((ImageView) v);
        }
    }

    private void onClickImage(ImageView v) {
        if (this.selectedImageViews.contains(v)) {
            v.setBackgroundResource(R.color.gray);
            this.selectedImageViews.remove(v);
        } else {
            if (this.selectedImageViews.size() < 6) {
                v.setBackgroundResource(R.color.white);
                this.selectedImageViews.add(v);
                if (this.selectedImageViews.size() == 6){
                    Animation btn_emphasis = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.emphasis);
                    game_btn.startAnimation(btn_emphasis);
                }
            } else{
                Toast.makeText(this, "There are already 6 images, remove one or start game!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onClickFetchButton() {
        EditText et = findViewById(R.id.edit_url);
        String URLString = String.valueOf(et.getText());

        // reset all images
        this.selectedImageViews = new ArrayList<ImageView>();
        for (ImageView iv : this.imageViews
        ) {
            iv.setImageResource(R.drawable.q_mark);
            iv.setBackgroundResource(R.color.gray);
        }

        startDownloadImage(URLString);
    }

    private void setupButtons() {
        fetch_btn = findViewById(R.id.fetch_btn);
        fetch_btn.setOnClickListener(this);
        game_btn = findViewById(R.id.game_btn);
        game_btn.setOnClickListener(this);
        default_game_btn = findViewById(R.id.default_game_btn);
        default_game_btn.setOnClickListener(this);
        double_game_btn = findViewById((R.id.double_game_btn));
        double_game_btn.setOnClickListener(this);
    }

    private void setupURLEditText() {
        EditText et = findViewById(R.id.edit_url);
        et.setText(DEFAULT_URL);
    }

    private void setupImagePlaceholders() {
        /*
         * Init the 20 images place holders
         * */
        this.imageViews = new ArrayList<ImageView>();
        TableLayout imgTable = findViewById(R.id.img_table);
        for (int i = 0; i < 5; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            for (int j = 0; j < 4; j++) {
                ImageView holder = new ImageView(this);
                holder.setImageResource(R.drawable.q_mark);
                holder.setPadding(8, 10, 8, 10);
                holder.setLayoutParams(params);

                holder.getLayoutParams().height = 338;
                holder.getLayoutParams().width = 338;
                holder.requestLayout();
                holder.setScaleType(ImageView.ScaleType.FIT_XY);

                tr.addView(holder);
                this.imageViews.add(holder);
            }
            imgTable.addView(tr);
        }
    }

    protected void startDownloadImage(String imgURL) {
        // make images not clickable
        RemoveOnClickListenersForImages();
        // progress bar to zero
        ConstraintLayout progressLayout = findViewById(R.id.progress);
        progressLayout.setVisibility(View.VISIBLE);
        ProgressBar pb = findViewById(R.id.download_bar);
        pb.setProgress(0);
        TextView pt = findViewById(R.id.progress_text);
        pt.setText("0 of 20 images downloaded");//init progress text
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
        DownloadIndividualImagesTask task = new DownloadIndividualImagesTask();
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

    private class DownloadIndividualImagesTask extends AsyncTask<String, Void, List<String>> {
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

            /**
             * Make the images ready for selection
             */
            SetOnClickListenersForImages();
        }

    }

    private void SetOnClickListenersForImages() {
        for (ImageView iv : this.imageViews
        ) {
            iv.setOnClickListener(this);
        }
    }

    private void RemoveOnClickListenersForImages() {
        for (ImageView iv : this.imageViews
        ) {
            iv.setOnClickListener(null);
        }
    }

    private void downloadImageAndUpdateUI(File externalFilesDir, List<String> allImageUrls, int i, int j) {
        ImageDownloader imgDL = new ImageDownloader();
        String url = allImageUrls.get(i * 4 + j);
        new Thread(() -> {
            File destFile = new File(externalFilesDir, ("img-" + String.valueOf(i*4+j)));
            TableLayout imgTable = findViewById(R.id.img_table);
            if (imgDL.downloadImage(url, destFile)) {
                runOnUiThread(() -> {
                    TableRow tr = (TableRow) imgTable.getChildAt(i);
                    ImageView iv = (ImageView) tr.getChildAt(j);
                    Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
                    iv.setImageBitmap(bitmap);
                    iv.setId(i*4+j);
                    // progress bar update
                    ProgressBar pb = findViewById(R.id.download_bar);
                    pb.incrementProgressBy(100 / 20); // 20 total imgs
                    int progress = pb.getProgress()/5;
                    TextView pt = findViewById(R.id.progress_text);
                    pt.setText(progress + " of 20 images downloaded");
                });
            }
        }).start();
    }


}