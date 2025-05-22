package com.optimum.coolkeybord;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import java.util.List;
import com.optimum.coolkeybord.adapter.Gifgridviewadapter;  // Adjust package if needed
import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.GridLayoutManager;
import com.optimum.coolkeybord.models.Gifdata;  // Adjust the package path as per your project
import java.util.ArrayList;
import java.util.Map;
import android.widget.Toast;


public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";

    RecyclerView recyclerFavorites;
    Gifgridviewadapter gifAdapter;
    List<String> favoriteUrls = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Started");
        Toast.makeText(getContext(), "Favorites Fragment Loaded", Toast.LENGTH_SHORT).show();

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);

        loadFavorites();

        Log.d(TAG, "Favorites loaded: " + favoriteUrls.size());  // Log number of favorites loaded

        ArrayList<Gifdata> favoriteGifs = new ArrayList<>();
        for (String url : favoriteUrls) {
            Log.d(TAG, "Adding favorite URL to list: " + url);
            favoriteGifs.add(new Gifdata(url,"default_id", url, "default_youtube_url", false));
        }
        gifAdapter = new Gifgridviewadapter(favoriteGifs, getContext());

        recyclerFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerFavorites.setAdapter(gifAdapter);

        return view;
    }

    private void loadFavorites() {
        Context context = getActivity();
        SharedPreferences prefs = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        Map<String, ?> allFavorites = prefs.getAll();
        favoriteUrls.clear();

        for (String key : allFavorites.keySet()) {
            favoriteUrls.add(key);
            Log.d(TAG, "Loaded favorite URL: " + key);  // Log each favorite URL loaded
        }

        // 🔥 Add a test GIF manually to check if RecyclerView and Adapter are working
        favoriteUrls.add("https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.gif");
        favoriteUrls.add("https://media.giphy.com/media/l0HlQ7LRalZzrc5Gw/giphy.gif");
        favoriteUrls.add("https://media.giphy.com/media/5GoVLqeAOo6PK/giphy.gif");
        favoriteUrls.add("https://media.giphy.com/media/xT0xeJpnrWC4XWblEk/giphy.gif");
        favoriteUrls.add("https://media.giphy.com/media/ICOgUNjpvO0PC/giphy.gif");
        favoriteUrls.add("https://media.giphy.com/media/3o6ZsZZ1nHWo2cWxH6/giphy.gif");


        Log.d(TAG, "Total favorites after dummy: " + favoriteUrls.size());
    }

}
