package com.example.bhavesh.moviefinder;

import java.util.ArrayList;

/**
 * Created by bhavesh on 26/04/17.
 */

public class SearchListBean
{
    ArrayList<MovieResponse> list;

    public SearchListBean() {
        list = new ArrayList<>();
    }

    public ArrayList<MovieResponse> getList() {
        return list;
    }

    public void setList(ArrayList<MovieResponse> list) {
        this.list = list;
    }
}
