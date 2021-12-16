/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.optimum.coolkeybord.android;


import static android.view.inputmethod.InputConnection.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Dialog;
//import android.content.SharedPreferences;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.content.FileProvider;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.optimum.coolkeybord.R;
import com.optimum.coolkeybord.adapter.CategoriesAdapter;
import com.optimum.coolkeybord.database.Dao;

import com.optimum.coolkeybord.database.HistoryDatabase;
import com.optimum.coolkeybord.gifview.Gifgridviewpopup;
import com.optimum.coolkeybord.listners.GifDownloader;
import com.optimum.coolkeybord.models.Categoriesmodel;
import com.optimum.coolkeybord.models.Gifdata;
import com.optimum.coolkeybord.models.Historymodal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class SoftKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener ,  InputConnectionCompat.OnCommitContentListener {

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
    private ImageView searchimg;
    private ImageView cancelimg;
    private ImageView searchimgdone;
    private File mGifFile;
    private static final String AUTHORITY = "com.optimum.coolkeybord";
    private static final String MIME_TYPE_GIF = "image/gif";
    //+++++++++++
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;
    private StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private boolean mSound;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    private String mWordSeparators;
    // Keyboards (not subtypes)
    private LatinKeyboard mQwertyKeyboard;
//    private LatinKeyboard mFarsiKeyboard;
//    private LatinKeyboard mPashtoKeyboard;
//    private LatinKeyboard mPashtoLatinKeyboard;
//    private LatinKeyboard mPashtoLatinShiftedKeyboard;
    private LatinKeyboard mNumbersKeyboard;
//    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
//    private LatinKeyboard mSymbolsAFKeyboard;
    private LatinKeyboard mSymbolsShiftedAFKeyboard;
    private LatinKeyboard mPhoneKeyboard;
    private LatinKeyboard mCurKeyboard;
    public static String mActiveKeyboard;
//    private EmojiconsPopup popupWindow = null;
    private Gifgridviewpopup gifpopupWindow = null;
//    private DatabaseManager db;
    private ArrayList<String> list;
//    SharedPreferences sharedPreferences;
    //++++For room database+++++
    HistoryDatabase historyDatabase;

    int[] THE_LAYOUTS = {R.layout.input_1};
//    int[] THE_LAYOUTS = {R.layout.input_1, R.layout.input_2, R.layout.input_3,
//            R.layout.input_4, R.layout.input_5, R.layout.input_6, R.layout.input_7,
//            R.layout.input_8, R.layout.input_9, R.layout.input_10};
    private Dao userDao;
    private File imagesDir;
    @Override
    public void onCreate() {
        super.onCreate();

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        // TODO: Avoid file I/O in the main thread.
      imagesDir = new File(getFilesDir(), "sendgifs");
        imagesDir.mkdirs();


//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private static File getFileForResource(@NonNull Context context, @RawRes int res, @NonNull File outputDir, @NonNull String filename) {
        final File outputFile = new File(outputDir, filename);
        final byte[] buffer = new byte[4096];
        InputStream resourceReader = null;
        try {
            try {
                resourceReader = context.getResources().openRawResource(res);
                OutputStream dataWriter = null;
                try {
                    dataWriter = new FileOutputStream(outputFile);
                    while (true) {
                        final int numRead = resourceReader.read(buffer);
                        if (numRead <= 0) {
                            break;
                        }
                        dataWriter.write(buffer, 0, numRead);
                    }
                    return outputFile;
                } finally {
                    if (dataWriter != null) {
                        dataWriter.flush();
                        dataWriter.close();
                    }
                }
            } finally {
                if (resourceReader != null) {
                    resourceReader.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }

        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
//        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.symbols_shift);
//        mFarsiKeyboard = new LatinKeyboard(this, R.xml.farsi);
//        mPashtoKeyboard = new LatinKeyboard(this, R.xml.pashto);
//        mPashtoLatinKeyboard = new LatinKeyboard(this, R.xml.pashto_latin);
//        mPashtoLatinShiftedKeyboard = new LatinKeyboard(this, R.xml.pashto_latin_shift);
        mNumbersKeyboard = new LatinKeyboard(this, R.xml.numbers);
//        mSymbolsAFKeyboard = new LatinKeyboard(this, R.xml.symbols_af);
        mSymbolsShiftedAFKeyboard = new LatinKeyboard(this, R.xml.symbols_shift_af);
        mPhoneKeyboard = new LatinKeyboard(this, R.xml.phone);

    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateInputView() {

        // Set custom theme to input view.
       View  vx = (LinearLayout) getLayoutInflater().inflate(R.layout.input_1, null);
//       View  vx = (LinearLayout) getLayoutInflater().inflate(THE_LAYOUTS[sharedPreferences.getInt(THEME_KEY, 0)], null);
        searched = (EditText) vx.findViewById(R.id.searched);
        searchimg =  vx.findViewById(R.id.searchimg);
        cancelimg =  vx.findViewById(R.id.cancelimg);
        searchimgdone =  vx.findViewById(R.id.searchimgdone);
        mInputView = (LatinKeyboardView) vx.findViewById(R.id.keyboard);
        keyboardxxview = (LatinKeyboardView) vx.findViewById(R.id.keyboardxx);

        searched.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s ==null)
                {
                    cancelimg.setVisibility(View.VISIBLE);
                    searchimgdone.setVisibility(View.GONE);
                }else {
                    cancelimg.setVisibility(View.GONE);
                    searchimgdone.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchimgdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onSearchDoneListner.onSearchDone(searched.getText().toString());
                if(searched.getText().toString().equals(""))
                {
                    showGifGridview("");
                }else {
                    showGifGridview(searched.getText().toString());
                }
            }
        });
        searchimg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(cancelimg.getVisibility() == View.GONE && searched.getVisibility() == View.INVISIBLE)
                {
                    searched.setVisibility(View.VISIBLE);
                    cancelimg.setVisibility(View.VISIBLE);
                    searchimg.setVisibility(View.GONE);
                }
                return true;
            }
        });
        cancelimg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(cancelimg.getVisibility() == View.VISIBLE && searched.getVisibility() == View.VISIBLE)
                {
                    searched.setVisibility(View.INVISIBLE);
                    cancelimg.setVisibility(View.GONE);
                    searchimg.setVisibility(View.VISIBLE);

                }
                return true;
            }
        });
        mInputView.setOnKeyboardActionListener(this);
        keyboardxxview.setOnKeyboardActionListener(this);
//        keyboardxxview.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
//            @Override
//            public void onPress(int primaryCode) {
//                Log.e("primaryCode" , "is"+primaryCode);
//            }
//
//            @Override
//            public void onRelease(int primaryCode) {
//
//            }
//
//            @Override
//            public void onKey(int primaryCode, int[] keyCodes) {
//                Log.e("primaryCode" , "is"+primaryCode);
//            }
//
//            @Override
//            public void onText(CharSequence text) {
//                Log.e("Text" , "is"+text);
//            }
//
//            @Override
//            public void swipeLeft() {
//
//            }
//
//            @Override
//            public void swipeRight() {
//
//            }
//
//            @Override
//            public void swipeDown() {
//
//            }
//
//            @Override
//            public void swipeUp() {
//
//            }
//        });onPress
        // Close popup keyboard when screen is touched, if it's showing
        mInputView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mInputView.closing();
                }
                return false;
            }
        });

        // Apply the selected keyboard to the input view.
        setLatinKeyboard(getSelectedSubtype());

//        return mInputView;
        return vx;
    }

    private void doCommitContent(@NonNull String description, @NonNull String mimeType,
                                 @NonNull File file) {
        Log.e("Commiting" , "File");
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
          getCurrentInputConnection().performEditorAction(1);
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
            } catch (Exception e){
                Log.e("SoftKeyboard", "grantUriPermission failed packageName=" + editorInfo.packageName
                        + " contentUri=" + contentUri, e);
            }
        }

        final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(description, new String[]{mimeType}),
                null /* linkUrl */);

           InputConnectionCompat.commitContent(
                getCurrentInputConnection(), getCurrentInputEditorInfo(), inputContentInfoCompat,
                flag, null);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final AppOpsManager appOpsManager =
                    (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            try {
                appOpsManager.checkPackage(packageUid, packageName);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        final PackageManager packageManager = getPackageManager();
        final String possiblePackageNames[] = packageManager.getPackagesForUid(packageUid);
        for (final String possiblePackageName : possiblePackageNames) {
            if (packageName.equals(possiblePackageName)) {
                return true;
            }
        }
        return false;
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

        return mCandidateView;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed ic_information_48
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        // Restart the InputView to apply right theme selected.
        setInputView(onCreateInputView());

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.

        mComposing.setLength(0);
        updateCandidates();

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
                mCurKeyboard = mNumbersKeyboard;
                break;
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
//                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mPhoneKeyboard;
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
            mPredictionOn = false;
        if (mPredictionOn)
        {
//            db = new DatabaseManager(this);
            historyDatabase = HistoryDatabase.getInstance(this);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    userDao = historyDatabase.Dao();
                    List<Historymodal> usershisatories = userDao.getAllHistories();
                    if(usershisatories.isEmpty())
                    {
                        Log.e("No history" , "found"+usershisatories.size());

//                        usershisatories.add( new Historymodal(1 ,"test" , "S" , "ww.gg.com" , 1));
                    }else {
                        Log.e(" history" , "found"+usershisatories.size());
                    }
                }
            });

//            historyDatabase = Room.databaseBuilder(getApplicationContext(),
//                    HistoryDatabase.class, "database-name").build();
//            viewmodal = new ViewModelProvider(this).get(ViewModal.class);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);

//        mSound = sharedPreferences.getBoolean("sound", true);
        mSound = false;

        // Apply the selected keyboard to the input view.
        setLatinKeyboard(mCurKeyboard);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();

        // clear suggestions list
        clearCandidateView();

        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }

//        if (db != null)
//        {
//
//            db.close();
//        }
        if (historyDatabase != null)
        {

            historyDatabase.close();
        }

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


        boolean gifSupported = false;
        for (String mimeType : mimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, "image/gif")) {
                gifSupported = true;
            }
        }

        if (gifSupported) {
          Log.e("It is " , "Gif supported");
        } else {
            Log.e("It is " , "Gif NOT supported");
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
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
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
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
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
            updateCandidates();
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
    private void keyDownUp(int keyEventCode) {
        Log.e("Here " , "key event");
      onKey(-111334 , null);
//        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
//        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    /**
     * Implementation of KeyboardViewListener
     */
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.e("ON" , "key working"+primaryCode);
//        if (mComposing.length() > 0) {
            if (list != null) {
                Log.e("List" , "is not empty");
                clearCandidateView();
            }
//        }

        Log.e("List" , "is empty");
        // Add update word in the dictionary
        addUpdateWord();
        if (isWordSeparator(primaryCode)) {
            // Handle separator
            if (mComposing.length() > 0) {
                commitTyped(getCurrentInputConnection());
            }
            if (primaryCode == 32) {
                if (list != null) {
                    Log.e("List" , "is not empty");
                    clearCandidateView();
                }
                Log.e("List" , "is empty");
                // Add update word in the dictionary
                addUpdateWord();
            }
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
//            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
            // Show a menu or something'
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
//            if ((current == mSymbolsAFKeyboard || current == mSymbolsShiftedAFKeyboard)
//                    && getSelectedSubtype() == mFarsiKeyboard) {
//                setLatinKeyboard(mFarsiKeyboard);
//                updateShiftIcon();
//            } else
//                if ((current == mSymbolsAFKeyboard || current == mSymbolsShiftedAFKeyboard)
//                    && getSelectedSubtype() == mPashtoKeyboard) {
//                setLatinKeyboard(mPashtoKeyboard);
//                updateShiftIcon();
//            } else if (current == mFarsiKeyboard || current == mPashtoKeyboard) {
//                setLatinKeyboard(mSymbolsAFKeyboard);
//                mSymbolsAFKeyboard.setShifted(false);
//            } else if ((current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) && getSelectedSubtype() == mPashtoLatinKeyboard) {
//                setLatinKeyboard(mPashtoLatinKeyboard);
//                updateShiftIcon();
//            } else
//                if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
//                setLatinKeyboard(mQwertyKeyboard);
//                updateShiftIcon();
//            } else {
//                setLatinKeyboard(mSymbolsKeyboard);
//                mSymbolsKeyboard.setShifted(false);
//            }

        } else if (primaryCode == -10000) {
            // Show Emoticons
//            showEmoticons();
        } else if (primaryCode == -111334) {
            // Show Emoticons
            Log.e("Working" , "On this");
//            showEmoticons();
            if(searched.getText().toString().equals(""))
            {
                showGifGridview("");
            }else {
                showGifGridview(searched.getText().toString());
            }

        } else if (primaryCode == -10001) {
            // Zero Space
            mComposing.append("\u200C");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (primaryCode == -10002) {
            // ẋ
            mComposing.append("ẋ");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (primaryCode == -10003) {
            // Ẋ
            mComposing.append("\u1E8A");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (primaryCode == 1567) {
            // Question mark.
            mComposing.append("\u061F");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else {
            ///++++++++++++++++++++++++++++++++++++++++After key presse and it's character+++

            handleCharacter(primaryCode, keyCodes);
        }

        if (mSound) playClick(primaryCode); // Play sound with button click.
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
        Log.e("got" , "text"+text);
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        if (!mCompletionOn && mPredictionOn) {
            if (mComposing.length() > 0) {
                SelectDataTask selectDataTask = new SelectDataTask();

                list = new ArrayList<>();
                list.add(mComposing.toString());

                selectDataTask.getSubtype(mCurKeyboard);
                selectDataTask.execute(mComposing.toString());
            } else {
                setSuggestions(null, false, false);
            }
        }
    }

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
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
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
//        else if (currentKeyboard == mSymbolsKeyboard) {
//            mSymbolsKeyboard.setShifted(true);
//            setLatinKeyboard(mSymbolsShiftedKeyboard);
//            mSymbolsShiftedKeyboard.setShifted(true);
//        } else if (currentKeyboard == mSymbolsAFKeyboard) {
//            mSymbolsKeyboard.setShifted(true);
//            setLatinKeyboard(mSymbolsShiftedAFKeyboard);
//            mSymbolsShiftedKeyboard.setShifted(true);
//        }
//        else if (currentKeyboard == mSymbolsShiftedAFKeyboard) {
//            mSymbolsShiftedAFKeyboard.setShifted(false);
//            setLatinKeyboard(mSymbolsAFKeyboard);
//            mSymbolsAFKeyboard.setShifted(false);
//        }
//        else if (currentKeyboard == mSymbolsShiftedKeyboard) {
//            mSymbolsShiftedKeyboard.setShifted(false);
//            setLatinKeyboard(mSymbolsKeyboard);
//            mSymbolsKeyboard.setShifted(false);
//        }
//        else if (mPashtoLatinKeyboard == currentKeyboard) {
//            setLatinKeyboard(mPashtoLatinShiftedKeyboard);
//            mActiveKeyboard = "ps_latin_AF_Shift";
//            mPashtoLatinKeyboard.setShifted(false);
//        } else if (mPashtoLatinShiftedKeyboard == currentKeyboard) {
//            setLatinKeyboard(mPashtoLatinKeyboard);
//            mActiveKeyboard = "ps_latin_AF";
//            mPashtoLatinShiftedKeyboard.setShifted(false);
//        }

        updateShiftIcon();
    }

    /**
     * Change shift icon
     */
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

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if(cancelimg.getVisibility() == View.GONE && !(searchimgdone.getVisibility() ==View.VISIBLE ))
        {
            Toast.makeText(this, "Tap on search icon first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        if (isAlphabet(primaryCode) && mPredictionOn) {
            //+++++++The code which commit text to the selected input with prediction++++++++
            mComposing.append((char) primaryCode);
//            getCurrentInputConnection().setComposingText(mComposing, 1);
//            updateShiftKeyState(getCurrentInputEditorInfo());
//            updateCandidates();
        } else {
            //+++++++The code which commit text to the selected input++++++++
            mComposing.append((char) primaryCode);

//            getCurrentInputConnection().setComposingText(mComposing, 1);

        }
        //++++++++++++++++++++++++++++++++++++++++++++++++searched edit text+++++++++++++++++++++++++++++++++
        searched.setText(mComposing.toString());
        Log.e("Handling" ,"Charqacters "+mComposing.toString());
//        cancelimg.setVisibility(View.VISIBLE);
//        searchimgdone.setVisibility(View.GONE);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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

    public boolean isWordSeparator(int code) {
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
            mComposing.setLength(index);
            mComposing = new StringBuilder(list.get(index) + " ");
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
        Log.e("Primary" , "code"+primaryCode);
        mInputView.setPreviewEnabled(true);

        // Disable preview key on Shift, Delete, Space, Language, Symbol and Emoticon.
        if (primaryCode == -1 || primaryCode == -2 || primaryCode == -10000
                || primaryCode == -101 || primaryCode == 32) {
            Log.e("previe" , "setPreviewEnabled");
            mInputView.setPreviewEnabled(false);
        }else  if( primaryCode == -5 )
        {
            final int length = searched.getText().toString().length();
            if (length > 1) {
                mComposing.delete(length - 1, length);
                searched.setText(mComposing);
                searched.setSelection(mComposing.length());
            } else if (length > 0) {
                mComposing.setLength(0);
//                getCurrentInputConnection().commitText("", 0);
                searched.setText("");
                searched.setSelection(mComposing.length());
//                updateCandidates();
            }
        }
    }

    public void onRelease(int primaryCode) {
    }
    /**
     * This method displays Gifs when called.
     * @param
     */
    private void showGifGridview(String searchtext) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        View view = dialog.getView();
        if (layoutInflater != null) {
            View popupView = layoutInflater.inflate(R.layout.gifgridviewlayout, null);
            gifpopupWindow = new Gifgridviewpopup(popupView, this ,searchtext);
            gifpopupWindow.setSizeForSoftKeyboard();
            gifpopupWindow.setSize(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            gifpopupWindow.showAtLocation(mInputView.getRootView(), Gravity.BOTTOM, 0, 0);
            gifpopupWindow.setOnGifclickedListnermethod(new Gifgridviewpopup.onGifclickedListner() {
                @Override
                public void onGifclicked(Gifdata gifitem) {
                    Log.e("A Gifdata" ,"Gif choosen "+gifitem.getMultilineText());
                    Glide.with(popupView.getContext()).asGif()
                            .load(gifitem.getGif())
                            .into(new SimpleTarget<GifDrawable>() {

                                @Override
                                public void onResourceReady(@NonNull GifDrawable gifDrawable, @Nullable Transition<? super GifDrawable> transition) {
                                Log.e("Got " , "Data");
                                    ByteBuffer byteBuffer = gifDrawable.getBuffer();
                                    FileOutputStream output = null;
                                    try {
                                        File outputDir = popupView.getContext().getCacheDir(); // context being the Activity pointer
                                        mGifFile = File.createTempFile("gifitem", ".gif", imagesDir);
//                                        mGifFile = File.createTempFile("gifitem", ".gif", outputDir);

                                        output = new FileOutputStream(mGifFile);
                                        byte[] bytes = new byte[byteBuffer.remaining()];
                                        byteBuffer.get(bytes);
                                        ( (ByteBuffer)  byteBuffer.duplicate().clear()  ).get(bytes);
                                        output.write(bytes, 0, bytes.length);
                                        output.close();
                                        SoftKeyboard.this.doCommitContent("A waving flag", MIME_TYPE_GIF,mGifFile );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

//                    if(gifitem.getGifname().startsWith("gg"))
//                    {
//                        mGifFile = getFileForResource(mInputView.getContext(), R.raw.gg, imagesDir, "gg.gif");
//                    }else {
//                        mGifFile = getFileForResource(mInputView.getContext(), R.raw.blast, imagesDir, "blast.gif");
//                    }
////                    mGifFile = getFileForResource(mInputView.getContext(), R.raw.gg, imagesDir, "gg.gif");
//                    SoftKeyboard.this.doCommitContent("A waving flag", MIME_TYPE_GIF, mGifFile);
                }
            });
//                InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(
//                        getURLForResource(R.drawable.gg),
//                        new ClipDescription("gg", new String[]{"image/gif"}),
//                        null
//                );
//            InputConnection inputConnection = getCurrentInputConnection();
//            EditorInfo editorInfo = getCurrentInputEditorInfo();
//            int flags = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
//                flags |= InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
//            }
//            InputConnectionCompat.commitContent(
//                    inputConnection, editorInfo, inputContentInfo, flags, null);
        }
    }

    private Uri getURLForResource(int gg) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +gg);
    }

    @Override
    public boolean onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
        return false;
    }



    /**
     * This class improves performance of the app when prediction is on.
     * The database query is executed in the background.
     */
    private class SelectDataTask extends AsyncTask<String, Void, ArrayList<String>> {

        private String subType;

        void getSubtype(LatinKeyboard mCurKeyboard) {
            if (mCurKeyboard == mQwertyKeyboard) {
                subType = "english";
            }
//            else if (mCurKeyboard == mPashtoKeyboard) {
//                subType = "pashto";
//            } else {
//                subType = "farsi";
//            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... str) {
//            list = db.getAllRow(str[0], subType);
//            ArrayList<String> historylist = new ArrayList<>();
            Log.e("Historymodal" , "doInBackground");
            list.clear();
          for(Historymodal words : userDao.getAllHistbyOccurace(str[0]))
          {
              Log.e("Historymodal" , "words"+words.getTitle());
//              list.add(words.getTitle()) ;
              list.add(str.toString()) ;
          }
            Log.e("Historymodal" , "doInBackground"+list.size());
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            list = result;
            setSuggestions(result, true, true);
        }
    }

    /**
     * Add or update word in the dictionary
     */
    public void addUpdateWord() {
        Log.e("Historymodal", "getLastWord()" + getLastWord());
        if (!getLastWord().isEmpty()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Log.e("Historymodal", "getLastWord()" + getLastWord());
//            Integer freq = db.getWordFrequency(getLastWord(), mActiveKeyboard);
                    Integer freq = userDao.getWordFrequency(getLastWord());
                    Log.e("Historymodal", "freq" + freq);
                    if (freq != null) {
//                userDao.update(getLastWord(), freq, mActiveKeyboard);
                        userDao.update(new Historymodal(1, getLastWord(), "S", "ww.gosdgf", (freq + 1)));
                    } else {
//                userDao.insert(getLastWord(), mActiveKeyboard);
                        userDao.insert(new Historymodal(2, getLastWord(), "S", "ww.gosdgf", 1));
                    }
                }
            });

        }
    }

    /**
     * Return a last word from input connection with space
     *
     * @return
     */
    public String getLastWord() {
        CharSequence inputChars = getCurrentInputConnection().getTextBeforeCursor(50, 0);
        String inputString = String.valueOf(inputChars);

        return inputString.substring(inputString.lastIndexOf(" ") + 1);
    }

    /**
     * Clear the candidate view.
     */
    public void clearCandidateView() {
        if (list != null) list.clear();
    }


}