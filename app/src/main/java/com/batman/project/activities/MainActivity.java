package com.batman.project.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.batman.project.Apps;
import com.batman.project.R;
import com.batman.project.adapter.AdapterMain;
import com.batman.project.database.DbHelper;
import com.batman.project.models.Movie;
import com.batman.project.models.MovieResponse;
import com.batman.project.retrofit.ApiService;
import com.batman.project.retrofit.ApiUtils;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private ApiService mAPIService;
    private RecyclerView recyclerView;
    AdapterMain adapterMain;
    DbHelper dbHelper;
    File database;
    public  List<Movie> movies = new ArrayList<Movie>();
    private TextView appTitle;
    ProgressBar progressBar;
    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);




        mAPIService = ApiUtils.getAPIService();

        recyclerView = findViewById(R.id.recycleview);
        appTitle = findViewById(R.id.app_title);
        progressBar = (ProgressBar) findViewById(R.id.spin_kit);

        appTitle.setTypeface(Apps.font);


        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        progressBar.setVisibility(View.VISIBLE);

        // database
        dbHelper = new DbHelper(MainActivity.this);
        database = getDatabasePath(DbHelper.DB_NAME);

        if (database.exists()) {
            progressBar.setVisibility(View.GONE);
            movies = dbHelper.getMovieList();
            LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            adapterMain = new AdapterMain(MainActivity.this, movies);
            recyclerView.setAdapter(adapterMain);
        } else {
            if (isOnline()) {
                loginRetrofitFlag();
            } else {
                progressBar.setVisibility(View.GONE);
                //show dilaog
                showExitDialog(MainActivity.this);
            }

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void loginRetrofitFlag() {

        Call<MovieResponse> call1 = mAPIService.getBatmanMovie();
        call1.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                MovieResponse loginResponse = response.body();


                if (loginResponse != null) {
                    for (int i = 0; i < loginResponse.getSearch().size(); i++) {
                        //insert data into db
                        Log.e("test", "loginResponse 1 --> " + loginResponse.getSearch().get(i).getTitle());
                        dbHelper.insertData(i, loginResponse.getSearch().get(i).getImdbID(), loginResponse.getSearch().get(i).getTitle(), loginResponse.getSearch().get(i).getYear(), loginResponse.getSearch().get(i).getType(), loginResponse.getSearch().get(i).getPoster());
                    }

                }

                LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(manager);
                adapterMain = new AdapterMain(MainActivity.this, loginResponse.getSearch());
                recyclerView.setAdapter(adapterMain);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showExitDialog(MainActivity.this);
                call.cancel();
            }
        });
    }
    private Dialog showExitDialog(MainActivity content) {
        final Dialog dialog = new Dialog(content);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.no_internet_dialog);
        dialog.setCancelable(true);
        final TextView desc = (TextView) dialog.findViewById(R.id.desc);

        final TextView ok = (TextView) dialog.findViewById(R.id.ok);
        desc.setTypeface(Apps.font);
        ok.setTypeface(Apps.font);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.this.finish();
                dialog.dismiss();


            }
        });

        dialog.show();
        return dialog;
    }

}
