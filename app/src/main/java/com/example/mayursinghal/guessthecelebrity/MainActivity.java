package com.example.mayursinghal.guessthecelebrity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> CelebUrls = new ArrayList<String>();
    ArrayList<String> CelebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    int LocationOfCorrectAnswer=0;
    String[] answer=new String[4];
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    public void question() {
        Random random = new Random();
        chosenCeleb = random.nextInt(CelebUrls.size());

        DownLoadImage ImageTask = new DownLoadImage();
        Bitmap celebImage = null;
        try {
            celebImage = ImageTask.execute(CelebUrls.get(chosenCeleb)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(celebImage);
        LocationOfCorrectAnswer = random.nextInt(4);
        int IncorrectAnswerLocation = 0;

        for (int i = 0; i < 4; i++) {
            if (LocationOfCorrectAnswer == i) {
                answer[i] = CelebNames.get(chosenCeleb);
            } else {
                IncorrectAnswerLocation = random.nextInt(CelebNames.size());
                while (IncorrectAnswerLocation ==chosenCeleb) {
                    IncorrectAnswerLocation = random.nextInt(CelebNames.size());
                }
                answer[i] = CelebNames.get(IncorrectAnswerLocation);
            }
            button1.setText(answer[0]);
            button2.setText(answer[1]);
            button3.setText(answer[2]);
            button4.setText(answer[3]);
            button1.setBackgroundColor(Color.LTGRAY);
            button2.setBackgroundColor(Color.LTGRAY);
            button3.setBackgroundColor(Color.LTGRAY);
            button4.setBackgroundColor(Color.LTGRAY);
        }
    }

    public void nextQuestion(View view)
    {
        question();
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
    }

    public void celebChosen(View view){


        if(view.getTag().toString().equals(Integer.toString(LocationOfCorrectAnswer))){
            view.setBackgroundColor(Color.GREEN);
        }
        else {
            view.setBackgroundColor(Color.RED);
            if(button1.getTag().toString().equals(Integer.toString(LocationOfCorrectAnswer)))
            {
                button1.setBackgroundColor(Color.GREEN);
            }
            if(button2.getTag().toString().equals(Integer.toString(LocationOfCorrectAnswer)))
            {
                button2.setBackgroundColor(Color.GREEN);
            }
            if(button3.getTag().toString().equals(Integer.toString(LocationOfCorrectAnswer)))
            {
                button3.setBackgroundColor(Color.GREEN);
            }
            if(button4.getTag().toString().equals(Integer.toString(LocationOfCorrectAnswer)))
            {
                button4.setBackgroundColor(Color.GREEN);
            }

        }
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
    }



    public class DownLoadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

        public class DownloadTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                String result = "";
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(strings[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        result += current;
                        data = reader.read();
                    }
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            imageView=(ImageView)findViewById(R.id.imageView);
            button1=(Button)findViewById(R.id.button1);
            button2=(Button)findViewById(R.id.button2);
            button3=(Button)findViewById(R.id.button3);
            button4=(Button)findViewById(R.id.button4);

            DownloadTask task = new DownloadTask();
            try {
                String result = null;
                result = task.execute("http://www.posh24.se/kandisar").get();
                String[] resultSplit = result.split("<div class=\"sidebarContainer\">");
                Pattern p = Pattern.compile("<img src=\"(.*?)\"");
                Matcher m = p.matcher(resultSplit[0]);

                while (m.find()) {
                    CelebUrls.add(m.group(1));
                }

                p = Pattern.compile("alt=\"(.*?)\"");
                m = p.matcher(resultSplit[0]);

                while (m.find()) {
                    CelebNames.add(m.group(1));
                }
                question();
                }

             catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
}
