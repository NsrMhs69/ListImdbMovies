package com.batman.project.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.batman.project.Apps;
import com.batman.project.R;
import com.batman.project.activities.DetailsActivity;
import com.batman.project.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.SecondCollectionViewHolder> {
    private List<Movie> movieResponses;
    private Context context;


    public AdapterMain(Context context, List<Movie> movieResponses) {
        this.context = context;
        this.movieResponses = movieResponses;
    }

    @Override
    public SecondCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View secondCollectionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new SecondCollectionViewHolder(secondCollectionView);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(final SecondCollectionViewHolder holder, final int position) {
        final Movie movie = movieResponses.get(position);
        holder.nameMovie.setText(movie.getTitle());
        holder.yearMovie.setText(movie.getYear());
        holder.typeMovie.setText(movie.getType());

        holder.nameMovie.setTypeface(Apps.font);
        holder.yearMovie.setTypeface(Apps.font);
        holder.typeMovie.setTypeface(Apps.font);

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("idImdb", movie.getImdbID());
                intent.putExtra("pic", movie.getPoster());
                intent.putExtra("id", position);
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("year", movie.getYear());
                context.startActivity(intent);

            }
        });
        try {
            String filename = getFileName(Uri.parse(movie.getPoster()));
            ContextWrapper cw = new ContextWrapper(context);
            final File directory = cw.getDir("mydir", Context.MODE_PRIVATE);
            final File myImageFile = new File(directory, filename); // Create image file

            String url = movie.getPoster();
            if (url != null && !url.isEmpty()) {

                Picasso.with(context)
                        .load(url)
                        .fit()
                        .centerCrop()
                        .into(holder.pic, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progress.setVisibility(View.GONE);
                                if (!myImageFile.exists()){
                                    BitmapDrawable draw = (BitmapDrawable) holder.pic.getDrawable();
                                    Bitmap bitmap = draw.getBitmap();

                                    FileOutputStream outStream = null;
                                    File outFile = new File(myImageFile.getAbsolutePath());
                                    try {
                                        outStream = new FileOutputStream(outFile);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                                    try {
                                        outStream.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        outStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                            @Override
                            public void onError() {
                                holder.progress.setVisibility(View.GONE);
                            }
                        });
            } else {
                holder.progress.setVisibility(View.INVISIBLE);
                holder.pic.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_launcher));
            }
        } catch (Exception e) {
        }

    }

    @Override
    public int getItemCount() {
        return movieResponses.size();
    }

    public class SecondCollectionViewHolder extends RecyclerView.ViewHolder {
        public TextView nameMovie,yearMovie,typeMovie;
        public RelativeLayout row;
        public ImageView pic;
        public ProgressBar progress;


        public SecondCollectionViewHolder(View itemView) {
            super(itemView);


            row = itemView.findViewById(R.id.row);
            pic =  itemView.findViewById(R.id.pic);
            nameMovie = itemView.findViewById(R.id.name_movie);
            yearMovie = itemView.findViewById(R.id.year_movie);
            typeMovie = itemView.findViewById(R.id.type_movie);
            progress = itemView.findViewById(R.id.progress);


        }
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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

}
