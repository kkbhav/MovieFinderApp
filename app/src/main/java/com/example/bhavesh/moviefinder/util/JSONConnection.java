package com.example.bhavesh.moviefinder.util;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import java.util.Map;

public class JSONConnection<T>
{

	CustomJSONRequest<T> mRequest;
	
	public JSONConnection(
			final String url, 
			Class<T> clazz, 
			final Map<String, String> header,
			final onResponseListener<T> listener, 
			final onResponseCustomHeaderListener headerListener)
	{

		mRequest = new CustomJSONRequest<T>(url, clazz, header, new Listener<T>()
		{

			@Override
			public void onResponse(T response)
			{

				if (response != null)
				{
					listener.onConnSuccess(response, url, null);
					headerListener.getResponseHeaders(getHeaders());
					
				} else
				{
					listener.onConnFail("Failed to parse Request", url, null);
				}
			}
		}, new ErrorListener()
		{

			@Override
			public void onErrorResponse(VolleyError error)
			{

				listener.onConnFail("Failed to connect", url, null);

			}
		});
		mRequest.setTag(url);
		execute(mRequest);
	}
	
	public JSONConnection(final String url, final Object json, Class<T> clazz, final onResponseListener<T> listener)
	{

		CustomJSONRequest<T> mRequest = new CustomJSONRequest<T>(url, json, clazz, new Listener<T>()
		{

			@Override
			public void onResponse(T response)
			{

				if (response != null)
				{
					listener.onConnSuccess(response, url, json);
				} else
				{
					listener.onConnFail("Failed to parse Request", url, json);
				}
			}
		}, new ErrorListener()
		{

			@Override
			public void onErrorResponse(VolleyError error)
			{

				listener.onConnFail("Failed to connect", url, json);

			}
		});
		mRequest.setTag(url);
		execute(mRequest);
	}

	public JSONConnection(final String url, Class<T> clazz, final onResponseListener<T> listener)
	{

		CustomJSONRequest<T> mRequest = new CustomJSONRequest<T>(url, clazz, new Listener<T>()
		{

			@Override
			public void onResponse(T response)
			{

				if (response != null)
				{
					listener.onConnSuccess(response, url, null);
				} else
				{
					listener.onConnFail("Failed to parse Request", url, null);
				}
			}
		}, new ErrorListener()
		{

			@Override
			public void onErrorResponse(VolleyError error)
			{

				listener.onConnFail("Failed to connect", url, null);

			}
		});
		mRequest.setTag(url);
		execute(mRequest);
	}

	
	public Map<String, String> getHeaders(){
		return mRequest.getParsedHeaders();
	}
	
	public static void cancelRequest(String tag)
	{

		try
		{
			MainApplication.getRequestQueue().cancelAll(tag);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static interface onResponseListener<T>
	{

		public void onConnSuccess(T response, String url, Object originalRequest);

		public void onConnFail(String errorMsg, String url, Object originalRequest);

	}

	public static interface onResponseCustomHeaderListener {

		public void getResponseHeaders(Map<String, String> header);

	}

	
	private void execute(Request<T> request)
	{
		request.setShouldCache(false);
		request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MainApplication.getRequestQueue().add(request);
	}
}
