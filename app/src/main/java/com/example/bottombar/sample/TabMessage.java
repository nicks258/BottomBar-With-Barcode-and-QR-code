package com.example.bottombar.sample;

/**
 * Created by iiro on 7.6.2016.
 */
public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.tab_recents:
                message += "OverView";
                break;
            case R.id.tab_favorites:
                message += "Clients";
                break;
            case R.id.tab_nearby:
                message += "App Rf";
                break;
            case R.id.tab_friends:
                message += "Access Point";
                break;
            case R.id.tab_food:
                message += "Switches";
                break;
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
