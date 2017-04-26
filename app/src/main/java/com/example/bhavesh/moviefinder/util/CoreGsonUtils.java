package com.example.bhavesh.moviefinder.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CoreGsonUtils
{

	private static Gson instance;

	private static Gson getGsonObject() {

		if (instance == null) {
			instance = new Gson();
		}
		return instance;

	}

	public static <T> T fromJson(String string, Class<T> model) {

		Gson gson = getGsonObject();
		T gfromat = null;
		try {
			gfromat = gson.fromJson(string, model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gfromat;
	}

	public static <T> T fromMap(Map<String, Object> map, Class<T> model) {
		Gson gson = getGsonObject();
		T gfromat = null;
		try {
			JsonElement jsonElement = gson.toJsonTree(map);
			gfromat = gson.fromJson(jsonElement, model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gfromat;
	}

	public static <T> String toJson(Object obj) {

		String gsonstr = "";

		Gson gson = getGsonObject();
		gsonstr = gson.toJson(obj);
		return gsonstr;
	}

	public static <T> ArrayList<T> toArrayList(String string, Class<T> model) {

		Gson gson = getGsonObject();
		T gfromat = null;
		ArrayList<T> localArrayList = new ArrayList<T>();
		try {

			JSONArray jsonInner = new JSONArray(string);
			int i = 0;
			while (i < jsonInner.length()) {
				gfromat = gson.fromJson(jsonInner.get(i).toString(), model);
				localArrayList.add(gfromat);
				i++;

			}

		} catch (Exception e)

		{
			e.printStackTrace();
		}

		return localArrayList;
	}

	public static Map<String, Object> toMap(Object obj) {
		String jsonString = toJson(obj);
		return toMap(jsonString);
	}

	public static Map<String, Object> toMap(String jsonString) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (!TextUtils.isEmpty(jsonString)) {
			try {
				JSONObject json = new JSONObject(jsonString);
				if (json != JSONObject.NULL) {
					retMap = toMapFromJsonObject(json);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return retMap;
	}

	private static Map<String, Object> toMapFromJsonObject(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toListFromJsonArray((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMapFromJsonObject((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	private static List<Object> toListFromJsonArray(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for(int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if(value instanceof JSONArray) {
				value = toListFromJsonArray((JSONArray) value);
			}

			else if(value instanceof JSONObject) {
				value = toMapFromJsonObject((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
}
