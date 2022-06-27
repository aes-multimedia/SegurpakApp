package com.multimedia.aes.gestnet_spak.Utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SORTJSONArray {

    public JSONArray SorteredArray;
    private Context context;

    public SORTJSONArray(Context context, JSONArray json, String keyStr) throws JSONException {

        this.context = context;
        SorteredArray = new JSONArray();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        for (int i = 0; i < json.length(); i++) {
            jsonList.add(json.getJSONObject(i));
        }
        Collections.sort( jsonList, new Comparator<JSONObject>() {

            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(keyStr);
                    valB = (String) b.get(keyStr);
                }
                catch (JSONException e) {
                    //do something
                }

                return valA.compareTo(valB);
            }
        });
        for (int i = 0; i < json.length(); i++) {
            SorteredArray.put(jsonList.get(i));
        }

    }
}

