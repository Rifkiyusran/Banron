package com.example.neaplus.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.neaplus.core.viewmodel.NewsViewModel;
import com.example.neaplus.databinding.FragmentHomeBinding;
import com.example.neaplus.core.model.Articles;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ImageView imageSearch;
    private NewsViewModel newsViewModel;
    private HomeListAdapter adapterNews;
    private RecyclerView recyclerViewNews;
    private Spinner spinner;
    private EditText inputSearch;
    private String category = "";
    private String country;
    private NestedScrollView scroll;
    ArrayList<Articles> articleArrayList = new ArrayList<>();
    private boolean onsearch = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        textViewResult = binding.textViewResult;
        final TextView textView = binding.textNews;
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

      spinner = binding.spinnerCountry;
        ArrayList<String> Item = new ArrayList<>();
        Item.add("id");
        Item.add("us");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        country = spinner.getSelectedItem().toString();

        recyclerViewNews = binding.listRecyclerView;
        imageSearch = binding.imageSearch;
        inputSearch = binding.inputSearch;
        scroll = binding.nestedScrollView;

        search_click();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ct = parent.getItemAtPosition(position).toString();
                country = ct;
                newsViewModel.init();
                loadNews(ct, category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    newsViewModel.init();
                    loadNewsSearch(inputSearch.getText().toString());
                    onsearch = true;
                    animation();
                    inputSearch.setText("");
                    return true;
                } else {
                    return false;
                }
            }
        });

        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(onsearch == true){
                    onsearch = true;
                    animation();
                }
            }
        });

        return root;
    }

    private void loadNews(String ct, String cty) {
        newsViewModel.getAllNews(ct, cty).observe(getViewLifecycleOwner(), news -> {
            // Update the cached copy of the words in the adapter.
            List<Articles> newsArticles = news.getArticles();
            articleArrayList.clear();
            articleArrayList.addAll(newsArticles);
            adapterNews.notifyDataSetChanged();
        });
        setupRecyclerView();
    }

    private void loadNewsSearch(String search) {
        newsViewModel.getAllNewsSearch(search).observe(getViewLifecycleOwner(), news -> {
            // Update the cached copy of the words in the adapter.
            List<Articles> newsArticles = news.getArticles();
            articleArrayList.clear();
            articleArrayList.addAll(newsArticles);
            adapterNews.notifyDataSetChanged();
        });
        setupRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        if (adapterNews == null) {
            adapterNews = new HomeListAdapter(this.getActivity(), articleArrayList, new HomeListAdapter.NewsDiff());
            recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewNews.setAdapter(adapterNews);
            recyclerViewNews.setItemAnimator(new DefaultItemAnimator());
        } else {
            adapterNews.notifyDataSetChanged();
        }
    }

    public void search_click() {
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation();
            }
        });
    }

    public void animation() {
        if (onsearch == false) {
            YoYo.with(Techniques.FadeOut)
                    .duration(0)
                    .playOn(binding.textNews);
            YoYo.with(Techniques.SlideInLeft)
                    .duration(0)
                    .playOn(binding.viewSearch);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.textNews.setVisibility(View.GONE);
                    binding.viewSearch.setVisibility(View.VISIBLE);
                }
            }, 500);
            onsearch = true;
        } else {
            YoYo.with(Techniques.FadeIn)
                    .duration(500)
                    .playOn(binding.textNews);
            YoYo.with(Techniques.SlideOutUp)
                    .duration(500)
                    .playOn(binding.viewSearch);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.textNews.setVisibility(View.VISIBLE);
                    binding.viewSearch.setVisibility(View.GONE);
                }
            }, 500);
            onsearch = false;
        }
    }

}
