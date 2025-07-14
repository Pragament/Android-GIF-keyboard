package com.optimum.coolkeybord.android;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.optimum.coolkeybord.DictionaryActivity;
import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.adapter.Gifgridviewadapter;
import com.optimum.coolkeybord.adapter.Historyadapter;
import com.optimum.coolkeybord.database.Dao;
import com.optimum.coolkeybord.database.Historyviewmodel;
import com.optimum.coolkeybord.gifview.Gifgridviewpopup;
import com.optimum.coolkeybord.listners.RecyclerItemClickListener;
import com.optimum.coolkeybord.models.Gifdata;
import com.optimum.coolkeybord.models.Historymodal;
import com.optimum.coolkeybord.models.RecentGifEntity;
import com.optimum.coolkeybord.settingssession.SettingSesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import timber.log.Timber;

public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, InputConnectionCompat.OnCommitContentListener, HistoryClicked {

    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */

    static final boolean PROCESS_HARD_KEYS = true;
    private InputMethodManager mInputMethodManager;
    private LatinKeyboardView mInputView;
    //++++For gif keybord+++++
    private LatinKeyboardView keyboardxxview;
    private EditText searched;

    Historyadapter historyadapter;
    private RecyclerView historyrecs;
    private final ArrayList<Historymodal> usershisatories = new ArrayList<>();
    private ImageView searchimg;
    private ImageView cancelimg;
    private ImageView searchimgdone;

    private SettingSesson settingSesson;
    private File mGifFile;
    private static final String AUTHORITY = "com.optimum.coolkeybord";
    private static final String MIME_TYPE_GIF = "image/gif";
    //+++++++++++
    private Historyviewmodel historyviewmodel;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;
    private final StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn;
    //    private SettingSesson settingSesson;
    private boolean mCompletionOn;
    private boolean mSound;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    private String mWordSeparators;
    // Keyboards (not subtypes)
    boolean gifSupported = false;
    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mCurKeyboard;
    public static String mActiveKeyboard;
    //    private EmojiconsPopup popupWindow = null;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++gifpopupWindow+++++++++++++++++++++++++++++++++
    private Gifgridviewpopup gifpopupWindow = null;

    HistoryClicked historyClicked;
    private Dao userDao;
    private File imagesDir;
    //    private FrameLayout vx;

    // Add new keyboard field
    private LatinKeyboard mSymbolsKeyboard;
    private MaterialTextView tvSuggestion1, tvSuggestion2, tvSuggestion3;
    private Button btnRealTimeSearchStatus;
    private RecyclerView rvRealTimeSearch, searchRecentGif;
    private LinearLayout llMainRecentView, imgBtnGoExplore;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable workRunnable;
    private ArrayList<Gifdata> realTimeSearchList;

    private final ArrayList<String> translatedWords = new ArrayList<>();
    private final ArrayList<String> transliteratedWords = new ArrayList<>();
    private int currentTranslationIndex = 0;
    private int currentTransliterationIndex = 0;

    ArrayList<Gifdata> recentsubgifdataArrayList;

    @Override
    public void onCreate() {
        super.onCreate();

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);

        //+++++++++++++++++++++++++need to be tested++++++++++++++++++++++++++++++++++
//      imagesDir.mkdirs();


//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        settingSesson = new SettingSesson(this);
        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        // symbols keyboard
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.keyboard_symbols);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    @Override
    public View onCreateInputView() {

        // Set custom theme to input view.
        LinearLayout vx = (LinearLayout) getLayoutInflater().inflate(R.layout.input_1, null);

        searched = vx.findViewById(R.id.searched);
        LinearLayout settingsimg = vx.findViewById(R.id.settingsimg);
        historyrecs = vx.findViewById(R.id.historyrecs);
        historyrecs.setLayoutManager(new LinearLayoutManager(vx.getContext(), LinearLayoutManager.HORIZONTAL, false));
        searchimg = vx.findViewById(R.id.searchimg);
        cancelimg = vx.findViewById(R.id.cancelimg);
        searchimgdone = vx.findViewById(R.id.searchimgdone);
//        mInputView = (LatinKeyboardView) vx.findViewById(R.id.keyboardxx);
        mInputView = vx.findViewById(R.id.keyboardxx);
        keyboardxxview = vx.findViewById(R.id.keyboard);
        tvSuggestion1 = vx.findViewById(R.id.tvSuggestion1);
        tvSuggestion2 = vx.findViewById(R.id.tvSuggestion2);
        tvSuggestion3 = vx.findViewById(R.id.tvSuggestion3);
        rvRealTimeSearch = vx.findViewById(R.id.rvRealTimeSearch);
        searchRecentGif = vx.findViewById(R.id.searchRecentGif);
        btnRealTimeSearchStatus = vx.findViewById(R.id.btnRealTimeSearchStatus);
        llMainRecentView = vx.findViewById(R.id.llMainRecentView);
        imgBtnGoExplore = vx.findViewById(R.id.imgBtnGoExplore);
        recentsubgifdataArrayList = new ArrayList<Gifdata>();
//        settingSesson = new SettingSesson(vx.getContext());
        settingsimg.setOnClickListener(view -> {
//                getCurrentInputConnection().commitText("",1);
            Intent intent = new Intent(view.getContext(), DictionaryActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        });
        searched.addTextChangedListener(new TextWatcher() {
            private String previousText = "";
            private String queryText = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searched.setSelection(searched.getText().length());
                previousText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null) {
                    llMainRecentView.setVisibility(View.VISIBLE);
                    cancelimg.setVisibility(View.VISIBLE);
                    searchimgdone.setVisibility(View.GONE);
                }else {
                   if (!s.toString().isEmpty()) {
                       String currentText = s.toString();
                       String[] words = currentText.split("\\s+");
                       if (words.length > 0) {
                           String lastWord = words[words.length - 1];
                           if (!lastWord.isEmpty() && !currentText.equals(previousText)) {
                               queryText = lastWord;
                           }
                       }

                       if (workRunnable != null) {
                           handler.removeCallbacks(workRunnable);
                       }

                       workRunnable = new Runnable() {
                           @Override
                           public void run() {
                               realTimeSearch(s.toString());
                               llMainRecentView.setVisibility(View.GONE);
                               fetchSuggestion(queryText, new SuggestionListener() {
                                   @Override
                                   public void onSuggestionReceived(ArrayList<String> suggestions) {
                                       switch (suggestions.size()) {
                                           case 1:
                                               tvSuggestion1.setText("");
                                               tvSuggestion2.setText(suggestions.get(0));
                                               tvSuggestion3.setText("");
                                               break;
                                           case 2:
                                               tvSuggestion1.setText(suggestions.get(1));
                                               tvSuggestion2.setText(suggestions.get(0));
                                               tvSuggestion3.setText("");
                                               break;
                                           case 3:
                                               tvSuggestion1.setText(suggestions.get(1));
                                               tvSuggestion2.setText(suggestions.get(0));
                                               tvSuggestion3.setText(suggestions.get(2));
                                               break;
                                           default:
                                               tvSuggestion1.setText("");
                                               tvSuggestion2.setText("");
                                               tvSuggestion3.setText("");
                                               break;
                                       }
                                   }
                               });
                           }
                       };
                       handler.postDelayed(workRunnable, 500);
                       cancelimg.setVisibility(View.GONE);
                       searchimgdone.setVisibility(View.VISIBLE);
                   } else {
                       llMainRecentView.setVisibility(View.VISIBLE);
                       handler.removeCallbacks(workRunnable);
                       btnRealTimeSearchStatus.setVisibility(View.GONE);
                       rvRealTimeSearch.setVisibility(View.GONE);
                       tvSuggestion1.setText("");
                       tvSuggestion2.setText("");
                       tvSuggestion3.setText("");
                   }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                searched.setSelection(searched.getText().length());
            }
        });
        searchimgdone.setOnClickListener(v -> {
//                onSearchDoneListner.onSearchDone(searched.getText().toString());
            int settingsize = Integer.parseInt(settingSesson.getMinimumcharacters());
            int edtxtsize = searched.getText().toString().length();
//                Log.e("search" , "txt sizes settingsize"+settingsize+" edtxtsize" +edtxtsize);
            if (settingsize > edtxtsize) {
                Toast.makeText(SoftKeyboard.this, "Search word is smaller than from settings", Toast.LENGTH_SHORT).show();
                return;
            } else {
                showGifGridview(searched.getText().toString(), historyviewmodel);
            }

            addUpdateWord();
        });
        searchimg.setOnTouchListener((v, event) -> {
            if (cancelimg.getVisibility() == View.GONE && searched.getVisibility() == View.INVISIBLE) {
                searched.setVisibility(View.VISIBLE);
                cancelimg.setVisibility(View.VISIBLE);
                searchimg.setVisibility(View.GONE);
                searched.requestFocus();
            }
            return true;
        });
        cancelimg.setOnTouchListener((v, event) -> {
            if (cancelimg.getVisibility() == View.VISIBLE && searched.getVisibility() == View.VISIBLE) {
                searched.setVisibility(View.INVISIBLE);
                cancelimg.setVisibility(View.GONE);
                searchimg.setVisibility(View.VISIBLE);

            }
            return true;
        });

        mInputView.setOnKeyboardActionListener(this);
        keyboardxxview.setOnKeyboardActionListener(this);
        mInputView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mInputView.closing();
            }
            return false;
        });

        String oldWord = searched.getText().toString();

        setLatinKeyboard(getSelectedSubtype());

        searched.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searched.post(() -> searched.requestFocus());
            }
            searched.setSelection(searched.getText().length());
        });

        tvSuggestion1.setOnClickListener(view ->
                updateSearchText(tvSuggestion1.getText().toString())
        );
        tvSuggestion2.setOnClickListener(view ->
                updateSearchText(tvSuggestion2.getText().toString())
        );
        tvSuggestion3.setOnClickListener(view ->
                updateSearchText(tvSuggestion3.getText().toString())
        );


        rvRealTimeSearch.addOnItemTouchListener(new RecyclerItemClickListener(this, rvRealTimeSearch, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                InputConnection ic = getCurrentInputConnection();
                Gifdata gifdata = realTimeSearchList.get(position);
                if (isLocalGifSupported()) {
                    if(settingSesson.getAppendlink()) {

                        ic.commitText(gifdata.getYoutubeUrl()  ,15);
                        ic.finishComposingText();
                    }else  if(settingSesson.getgiflink()) {
                        ic.commitText(gifdata.getMultilineText()  ,15);
                        ic.finishComposingText();
                    }
                } else {
                    ic.commitText(gifdata.getYoutubeUrl() + "\n" +gifdata.getMultilineText() ,15);
                    ic.finishComposingText();
                }

                Glide.with(SoftKeyboard.this).asGif()
                        .load(gifdata.getGif())
                        .into(new SimpleTarget<GifDrawable>() {
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                InputConnection inputConnection = getCurrentInputConnection();
                                if (isLocalGifSupported()) {
                                    if (settingSesson.getAppendlink()) {
                                        inputConnection.commitText(gifdata.getYoutubeUrl(), 15);
                                        inputConnection.finishComposingText();
                                    } else {
                                        inputConnection.commitText(gifdata.getMultilineText(), 15);
                                        inputConnection.finishComposingText();
                                    }
                                } else {
                                    inputConnection.commitText(gifdata.getYoutubeUrl() + "\n" + gifdata.getMultilineText() ,15);
                                    inputConnection.finishComposingText();
                                }
                            }

                            @Override
                            public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                                ByteBuffer byteBuffer = resource.getBuffer().duplicate();
                                FileOutputStream output;
                                try {
                                    imagesDir = new File(getFilesDir(), "sendgifs");
                                    if (!(imagesDir.exists())) {
                                        imagesDir.mkdir();
                                    }
                                    mGifFile = File.createTempFile("gifitem", ".gif", imagesDir);
                                    output = new FileOutputStream(mGifFile);
                                    byteBuffer.rewind();
                                    byte[] bytes = new byte[byteBuffer.remaining()];
                                    byteBuffer.get(bytes);
                                    output.write(bytes);
                                    output.close();
                                    SoftKeyboard.this.doCommitContent(mGifFile);


                                } catch (Exception e) {
                                    Log.d("RealTimeSearch", "onResourceReady: " + e.getLocalizedMessage());
                                }
                            }
                        });
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        btnRealTimeSearchStatus.setOnClickListener(view -> {
            handleGifNotFound(searched.getText().toString());
        });

        if (searched.getText().toString().equals("")) {
            llMainRecentView.setVisibility(View.VISIBLE);
            historyviewmodel = new Historyviewmodel(getApplication());

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
                    Gifgridviewadapter recentgifgridviewadapter = new Gifgridviewadapter(recentsubgifdataArrayList, this);
                    searchRecentGif.setAdapter(recentgifgridviewadapter);
                    searchRecentGif.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    recentgifgridviewadapter.notifyDataSetChanged();
                }

            });
        } else  {
            llMainRecentView.setVisibility(View.GONE);
        }

        searchRecentGif.addOnItemTouchListener(new RecyclerItemClickListener(this, searchRecentGif, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                InputConnection ic = getCurrentInputConnection();
                Gifdata gifdata = recentsubgifdataArrayList.get(position);
                if (isLocalGifSupported()) {
                    if(settingSesson.getAppendlink()) {

                        ic.commitText(gifdata.getYoutubeUrl()  ,15);
                        ic.finishComposingText();
                    }else  if(settingSesson.getgiflink()) {
                        ic.commitText(gifdata.getMultilineText()  ,15);
                        ic.finishComposingText();
                    }
                } else {
                    ic.commitText(gifdata.getYoutubeUrl() + "\n" +gifdata.getMultilineText() ,15);
                    ic.finishComposingText();
                }

                Glide.with(SoftKeyboard.this).asGif()
                        .load(gifdata.getGif())
                        .into(new SimpleTarget<GifDrawable>() {
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                InputConnection inputConnection = getCurrentInputConnection();
                                if (isLocalGifSupported()) {
                                    if (settingSesson.getAppendlink()) {
                                        inputConnection.commitText(gifdata.getYoutubeUrl(), 15);
                                        inputConnection.finishComposingText();
                                    } else {
                                        inputConnection.commitText(gifdata.getMultilineText(), 15);
                                        inputConnection.finishComposingText();
                                    }
                                } else {
                                    inputConnection.commitText(gifdata.getYoutubeUrl() + "\n" + gifdata.getMultilineText() ,15);
                                    inputConnection.finishComposingText();
                                }
                            }

                            @Override
                            public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                                ByteBuffer byteBuffer = resource.getBuffer().duplicate();
                                FileOutputStream output;
                                try {
                                    imagesDir = new File(getFilesDir(), "sendgifs");
                                    if (!(imagesDir.exists())) {
                                        imagesDir.mkdir();
                                    }
                                    mGifFile = File.createTempFile("gifitem", ".gif", imagesDir);
                                    output = new FileOutputStream(mGifFile);
                                    byteBuffer.rewind();
                                    byte[] bytes = new byte[byteBuffer.remaining()];
                                    byteBuffer.get(bytes);
                                    output.write(bytes);
                                    output.close();
                                    SoftKeyboard.this.doCommitContent(mGifFile);


                                } catch (Exception e) {
                                    Log.d("RealTimeSearch", "onResourceReady: " + e.getLocalizedMessage());
                                }
                            }
                        });
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        imgBtnGoExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gifpopupWindow == null || !gifpopupWindow.isShowing()) {
                    showGifGridview("", historyviewmodel);
                }
            }
        });

        return vx;
    }

    private void updateSearchText(String newText) {
        String currentText = searched.getText().toString().trim();
        String[] words = currentText.split("\\s+");
        if (words.length > 0) {
            words[words.length - 1] = newText;
            String updatedText = String.join(" ", words);
            searched.setText(updatedText);
            searched.setSelection(updatedText.length());
            mComposing.setLength(0);
            mComposing.append(updatedText);
        }
    }

    private void handleGifNotFound(String word) {
        String[] languages = settingSesson.getSlelectedlang().split(",");
        translatedWords.clear();
        transliteratedWords.clear();
        currentTranslationIndex = 0;
        currentTransliterationIndex = 0;

        translateWord(word, languages, () -> {
            transliterateWord(languages, translatedWords, () -> {
                StringBuilder output = new StringBuilder();

                for (String str : transliteratedWords) {
                    output.append(str).append("\n");
                }
                InputConnection ic = getCurrentInputConnection();
                ic.commitText(output.toString().trim(), 1);
                ic.finishComposingText();
            });
        });
    }

    private void translateWord(final String text, final String[] languages, Listener listener) {
        if (currentTranslationIndex >= languages.length) {
            listener.onComplete();
            return;
        }

        String targetLang = languages[currentTranslationIndex];
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" + targetLang + "&dt=t&q=" + text;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Translation", "Translation failed: " + e.getLocalizedMessage());
                currentTranslationIndex++;
                translateWord(text, languages, listener);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        String translatedText = jsonArray.getJSONArray(0).getJSONArray(0).getString(0);
                        translatedWords.add(translatedText);
                    } catch (JSONException e) {
                        Log.e("Translation", "JSON Parsing Error: " + e.getMessage());
                    }
                }
                currentTranslationIndex++;
                translateWord(text, languages, listener);
            }
        });
    }

    private void transliterateWord(String[] languages, ArrayList<String> translatedTexts, Listener listener) {
        if (currentTransliterationIndex >= translatedTexts.size()) {
            listener.onComplete();
            return;
        }

        String sourceLang = languages[currentTransliterationIndex];
        String textToTransliterate = translatedTexts.get(currentTransliterationIndex);

        if (!Objects.equals(sourceLang, "en")){
            try {
                String encodedText = URLEncoder.encode(textToTransliterate, "UTF-8");
                String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + sourceLang + "-Latn&dt=rm&q=" + encodedText;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).get().build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("Transliteration", "Failed: " + e.getLocalizedMessage());
                        currentTransliterationIndex++;
                        transliterateWord(languages, translatedTexts, listener);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseBody = response.body().string();
                            try {
                                JSONArray jsonArray = new JSONArray(responseBody);
                                String transliteratedText = jsonArray.getJSONArray(0).getJSONArray(0).getString(3);
                                transliteratedWords.add(transliteratedText);
                            } catch (JSONException e) {
                                Log.e("Transliteration", "JSON Error: " + e.getMessage());
                            }
                        }
                        currentTransliterationIndex++;
                        transliterateWord(languages, translatedTexts, listener);
                    }
                });
            } catch (Exception e) {
                Log.e("Transliteration", "Encoding Error: " + e.getMessage());
                listener.onComplete();
            }
        } else {
            currentTransliterationIndex++;
            transliteratedWords.add(textToTransliterate);
            transliterateWord(languages, translatedTexts, listener);
        }
    }

    public interface Listener {
        void onComplete();
    }

    private void realTimeSearch(String query) {
        if (!Objects.equals(query, "")) {
            rvRealTimeSearch.setVisibility(View.GONE);
            btnRealTimeSearchStatus.setVisibility(View.VISIBLE);
            btnRealTimeSearchStatus.setText("Searching Gif...");
            realTimeSearchList = new ArrayList<>();
            String baseUrl ="https://expressjs-api-chat-keyboard.onrender.com/api/v1/items?searchtext="+query+"&a=b";
            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest subcatjsonObjectRequest = new JsonObjectRequest(baseUrl, response -> {
                try{
                    JSONArray jsonArray = response.getJSONArray("items");
                    if (jsonArray.length() != 0) {
                        for (int i = 0; i<jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            realTimeSearchList.add(new Gifdata(object.getString("multiline_text"), object.getString("gif"),
                                    object.getString("thumbnail_gif"), object.getString("youtube_url") , false));
                        }
                        btnRealTimeSearchStatus.setVisibility(View.GONE);
                        rvRealTimeSearch.setVisibility(View.VISIBLE);
                        Gifgridviewadapter adapter = new Gifgridviewadapter(realTimeSearchList, this);
                        rvRealTimeSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        rvRealTimeSearch.setAdapter(adapter);
                    } else {
                        rvRealTimeSearch.setVisibility(View.GONE);
                        btnRealTimeSearchStatus.setText(getResources().getString(R.string.not_found_text,
                                searched.getText().toString()));
                    }
                }catch (Exception e) {
                    Log.d("RealTimeSearch", "realTimeSearch: " + e.getLocalizedMessage());
                }
            }, error -> {
                Log.d("RealTimeSearch", "realTimeSearch: " + error.getMessage());
                rvRealTimeSearch.setVisibility(View.GONE);
                btnRealTimeSearchStatus.setVisibility(View.VISIBLE);
                btnRealTimeSearchStatus.setText("Something went wrong...");
            });
            queue.add(subcatjsonObjectRequest);
        } else {
            btnRealTimeSearchStatus.setVisibility(View.GONE);
            rvRealTimeSearch.setVisibility(View.GONE);
        }
    }

    private void fetchSuggestion(String query, SuggestionListener listener) {
        if (!query.trim().isEmpty()) {
            Request request = new Request.Builder()
                    .url("https://api.datamuse.com/sug?s=" + query)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    listener.onSuggestionReceived(new ArrayList<>());
                    Log.d("WordSuggestions", "onFailure: " + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        listener.onSuggestionReceived(new ArrayList<>());
                        return;
                    }

                    ArrayList<String> topSuggestions = new ArrayList<>();
                    try {
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);

                        for (int i = 0; i < Math.min(jsonArray.length(), 3); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String word = jsonObject.getString("word");
                            topSuggestions.add(word);
                        }

                        new Handler(Looper.getMainLooper()).post(() -> listener.onSuggestionReceived(topSuggestions));

                    } catch (JSONException e) {
                        Log.d("WordSuggestions", "onResponse: " + e.getLocalizedMessage());
                    }
                }
            });
        } else {
            tvSuggestion1.setText("");
            tvSuggestion2.setText("");
            tvSuggestion3.setText("");
            listener.onSuggestionReceived(new ArrayList<>());
        }
    }

    private interface SuggestionListener {
        void onSuggestionReceived(ArrayList<String> suggestions);
    }

    private void doCommitContent(@NonNull File file) {
//        Log.e("Commiting" , "File");
        final EditorInfo editorInfo = getCurrentInputEditorInfo();

        // Validate packageName again just in case.
        if (!validatePackageName(editorInfo)) {
            return;
        }

        final Uri contentUri = FileProvider.getUriForFile(this, AUTHORITY, file);

        // As you as an IME author are most likely to have to implement your own content provider
        // to support CommitContent API, it is important to have a clear spec about what
        // applications are going to be allowed to access the content that your are going to share.
        final int flag;
        if (Build.VERSION.SDK_INT >= 25) {
            // On API 25 and later devices, as an analogy of Intent.FLAG_GRANT_READ_URI_PERMISSION,
            // you can specify InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION to give
            // a temporary read access to the recipient application without exporting your content
            // provider.
            flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
//          getCurrentInputConnection().performEditorAction(1);
        } else {
            // On API 24 and prior devices, we cannot rely on
            // InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION. You as an IME author
            // need to decide what access control is needed (or not needed) for content URIs that
            // you are going to expose. This sample uses Context.grantUriPermission(), but you can
            // implement your own mechanism that satisfies your own requirements.
            flag = 0;
            try {
                // TODO: Use revokeUriPermission to revoke as needed.
                grantUriPermission(
                        editorInfo.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e) {
                Log.e("SoftKeyboard", "grantUriPermission failed packageName=" + editorInfo.packageName
                        + " contentUri=" + contentUri, e);
            }
        }

        final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
                contentUri,
                new ClipDescription("xyz", new String[]{SoftKeyboard.MIME_TYPE_GIF}),
                null /* linkUrl */);

        InputConnectionCompat.commitContent(
                getCurrentInputConnection(), getCurrentInputEditorInfo(), inputContentInfoCompat,
                flag, null);

        // Switch back to the default keyboard

        boolean isDefaultKeyboard = settingSesson.switchKeyboardToDefault();
        if (isDefaultKeyboard) {
            switchToDefaultKeyboard();
        }

//        getCurrentInputConnection().commitText("sdfsd",1);
    }

    private void switchToDefaultKeyboard() {

        try {
            ContentResolver resolver = getContentResolver();
            String defaultKeyboard = Settings.Secure.getString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD);
            Settings.Secure.putString(resolver, Settings.Secure.ENABLED_INPUT_METHODS, defaultKeyboard);
            Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD, defaultKeyboard);
        } catch (Exception e) {
            Log.e("KeyboardSwitch", "Failed to switch to default keyboard", e);
            InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
        }


    }

    private boolean validatePackageName(@Nullable EditorInfo editorInfo) {
        if (editorInfo == null) {
            return false;
        }
        final String packageName = editorInfo.packageName;
        if (packageName == null) {
            return false;
        }

        // In Android L MR-1 and prior devices, EditorInfo.packageName is not a reliable identifier
        // of the target application because:
        //   1. the system does not verify it [1]
        //   2. InputMethodManager.startInputInner() had filled EditorInfo.packageName with
        //      view.getContext().getPackageName() [2]
        // [1]: https://android.googlesource.com/platform/frameworks/base/+/a0f3ad1b5aabe04d9eb1df8bad34124b826ab641
        // [2]: https://android.googlesource.com/platform/frameworks/base/+/02df328f0cd12f2af87ca96ecf5819c8a3470dc8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }

        final InputBinding inputBinding = getCurrentInputBinding();
        if (inputBinding == null) {
            // Due to b.android.com/225029, it is possible that getCurrentInputBinding() returns
            // null even after onStartInputView() is called.
            // TODO: Come up with a way to work around this bug....
            Log.e("SoftKeyboard", "inputBinding should not be null here. " + "You are likely to be hitting b.android.com/225029");
            return false;
        }
        final int packageUid = inputBinding.getUid();

        final AppOpsManager appOpsManager =
                (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        try {

            appOpsManager.checkPackage(packageUid, packageName);
        } catch (Exception e) {
            return false;
        }
        return true;

    }


    /**
     * Use the right subtype based on language selected.
     *
     * @return mCurKeyboard
     */
    private LatinKeyboard getSelectedSubtype() {
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        String s = subtype.getLocale();
        switch (s) {
            case "ps_AF":
                mActiveKeyboard = "ps_AF";
//                mCurKeyboard = mPashtoKeyboard;
                break;
            case "ps_latin_AF":
                mActiveKeyboard = "ps_latin_AF";
//                mCurKeyboard = mPashtoLatinKeyboard;
                break;
            case "fa_AF":
                mActiveKeyboard = "fa_AF";
//                mCurKeyboard = mFarsiKeyboard;
                break;
            default:
                mActiveKeyboard = "en_US";
                mCurKeyboard = mQwertyKeyboard;

        }

        return mCurKeyboard;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        //boolean shouldSupportLanguageSwitchKey = mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
//        nextKeyboard.setLanguageSwitchKeyVisibility(true);
        //+++++++++Keyboard setting++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        mInputView.setKeyboard(nextKeyboard);
        keyboardxxview.setKeyboard(nextKeyboard);
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        historyClicked = SoftKeyboard.this;
//
        return mCandidateView;
    }

//    @Override
//    public void onWindowShown() {
////        onKey(-111334, null);
//        new CountDownTimer(300, 100) {
//
//            public void onTick(long millisUntilFinished) {
//                Log.e("Counter" , "tickin"+"seconds remaining: " + millisUntilFinished / 1000);
////                textView.setText("seconds remaining: " + millisUntilFinished / 1000);
//            }
//
//            public void onFinish() {
//                handleScreenUi();
//            }
//        }.start();
//
//        super.onWindowShown();
//    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed ic_information_48
     * about the target of our edits.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        // Restart the InputView to apply right theme selected.
        setInputView(onCreateInputView());

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.

        mComposing.setLength(0);


        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
//                mCurKeyboard = mNumbersKeyboard;
                break;
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
//                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
//                mCurKeyboard = mPhoneKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = getSelectedSubtype();
                mPredictionOn = true;
//                mPredictionOn = sharedPreferences.getBoolean("suggestion", true);

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                    mActiveKeyboard = "en_US";
                    mCurKeyboard = mQwertyKeyboard;
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = getSelectedSubtype();
                updateShiftKeyState(attribute);
        }
//        if (mCurKeyboard == mPashtoLatinKeyboard || mCurKeyboard == mPashtoLatinShiftedKeyboard)
        mPredictionOn = true;
        //            db = new DatabaseManager(this);
        historyviewmodel = new Historyviewmodel(getApplication());
        historyviewmodel.getAllWords().observeForever(words -> {
            if (words.isEmpty())
                return;
            usershisatories.clear();
            Historymodal firstword = null;
//            Log.e("Got" , "words :"+words.get(0).getTitle());
            for (int i = 0; i < words.size(); i++) {
                if (i == 0) {
                    firstword = words.get(i);
                    usershisatories.add(words.get(i));
                } else {
                    if (!(words.get(i).getTitle().equals(firstword.getTitle()))) {
                        usershisatories.add(words.get(i));
                        firstword = words.get(i);
                    }

                }

            }
//            usershisatories.addAll(words);

            historyadapter = new Historyadapter(usershisatories, getApplication(), userDao, historyClicked);
            historyrecs.setAdapter(historyadapter);
            historyadapter.notifyDataSetChanged();
        });

//        historyDatabase = HistoryDatabase.getInstance(this);

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                userDao = historyDatabase.Dao();
//                usershisatories = (ArrayList<Historymodal>) userDao.getAllHistories();
//                if(usershisatories.isEmpty())
//                {
//                    Log.e("No history" , "found");
////                    historyDatabase.close();
////                    historyrecs
////                        usershisatories.add( new Historymodal(1 ,"test" , "S" , "ww.gg.com" , 1));
//                }else {
//                    Log.e(" history" , "found"+usershisatories.get(0).getTitle());
//
//
////                    historyDatabase.close();
//                }
//            }
//        });

//            historyDatabase = Room.databaseBuilder(getApplicationContext(),
//                    HistoryDatabase.class, "database-name").build();
//            viewmodal = new ViewModelProvider(this).get(ViewModal.class);

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);

//        mSound = sharedPreferences.getBoolean("sound", true);
        mSound = false;

        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
        hideGifPopupWindow();
        handleScreenUi();
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();


        // Clear current composing text and candidates.
        mComposing.setLength(0);


        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }
        hideGifPopupWindow();

    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);

//++++++++++++++++++++++++++++++++++++++++++ Dismiss the Emoticons before showing the soft keyboard.++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        closeEmoticons();

        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);
        String[] mimeTypes = EditorInfoCompat.getContentMimeTypes(attribute);


        for (String mimeType : mimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, "image/gif")) {
                gifSupported = true;
            }
        }
        handleScreenUi();
        if (cancelimg.getVisibility() == View.GONE && searched.getVisibility() == View.INVISIBLE) {
            searched.setVisibility(View.VISIBLE);
            cancelimg.setVisibility(View.VISIBLE);
            searchimg.setVisibility(View.GONE);
            searched.requestFocus();
            searched.setSelection(searched.getText().length());
        }
    }

    /**
     * Switch to language when it is changed from Choose Input Method.
     */
    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
        String s = subtype.getLocale();
        switch (s) {
            case "ps_AF":
                mActiveKeyboard = "ps_AF";
//                mCurKeyboard = mPashtoKeyboard;
                break;
            case "ps_latin_AF":
                mActiveKeyboard = "ps_latin_AF";
//                mCurKeyboard = mPashtoLatinKeyboard;
                break;
            case "fa_AF":
                mActiveKeyboard = "fa_AF";
//                mCurKeyboard = mFarsiKeyboard;
                break;
            default:
                mActiveKeyboard = "en_US";
                mCurKeyboard = mQwertyKeyboard;
        }

        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);

            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    @Override
    public Dialog getWindow() {
        return super.getWindow();
    }

    @Override
    public boolean isInputViewShown() {
//        onKey(-111334, null);

        return super.isInputViewShown();
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourselves, since the editor can not be seen
     * in that situation.
     */

    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<>();
            for (CompletionInfo ci : completions) {
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);
            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length() - 1);
            }
        }

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;

            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp();
                            keyDownUp();
                            keyDownUp();
                            keyDownUp();
                            keyDownUp();
                            keyDownUp();
                            keyDownUp();
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);

        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null
                && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(mCapsLock || caps != 0);

            // Change Shift key icon - 2
            updateShiftIcon();
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        return Character.isLetter(code);
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp() {
        Log.e("Here ", "key event");
        onKey(-111334, null);

    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        if (keyCode == '\n') {
            keyDownUp();
        } else {
            if (keyCode >= '0' && keyCode <= '9') {
                keyDownUp();
            } else {
                getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
            }
        }
    }

    /**
     * Implementation of KeyboardViewListener
     */
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
        } else if (primaryCode == -10) {  // This is the code for ?123 / ABC switch
            // Switch keyboard
            if (mInputView.getKeyboard() == mQwertyKeyboard) {
                mCurKeyboard = mSymbolsKeyboard;
            } else {
                mCurKeyboard = mQwertyKeyboard;
            }
            setLatinKeyboard(mCurKeyboard);
        } else if (primaryCode == -111334) {
            // Show Emoticons/GIF handling
            if (searched.getText().toString().equals("")) {
                showGifGridview("", historyviewmodel);
            } else {
                showGifGridview(searched.getText().toString(), historyviewmodel);
            }
        } else {
            handleCharacter(primaryCode);
        }

        if (mSound) playClick(primaryCode);
    }


    /**
     * Play sound when key is pressed.
     */
    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            switch (keyCode) {
                case 32:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                    break;
                case Keyboard.KEYCODE_DONE:
                case 10:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                    break;
                case Keyboard.KEYCODE_DELETE:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                    break;
                default:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
            }
        }
    }

    public void onText(CharSequence text) {
        Log.e("got", "text" + text);
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
//        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */


    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    private void handleBackspace() {
        try {
            String currentText = searched.getText().toString();
            int cursorPosition = searched.getSelectionStart();

            if (currentText.length() > 0 && cursorPosition > 0) {
                // Remove character before cursor
                StringBuilder newText = new StringBuilder(currentText);
                newText.deleteCharAt(cursorPosition - 1);

                mComposing.setLength(0);
                mComposing.append(newText);

                searched.setText(newText);
                searched.setSelection(cursorPosition - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }

        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mQwertyKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        }

        updateShiftIcon();
    }

    /**
     * Change shift icon
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateShiftIcon() {
        List<Keyboard.Key> keys = mQwertyKeyboard.getKeys();
        Keyboard.Key currentKey;
        for (int i = 0; i < keys.size() - 1; i++) {
            currentKey = keys.get(i);
            mInputView.invalidateAllKeys();
            if (currentKey.codes[0] == -1) {
                currentKey.label = null;
                if (mInputView.isShifted() || mCapsLock) {
                    currentKey.icon = getResources().getDrawable(R.drawable.ic_keyboard_capslock_on_24dp);
                } else {
                    currentKey.icon = getResources().getDrawable(R.drawable.ic_keyboard_capslock_24dp);
                }
                break;
            }
        }
    }

    private void handleCharacter(int primaryCode) {
        if (searched.getVisibility() != View.VISIBLE) {
            if (searchimg.getVisibility() == View.VISIBLE) {
                Toast.makeText(this, "Tap on search icon first", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        searched.requestFocus();

        // Handle backspace
        if (primaryCode == -5) {
            try {
                String currentText = searched.getText().toString();
                int cursorPosition = searched.getSelectionStart();

                if (currentText.length() > 0 && cursorPosition > 0) {
                    StringBuilder newText = new StringBuilder(currentText);
                    newText.deleteCharAt(cursorPosition - 1);

                    mComposing.setLength(0);
                    mComposing.append(newText);

                    searched.setText(newText);
                    searched.setSelection(cursorPosition - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // Check if it's a special character that needs custom handling
        boolean isSpecialChar = false;
        switch (primaryCode) {
            case 46: // dot (.)
            case 64: // at (@)
            case 35: // hash (#)
            case 36: // dollar ($)
            case 95: // underscore (_)
            case 38: // ampersand (&)
            case 45: // minus (-)
            case 43: // plus (+)
            case 40: // left parenthesis (
            case 41: // right parenthesis )
            case 47: // forward slash (/)
            case 32: // space
            case -14111123: // alternative space code
                isSpecialChar = true;
                break;
        }

        // Handle all characters including special ones
        if (primaryCode > 0) {
            char character = (char) primaryCode;

            // shift only for letters
            if (isInputViewShown() && mInputView.isShifted() && Character.isLetter(character)) {
                character = Character.toUpperCase(character);
            }

            mComposing.append(character);
            String newText = mComposing.toString();
            searched.setText(newText);
            searched.setSelection(newText.length());

            if (isSpecialChar) {
                getCurrentInputConnection().finishComposingText();
            }
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }

        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), true /* onlyCurrentIme */);
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
        }
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    private boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    // Tap on suggestion to commit
    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0 && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }

            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here. But for this sample,
            // we will just commit the current text.

            commitTyped(getCurrentInputConnection());
        }
    }

    public void swipeRight() {
        if (mCompletionOn) {
            pickDefaultCandidate();
        }
    }

    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
        mInputView.setPreviewEnabled(true);

        // Disable preview key on Shift, Delete, Space, Language, Symbol and Emoticon.
        if (primaryCode == -1 || primaryCode == -2 || primaryCode == -10000
                || primaryCode == -101 || primaryCode == 32) {
            mInputView.setPreviewEnabled(false);
        } else if (primaryCode == -5) {
            // Remove backspace handling from onPress completely
            mInputView.setPreviewEnabled(false);
        } else if (primaryCode == -14111123) {
            searched.setText(searched.getText().toString());
        }
    }

    public void onRelease(int primaryCode) {
    }

    /**
     * This method displays Gifs when called.
     */
    private void showGifGridview(String searchtext, Historyviewmodel historyviewmodel) {
        try {
//            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
//        View view = dialog.getView();
            if (layoutInflater != null) {

                @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.gifgridviewlayout, null);
                gifpopupWindow = new Gifgridviewpopup(popupView, this, searchtext, historyviewmodel);
                gifpopupWindow.setSizeForSoftKeyboard();
                gifpopupWindow.setSize(gifpopupWindow.getWidth(), (gifpopupWindow.getHeight()));
//                gifpopupWindow.setSize(mQwertyKeyboard.getMinWidth() +20, (mQwertyKeyboard.getHeight() +120));
                gifpopupWindow.showAtLocation(mInputView.getRootView(), Gravity.BOTTOM, 0, 0);
                gifpopupWindow.setOnGifclickedListnermethod((gifitem, settingSesson, pos1) -> {
                    settingSesson = new SettingSesson(gifpopupWindow.getContentView().getContext());

                    boolean sessionsx = settingSesson.getAppendlink();
                    Boolean sessionsxgiflink = settingSesson.getgiflink();
                    if (pos1 == 1) {
                        SoftKeyboard.this.historyviewmodel.insertgif(new RecentGifEntity(new Gson().toJson(gifitem, Gifdata.class)));
                    }

                    Log.e("settingSesson Gifdata", "Gif getAppendlink " + sessionsx);
                    Glide.with(popupView.getContext()).asGif()
                            .load(gifitem.getGif())
                            .into(new SimpleTarget<GifDrawable>() {

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    InputConnection inputConnection = getCurrentInputConnection();
                                    if (isLocalGifSupported()) {
                                        if (sessionsx) {
                                            inputConnection.commitText(gifitem.getYoutubeUrl(), 15);
                                            inputConnection.finishComposingText();
                                        } else {
                                            inputConnection.commitText(gifitem.getMultilineText(), 15);
                                            inputConnection.finishComposingText();
                                        }
                                    } else {
                                        inputConnection.commitText(gifitem.getYoutubeUrl() + "\n" + gifitem.getMultilineText(), 15);
                                        inputConnection.finishComposingText();
                                    }
                                }

                                @Override
                                public void onResourceReady(@NonNull GifDrawable gifDrawable, @Nullable Transition<? super GifDrawable> transition) {
                                    InputConnection ic = getCurrentInputConnection();
                                    if (isLocalGifSupported()) {
                                        if (sessionsx) {

                                            ic.commitText(gifitem.getYoutubeUrl(), 15);
                                            ic.finishComposingText();
                                        } else if (sessionsxgiflink) {
                                            ic.commitText(gifitem.getMultilineText(), 15);
                                            ic.finishComposingText();
                                        }

                                        Log.e("It is ", "Gif supported");
                                    } else {
                                        Log.e("It is ", "Gif NOT supported");
                                        ic.commitText(gifitem.getYoutubeUrl() + "\n" + gifitem.getMultilineText(), 15);
                                        ic.finishComposingText();
                                    }

                                    ByteBuffer byteBuffer = gifDrawable.getBuffer().duplicate();
                                    FileOutputStream output;
                                    try {
                                        imagesDir = new File(getFilesDir(), "sendgifs");
                                        if (!(imagesDir.exists())) {
                                            imagesDir.mkdir();
                                        }
//                                        imagesDir = new File(getFilesDir(), "sendgifs");
                                        mGifFile = File.createTempFile("gifitem", ".gif", imagesDir);
//                                        mGifFile = File.createTempFile("gifitem", ".gif", outputDir);

                                        output = new FileOutputStream(mGifFile);
                                        byteBuffer.rewind();
                                        byte[] bytes = new byte[byteBuffer.remaining()];
                                        byteBuffer.get(bytes);
                                        output.write(bytes);
                                        output.close();
                                        SoftKeyboard.this.doCommitContent(mGifFile);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                });

            }
        } catch (Exception e) {
            Log.e("softkeyboard", "token not found");
            e.printStackTrace();

        }

    }

    private boolean isLocalGifSupported() {
        String[] mimeTypes = EditorInfoCompat.getContentMimeTypes(getCurrentInputEditorInfo());
        boolean localgifSupported = false;
        for (String mimeType : mimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, "image/gif")) {
                localgifSupported = true;
            }
        }
        return localgifSupported;
    }

    @Override
    public boolean onCommitContent(@NonNull InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
        return false;
    }

    @Override
    public void OnHstoryclicked(Historymodal historymodal) {
        showGifGridview(historymodal.getTitle(), historyviewmodel);
        Log.e("Got", "some model");
    }


    /**
     * Add or update word in the dictionary
     */
    public void addUpdateWord() {

        Log.e("Historymodal", "getLastWord()" + getLastWord());

        historyviewmodel.insert(new Historymodal(searched.getText().toString(), "S", "ww.gosdgf", 1));

        AsyncTask.execute(() -> Log.e("Historymodal", "getLastWord()" + searched.getText().toString()));

    }

    public String getLastWord() {
        CharSequence inputChars = getCurrentInputConnection().getTextBeforeCursor(50, 0);
        String inputString = String.valueOf(inputChars);

        return inputString.substring(inputString.lastIndexOf(" ") + 1);
    }

    private void handleScreenUi() {
        if (settingSesson.getShowSearchScreenByDefault()) {
            if (cancelimg.getVisibility() == View.GONE && searched.getVisibility() == View.INVISIBLE) {
                searched.setVisibility(View.VISIBLE);
                cancelimg.setVisibility(View.VISIBLE);
                searchimg.setVisibility(View.GONE);
                searched.requestFocus();
                searched.setSelection(searched.getText().length());
            }
            hideGifPopupWindow();
        } else {
            if (gifpopupWindow == null || !gifpopupWindow.isShowing()) {
                showGifGridview("", historyviewmodel);
            }
        }
    }

    private void hideGifPopupWindow() {
        if (gifpopupWindow != null && gifpopupWindow.isShowing()) {
            gifpopupWindow.dismiss();
        }
    }

}