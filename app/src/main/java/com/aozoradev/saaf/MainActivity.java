package com.aozoradev.saaf;

import com.aozoradev.saaf.variables.Constant;
import com.aozoradev.saaf.variables.Static;
import com.aozoradev.saaf.utils.OSWUtil;
import com.aozoradev.saaf.utils.CheckUpdate;
import com.aozoradev.saaf.radioplayer.RadioPlayer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.anggrayudi.storage.file.DocumentFileUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Bundle;
import android.os.Build;
import android.os.Looper;
import android.os.Environment;
import android.graphics.drawable.ColorDrawable;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.net.Uri;
import android.app.Activity;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
  private Button button;
  private Menu mainMenu;
  private RecyclerView recyclerView;
  private ArrayList<Radio> radio;
  private AlertDialog backPressedDialog, loading, closeFile;
  private ExecutorService executor;
  private Handler handler;
  private DocumentFile df;
  private boolean canBack = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initialize(savedInstanceState);
    initializeLogic();
  }
  
  @Override
  protected void onPause() {
    RadioPlayer.pause(this);
    super.onPause();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    mainMenu = menu;
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    
    if (itemId == R.id.create_idx) {
      try {
        OSWUtil.createIDX(DocumentFileUtils.getAbsolutePath(df, this));
        Toast.makeText(this, df.getName() + ".idx created successfully!", Toast.LENGTH_LONG).show();
      } catch (Exception err) {
        printError(err);
      }
      return true;
    } else if (itemId == R.id.settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }
    
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if (canBack) {
      closeFile.show();
    } else {
      backPressedDialog.show();
    }
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    executor.shutdown();
    executor.shutdownNow();
    System.exit(0);
  }
  
  @SuppressLint("NotifyDataSetChanged")
  private void lmaoTheFileIsClosedBruhLmaoAmogusSussyBakaSusAmogusWhenTheImposterIsSusLmaoFortniteCard() {
    canBack = false;
    Static.zipFile = null;
    radio.clear();
    recyclerView.getAdapter().notifyItemRangeRemoved(0, radio.size());
    if (radio.size() > 0) {
      recyclerView.getAdapter().notifyDataSetChanged();
    }
    button.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
    getSupportActionBar().setSubtitle(null);
    mainMenu.findItem(R.id.create_idx).setEnabled(false).setVisible(false);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1000) {
      initializeLogic();
    }
    
    if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
      executor.execute(() -> {
        handler.post(() -> loading.show());
        
        Uri uri = data.getData();
        df = DocumentFile.fromSingleUri(getApplicationContext(), uri);
        String nodeName = df.getName();
        
        try {
          radio = Radio.createRadioList(this, uri, nodeName.replaceAll(".osw", ""));
        } catch (Exception err) {
          handler.post(() -> {
            if (err.getMessage() == null) {
              Toast.makeText(this, "Failed to load the file", Toast.LENGTH_LONG).show();
              loading.dismiss();
            } else {
              printError(err);
              loading.dismiss();
            }
          });
          return;
        }
        
        if (radio.isEmpty()) {
          handler.post(() -> {
            Toast.makeText(this, "This is not a GTASA STREAMS file", Toast.LENGTH_LONG).show();
            loading.dismiss();
          });
          return;
        }
        
        handler.post(() -> {
          RadioAdapter adapter = new RadioAdapter(radio);
          recyclerView.setAdapter(adapter);
          recyclerView.setLayoutManager(new LinearLayoutManager(this));
          recyclerView.setVisibility(View.VISIBLE);
          button.setVisibility(View.GONE);
          getSupportActionBar().setSubtitle(Static.station);
          canBack = true;
          closeFile = new MaterialAlertDialogBuilder(this)
          .setMessage("Do you want to close " + nodeName + "?")
          .setPositiveButton("Yes", (_which, _dialog) -> lmaoTheFileIsClosedBruhLmaoAmogusSussyBakaSusAmogusWhenTheImposterIsSusLmaoFortniteCard())
          .setNegativeButton("No", null)
          .create();
          mainMenu.findItem(R.id.create_idx).setEnabled(true).setVisible(true);
          loading.dismiss();
        });
      });
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 1000) {
      initializeLogic();
    }
  }
  
  private void printError (Throwable err) {
    Toast.makeText(this, "Error: " + err.getMessage(), Toast.LENGTH_LONG).show();
    err.printStackTrace();
  }
  
  // https://stackoverflow.com/a/66366102
  private boolean checkPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return Environment.isExternalStorageManager();
    } else {
      int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
      int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
      return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
  }
  
  // https://stackoverflow.com/a/66366102
  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      try {
        Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
        startActivityForResult(intent, 1000);
      } catch (Exception err) {
        printError(err);
      }
    } else {
      ActivityCompat.requestPermissions(this, Constant.permissions, 1000);
    }
  }

  private void initialize(Bundle _savedInstanceState) {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    button = (Button) findViewById(R.id.button);
    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    setSupportActionBar(toolbar);
    
    backPressedDialog = new MaterialAlertDialogBuilder(this)
    .setCancelable(true)
    .setMessage("Are you sure you want to close this app?")
    .setPositiveButton("YES", (_which, _dialog) -> finish())
    .setNegativeButton("NO", null)
    .create();
    
    loading = new AlertDialog.Builder(this)
    .setCancelable(false)
    .setView(View.inflate(this, R.layout.haha_custom_progress_bar_layout_go_bbrrrr, null))
    .create();
    loading.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    
    recyclerView.setHasFixedSize(true);
    recyclerView.setVisibility(View.GONE);
    
    executor = Executors.newSingleThreadExecutor();
    handler = new Handler(Looper.getMainLooper());
  }

  private void initializeLogic() {
    if (!checkPermission()) {
      new MaterialAlertDialogBuilder(this)
      .setCancelable(false).setMessage("This app requires storage access to work properly. Please grant storage permission.")
      .setPositiveButton("OK", (_dialog, _which) -> requestPermission()).show();
      return;
    }
    
    executor.execute(() -> {
      try {
        CheckUpdate cu = new CheckUpdate(this, Constant.updateURL);
        handler.post(() -> cu.check());
      } catch (Exception err) {
        err.printStackTrace();
      }
    });
    
    button.setVisibility(View.VISIBLE);
    button.setOnClickListener(v -> {
      Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      chooseFile.setType("*/*");
      chooseFile = Intent.createChooser(chooseFile, "Choose a file");
      startActivityForResult(chooseFile, 200);
    });
  }
}