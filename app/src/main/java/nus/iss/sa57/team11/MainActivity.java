package nus.iss.sa57.team11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button fetch_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFetchButton();
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

}