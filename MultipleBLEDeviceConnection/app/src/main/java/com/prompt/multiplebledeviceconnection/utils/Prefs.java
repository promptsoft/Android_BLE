package com.prompt.multiplebledeviceconnection.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @CreatedBy: Hiren Vaghela
 * @CreatedOn: 5/2/16
 * @purpose: In Whole application Preference value store and retrive from here.
 */

public class Prefs {
	public static SharedPreferences sharedPreferences = null;

	/**
	 * @CreatedBy: Hiren Vaghela
	 * @CreatedOn: 5/2/16
	 * @param: Context
	 * @purpose: open or initialize Preference
	 * @return void.
	 */
	public static void openPrefs(Context context) {

		sharedPreferences = context.getSharedPreferences(Const.PREF_FILE,
				Context.MODE_PRIVATE);
	}
	/**
	 * @CreatedBy: Hiren Vaghela
	 * @CreatedOn: 5/2/16
	 * @param: Context,String fieldName,String defaultVal.
	 * @purpose: From field name give it value from SharedPreference.
	 * @return String.
	 */
	public static String getvalue(Context context, String key,
                                  String defaultValue) {

		Prefs.openPrefs(context);

		String result = Prefs.sharedPreferences.getString(key, defaultValue);
		Prefs.sharedPreferences = null;
		return result;
	}
	/**
	 * @CreatedBy: Hiren Vaghela
	 * @CreatedOn: 5/2/16
	 * @param: Context,String FieldName,String value
	 * @purpose: from FieldName set it Value in SharedPreference.
	 * @return void.
	 */
	public static void setValue(Context context, String key, String value) {
		Prefs.openPrefs(context);
		Editor preferenceEditor = Prefs.sharedPreferences.edit();
		preferenceEditor.putString(key, value);
		preferenceEditor.commit();
		preferenceEditor = null;
		Prefs.sharedPreferences = null;
	}
	/**
	 * @CreatedBy: Hiren Vaghela
	 * @CreatedOn: 5/2/16
	 * @param: Context
	 * @purpose: clear All Preferences.
	 * @return void.
	 */
	public static void setClear(Context context) {
		Prefs.openPrefs(context);
		Editor preferenceEditor = Prefs.sharedPreferences.edit();
		preferenceEditor.clear().commit();
		preferenceEditor = null;
		Prefs.sharedPreferences = null;
	}
	/**
	 * @CreatedBy: Hiren Vaghela
	 * @CreatedOn: 5/2/16
	 * @param: Context,String fieldName
	 * @purpose: Remove perticular field from SharedPreferences.
	 * @return void.
	 */
	public static void remove(Context context, String key) {
		Prefs.openPrefs(context);
		Editor preferenceEditor = Prefs.sharedPreferences.edit();
		preferenceEditor.remove(key);
		preferenceEditor.commit();
		preferenceEditor = null;
		Prefs.sharedPreferences = null;

	}

}
