package com.optimum.coolkeybord.gifview;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.optimum.coolkeybord.DictionaryActivity;
import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.adapter.CategoriesAdapter;
import com.optimum.coolkeybord.adapter.Gifgridviewadapter;
import com.optimum.coolkeybord.adapter.Subcatadapter;
import com.optimum.coolkeybord.database.Historyviewmodel;
import com.optimum.coolkeybord.listners.RecyclerItemClickListener;
import com.optimum.coolkeybord.models.Categoriesmodel;
import com.optimum.coolkeybord.models.Gifdata;
import com.optimum.coolkeybord.models.Sub_catitemModel;
import com.optimum.coolkeybord.settingssession.SettingSesson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

public class Gifgridviewpopup extends PopupWindow {
    private SettingSesson settingSesson;
    private RecyclerView categoriesrec;
    private RecyclerView subcatrec;
    private RecyclerView gifgridlay;
    private RecyclerView recentgifrec;
    private ImageButton emojis_keyboard_image;
    LinearLayout recent_gfs;
    //++++++++++++++++++++++++++++++Progress bar++++++++++++++++++++++
    private ProgressBar progresbarfull;
    private RelativeLayout progresbarfulli;
    //+++++++++++++++++++++++++++++++++++++++++++++++++
    ArrayList<Categoriesmodel> categoriesmodelArrayList = new ArrayList<>();
    ArrayList<Sub_catitemModel>  subcategoriesmodelArrayList = new ArrayList<>();
    ArrayList<Gifdata> subgifdataArrayList = new ArrayList<>();
    ArrayList<Gifdata> recentsubgifdataArrayList = new ArrayList<>();
    private CategoriesAdapter categoriesAdapter;
    private Subcatadapter subcatadapter;
    private Gifgridviewadapter gifgridviewadapter;
    private Gifgridviewadapter recentgifgridviewadapter;
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    View rootView;
    String searchtext;
    Context mContext;
    private int keyBoardHeight = 0;
    private Boolean pendingOpen = false;
    Historyviewmodel historyviewmodel;
    private onGifclickedListner onGifclickedListner;
    private int processisnotdone = 0;

    public Gifgridviewpopup(View rootView, Context mContext, String searchtextx, Historyviewmodel historyviewmodelx) {
        super(mContext);
        this.mContext = mContext;
        this.rootView = rootView;
        this.searchtext = searchtextx;
        this.historyviewmodel = historyviewmodelx;


        View customView = createCustomView();
        setContentView(customView);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize((int) mContext.getResources().getDimension(R.dimen.keyboard_height), WindowManager.LayoutParams.MATCH_PARENT);

    }

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        //++++++++++++++it will be null as it is independent view++++++++++++++++
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.gifgridviewlayout, null, false);
         recent_gfs = (LinearLayout) view.findViewById(R.id.recent_gfs);
        gifgridlay = (RecyclerView) view.findViewById(R.id.gridlay);
        recentgifrec = (RecyclerView) view.findViewById(R.id.recentgif);
        ///+++++++++++++++++++++++++++++++++++++++++++Gif made++++++++++++++++++++++++++++++++++++++++++++++
        categoriesrec = (RecyclerView) view.findViewById(R.id.categoriesrec);
        subcatrec = (RecyclerView) view.findViewById(R.id.subcatrec);
        //+++++++++++++++++++++++++++++++++++Progress bar++++++++++++++++++++++++++++++
        progresbarfull = (ProgressBar) view.findViewById(R.id.progresbarfull);
        progresbarfulli = (RelativeLayout) view.findViewById(R.id.progresbarfulli);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ImageView settings = view.findViewById(R.id.settings);

//        makeTempGifs();
        gifgridlay.setLayoutManager(new GridLayoutManager(mContext , 3 ));
        recentgifrec.setLayoutManager(new GridLayoutManager(mContext , 3 ));


        gifgridlay.addOnItemTouchListener(new RecyclerItemClickListener(mContext, gifgridlay, new RecyclerItemClickListener.OnItemClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemClick(View view, int position) {
                LinearLayout topli = view.findViewById(R.id.topli);
                if(!subgifdataArrayList.get(position).getSelectedornot())
                {
                    subgifdataArrayList.get(position).setSelectedornot(true);
                    topli.setBackground(view.getContext().getDrawable(R.drawable.selectedgif));

                }else {
                    subgifdataArrayList.get(position).setSelectedornot(false);
                    topli.setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                }
//                view.findViewById(R.id.emojis_keyboard_image).setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                onGifclickedListner.onGifclicked(subgifdataArrayList.get(position) ,settingSesson , 1);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        recentgifrec.addOnItemTouchListener(new RecyclerItemClickListener(mContext, recentgifrec, new RecyclerItemClickListener.OnItemClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemClick(View view, int position) {
                LinearLayout topli = view.findViewById(R.id.topli);
                if(!recentsubgifdataArrayList.get(position).getSelectedornot())
                {
                    recentsubgifdataArrayList.get(position).setSelectedornot(true);
                    topli.setBackground(view.getContext().getDrawable(R.drawable.selectedgif));
                }else {
                    recentsubgifdataArrayList.get(position).setSelectedornot(false);
                    topli.setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                }
//                view.findViewById(R.id.emojis_keyboard_image).setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                onGifclickedListner.onGifclicked(recentsubgifdataArrayList.get(position) ,settingSesson ,2);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        historyviewmodel.getmAllRecentGifs().observeForever( gifEntities ->{
            if(gifEntities.isEmpty())
                return;
            recentsubgifdataArrayList.clear();
            JSONObject firstgif = null;
            for(int i=0;i<gifEntities.size();i++)
            {

                try {
                    JSONObject obejct   = new JSONObject(gifEntities.get(i).gifjson);
                    Log.w("gifs" , "saved"+obejct.getString("gif"));
                if( i==0){
//                        Log.e("gifs" , "saved"+obejct.toString());
                        recentsubgifdataArrayList.add(new Gifdata(obejct.getString("multiline_text") ,obejct.getString("gif")
                                ,obejct.getString("thumbnail_gif") ,obejct.getString("youtube_url") , false));
                         firstgif = obejct;


                }
                else {
                    try {
                        assert firstgif != null;
                        if(!(firstgif.getString("gif").equals(obejct.getString("gif"))))

                        {
                            recentsubgifdataArrayList.add(new Gifdata(obejct.getString("multiline_text") ,obejct.getString("gif")
                                    ,obejct.getString("thumbnail_gif") ,obejct.getString("youtube_url") , false));
                        }else {
                            firstgif = obejct;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Set<Gifdata> set = new HashSet<>(recentsubgifdataArrayList);
                recentsubgifdataArrayList.clear();
                recentsubgifdataArrayList.addAll(set);
                recentgifgridviewadapter = new Gifgridviewadapter(recentsubgifdataArrayList, mContext);
                recentgifrec.setAdapter(recentgifgridviewadapter);
                recentgifgridviewadapter.notifyDataSetChanged();
            }

        });
        recent_gfs.setOnTouchListener((view1, motionEvent) -> {
            if(!(categoriesmodelArrayList.isEmpty()))
            {
                for(int i =0 ;i<categoriesmodelArrayList.size() ;i++)
                {
                    categoriesmodelArrayList.get(i).setSelectedornot(false);
                }

                categoriesAdapter.notifyDataSetChanged();
            }
            if(!(subcategoriesmodelArrayList.isEmpty()))
            {
                for(int i =0 ;i<subcategoriesmodelArrayList.size() ;i++)
                {
                    subcategoriesmodelArrayList.get(i).setSelectedornot(false);
                }

                subcatadapter.notifyDataSetChanged();
            }

            recent_gfs.setBackground(view1.getContext().getDrawable(R.drawable.selectedgif));
//            view.findViewById(R.id.emojis_keyboard_image).setBackground(view.getContext().getDrawable(R.drawable.disselectback));
            Toast.makeText(mContext, "recent found", Toast.LENGTH_SHORT).show();
            gifgridlay.setVisibility(View.GONE);
            recentgifrec.setVisibility(View.VISIBLE);
            return false;
        });
        settings.setOnClickListener(view12 -> {
            Toast.makeText(view12.getContext(), "Settings", Toast.LENGTH_SHORT).show();

            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            Intent intent = new Intent(view12.getContext() ,DictionaryActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
          view12.getContext().startActivity(intent);
        });

        Log.e("searchtext" ,""+searchtext);
        if(searchtext.equals(""))
        {
            makeCategories(view);
        }else {
            getSubcategorieswithString(searchtext , mContext ,view);
        }
        emojis_keyboard_image =  view.findViewById(R.id.emojis_keyboard_image);
        view.findViewById(R.id.emojis_keyboard_image).setOnClickListener(v -> dismiss());

        return  view;
    }

    private void getSubcategorieswithString(String searchtext, Context mContext, View view) {
        progresbarfull.setVisibility(View.VISIBLE);
        progresbarfulli.setVisibility(View.VISIBLE);
        progresbarfull.animate();
        progresbarfull.setIndeterminate(true);
        settingSesson = new SettingSesson(mContext);
        String subcaturl ="https://d9.technikh.com/index.php/api/v1/gif/search/"+searchtext+"?a=b";

        RequestQueue queue = Volley.newRequestQueue(this.mContext);
        String selectedlang;
        String selectesearchendwith;
        if(!settingSesson.getSlelectedlang().equals("no"))
        {
            selectedlang = settingSesson.getSlelectedlang();
            subcaturl = subcaturl+"&languages="+selectedlang+"&";
        }
        Log.e("selectedlang" , "url"+subcaturl);
        if(settingSesson.getSearchbyStartsorEnd().equals("S"))
        {
            selectesearchendwith = "startsWith";
            subcaturl = subcaturl+"searchMode="+selectesearchendwith;
        }else {
            selectesearchendwith = "endsWith";
            subcaturl = subcaturl+"searchMode"+selectesearchendwith;

        }
        Log.e("selectesearchendwith" , " url "+subcaturl);

        subgifdataArrayList.clear();

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest subcatjsonObjectRequest = new JsonObjectRequest(subcaturl, response -> {

            try{
                if(response.getJSONArray("items").length() ==0)
                {
                    progresbarfull.setVisibility(View.GONE);
                    Toast.makeText(mContext, "No data found with " +searchtext, Toast.LENGTH_SHORT).show();
                    makeCategories(view);
                    return;
                }else {
                    for(int i =0 ;i < response.getJSONArray("items").length();i++)
                    {
                        JSONObject obejct = response.getJSONArray("items").getJSONObject(i);
                        subgifdataArrayList.add(new Gifdata(obejct.getString("multiline_text") ,obejct.getString("gif")
                                ,obejct.getString("thumbnail_gif") ,obejct.getString("youtube_url") , false));


                    }
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            gifgridviewadapter = new Gifgridviewadapter(subgifdataArrayList, mContext);
            gifgridlay.setAdapter(gifgridviewadapter);
            gifgridviewadapter.notifyDataSetChanged();

            makeCategories(view);
//            makeCategories(mContext);
            progresbarfull.setVisibility(View.GONE);
        }, error -> {
            Log.e("volley" , "error"+error.networkResponse);
            Log.e("volley" , "error"+error.getMessage());
            Log.e("volley" , "error"+error.getCause());
            progresbarfull.setVisibility(View.GONE);
        });
        queue.add(subcatjsonObjectRequest);
    }

    public void setOnGifclickedListnermethod(Gifgridviewpopup.onGifclickedListner listener){
        this.onGifclickedListner = listener;
    }
    private void makeCategories(View context) {
        progresbarfull.setVisibility(View.VISIBLE);
        progresbarfulli.setVisibility(View.VISIBLE);
        progresbarfull.animate();
        progresbarfull.setIndeterminate(true);
        Log.e("Serch text" , "is"+searchtext);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context.getContext());
        String caturl ="https://d9.technikh.com/index.php/api/v1/gif/categories";
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest catjsonArrayRequest = new JsonArrayRequest(caturl, response -> {

            Log.e("volley" , "response"+response.toString());

            for(int i=0 ;i<response.length() ;i++)
            {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String catname = jsonObject.getString("category");
                    String category_id = jsonObject.getString("category-id");
                    ArrayList<Sub_catitemModel> allsubcategories = new ArrayList<>();
                    categoriesmodelArrayList.add(new Categoriesmodel(catname , category_id, allsubcategories ,false));
                    for(int ip = 0;ip<jsonObject.getJSONArray("sub-category-items").length();ip++)
                    {

                        JSONObject subobject = jsonObject.getJSONArray("sub-category-items").getJSONObject(ip);
                        Log.e("category" , "items"+catname);
                        if(ip==0)
                        {
                            allsubcategories.add(new Sub_catitemModel(subobject.getString("sub-category") , subobject.getString("sub-category-id") ,true));
                        }else {
                            allsubcategories.add(new Sub_catitemModel(subobject.getString("sub-category") , subobject.getString("sub-category-id") ,false));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            //++++++++++++++++++++++
            categoriesrec.addOnItemTouchListener(new RecyclerItemClickListener(mContext ,categoriesrec ,new RecyclerItemClickListener.OnItemClickListener(){

                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onItemClick(View view, int position) {
                    gifgridlay.setVisibility(View.VISIBLE);
                    recentgifrec.setVisibility(View.GONE);
                    recent_gfs.setBackground(context.getContext().getDrawable(R.drawable.disselectback));
                    subcategoriesmodelArrayList.clear();
                    LinearLayout topli = view.findViewById(R.id.toplicat);
                    subcatrec.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.HORIZONTAL, false));
                    for ( Sub_catitemModel sub_catitemModel:categoriesmodelArrayList.get(position).getSub_catitemModels()) {
                        subcategoriesmodelArrayList.add(new Sub_catitemModel(sub_catitemModel.getSubcategory() , sub_catitemModel.getSubcategoryid() ,false));
                    }

                    subcatadapter = new Subcatadapter( subcategoriesmodelArrayList, mContext);
                    subcategoriesmodelArrayList.get(0).setSelectedornot(true);
                    subcatrec.setAdapter(subcatadapter);
                    subcatadapter.notifyItemChanged(0);
                    subcatrec.addOnItemTouchListener(new RecyclerItemClickListener(mContext, subcatrec, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            gifgridlay.setVisibility(View.VISIBLE);
                            recentgifrec.setVisibility(View.GONE);
                            recent_gfs.setBackground(context.getContext().getDrawable(R.drawable.disselectback));
                            getSubcategories(subcategoriesmodelArrayList.get(position).getSubcategoryid() ,mContext, 1);
//                                LinearLayout subcattopli = view.findViewById(R.id.toplicat);
//                            view.findViewById(R.id.emojis_keyboard_image).setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    }));
                    emojis_keyboard_image.setBackground(view.getContext().getDrawable(R.drawable.disselectback));
//                    view.findViewById(R.id.emojis_keyboard_image).setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                    getSubcategories(subcategoriesmodelArrayList.get(position).getSubcategoryid() ,mContext, 1);
//                        getSubcategories(categoriesmodelArrayList.get(position).getSubcategoryid() ,mContext);

                    if(!categoriesmodelArrayList.get(position).isSelectedornot())
                    {
                        for(int i=0 ;i<categoriesmodelArrayList.size();i++)
                        {
                            categoriesmodelArrayList.get(i).setSelectedornot(false);
                        }

                        categoriesmodelArrayList.get(position).setSelectedornot(true);
                        topli.setBackground(view.getContext().getDrawable(R.drawable.selectedgif));

                    }else {

                        categoriesmodelArrayList.get(position).setSelectedornot(false);
                        topli.setBackground(view.getContext().getDrawable(R.drawable.disselectback));

                    }

//                        categoriesmodelArrayList.get(0).setSelectedornot(true);
                    categoriesAdapter.notifyDataSetChanged();

                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            }));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                List<Categoriesmodel> deduped = categoriesmodelArrayList.stream().distinct().collect(Collectors.toList());
                categoriesmodelArrayList.addAll(deduped);
            }else {
                List<Categoriesmodel> deDupStringList = new ArrayList<>(new HashSet<>(categoriesmodelArrayList));
                categoriesmodelArrayList.addAll(deDupStringList);
            }

            categoriesrec.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.HORIZONTAL, false));
            categoriesAdapter = new CategoriesAdapter( categoriesmodelArrayList, mContext);
            categoriesrec.setAdapter(categoriesAdapter);
            if(searchtext.isEmpty())
            {
                categoriesmodelArrayList.get(0).setSelectedornot(true);
            }else {
                context.findViewById(R.id.emojis_keyboard_image).setBackground(context.getContext().getDrawable(R.drawable.selectedgif));
            }

            categoriesAdapter.notifyDataSetChanged();
            //++++++++++++++++If empty+++++++++++++++++++++++++++++++++++++++++++++++++++++
            if(subcategoriesmodelArrayList.isEmpty() && searchtext.equals(""))
            {
                subcatrec.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.HORIZONTAL, false));


                for ( Sub_catitemModel sub_catitemModel:categoriesmodelArrayList.get(0).getSub_catitemModels()) {

                    subcategoriesmodelArrayList.add(new Sub_catitemModel(sub_catitemModel.getSubcategory() , sub_catitemModel.getSubcategoryid(),false));
                }
                subcatadapter = new Subcatadapter( subcategoriesmodelArrayList, mContext);
                subcatrec.setAdapter(subcatadapter);

                subcatrec.addOnItemTouchListener(new RecyclerItemClickListener(mContext, subcatrec, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        gifgridlay.setVisibility(View.VISIBLE);
                        recentgifrec.setVisibility(View.GONE);
                        getSubcategories(subcategoriesmodelArrayList.get(position).getSubcategoryid() ,mContext ,1 );
//                        view.findViewById(R.id.emojis_keyboard_image).setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

            }

            if(!subcategoriesmodelArrayList.isEmpty())
            {
                subcategoriesmodelArrayList.get(0).setSelectedornot(true);
                subcatadapter.notifyItemChanged(0);
                try{
                    Log.e("subcategoriesmodel" ,""+ subcategoriesmodelArrayList.toString());
                    getSubcategories(subcategoriesmodelArrayList.get(0).getSubcategoryid() ,mContext, 1);
                }catch (Exception E )
                {
                    Toast.makeText(mContext, "Can't find any subcategory showing default", Toast.LENGTH_SHORT).show();

                    E.printStackTrace();
                }
            }

            progresbarfull.setVisibility(View.GONE);
        }, error -> {
            Log.e("volley" , "error"+error.networkResponse);
            Log.e("volley" , "error"+error.getMessage());
            Log.e("volley" , "error"+error.getCause());
            progresbarfull.setVisibility(View.GONE);
        });
// Add the request to the RequestQueue.
        queue.add(catjsonArrayRequest);

    }

    private void getSubcategories(String subcategoryid, Context mContext, int pagenumber) {
        Log.e("page" , "number"+pagenumber+"with"+subcategoryid);
        progresbarfull.setVisibility(View.VISIBLE);
        progresbarfulli.setVisibility(View.VISIBLE);
        progresbarfull.animate();
        progresbarfull.setIndeterminate(true);
      SettingSesson settingSesson =  new SettingSesson(mContext);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String subcaturl ="https://d9.technikh.com/index.php/api/v1/gif/search/all?sub-category-id="+subcategoryid+"&languages="+ settingSesson.getSlelectedlang() +"&current_page="+pagenumber;
      if(pagenumber ==1)
      {
          subgifdataArrayList.clear();
      }

// Request a string response from the provided URL.
        JsonObjectRequest subcatjsonObjectRequest = new JsonObjectRequest(subcaturl, new Response.Listener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(JSONObject response) {

//                Log.e("Response" , "for gifs"+response.toString());
                try{
                    progresbarfull.setVisibility(View.VISIBLE);
                    progresbarfulli.setVisibility(View.VISIBLE);
                    for(int i =0 ;i < response.getJSONArray("items").length();i++)
                    {

                        JSONObject obejct = response.getJSONArray("items").getJSONObject(i);
                        if( i ==0)
                        {
                            if(searchtext.isEmpty())
                            {
                                Log.e("getSubcategories" , "subcaturl for gifs"+obejct.toString());
                                subgifdataArrayList.add(new Gifdata(obejct.getString("multiline_text") ,obejct.getString("gif")
                                        ,obejct.getString("thumbnail_gif") ,obejct.getString("youtube_url") , true));
                            }


                        }else {
                            subgifdataArrayList.add(new Gifdata(obejct.getString("multiline_text") ,obejct.getString("gif")
                                    ,obejct.getString("thumbnail_gif") ,obejct.getString("youtube_url") , false));
                        }

                    }
                    progresbarfull.setVisibility(View.GONE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                gifgridviewadapter = new Gifgridviewadapter(subgifdataArrayList,mContext);
                gifgridlay.setAdapter(gifgridviewadapter);
                gifgridviewadapter.notifyDataSetChanged();
                progresbarfull.setVisibility(View.VISIBLE);
                gifgridlay.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (!recyclerView.canScrollVertically(1)) {
//                            Toast.makeText(mContext, "Last", Toast.LENGTH_LONG).show();
                            if(processisnotdone ==0)
                            {
                                processisnotdone =1;
                                getSubcategories( subcategoryid,  mContext,  pagenumber +1);
                            }else {
                                processisnotdone =1;
                                Log.e("Plaeas" ,"wait");
                            }

                        }
                    }
                });

                processisnotdone = 0;
                progresbarfull.setVisibility(View.GONE);
                progresbarfulli.setVisibility(View.GONE);
            }
        }, error -> {
            Toast.makeText(mContext, "Error" +error.networkResponse, Toast.LENGTH_SHORT).show();
            Log.e("volley" , "error"+error.networkResponse);
            Log.e("volley" , "error"+error.getMessage());
            Log.e("volley" , "error"+error.getCause());
            progresbarfull.setVisibility(View.GONE);
            progresbarfulli.setVisibility(View.GONE);
            processisnotdone = 0;
        });
        queue.add(subcatjsonObjectRequest);
    }

    public void setSizeForSoftKeyboard(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int screenHeight = getUsableScreenHeight();
            int heightDifference = screenHeight
                    - (r.bottom - r.top);
            int resourceId = mContext.getResources()
                    .getIdentifier("status_bar_height",
                            "dimen", "android");
            if (resourceId > 0) {
                heightDifference -= mContext.getResources()
                        .getDimensionPixelSize(resourceId);
            }
            if (heightDifference > 100) {
                keyBoardHeight = heightDifference;
                setSize(WindowManager.LayoutParams.MATCH_PARENT, keyBoardHeight);

                if(pendingOpen){
                    showAtBottom();
                    pendingOpen = false;
                }
            }

        });
    }
    private int getUsableScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        return metrics.heightPixels;

    }
    public void setSize(int width, int height){
        setWidth(width);
        setHeight(height);
    }

    public void showAtBottom(){
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    public interface onGifclickedListner {
        void onGifclicked(Gifdata gifitem, SettingSesson settingSesson ,int place);
    }

}
