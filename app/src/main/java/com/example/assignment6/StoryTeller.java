package com.example.assignment6;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class StoryTeller extends AppCompatActivity {
    String url = "https://api.textcortex.com/v1/texts/social-media-posts";
    String API_KEY = "gAAAAABlTUN4LXTuSUUWaPXiOyqsgVxqQYTBcZy3xTWfi_mxvOHZUTBClrjdn4V-ST6An5ABNy1SIqY4AguqZQrxG_Ju08EAC0mz7Hfz36y0LpD71Cjbod28A6TViOkKo8JjrdeIx4gJ";
    ArrayList<CheckBox> list_boxes = new ArrayList<>();
    ArrayList<SavedItem> data3 = new ArrayList<>();
    ListView lv2;
    SavedListAdapter adapter2;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teller_story);
        databaseWorkS();
        adapter2 = new SavedListAdapter(this, R.layout.story_list_item, data3);
        lv2 = findViewById(R.id.mylist);
        // setting listview to list_item view
        lv2.setAdapter(adapter2);
        // default sketch checked
        CheckBox box = findViewById(R.id.check);
        box.setChecked(true);
        // initializing list items
        EditText find = findViewById(R.id.find);
        databaseQueryAll(find);
    }

    // checkbox sketches functionality
    public void Check(View view){
        CheckBox box = findViewById(R.id.check);
        EditText find = findViewById(R.id.find);
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle checkbox click
                boolean isChecked = ((CheckBox) view).isChecked();
                if(isChecked){
                    databaseWorkS();
                    databaseQueryAll(find);
                }
                else{
                    databaseWorknoS();
                    databaseQueryAll(find);
                }
            }
        });
    }
    ArrayList<String> selected = new ArrayList<>();
//    public void List_Check(View view) {
//        // adding tags to You selected:
////        R.layout.story_list_item
////        CheckBox Box = findViewById(R.id.checkbox);
//
////        // Set a listener for checkbox changes
////        for (int i = 0; i < lv2.getChildCount(); i++) {
////            View v = lv2.getChildAt(i);
////            CheckBox checkBox = v.findViewById(R.id.checkbox);
////              Log.v("data3 size equals", String.valueOf(data3.size()));
////            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                @Override
////                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                    SavedItem item = (SavedItem) buttonView.getTag();
////                    if (isChecked) {
////                        // Checkbox is checked, add text to TextView
////                        Log.v("is checked", "works");
////                        String curr = (String) Text.getText();
////                        Text.setText(curr + " " + item.tag);
////                    } else {
////                        Log.v("NOT checked", "works");
////                        // Checkbox is unchecked, handle accordingly
////                    }
////                }
////            });
////
//        Log.v("list_check", "is running");
//        TextView Text = findViewById(R.id.selection);
//        CheckBox box = findViewById(R.id.checkbox);
////        for(SavedItem item : data3){
////            Log.v("inside", "loop");
////            final CheckBox box = item.box;
//        box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Handle checkbox click
//                boolean isChecked = ((CheckBox) view).isChecked();
//                if (isChecked) {
//                    Log.v("is checked", "works");
//                    // get tags
//                    TextView txt = findViewById(R.id.savedtags);
//                    // add tags to arrl to be output
//                    selected.add(txt.getText().toString());
//                    String s = selected.toString();
//                    s = s.replaceAll("\\[|\\]", "");
//                    // set selected: to arr: str
//                    Text.setText("you selected: " + s);
////                    String curr = (String) Text.getText();
////                    TextView txt = findViewById(R.id.savedtags);
////                    Text.setText(curr + " " + txt.getText());
//                } else {
//                    Log.v("NOT checked", "works");
//                    // get tags
//                    TextView txt = findViewById(R.id.savedtags);
//                    // remove tags to arrl to be output
//                    selected.remove(txt.getText().toString());
//                    String s = selected.toString();
//                    s = s.replaceAll("\\[|\\]", "");
//                    // set selected: to arr: str
//                    Text.setText("you selected: " + s);
//                }
//            }
//        });
//    }
//            });
//
//
//            box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    // Your code to handle checkbox state change
//                    if (isChecked) {
//                        Log.v("is checked", "works");
//                        String curr = (String) Text.getText();
//                        Text.setText(curr + " " + item.tag);
//                    } else {
//                        Log.v("NOT checked", "works");
//                        // remove tag from selected
//                    }
//                }
//            });
//            if(item.box.isChecked()) {
//
//                String curr = (String) Text.getText();
//                Text.setText(curr + " " + item.tag);
//            }
//        }
//        if(Box.isChecked()){
//

    // public db columns
    String findTags = "";
    ArrayList<Bitmap> images = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();
    public void back(View view) {
        Intent b = new Intent(this, MainActivity.class);
        startActivity(b);
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

    private void databaseQuery(EditText find) {
        findTags = find.getText().toString();
        // resetting images, dates, & tags
        images = new ArrayList<>();
        dates = new ArrayList<>();
        tags = new ArrayList<>();
        data3.clear();
        // querying the db -- hopefully works the same with arraylist to str conversion for db entry
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
        Cursor c = mydb.rawQuery("SELECT * FROM STORY WHERE combined_tags LIKE '%" + findTags + "%' ORDER BY combined_date DESC" , null);
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
        Log.v("count", String.valueOf(c.getCount()));
        // putting new info into images & texts
        for(int i = 0; i<images.size(); i++){
            data3.add(new SavedItem(images.get(i), dates.get(i), tags.get(i), false));
        }
        // NEW text unavailable implementation
        if(images.size() == 0){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
        }
        if(images.size() == 1){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
        }
        if(images.size() == 2){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
        }
        adapter2.notifyDataSetChanged();
    }

    private void databaseQueryAll(EditText find) {
        // resetting images, dates, & tags
        images = new ArrayList<>();
        dates = new ArrayList<>();
        tags = new ArrayList<>();
        data3.clear();
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);

        Cursor c = mydb.rawQuery("SELECT * FROM STORY ORDER BY combined_date DESC", null);
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
        // putting new info into images & texts
        for(int i = 0; i<images.size(); i++){
            data3.add(new SavedItem(images.get(i), dates.get(i), tags.get(i), false));
        }
        // NEW text unavailable implementation
        if(images.size() == 0){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
        }
        if(images.size() == 1){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
        }
        if(images.size() == 2){
            Bitmap graybox = BitmapFactory.decodeResource(getResources(),R.drawable.graybox);
            data3.add(new SavedItem(graybox, "", "Text Unavailable", false));
        }
        adapter2.notifyDataSetChanged();
    }
    // create STORY table with sketches combined
    void databaseWorkS(){
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
//        mydb.execSQL("DELETE FROM INFO");
        mydb.execSQL("DROP TABLE IF EXISTS STORY");
        String createCombinedTableQuery = "CREATE TABLE IF NOT EXISTS STORY AS " +
                "SELECT pic AS combined_pic, date AS combined_date, tags AS combined_tags FROM INFO " +
                "UNION " +
                "SELECT drawing AS combined_pic, date AS combined_date, tags AS combined_tags FROM DRAW;";

        mydb.execSQL(createCombinedTableQuery);

    }
    // create STORY table with NO sketches
    void databaseWorknoS(){
        SQLiteDatabase mydb = this.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);
//        mydb.execSQL("DELETE FROM INFO");
        mydb.execSQL("DROP TABLE IF EXISTS STORY");
        String createCombinedTableQuery = "CREATE TABLE IF NOT EXISTS STORY AS " +
                "SELECT pic AS pic, date AS combined_date, tags AS combined_tags FROM INFO ";

        mydb.execSQL(createCombinedTableQuery);

    }


    //    {
//        "augment": null,
//            "context": "funny story",
//            "keywords": [
//        "phone", "dad"
//  ],
//        "max_tokens": 100,
//            "mode": "twitter",
//            "model": "chat-sophos-1",
//            "n": 1,
//            "source_lang": "en",
//            "target_lang": "en",
//            "temperature": 0.65
//    }
    void makeHTTPRequest() throws JSONException {
        JSONObject data = new JSONObject();
        // getting text from editText view
        String context = "funny story";
        data.put("context", context);
        data.put("max_tokens", "100");
        data.put("mode", "twitter");
        data.put("model", "chat-sophos-1");

        // get keywords from selected arraylist
        String s = selected.toString();
        s = s.replaceAll("\\[|\\]", "");
        String keywordsStr = s;
        // turn string into array
        String[] arrayOfStrings = keywordsStr.split(",");
        Log.v("keywords= ", Arrays.toString(arrayOfStrings));
        data.put("keywords", new JSONArray(arrayOfStrings));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("success", response.toString());
                TextView story = findViewById(R.id.story);
                try {
                    String storyOutput = response.getJSONObject("data")
                            .getJSONArray("outputs")
                            .getJSONObject(0)
                            .getString("text");
                    story.setText(storyOutput);
                    initializeTextToSpeech(story);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", new String (error.networkResponse.data));

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + API_KEY);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void submitHTTP(View view){
        try {
            makeHTTPRequest();
        } catch (JSONException e) {
            Log.e("error", e.toString());
        }
    }

    public class SavedItem {
        Bitmap imageResource;
        String date;
        String tag;
        boolean state;
//        CheckBox box;
        SavedItem(Bitmap imageResource, String date, String tags, boolean state) {
            this.imageResource = imageResource;
            this.date = date;
            this.tag = tags;
            this.state = state;
        }
    }

    public class SavedListAdapter extends ArrayAdapter<SavedItem> {

        SavedListAdapter(Context context, int resource, ArrayList<SavedItem> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.story_list_item, parent, false);
            }
            SavedItem currentItem = getItem(position);

            ImageView Image = convertView.findViewById(R.id.savedimage);
            TextView Tags = convertView.findViewById(R.id.savedtags);
            TextView Date = convertView.findViewById(R.id.date);
            CheckBox Box = convertView.findViewById(R.id.checkbox);

            // try again in adapter
            Log.v("list_check", "is running");
            TextView Text = findViewById(R.id.selection);
//            Box.setOnClickListener(view -> {
//                Checkbox cb = (Checkbox) view;
//                FoodItem currentItem = getItem(position);
//
//            });
            Box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle checkbox click
                    Log.i("box", currentItem.tag);
                    if (Box.isChecked()) {
                        // if click is being checked
                        if(selected.size() >= 3){
                            // add nothing, limit is 3
                        }
                        else {
                            Log.v("is checked", "works");
                            // add tags to arrl to be output
                            selected.add(Tags.getText().toString());
                            String s = selected.toString();
                            s = s.replaceAll("\\[|\\]", "");
                            // set selected: to str
                            Text.setText("you selected: " + s);
                        }
                    }
                    // if click is unchecking
                    else {
                        Log.v("NOT checked", "works");
                        // remove tags to arrl to be output
                        selected.remove(Tags.getText().toString());
                        String s = selected.toString();
                        s = s.replaceAll("\\[|\\]", "");
                        // set selected: to arr: str
                        Text.setText("you selected: " + s);
                    }
                }
            });

            Image.setImageBitmap(currentItem.imageResource);
            Tags.setText(currentItem.tag);
            Date.setText(currentItem.date);
            Box.setChecked(currentItem.state);
//            Box.setChecked(currentItem.box.isChecked());
            return convertView;
        }
    }

    public void initializeTextToSpeech(View view) {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
//                    tts.speak("This is a test", TextToSpeech.QUEUE_FLUSH, null, null);
                    TextView text = findViewById(R.id.story);
                    String story = String.valueOf(text.getText());
                    tts.speak(story, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }

}
