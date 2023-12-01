package com.example.assignment6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SketchTagger extends AppCompatActivity {
    private final String API_KEY = "AIzaSyDrUEnTRfv6M5dFBNQK5V0b2ocnmBh2Cag";
    ArrayList<SavedItem> data2 = new ArrayList<>();
    ListView lv2;
    SavedListAdapter adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tagger_sketch);
//        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
//        mydb.execSQL("DELETE FROM INFO");
        // creating the list and adapter for main xml list
        adapter2 = new SavedListAdapter(this, R.layout.list_item, data2);
        lv2 = findViewById(R.id.mylist1);
        // setting listview to list_item view
        lv2.setAdapter(adapter2);

        // must be commented out on first start app bc db not created yet, must save 1 drawing
        showLatestDrawings();
    }

    public void Reset(View view){
        MyDrawingArea mcas = findViewById(R.id.cusview);
        mcas.clearDrawing();
    }

    String timeStamp = "";
    String tag = "";
    byte[] ba;
    public void saveDrawing_Tag(View view){
        // SAVING the tags to strTags var
//        EditText tags = findViewById(R.id.tags);
//        strTags = tags.getText().toString();

        // SAVING the drawing image to ba var
        MyDrawingArea mcas = findViewById(R.id.cusview);
        Bitmap b = mcas.getBitmap(); //we wrote this function inside custom view
        // convert Bitmap to blob (byte[])
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        ba = stream.toByteArray();
        // SAVING timestamp
        timeStamp = new SimpleDateFormat("MMM d, yyyy - ha",
                Locale.US).format(new Date());

        TextView txt = findViewById(R.id.tags);
        // sets tags to "tags" output from vision tester...when did I call vision tester
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // initializes vision tags var
                    // converts byte[] to bitmap, given to vision tester
                    Bitmap b = BitmapFactory.decodeByteArray(ba, 0, ba.length);
                    myVisionTester(b);

                    // Now we need to update the UI with the results obtained from the worker thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txt.setText(tag);
                        }
                    });

                } catch (IOException e) {
                    Log.e("vision", e.toString());
                }
            }
        });
        worker.start();
        try {
            // Make the current thread wait until the worker thread finishes
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        // creating the db with our new values
        Log.v("tag equals before db work", tag);
        databaseWork();
        databaseQueryRetRecent3();
    }

    // find dates & tags for each image upon creation :/
    private void showLatestDrawings() {
        databaseQueryRetRecent3();
    }
    String findTags = "";
    ArrayList<Bitmap> images = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();
    // will have to change to include tag return
    void databaseQueryRetRecent3(){
        images = new ArrayList<>();
        dates = new ArrayList<>();
        tags = new ArrayList<>();
        data2.clear();
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
        Cursor c = mydb.rawQuery("SELECT * FROM DRAW ORDER BY date DESC LIMIT 3", null);
        // can't directly compare blob to byte[]
        c.moveToFirst();
        for (int i = 0; i<c.getCount(); i++) {
            byte[] blob = c.getBlob(0);
            Bitmap b = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            // adding values to arrays
            images.add(b);
            dates.add(c.getString(1));
            tags.add(c.getString(2));
            c.moveToNext();
        }
        for(int i = 0; i<images.size(); i++){
            data2.add(new SavedItem(images.get(i), dates.get(i), tags.get(i)));
        }
        // NEW text unavailable implementation
        if(images.size() == 0){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        if(images.size() == 1){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        if(images.size() == 2){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        adapter2.notifyDataSetChanged();
    }
    // creating db conditionally - inserting 1 row
    void databaseWork(){
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
//        mydb.execSQL("DELETE FROM INFO");
//        mydb.execSQL("DROP TABLE IF EXISTS DRAW");
        mydb.execSQL("CREATE TABLE IF NOT EXISTS DRAW(drawing BLOB PRIMARY KEY, date TEXT, tags TEXT)");
        // creating db like | BLOB | DATE/TIME | TAGS |
//        mydb.execSQL("CREATE TABLE INFO(pic BLOB PRIMARY KEY, date TEXT, tags TEXT)");
        ContentValues cv = new ContentValues();
        cv.put("drawing", ba);
        cv.put("date", timeStamp);
        Log.v("tag entry in db", tag);
        cv.put("tags", tag);
        mydb.insert("DRAW", null, cv);
    }
    public void findButton(View view){
        // storing the find tag
        EditText find = findViewById(R.id.find);
        findTags = find.getText().toString();
        if(findTags.equals("")){
            // querying for all
            databaseQueryAll(find);
        }
        else{
            // guessing it's fine to give it this parameter, never uses it
            databaseQuery(find);
        }
    }

    // Querying the database for all
    public void databaseQueryAll(View view){
        // resetting images, dates, & tags
        images = new ArrayList<>();
        dates = new ArrayList<>();
        tags = new ArrayList<>();
        data2.clear();
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
        Cursor c = mydb.rawQuery("SELECT * FROM DRAW ORDER BY date DESC", null);
        c.moveToFirst();
        for (int i = 0; i<c.getCount(); i++){
            // converting byte[] to bitmap
            byte[] blob = c.getBlob(0);
            Bitmap b = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            // adding values to arrays
            images.add(b);
            dates.add(c.getString(1));
            tags.add(c.getString(2));
            c.moveToNext();
        }
        for(int i = 0; i<images.size(); i++){
            data2.add(new SavedItem(images.get(i), dates.get(i), tags.get(i)));
        }
        // NEW text unavailable implementation
        if(images.size() == 0){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        if(images.size() == 1){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        if(images.size() == 2){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        adapter2.notifyDataSetChanged();
    }
    // Querying the database for tags
    public void databaseQuery(View view){
        // storing the find tag
        EditText find = findViewById(R.id.find);
        findTags = find.getText().toString();
        // resetting images, dates, & tags
        images = new ArrayList<>();
        dates = new ArrayList<>();
        tags = new ArrayList<>();
        data2.clear();
        // querying the db
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
        Cursor c = mydb.rawQuery("SELECT * FROM DRAW WHERE tags LIKE '%" + findTags + "%' ORDER BY date DESC LIMIT 3" , null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            // converting byte[] to bitmap
            byte[] blob = c.getBlob(0);
            Bitmap b = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            // adding values to arrays
            images.add(b);
            dates.add(c.getString(1));
            tags.add(c.getString(2));
            c.moveToNext();
        }
        for(int i = 0; i<images.size(); i++){
            data2.add(new SavedItem(images.get(i), dates.get(i), tags.get(i)));
        }
        // NEW text unavailable implementation
        if(images.size() == 0){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        if(images.size() == 1){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        if(images.size() == 2){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data2.add(new SavedItem(graybox, "", "Text Unavailable"));
        }
        adapter2.notifyDataSetChanged();
    }

    public void back(View view) {
        Intent b = new Intent(this, MainActivity.class);
        startActivity(b);
    }

    // NEW code for Vision

    public class SavedItem {
        Bitmap imageResource;
        String date;
        String tag;

        SavedItem(Bitmap imageResource, String date, String tags) {
            this.imageResource = imageResource;
            this.date = date;
            this.tag = tags;
        }
    }

    public class SavedListAdapter extends ArrayAdapter<SketchTagger.SavedItem> {

        SavedListAdapter(Context context, int resource, ArrayList<SketchTagger.SavedItem> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item, parent, false);
            }

            SketchTagger.SavedItem currentItem = getItem(position);

            // getting list_item's views
            ImageView Image = convertView.findViewById(R.id.savedimage);
            TextView Tags = convertView.findViewById(R.id.savedtags);
            TextView Date = convertView.findViewById(R.id.date);
            // setting list_item's views
            Image.setImageBitmap(currentItem.imageResource);
            Tags.setText(currentItem.tag);
            Date.setText(currentItem.date);

            return convertView;
        }

    }

    private void myVisionTester(Bitmap bitmap) throws IOException {
        //1. ENCODE image.
//        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(b)).getBitmap();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);
        Image myimage = new Image();
        myimage.encodeContent(bout.toByteArray());

        //2. PREPARE AnnotateImageRequest
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
        annotateImageRequest.setImage(myimage);
        Feature f = new Feature();
        f.setType("LABEL_DETECTION");
        f.setMaxResults(5);
        List<Feature> lf = new ArrayList<Feature>();
        lf.add(f);
        annotateImageRequest.setFeatures(lf);

        //3.BUILD the Vision
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(new VisionRequestInitializer(API_KEY));
        Vision vision = builder.build();

        //4. CALL Vision.Images.Annotate
        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        List<AnnotateImageRequest> list = new ArrayList<AnnotateImageRequest>();
        list.add(annotateImageRequest);
        batchAnnotateImagesRequest.setRequests(list);
        Vision.Images.Annotate task = vision.images().annotate(batchAnnotateImagesRequest);
        BatchAnnotateImagesResponse response = task.execute();
        Log.v("MYTAG", response.toPrettyString());
//        response.getResponses().get(0).getLabelAnnotations().get(0).getDescription();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        tag = "";
        for(int i = 0; i<labels.size(); i++){
            if(labels.get(i).getScore() > 0.85){
                if(tag.equals("")) {
                    tag = labels.get(i).getDescription();
                }
                else{
                    // ig this is fine
                    tag = tag + ", " + labels.get(i).getDescription();
                }
            }
        }
        if(tag.equals("")){
            tag = labels.get(0).getDescription();
        }
        Log.v("tag equals", tag);
    }
}
