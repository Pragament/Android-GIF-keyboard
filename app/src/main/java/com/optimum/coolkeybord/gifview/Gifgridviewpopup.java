package com.optimum.coolkeybord.gifview;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;
import androidx.core.util.Predicate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.reflect.TypeToken;
import com.optimum.coolkeybord.DictionaryActivity;
import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.adapter.CategoriesAdapter;
import com.optimum.coolkeybord.adapter.Gifgridviewadapter;
import com.optimum.coolkeybord.adapter.Subcatadapter;
import com.optimum.coolkeybord.listners.ItemWrapper;
import com.optimum.coolkeybord.listners.RecyclerItemClickListener;
import com.optimum.coolkeybord.models.Categoriesmodel;
import com.optimum.coolkeybord.models.Gifdata;
import com.optimum.coolkeybord.models.Sub_catitemModel;
import com.optimum.coolkeybord.settingssession.SettingSesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import github.ankushsachdeva.emojicon.EmojiconsPopup;

public class Gifgridviewpopup extends PopupWindow {
    //++++++++++++++++++++++++++++++++++++++++++++RecyclerView of gifs++++++++++++++++++++++++++++
    private SettingSesson settingSesson;
    private RecyclerView categoriesrec;
    private RecyclerView subcatrec;
    private ImageView settings;
    private ImageView backimgtogif;
    private RecyclerView gifgridlay;
    private ProgressBar progresbarfull;
    ArrayList<Categoriesmodel> categoriesmodelArrayList = new ArrayList<>();
    ArrayList<Sub_catitemModel>  subcategoriesmodelArrayList = new ArrayList<>();
    ArrayList<Gifdata> subgifdataArrayList = new ArrayList<>();
    private CategoriesAdapter categoriesAdapter;
    private Subcatadapter subcatadapter;
    private CategoriesAdapter subcategoriesAdapter;
    private Gifgridviewadapter gifgridviewadapter;
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    View rootView;
    String searchtext;
    Context mContext;
    private Boolean isOpened = false;
    private int keyBoardHeight = 0;
    private Boolean pendingOpen = false;
    EmojiconsPopup.OnSoftKeyboardOpenCloseListener onSoftKeyboardOpenCloseListener;
    private onGifclickedListner onGifclickedListner;


    public Gifgridviewpopup(View rootView, Context mContext, String searchtextx) {
        super(mContext);
        this.mContext = mContext;
        this.rootView = rootView;
        this.searchtext = searchtextx;

        View customView = createCustomView();

        setContentView(customView);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize((int) mContext.getResources().getDimension(github.ankushsachdeva.emojicon.R.dimen.keyboard_height), WindowManager.LayoutParams.MATCH_PARENT);

        //default size
//        setSize((int) mContext.getResources().getDimension(github.ankushsachdeva.emojicon.R.dimen.keyboard_height), WindowManager.LayoutParams.MATCH_PARENT);
    }

    private View createCustomView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gifgridviewlayout, null, false);
        gifgridlay = (RecyclerView) view.findViewById(R.id.gridlay);

        settings = view.findViewById(R.id.settings);
        backimgtogif = view.findViewById(R.id.backimgtogif);
//        makeTempGifs();
        gifgridlay.setLayoutManager(new GridLayoutManager(mContext , 3 ));

        gifgridlay.addOnItemTouchListener(new RecyclerItemClickListener(mContext, gifgridlay, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LinearLayout topli = view.findViewById(R.id.topli);
                if(!subgifdataArrayList.get(position).getSelectedornot())
                {
                    subgifdataArrayList.get(position).setSelectedornot(true);
                    topli.setBackground(view.getContext().getDrawable(R.drawable.selectedgif));
                    onGifclickedListner.onGifclicked(subgifdataArrayList.get(position) ,settingSesson);
                }else {
                    subgifdataArrayList.get(position).setSelectedornot(false);
                    topli.setBackground(view.getContext().getDrawable(R.drawable.disselectback));
                    onGifclickedListner.onGifclicked(subgifdataArrayList.get(position) ,settingSesson);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Settings", Toast.LENGTH_SHORT).show();

                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                Intent intent = new Intent(view.getContext() ,DictionaryActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
              view.getContext().startActivity(intent);
            }
        });
//        backimgtogif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                settingspage.setVisibility(View.GONE);
//                gifviewpagli.setVisibility(View.VISIBLE);
//            }
//        });
        ///+++++++++++++++++++++++++++++++++++++++++++Gif made++++++++++++++++++++++++++++++++++++++++++++++
        categoriesrec = (RecyclerView) view.findViewById(R.id.categoriesrec);
        subcatrec = (RecyclerView) view.findViewById(R.id.subcatrec);
        progresbarfull = (ProgressBar) view.findViewById(R.id.progresbarfull);
        Log.e("searchtext" ,""+searchtext);
        if(searchtext.equals(""))
        {
            makeCategories(view.getContext());
//            getSubcategorieswithString("all" , mContext);
        }else {
            makeCategories(view.getContext());
            getSubcategorieswithString(searchtext , mContext);
        }

//         Hide Emoticons with keyboard icon.
        view.findViewById(github.ankushsachdeva.emojicon.R.id.emojis_keyboard_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return  view;
    }

    private void getSubcategorieswithString(String searchtext, Context mContext) {
        settingSesson = new SettingSesson(mContext);
//        String subcaturl ="https://d9.technikh.com/index.php/api/v1/gif/search/"+searchtext;
        String subcaturl ="https://d9.technikh.com/index.php/api/v1/gif/search/mov?"+searchtext;
        progresbarfull.setVisibility(View.VISIBLE);
        progresbarfull.animate();
        progresbarfull.setIndeterminate(true);
        RequestQueue queue = Volley.newRequestQueue(this.mContext);
        String selectedlang= "";
        String selectesearchendwith= "";
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

//        String subcaturl ="https://d9.technikh.com/index.php/api/v1/gif/search/"+searchtext;
        subgifdataArrayList.clear();
// Request a string response from the provided URL.
        JsonObjectRequest subcatjsonObjectRequest = new JsonObjectRequest(subcaturl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.e("Response" , "for gifs"+response.toString());
                try{
                    if(response.getJSONArray("items").length() ==0)
                    {
                        Toast.makeText(mContext, "No data found with " +searchtext, Toast.LENGTH_SHORT).show();
                        progresbarfull.setVisibility(View.GONE);
                        return;
                    }
                    for(int i =0 ;i < response.getJSONArray("items").length();i++)
                    {
                        JSONObject obejct = response.getJSONArray("items").getJSONObject(i);
                        subgifdataArrayList.add(new Gifdata(obejct.getString("multiline_text") ,obejct.getString("gif")
                                ,obejct.getString("thumbnail_gif") ,obejct.getString("youtube_url") , false));
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                gifgridviewadapter = new Gifgridviewadapter(subgifdataArrayList, mContext,"0");
                gifgridlay.setAdapter(gifgridviewadapter);
                gifgridviewadapter.notifyDataSetChanged();
                progresbarfull.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley" , "error"+error.networkResponse);
                Log.e("volley" , "error"+error.getMessage());
                Log.e("volley" , "error"+error.getCause());
                progresbarfull.setVisibility(View.GONE);
            }
        });
// Add the request to the RequestQueue.
        queue.add(subcatjsonObjectRequest);
    }

    private void makeTempGifs() {
//        gifdataArrayList.add(new Gifdata(R.raw.gg ,"gg" ,"local"));
//        gifdataArrayList.add(new Gifdata(R.raw.blast ,"blast" ,"local"));
    }
    /**
     * Set the listener for the event when any of the emojicon is clicked
     */
    public void setOnGifclickedListnermethod(Gifgridviewpopup.onGifclickedListner listener){
        this.onGifclickedListner = listener;
    }
    private void makeCategories(Context context) {
        progresbarfull.setVisibility(View.VISIBLE);
        progresbarfull.animate();
        progresbarfull.setIndeterminate(true);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String caturl ="https://d9.technikh.com/index.php/api/v1/gif/categories";
//        String caturl ="https://d9.technikh.com/index.php/freelancer-sub-categories-api";

// Request a string response from the provided URL.
        JsonArrayRequest catjsonArrayRequest = new JsonArrayRequest(caturl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                Log.e("volley" , "response"+response.toString());

                for(int i=0 ;i<response.length() ;i++)
                {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String catname = jsonObject.getString("category");
                        String category_id = jsonObject.getString("category-id");
//                        Type listsubType = new TypeToken<ArrayList<Sub_catitemModel>>(){}.getType();
                        ArrayList<Sub_catitemModel> allsubcategories = new ArrayList<>();
                        for(int ip = 0;ip<jsonObject.getJSONArray("sub-category-items").length();ip++)
                        {

                            JSONObject subobject = jsonObject.getJSONArray("sub-category-items").getJSONObject(ip);
                            Log.e("sub-category" , "items"+subobject.getString("sub-category"));
                            allsubcategories.add(new Sub_catitemModel(subobject.getString("sub-category") , subobject.getString("sub-category-id") ,false));
                        }

//                        Log.e("getSub_catitemModels" , ""+allsubcategories.get(0).getSubcategory());
                        categoriesmodelArrayList.add(new Categoriesmodel(catname , category_id, allsubcategories ,false));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    categoriesmodelArrayList.add(new Categoriesmodel("Recent" , "1"));

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    categoriesmodelArrayList= (ArrayList<Categoriesmodel>) categoriesmodelArrayList.stream().filter(distinctByKey(cust -> cust.))
                    categoriesmodelArrayList = (ArrayList<Categoriesmodel>)  categoriesmodelArrayList.stream()
                            .map(ItemWrapper::new)
                            .distinct()
                            .map(wrapper -> wrapper.item)
                            .collect(Collectors.toList());


                }
                categoriesrec.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.HORIZONTAL, false));
                categoriesAdapter = new CategoriesAdapter( categoriesmodelArrayList, mContext ,"0" );

                categoriesrec.setAdapter(categoriesAdapter);
                //++++++++++++++++If empty+++++++++++++++++++++++++++++++++++++++++++++++++++++
                if(subcategoriesmodelArrayList.isEmpty() && searchtext.equals(""))
                {
                    subcatrec.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.HORIZONTAL, false));
                    int siex = categoriesmodelArrayList.get(0).getSub_catitemModels().size();

                    for ( Sub_catitemModel sub_catitemModel:categoriesmodelArrayList.get(0).getSub_catitemModels()) {
//                        Log.e("getSubcategory" , ""+sub_catitemModel.getSubcategory());
                        subcategoriesmodelArrayList.add(new Sub_catitemModel(sub_catitemModel.getSubcategory() , sub_catitemModel.getSubcategoryid(),false));
                    }
//                        subcategoriesmodelArrayList.addAll(categoriesmodelArrayList.get(position).sub_catitemModels);
                    subcatadapter = new Subcatadapter( subcategoriesmodelArrayList, mContext ,"0" );
                    subcatrec.setAdapter(subcatadapter);
                    subcatrec.addOnItemTouchListener(new RecyclerItemClickListener(mContext, subcatrec, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            getSubcategories(subcategoriesmodelArrayList.get(position).getSubcategoryid() ,mContext);
                            LinearLayout subcattopli = view.findViewById(R.id.toplicat);
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    }));
                        getSubcategories(subcategoriesmodelArrayList.get(0).getSubcategoryid() ,mContext);
                }

                //++++++++++++++++++++++
                categoriesrec.addOnItemTouchListener(new RecyclerItemClickListener(mContext ,categoriesrec ,new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int position) {
                        subcategoriesmodelArrayList.clear();
                        LinearLayout topli = view.findViewById(R.id.toplicat);
                        subcatrec.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.HORIZONTAL, false));
                        int siex = categoriesmodelArrayList.get(position).getSub_catitemModels().size();

                        for ( Sub_catitemModel sub_catitemModel:categoriesmodelArrayList.get(position).getSub_catitemModels()) {
//                            Log.e("getSubcategory" , ""+sub_catitemModel.getSubcategory());
                            subcategoriesmodelArrayList.add(new Sub_catitemModel(sub_catitemModel.getSubcategory() , sub_catitemModel.getSubcategoryid() ,false));
                        }
//                        subcategoriesmodelArrayList.addAll(categoriesmodelArrayList.get(position).sub_catitemModels);
                        subcatadapter = new Subcatadapter( subcategoriesmodelArrayList, mContext ,"0" );
                        subcatrec.setAdapter(subcatadapter);
                        subcatrec.addOnItemTouchListener(new RecyclerItemClickListener(mContext, subcatrec, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                getSubcategories(subcategoriesmodelArrayList.get(position).getSubcategoryid() ,mContext);
                                LinearLayout subcattopli = view.findViewById(R.id.toplicat);

                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        }));
//                        getSubcategories(categoriesmodelArrayList.get(position).getSubcategoryid() ,mContext);
                        progresbarfull.setVisibility(View.GONE);
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

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley" , "error"+error.networkResponse);
                Log.e("volley" , "error"+error.getMessage());
                Log.e("volley" , "error"+error.getCause());
                progresbarfull.setVisibility(View.GONE);
            }

        });
// Add the request to the RequestQueue.
        queue.add(catjsonArrayRequest);

    }

    private void getSubcategories(String subcategoryid, Context mContext) {
        progresbarfull.setVisibility(View.VISIBLE);
        progresbarfull.animate();
        progresbarfull.setIndeterminate(true);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String subcaturl ="https://d9.technikh.com/index.php/api/v1/gif/search/all?sub-category-id="+subcategoryid;
        subgifdataArrayList.clear();
// Request a string response from the provided URL.
        JsonObjectRequest subcatjsonObjectRequest = new JsonObjectRequest(subcaturl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.e("Response" , "for gifs"+response.toString());
                try{
                    for(int i =0 ;i < response.getJSONArray("items").length();i++)
                    {
                        JSONObject obejct = response.getJSONArray("items").getJSONObject(i);
                        subgifdataArrayList.add(new Gifdata(obejct.getString("multiline_text") ,obejct.getString("gif")
                        ,obejct.getString("thumbnail_gif") ,obejct.getString("youtube_url") , false));
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                gifgridviewadapter = new Gifgridviewadapter(subgifdataArrayList,mContext,"0");
                gifgridlay.setAdapter(gifgridviewadapter);
                gifgridviewadapter.notifyDataSetChanged();
                progresbarfull.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error" +error.networkResponse, Toast.LENGTH_SHORT).show();
                Log.e("volley" , "error"+error.networkResponse);
                Log.e("volley" , "error"+error.getMessage());
                Log.e("volley" , "error"+error.getCause());
                progresbarfull.setVisibility(View.GONE);
            }
        });
// Add the request to the RequestQueue.
        queue.add(subcatjsonObjectRequest);
    }

    // predicate to filter the duplicates by the given key extractor.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> Predicate<T> distinctByKey(Function<? super T, Categoriesmodel> keyExtractor) {
        Map<Categoriesmodel, Boolean> uniqueMap = new ConcurrentHashMap<>();
        return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    public void setSizeForSoftKeyboard(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
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
                    if(isOpened == false){
                        if(onSoftKeyboardOpenCloseListener!=null)
                            onSoftKeyboardOpenCloseListener.onKeyboardOpen(keyBoardHeight);
                    }
                    isOpened = true;
                    if(pendingOpen){
                        showAtBottom();
                        pendingOpen = false;
                    }
                }
                else{
                    isOpened = false;
                    if(onSoftKeyboardOpenCloseListener!=null)
                        onSoftKeyboardOpenCloseListener.onKeyboardClose();
                }
            }
        });
    }
    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return rootView.getRootView().getHeight();
        }
    }
    public void setSize(int width, int height){
        setWidth(width);
        setHeight(height);
    }
    /**
     *
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    public Boolean isKeyBoardOpen(){
        return isOpened;
    }
    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    public void setOnSoftKeyboardOpenCloseListener(EmojiconsPopup.OnSoftKeyboardOpenCloseListener listener){
        this.onSoftKeyboardOpenCloseListener = listener;
    }
    public void showAtBottom(){
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    public interface onGifclickedListner {
        void onGifclicked(Gifdata gifitem, SettingSesson settingSesson);
    }

}
