package com.example.bhavesh.moviefinder.util;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T> JSON type of response expected
 */
public class CustomJSONRequest<T> extends Request<T>
{

	/** Charset for request. */
	private static final String PROTOCOL_CHARSET = "utf-8";

	/** Content type for request. */
	private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

	private final Listener<T> mListener;

	private final String mRequestBody;

	private Class<T> clazz;
	
	private Map<String, String> mRequestHeaders;

	private Map<String, String> mResponseHeader;
	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param url URL of the request to make
	 * @param clazz Relevant class object, for Gson's reflection
	 */
	public CustomJSONRequest(String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener)
	{

		super(Method.GET, url, errorListener);
		this.clazz = clazz;
		this.mListener = listener;
		mRequestBody = null;
		mRequestHeaders = new HashMap<String, String>();
	}
	
	public CustomJSONRequest(String url, Class<T> clazz, Map<String, String> header, Listener<T> listener, ErrorListener errorListener)
	{
		super(Method.GET, url, errorListener);
		this.clazz = clazz;
		this.mListener = listener;
		mRequestBody = null;
		mRequestHeaders = header;
	}
	

	public CustomJSONRequest(String url, Object requestObject, Class<T> clazz, Listener<T> listener, ErrorListener errorListener)
	{

		super(Method.POST, url, errorListener);
		this.clazz = clazz;
		this.mListener = listener;
		mRequestHeaders = new HashMap<String, String>();
		if (!(requestObject instanceof String))
		{
			mRequestBody = CoreGsonUtils.toJson(requestObject);
		} else
		{
			mRequestBody = (String) requestObject;
		}
	}

	@Override
	protected void deliverResponse(T response)
	{

		mListener.onResponse(response);
	}

	/**
	 * @deprecated Use {@link #getBodyContentType()}.
	 */
	@Override
	public String getPostBodyContentType()
	{

		return getBodyContentType();
	}

	/**
	 * @deprecated Use {@link #getBody()}.
	 */
	@Override
	public byte[] getPostBody()
	{

		return getBody();
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mRequestHeaders;
	}
	
	@Override
	public String getBodyContentType()
	{

		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody()
	{

		try
		{
			return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
		} catch (UnsupportedEncodingException uee)
		{
			VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, PROTOCOL_CHARSET);
			return null;
		}
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response)
	{

		try
		{
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			mResponseHeader = response.headers;
			if (clazz == String.class)
			{
				return Response.success(((T) jsonString), HttpHeaderParser.parseCacheHeaders(response));
			} else
			{
				return Response.success(CoreGsonUtils.fromJson(jsonString, clazz), HttpHeaderParser.parseCacheHeaders(response));
			}
		} catch (UnsupportedEncodingException e)
		{
			return Response.error(new ParseError(e));
		} catch (Exception je)
		{
			return Response.error(new ParseError(je));
		}
	}
	
	public Map<String, String> getParsedHeaders(){
		return (Map<String, String>)mResponseHeader;
	}
}