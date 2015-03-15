package com.zousan.homesecurity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import JsonObject.Intruder;
import JsonObject.IntruderResponse;



public class MainActivity extends ActionBarActivity {

    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.intruder_list);

        List<Intruder> initList = new ArrayList<>();
        adapter = new ListAdapter(this, R.layout.intruder_item, initList);
        listView.setAdapter(adapter);

        request(getUrl());

    }

    private String getUrl(){
        return "https://roomsecurity.herokuapp.com/api/v1/intruders/list/";
    }

    private void request(String url){
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                            @Override
                            public JsonElement serialize(final Date date, final Type typeOfSrc,
                                                         final JsonSerializationContext context) {
                                return new JsonPrimitive(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
                                        .format(date));
                            }
                        })
                        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                            @Override
                            public Date deserialize(final JsonElement json, final Type typeOfT,
                                                    final JsonDeserializationContext context) throws JsonParseException {
                                try {
                                    return DateUtils.parseDate(json.getAsString(),
                                            new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSSSSS"});
                                } catch (final DateParseException exception) {
                                    throw new JsonParseException(exception);
                                }
                            }
                        })
                        .create();
                IntruderResponse result = gson.fromJson(response.toString(), IntruderResponse.class);
                List<Intruder> intruderList = new ArrayList<Intruder>();
                if(result.intruders!=null&&!result.intruders.isEmpty()){
                    for(Intruder intruder : result.intruders){
                        intruderList.add(intruder);
                    }
                }
                adapter.addAll(intruderList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ListAdapter extends ArrayAdapter<Intruder>{
        private int resourceId;
        private Context context;

        private class ViewHolder{
            TextView name;
            TextView timeStamp;
        }

        public ListAdapter(Context context, int resourceId, List<Intruder> objects){
            super(context, resourceId, objects);
            this.resourceId = resourceId;
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            Intruder intruder = getItem(position);
            ViewHolder holder;

            if(convertView==null){
                convertView = getLayoutInflater().inflate(resourceId, null);
                holder = new ViewHolder();
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.timeStamp = (TextView)convertView.findViewById(R.id.time_stamp);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.name.setText(intruder.name);
            holder.timeStamp.setText(Utils.timeConverter(intruder.timeStamp));

            return convertView;
        }
    }
}
