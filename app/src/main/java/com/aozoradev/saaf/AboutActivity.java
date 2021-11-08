package com.aozoradev.saaf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.os.Bundle;
import android.content.pm.ApplicationInfo;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    initialize(savedInstanceState);
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  private void initialize(Bundle _savedInstanceState) {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    StringBuilder sb = new StringBuilder();
    sb.append(getString(R.string.app_name));
    sb.append(" v" + BuildConfig.VERSION_NAME);
    sb.append(" (" + BuildConfig.VERSION_CODE + ")");
    sb.append("\nCreated by:");
    
    TextView textView = (TextView) findViewById(R.id.textView);
    textView.setText(sb.toString());
  }
}