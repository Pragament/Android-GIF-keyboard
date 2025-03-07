package com.optimum.coolkeybord.settingssession;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingSesson {
    SharedPreferences pref, pref11;
    SharedPreferences.Editor editor, editor1;
    Context context, ocontext;
    int PRIVATE_MODE=0, PRIVATE_MODE1 = 0;
    private static final String Pref_Name="Settingfile";
    private static final String Pref_Name1="Orderfile";
    private static final String Appendlink="appendlinkyou";
    private static final String Appendgiflink="appendgiflink";
    private static final String ShowTextInsteadOfThumbnail = "show_text_instead_of_thumbnail";
    private static final String SwitchKeyboardToDefault = "switchKeyboardToDefault";
    private static final String SearchbyStartsEnd="searchbystarts";
    private static final String SearchbyEnd="searchbyend";
    private static final String Minimumcharacters="minimumcharacters";
    private static final String Showenglish="showenglish";
    private static final String Showtelugu="showtelugu";
    private static final String  Showtamil=" showtamil";
    private static final String  Showhindi=" showhindi";
    private static final String  Showkannada=" showkannada";
    private static final String  Showmalayalam=" showmalayalam";
    private static final String  ShowSearchScreenByDefault=" showsearchscreenbydefault";
    private static final String datesTeps="datasteps";
    private static final String IS_Image="Is Image In";

    public SettingSesson(Context context) {
        this.context = context;
        pref=context.getSharedPreferences(Pref_Name,PRIVATE_MODE);
        pref11 = context.getSharedPreferences(Pref_Name1,PRIVATE_MODE1);
        editor = pref.edit();
        editor1 = pref11.edit();
    }

    public boolean getAppendlink() {
        return pref.getBoolean(Appendlink, false);
//        return Appendlink;
    }
    public boolean getgiflink() {
        return pref.getBoolean(Appendgiflink, false);
//        return Appendlink;
    }
    public boolean showTextInsteadOfThumbnail()
    {
        return pref.getBoolean(ShowTextInsteadOfThumbnail, false);
//        return Appendlink;
    }

    public boolean switchKeyboardToDefault()
    {
        return pref.getBoolean(SwitchKeyboardToDefault, false);
//        return Appendlink;
    }


    public String getSearchbyStartsorEnd() {
        return pref.getString(SearchbyStartsEnd, "S");
//        return pref.getBoolean(SearchbyEnd, true);

    }

    public  String getMinimumcharacters() {

        return pref.getString(Minimumcharacters, "4");
    }

    public boolean getShowenglish() {
        return pref.getBoolean(Showenglish, true);
    }
    public void setShowenglish( boolean value)
    {
        editor.putBoolean(Showenglish, value);

        // commit changes
        editor.commit();
    }
    public boolean getShowtelugu() {
//        return Showtelugu;
        return pref.getBoolean(Showtelugu, false);
    }
    public void setShowtelugu( boolean value)
    {
        editor.putBoolean(Showtelugu, value);

        // commit changes
        editor.commit();
    }
    public boolean getShowtamil() {
//        return Showtamil;
        return pref.getBoolean(Showtamil, false);
    }
    public void setShowtamil( boolean value)
    {
        editor.putBoolean(Showtamil, value);

        // commit changes
        editor.commit();
    }
    public boolean getShowHindi() {
        return pref.getBoolean(Showhindi, false);
    }
    public void setShowHindi( boolean value) {
        editor.putBoolean(Showhindi, value);
        editor.commit();
    }
    public boolean getShowKannada() {
        return pref.getBoolean(Showkannada, false);
    }
    public void setShowKannada( boolean value) {
        editor.putBoolean(Showkannada, value);
        editor.commit();
    }
    public boolean getShowMalayalam() {
        return pref.getBoolean(Showmalayalam, false);
    }
    public void setShowMalayalam( boolean value) {
        editor.putBoolean(Showmalayalam, value);
        editor.commit();
    }
    public String getSlelectedlang() {
        String slectedland = "no";
        if(getShowenglish())
        {
            slectedland = "en";
//            return pref.getBoolean(Showenglish, true);
        }
        if(getShowtelugu())
        {
            if(slectedland.contains("en") )
            {
                slectedland = "en"+",te";
            }else {
                slectedland = "te";
            }

//            return pref.getBoolean(Showtelugu, false);
        }
        if(getShowtamil() ) {
            if(slectedland.contains("en") || slectedland.contains("te"))
            {
                slectedland = slectedland+",ta";
            }else {
                slectedland = "ta";
            }
        }
        if(getShowHindi() ) {
            if(slectedland.contains("en") || slectedland.contains("te") || slectedland.contains("ta")) {
                slectedland = slectedland+",hi";
            }else {
                slectedland = "hi";
            }
        }
        if(getShowKannada() ) {
            if(slectedland.contains("en") || slectedland.contains("te") || slectedland.contains("ta")
                    || slectedland.contains("hi")) {
                slectedland = slectedland+",kn";
            }else {
                slectedland = "kn";
            }
        }
        if(getShowMalayalam() ) {
            if(slectedland.contains("en") || slectedland.contains("te") || slectedland.contains("ta")
                    || slectedland.contains("hi") || slectedland.contains("kn")) {
                slectedland = slectedland+",ml";
            }else {
                slectedland = "ml";
            }
        }
        return slectedland;
    }

//    public static String getIS_Image() {
//        return IS_Image;
//    }

    public void appendlinkyou(Boolean is_il)
    {
        editor.putBoolean(Appendlink, is_il);

        // commit changes
        editor.commit();
    }
    public void appendgiflink(Boolean is_il)
    {
        editor.putBoolean(Appendgiflink, is_il);

        // commit changes
        editor.commit();
    }
    public void showTextInsteadOfThumbnail(Boolean is_il)
    {
        editor.putBoolean(ShowTextInsteadOfThumbnail, is_il);
        editor.commit();
    }
    public void setSwitchKeyboardToDefault(Boolean is_il)
    {
        editor.putBoolean(SwitchKeyboardToDefault, is_il);
        editor.commit();
    }


    public void setSearchbystarts( String value)
    {
        editor.putString(SearchbyStartsEnd, value);

        // commit changes
        editor.commit();
    }
    public void setMinimumcharacters( String maxvalue)
    {
        editor.putString(Minimumcharacters, maxvalue);

        // commit changes
        editor.commit();
    }

    public void setShowSearchScreenByDefault(boolean value) {
        editor.putBoolean(ShowSearchScreenByDefault, value);
        editor.commit();
    }

    public boolean getShowSearchScreenByDefault() {
        return pref.getBoolean(ShowSearchScreenByDefault, false);
    }

    public void setLaunguages(Boolean isend ,Boolean istelgu ,Boolean istamil)
    {
        editor.putBoolean(Showenglish, isend);
        editor.putBoolean(Showtelugu, istelgu);
        editor.putBoolean(Showtamil, istamil);
//        editor.putString(datesTeps, datetxt);

        // commit changes
        editor.commit();
//        editor.apply();
    }
//    public void setStepponly(int is_il )
//    {
//        editor.putInt(sTeps, is_il);
////        editor.putString(datesTeps, datetxt);
//
//        // commit changes
//        editor.commit();
////        editor.apply();
//    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
       /* Intent i = new Intent(context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
    }

//    public int getissetTop()
//    {
//
//        return pref.getInt(sTeps, 0);
//    }
    public String getissetDate()
    {
        return pref.getString(datesTeps, "");
    }
    public boolean isImageString()
    {
        return pref.getBoolean(IS_Image, false);
    }

    public void clearAll() {
        editor.clear();
        editor.commit();
    }
}
