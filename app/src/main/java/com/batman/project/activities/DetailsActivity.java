package com.batman.project.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.batman.project.R;
import com.batman.project.retrofit.ApiUtils;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.batman.project.Apps;
import com.batman.project.ImageHelper;
import com.batman.project.database.DbHelper;
import com.batman.project.models.Details;
import com.batman.project.retrofit.ApiService;
import com.batman.project.retrofit.ApiUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsActivity extends AppCompatActivity {

    private ApiService mAPIService;
    String idImdb, pic,title, year;
    int id;
    DbHelper dbHelper;
    File dbFiles;
    private ImageView poster;
    private TextView directorTitle,writerTitle,actorTitle,voteTitle,timeTitle,ageTitle,yearTitle;
    private TextView director,writer,actor,vote,time,age,date;
    private TextView name,released,rate;
    public List<Details> detailsList = new ArrayList<Details>();
    RelativeLayout main;
    public ProgressBar progress;
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        mAPIService = ApiUtils.getAPIService();

        // get intents from adapter
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        idImdb = intent.getStringExtra("idImdb");
        pic = intent.getStringExtra("pic");
        title = intent.getStringExtra("title");
        year = intent.getStringExtra("year");

        // init vies
        initViews();

        try {
            //get the images from directory
            ContextWrapper cw1 = new ContextWrapper(DetailsActivity.this);
            File directory1 = cw1.getDir("mydir", Context.MODE_PRIVATE);
            File myImageFile1 = new File(directory1, getFileName(Uri.parse(pic)));
            poster.setImageBitmap(ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeFile(myImageFile1.getAbsolutePath()),50));
        } catch (
                Exception e) {
        }


        dbHelper = new DbHelper(DetailsActivity.this);
        dbFiles = getDatabasePath(DbHelper.DB_NAME);
        detailsList = dbHelper.getDetail(idImdb);
        if (dbFiles.exists()) {
            if (dbHelper.rowIdExists(idImdb)) {

                name.setText(title);
                released.setText(detailsList.get(0).getReleased());
                rate.setText(detailsList.get(0).getImdbRating());
                date.setText(year);
                time.setText(detailsList.get(0).getRuntime());
                age.setText(detailsList.get(0).getRated());
                director.setText(detailsList.get(0).getDirector());
                writer.setText(detailsList.get(0).getWriter());
                actor.setText(detailsList.get(0).getActors());
                vote.setText(detailsList.get(0).getImdbVotes());
                progress.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);

            } else {
                if (isOnline()) {
                    loginRetrofitFlag();
                } else {

                    //show dialog
                    showExitDialog(DetailsActivity.this);
                    progress.setVisibility(View.GONE);
                }
            }
        } else {
            if (isOnline()) {
                loginRetrofitFlag();
            } else {
                progress.setVisibility(View.GONE);
                showExitDialog(DetailsActivity.this);
            }

        }

    }

    private void loginRetrofitFlag() {

        Call<Details> call1 = mAPIService.getDetails(idImdb);
        call1.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(Call<Details> call, Response<Details> response) {
                Details details = response.body();

                name.setText(details.getTitle());
                released.setText(details.getReleased());
                rate.setText(details.getImdbRating());
                date.setText(details.getYear());
                time.setText(details.getRuntime());
                age.setText(details.getRated());
                director.setText(details.getDirector());
                writer.setText(details.getWriter());
                actor.setText(details.getActors());
                vote.setText(details.getImdbVotes());

                //insert into database
                dbHelper.insertDataDetail(id, idImdb, details.getRated(), details.getReleased(), details.getRuntime(), details.getGenre(), details.getDirector(), details.getWriter(), details.getActors(),
                        details.getImdbRating(), details.getImdbVotes());
                progress.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<Details> call, Throwable t) {
                showExitDialog(DetailsActivity.this);
                call.cancel();
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    public void initViews(){

        progress = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        progress.setIndeterminateDrawable(circle);
        progress.setVisibility(View.VISIBLE);
        main = findViewById(R.id.main);
        poster = findViewById(R.id.poster);
        name = findViewById(R.id.name);
        released = findViewById(R.id.release);
        rate = findViewById(R.id.rate);
        date = findViewById(R.id.year);
        yearTitle = findViewById(R.id.year_title);
        time = findViewById(R.id.time);
        timeTitle = findViewById(R.id.time_title);
        age = findViewById(R.id.age);
        ageTitle = findViewById(R.id.age_title);
        director = findViewById(R.id.directors);
        directorTitle = findViewById(R.id.directors_title);
        writer = findViewById(R.id.writer);
        writerTitle = findViewById(R.id.writer_title);
        actor = findViewById(R.id.actors);
        actorTitle = findViewById(R.id.actors_title);
        vote = findViewById(R.id.vote);
        voteTitle = findViewById(R.id.vote_title);


        name.setTypeface(Apps.font);
        released.setTypeface(Apps.font);
        rate.setTypeface(Apps.font);
        yearTitle.setTypeface(Apps.font);
        date.setTypeface(Apps.font);
        time.setTypeface(Apps.font);
        timeTitle.setTypeface(Apps.font);
        age.setTypeface(Apps.font);
        ageTitle.setTypeface(Apps.font);
        director.setTypeface(Apps.font);
        directorTitle.setTypeface(Apps.font);
        writerTitle.setTypeface(Apps.font);
        writer.setTypeface(Apps.font);
        actorTitle.setTypeface(Apps.font);
        actor.setTypeface(Apps.font);
        vote.setTypeface(Apps.font);
        voteTitle.setTypeface(Apps.font);
    }
    private Dialog showExitDialog(DetailsActivity content) {
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

                DetailsActivity.this.finish();
                dialog.dismiss();


            }
        });

        dialog.show();
        return dialog;
    }
}
