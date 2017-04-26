package com.example.bhavesh.moviefinder;

import android.content.Context;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhavesh.moviefinder.util.Constants;
import com.example.bhavesh.moviefinder.util.JSONConnection;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener
{
    ListView listView;
    View search;
    EditText title;
    RadioGroup searchType;

    SearchAdapter adapter;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        listView = (ListView) findViewById(R.id.listView);
        search = findViewById(R.id.search);
        search.setOnClickListener(this);
        title = (EditText) findViewById(R.id.title);
        searchType = (RadioGroup) findViewById(R.id.radioGroup);

        listView.setEmptyView(findViewById(R.id.emptyView));
        adapter = new SearchAdapter(this, new SearchListBean());
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                if (title.length() < 1) {
                    Toast.makeText(SearchActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
                } else {
                    if (adapter != null) {
                        adapter.emptyList();
                    }
                    imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
                    setApiParams();
                }
                break;
        }
    }

    private void setApiParams() {
        Toast.makeText(this, "Searching for entered String", Toast.LENGTH_SHORT).show();
        String movieTitle = title.getEditableText().toString();
        String[] movies = movieTitle.split(",");
        String searchType = getSearchType();
        if (searchType.equalsIgnoreCase("both")) {
            for (String string : movies) {
                getMovie(string.trim(), "movie");
                getMovie(string.trim(), "series");
            }
        } else {
            for (String string : movies) {
                getMovie(string.trim(), searchType);
            }
        }
    }

    private void getMovie(final String movie, String searchType) {
        StringBuilder url = new StringBuilder(Constants.API_URL);
        url.append("t=" + movie);
        url.append("&type=" + searchType);

        new JSONConnection<MovieResponse>(url.toString(), MovieResponse.class, new JSONConnection.onResponseListener<MovieResponse>()
        {
            @Override
            public void onConnSuccess(MovieResponse response, String url, Object originalRequest) {
                if (response != null && response.getResponse().equalsIgnoreCase("True") && !TextUtils.isEmpty(response.getTitle())) {
                    setListView(response);
                } else {
                    Toast.makeText(SearchActivity.this, "No result found for search string " + movie, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onConnFail(String errorMsg, String url, Object originalRequest) {
                Toast.makeText(SearchActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSearchType() {
        String type = "";
        switch (searchType.getCheckedRadioButtonId()) {
            case R.id.movie:
                type = "movie";
                break;
            case R.id.series:
                type = "series";
                break;
            case R.id.episode:
                type = "both";
                break;
        }
        return type;
    }

    private void setListView(MovieResponse response) {
        if (adapter != null) {
            adapter.addToList(response);
        } else {
            adapter = new SearchAdapter(this, new SearchListBean());
            adapter.addToList(response);
            listView.setAdapter(adapter);
        }
    }

    public class SearchAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        SearchListBean list;

        public SearchAdapter(Context context, SearchListBean list) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.list = list;
        }

        public void addToList(MovieResponse response) {
            list.getList().add(response);
            notifyDataSetChanged();
        }

        public void emptyList() {
            list.getList().clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.getList().size();
        }

        @Override
        public Object getItem(int i) {
            return list.getList().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = null;
            ViewHolder holder = null;
            if (convertView == null) {
                view = inflater.inflate(R.layout.adapter_search, viewGroup, false);
                holder = new ViewHolder(view);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            MovieResponse response = (MovieResponse) getItem(position);
            holder.title.setText(response.getTitle());
            holder.genre.setText("Genre: " + response.getGenre());
            holder.release.setText("Release Date: " + response.getReleased());
            holder.rating.setText("Rating: " + response.getRated());
            holder.plot.setText(response.getPlot());

            ImageLoader.getInstance().displayImage(response.getPoster(), holder.image);

            view.setTag(holder);
            return view;
        }

        public class ViewHolder {
            ImageView image;
            TextView title, genre, release, rating, plot;

            public ViewHolder(View child) {
                image = (ImageView) child.findViewById(R.id.image);
                title = (TextView) child.findViewById(R.id.title);
                genre = (TextView) child.findViewById(R.id.genre);
                release = (TextView) child.findViewById(R.id.release);
                rating = (TextView) child.findViewById(R.id.rating);
                plot = (TextView) child.findViewById(R.id.plot);
            }
        }
    }
}
