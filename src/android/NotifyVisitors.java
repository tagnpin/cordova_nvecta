package com.plugin.notifyvisitors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.notifyvisitors.notifyvisitors.NotifyVisitorsApi;
import com.notifyvisitors.notifyvisitors.center.NVCenterStyleConfig;
import com.notifyvisitors.notifyvisitors.interfaces.NotificationCountInterface;
import com.notifyvisitors.notifyvisitors.interfaces.NotificationListDetailsCallback;
import com.notifyvisitors.notifyvisitors.interfaces.OnCenterCountListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnCenterDataListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnEventTrackListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnNotifyBotClickListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnReviewCompleteListener;
import com.notifyvisitors.notifyvisitors.push.NVNotificationChannels;
import com.notifyvisitors.notifyvisitors.interfaces.OnPushRuntimePermission;
import com.notifyvisitors.notifyvisitors.permission.NVPopupDesign;
import com.notifyvisitors.notifyvisitors.interfaces.OnCenterDataListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnInAppTriggerListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnKnownUserFound;
import com.notifyvisitors.notifyvisitors.interfaces.OnUserTrackListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnBuildUiListener;
import com.notifyvisitors.notifyvisitors.interfaces.OnNotificationClicksHandler;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;


/**
 * This class echoes a string called from JavaScript.
 */
public class NotifyVisitors extends CordovaPlugin {

    private PluginResult resultLink;
    private String fragmentName;
    private int dismiss = 0;

    private CallbackContext showCallback = null;
    private CallbackContext eventCallback = null;
    private CallbackContext commonCallback = null;
    private CallbackContext knownUserIdentifiedCallback = null;
    private CallbackContext notificationCenterCallback = null;
    private CallbackContext notificationClickCallback = null;
    private CallbackContext pushRegisteredCallback = null;


    private ArrayList<CallbackContext> _handlers = new ArrayList<>();

    private JSONObject lastEvent;
    private JSONObject tokens, customObjects;

    private String tab1Label, tab1Name;
    private String tab2Label, tab2Name;
    private String tab3Label, tab3Name;
    private String selectedTabColor, unSelectedTabColor, selectedTabIndicatorColor;
    private int selectedTabIndex;

    String eventName = null;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "EXECUTE !!");

        Context mContext = this.cordova.getActivity();
        boolean result = false;

        switch (action) {
            case NVUtils.SHOW:
                result = show_banners_surveys(args, mContext, backResponse);
                break;
            case NVUtils.SHOW_IN_APP_MESSAGE:
                result = show_in_app_message(args, mContext, backResponse);
                break;
            case NVUtils.SHOW_NOTIFICATIONS:
                result = open_notification_canter(args, mContext);
                break;
            case NVUtils.OPEN_NOTIFICATION_CENTER:
                result = open_notification_center(args, mContext, backResponse);
                break;
            case NVUtils.SCHEDULE_NOTIFICATION:
                result = schedule_notification(args, mContext);
                break;
            case NVUtils.USER_IDENTIFIER:
                result = user_identifier(args, mContext);
                break;
            case NVUtils.SET_USER_IDENTIFIER:
                result = set_user_identifier(args, mContext, backResponse);
                break;
            case NVUtils.EVENT:
                result = hit_event(args, mContext, backResponse);
                break;
            case NVUtils.STOP_NOTIFICATION:
                result = stop_banners(mContext);
                break;
            case NVUtils.NOTIFICATION_CENTER_DATA:
                result = get_notification_center_data(mContext, backResponse);
                break;
            case NVUtils.NOTIFICATION_CENTER_DATA_NEW:
                result = get_notification_center_data_new(mContext, backResponse);
                break;
            case NVUtils.NOTIFICATION_COUNT:
                result = unread_notification_count(mContext, backResponse);
                break;
            case NVUtils.AUTO_START_ANDROID:
                result = auto_start_android(mContext);
                break;
            // case NVUtils.CHAT_BOT:
            //     result = start_chat_bot_screen(args, mContext, backResponse);
            //     break;
            case NVUtils.CREATE_NOTIFICATION_CHANNEL:
                result = create_notification_channel(args, mContext);
                break;
            case NVUtils.DELETE_NOTIFICATION_CHANNEL:
                result = delete_notification_channel(args, mContext);
                break;
            case NVUtils.CREATE_NOTIFICATION_CHANNEL_GROUP:
                result = create_notification_channel_group(args, mContext);
                break;
            case NVUtils.DELETE_NOTIFICATION_CHANNEL_GROUP:
                result = delete_notification_channel_group(args, mContext);
                break;
            case NVUtils.GET_NV_UID:
                result = get_nv_uid(mContext, backResponse);
                break;
            case NVUtils.GET_EVENT_SURVEY_INFO:
                result = event_survey_callback(mContext, backResponse);
                break;
            case NVUtils.GET_LINK_DATA:
                result = get_link_data(args, backResponse);
                break;
            case NVUtils.GET_CLICK_INFO_CP:
                result = get_click_info_cp(mContext, backResponse);
                break;
            case NVUtils.GET_LINK_INFO:
                result = get_link_info(args, backResponse);
                break;
            case NVUtils.GET_REGISTRATION_TOKEN:
                result = push_registration_token(backResponse, mContext);
                break;
            case NVUtils.GOOGLE_IN_APP_REVIEW:
                result = enable_google_inApp_review(backResponse, mContext);
                break;
            case NVUtils.SUBSCRIBE_CATEGORY:
                result = subscribePushCategory(args, mContext);
                break;
            case NVUtils.GET_NOTIFICATION_CENTER_COUNT:
                result = getNotificationCenterCount(args, mContext, backResponse);
                break;
            case NVUtils.ACTIVATE_PUSH_PERMISSION_POPUP:
                result = activatePushPermissionPopup(args, mContext, backResponse);
                break;
            case NVUtils.ENABLE_PUSH_PERMISSION:
                result = enablePushPermission(args, mContext);
                break;
            case NVUtils.NATIVE_PUSH_PERMISSION_PROMPT:
                result = nativePushPermissionPrompt(mContext, backResponse);
                break;
            case NVUtils.KNOWN_USER_IDENTIFIED:
                result = known_user_identified(mContext, backResponse);
                break;
            case NVUtils.IS_PAYLOAD_FROM_NV_PLATFORM:
                result = is_payload_from_nv_platform(args, backResponse, mContext);
                break;
            case NVUtils.GET_NV_FCM_PAYLOAD:
                result = get_nv_fcm_payload(args, mContext);
                break;
            case NVUtils.SESSION_DATA:
                result = get_session_data(backResponse, mContext);
                break;
            case NVUtils.TRACK_SCREEN:
                result = track_screen(args, mContext);
                break;
            case NVUtils.NOTIFICATION_CLICK_CALLBACK:
                result = notification_click_callback(mContext, backResponse);
                break;
            default:
                Log.e(NVUtils.TAG, "INVALID ACTION : " + action);
                //Toast.makeText(mContext, "INVALID ACTION : " + action, Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    private boolean show_banners_surveys(JSONArray args, Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "SHOW !!");
        try {
            JSONObject jObject = args.getJSONObject(0);
            tokens = jObject.getJSONObject("tokens");
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SHOW TOKENS ERROR : " + e);
        }
        try {
            JSONObject jObject = args.getJSONObject(0);
            customObjects = jObject.getJSONObject("customObjects");
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SHOW CUSTOM OBJECT ERROR : " + e);
        }
        try {
            JSONObject jObject = args.getJSONObject(0);
            fragmentName = jObject.getString("fragmentName");
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SHOW FRAGMENT NAME ERROR : " + e);
        }

        try {
            Log.i(NVUtils.TAG, "Tokens : " + tokens.toString());
            Log.i(NVUtils.TAG, "Rules : " + customObjects.toString());
         } catch (Exception e) {
           Log.i(NVUtils.TAG, "ERRORS : " + e);
        }

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showCallback = backResponse;
                NotifyVisitorsApi.getInstance(mContext).show(tokens, customObjects, fragmentName);
            }
        });

        return true;
    }

    private boolean show_in_app_message(JSONArray args, Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "SHOW IN-APP MESSAGE !!");
        try {
            JSONObject jObject = args.getJSONObject(0);
            tokens = jObject.getJSONObject("tokens");
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SHOW TOKENS ERROR : " + e);
        }
        try {
            JSONObject jObject = args.getJSONObject(0);
            customObjects = jObject.getJSONObject("customObjects");
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SHOW CUSTOM OBJECT ERROR : " + e);
        }
        try {
            JSONObject jObject = args.getJSONObject(0);
            fragmentName = jObject.getString("fragmentName");
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SHOW FRAGMENT NAME ERROR : " + e);
        }

        try {
            Log.i(NVUtils.TAG, "Tokens : " + tokens.toString());
            Log.i(NVUtils.TAG, "Rules : " + customObjects.toString());
         } catch (Exception e) {
           Log.i(NVUtils.TAG, "ERRORS : " + e);
        }

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showCallback = backResponse;
                //NotifyVisitorsApi.getInstance(mContext).show(tokens, customObjects, fragmentName);

                NotifyVisitorsApi.getInstance(mContext).show(tokens, customObjects, fragmentName, new OnInAppTriggerListener() {
                    @Override
                    public void onDisplay(JSONObject response) {
                        try {
                            //Log.d("App", "Show response => " + response.toString());
                            PluginResult result = new PluginResult(PluginResult.Status.OK, response.toString());
                            result.setKeepCallback(true);
                            showCallback.sendPluginResult(result);
                        } catch (Exception e) {
                            Log.e(NVUtils.TAG, "ERRORS : " + e);
                        }
                    }
                });
            }
        });

        return true;
    }

    @Deprecated
    private boolean open_notification_canter(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS !!");
        try {
            JSONObject result;
            JSONObject appInboxInfo;

            result = null;
            tab1Label = null;
            tab1Name = null;
            tab2Label = null;
            tab2Name = null;
            tab3Label = null;
            tab3Name = null;
            selectedTabColor = null;
            unSelectedTabColor = null;
            selectedTabIndicatorColor = null;
            selectedTabIndex = 0;

            try {
                result = args.getJSONObject(0);
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "Error :" + e);
            }

            if (result != null) {
                try {
                    dismiss = result.getInt("dismiss");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS DISMISS ERROR : " + e);
                }

                if (!result.isNull("appInboxInfo") && result.getJSONObject("appInboxInfo") != null
                        && result.getJSONObject("appInboxInfo").length() > 0) {
                    appInboxInfo = result.getJSONObject("appInboxInfo");
                    try {
                        tab1Label = appInboxInfo.getString("label_one");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB1LABEL ERROR :" + e);
                    }

                    try {
                        tab1Name = appInboxInfo.getString("name_one");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB1NAME ERROR :" + e);
                    }

                    try {
                        tab2Label = appInboxInfo.getString("label_two");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB2LABEL ERROR :" + e);
                    }

                    try {
                        tab2Name = appInboxInfo.getString("name_two");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB2NAME ERROR :" + e);
                    }

                    try {
                        tab3Label = appInboxInfo.getString("label_three");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB3LABEL ERROR :" + e);
                    }

                    try {
                        tab3Name = appInboxInfo.getString("name_three");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB3NAME ERROR :" + e);
                    }

                    try {
                        selectedTabColor = appInboxInfo.getString("selectedTabTextColor");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS SELECTED TAB COLOR ERROR :" + e);
                        selectedTabColor = "#0000ff";
                    }

                    try {
                        unSelectedTabColor = appInboxInfo.getString("unselectedTabTextColor");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS UNSELECTED TAB COLOR ERROR :" + e);
                        unSelectedTabColor = "#779ecb";
                    }

                    try {
                        selectedTabIndicatorColor = appInboxInfo.getString("selectedTabBgColor");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS SELECTED TAB INDICATOR COLOR ERROR :" + e);
                        selectedTabIndicatorColor = "#0000ff";
                    }

                    try {
                        selectedTabIndex = appInboxInfo.getInt("selectedTabIndex_ios");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SELECTED TAB INDEX ERROR :" + e);
                    }

                    this.cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NVCenterStyleConfig config = new NVCenterStyleConfig();

                            if (tab1Label.equalsIgnoreCase("null")) {
                                tab1Label = null;
                            }

                            if (tab1Name.equalsIgnoreCase("null")) {
                                tab1Name = null;
                            }

                            if (tab2Label.equalsIgnoreCase("null")) {
                                tab2Label = null;
                            }

                            if (tab2Name.equalsIgnoreCase("null")) {
                                tab2Name = null;
                            }

                            if (tab3Label.equalsIgnoreCase("null")) {
                                tab3Label = null;
                            }

                            if (tab3Name.equalsIgnoreCase("null")) {
                                tab3Name = null;
                            }
                            config.setFirstTabDetail(tab1Label, tab1Name);
                            config.setSecondTabDetail(tab2Label, tab2Name);
                            config.setThirdTabDetail(tab3Label, tab3Name);

                            config.setSelectedTabColor(selectedTabColor);
                            config.setUnSelectedTabColor(unSelectedTabColor);
                            config.setSelectedTabIndicatorColor(selectedTabIndicatorColor);

                            NotifyVisitorsApi.getInstance(mContext).showNotifications(dismiss, config);
                        }
                    });
                } else {
                    Log.i(NVUtils.TAG, "INFO IS NULL GOING FOR STANDARD APP INBOX !!");
                    this.cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NotifyVisitorsApi.getInstance(mContext).showNotifications(dismiss);
                        }
                    });

                }
            } else {
                Log.i(NVUtils.TAG, "Arguments can't be null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean open_notification_center(JSONArray args, Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "OPEN NOTIFICATION CENTER !!");
        try {
            JSONObject result;
            JSONObject appInboxInfo;

            result = null;
            tab1Label = null;
            tab1Name = null;
            tab2Label = null;
            tab2Name = null;
            tab3Label = null;
            tab3Name = null;
            selectedTabColor = null;
            unSelectedTabColor = null;
            selectedTabIndicatorColor = null;
            selectedTabIndex = 0;

            try {
                result = args.getJSONObject(0);
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "Error :" + e);
            }

            if (result != null) {
                try {
                    dismiss = result.getInt("dismiss");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS DISMISS ERROR : " + e);
                }

                if (!result.isNull("appInboxInfo") && result.getJSONObject("appInboxInfo") != null
                        && result.getJSONObject("appInboxInfo").length() > 0) {
                    appInboxInfo = result.getJSONObject("appInboxInfo");
                    try {
                        tab1Label = appInboxInfo.getString("label_one");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB1LABEL ERROR :" + e);
                    }

                    try {
                        tab1Name = appInboxInfo.getString("name_one");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB1NAME ERROR :" + e);
                    }

                    try {
                        tab2Label = appInboxInfo.getString("label_two");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB2LABEL ERROR :" + e);
                    }

                    try {
                        tab2Name = appInboxInfo.getString("name_two");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB2NAME ERROR :" + e);
                    }

                    try {
                        tab3Label = appInboxInfo.getString("label_three");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB3LABEL ERROR :" + e);
                    }

                    try {
                        tab3Name = appInboxInfo.getString("name_three");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS TAB3NAME ERROR :" + e);
                    }

                    try {
                        selectedTabColor = appInboxInfo.getString("selectedTabTextColor");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS SELECTED TAB COLOR ERROR :" + e);
                        selectedTabColor = "#0000ff";
                    }

                    try {
                        unSelectedTabColor = appInboxInfo.getString("unselectedTabTextColor");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS UNSELECTED TAB COLOR ERROR :" + e);
                        unSelectedTabColor = "#779ecb";
                    }

                    try {
                        selectedTabIndicatorColor = appInboxInfo.getString("selectedTabBgColor");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SHOW NOTIFICATIONS SELECTED TAB INDICATOR COLOR ERROR :" + e);
                        selectedTabIndicatorColor = "#0000ff";
                    }

                    try {
                        selectedTabIndex = appInboxInfo.getInt("selectedTabIndex_ios");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "SELECTED TAB INDEX ERROR :" + e);
                    }

                    this.cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NVCenterStyleConfig config = new NVCenterStyleConfig();

                            if (tab1Label.equalsIgnoreCase("null")) {
                                tab1Label = null;
                            }

                            if (tab1Name.equalsIgnoreCase("null")) {
                                tab1Name = null;
                            }

                            if (tab2Label.equalsIgnoreCase("null")) {
                                tab2Label = null;
                            }

                            if (tab2Name.equalsIgnoreCase("null")) {
                                tab2Name = null;
                            }

                            if (tab3Label.equalsIgnoreCase("null")) {
                                tab3Label = null;
                            }

                            if (tab3Name.equalsIgnoreCase("null")) {
                                tab3Name = null;
                            }
                            config.setFirstTabDetail(tab1Label, tab1Name);
                            config.setSecondTabDetail(tab2Label, tab2Name);
                            config.setThirdTabDetail(tab3Label, tab3Name);

                            config.setSelectedTabColor(selectedTabColor);
                            config.setUnSelectedTabColor(unSelectedTabColor);
                            config.setSelectedTabIndicatorColor(selectedTabIndicatorColor);

                            //NotifyVisitorsApi.getInstance(mContext).showNotifications(dismiss, config);
                            NotifyVisitorsApi.getInstance(mContext).showNotifications(dismiss, config, new OnBuildUiListener() {
                                @Override
                                public void onCenterClose() {
                                    //Log.d(NVUtils.TAG, "Notification center closed");
                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("status", "success");
                                        data.put("message", "close button clicked");
                                        //reply.success(j.toString());
                                        //channel.invokeMethod("NotificationCenterResponse", data.toString());
                                    } catch (Exception e) {
                                        Log.e(NVUtils.TAG, "CLOSE BUTTON CLICK ERROR :" + e);
                                    }
                                    PluginResult result = new PluginResult(PluginResult.Status.OK, data.toString());
                                    result.setKeepCallback(true);
                                    backResponse.sendPluginResult(result);
                                }
                            });
                        }
                    });
                } else {
                    Log.i(NVUtils.TAG, "INFO IS NULL GOING FOR STANDARD APP INBOX !!");
                    this.cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //NotifyVisitorsApi.getInstance(mContext).showNotifications(dismiss);
                            NotifyVisitorsApi.getInstance(mContext).showNotifications(dismiss, null, new OnBuildUiListener() {
                                @Override
                                public void onCenterClose() {
                                    //Log.d(NVUtils.TAG, "Notification center closed");
                                    JSONObject data = new JSONObject();
                                    try {
                                        data.put("status", "success");
                                        data.put("message", "close button clicked");
                                        //reply.success(j.toString());
                                        //channel.invokeMethod("NotificationCenterResponse", data.toString());
                                    } catch (Exception e) {
                                        Log.e(NVUtils.TAG, "CLOSE BUTTON CLICK ERROR :" + e);
                                    }
                                    PluginResult result = new PluginResult(PluginResult.Status.OK, data.toString());
                                    result.setKeepCallback(true);
                                    backResponse.sendPluginResult(result);
                                }
                            });
                        }
                    });

                }
            } else {
                Log.i(NVUtils.TAG, "Arguments can't be null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean schedule_notification(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "SCHEDULE NOTIFICATION !!");
        try {
            String nid = null, tag = null, time = null,
                    title = null, message = null,
                    url = null, icon = null;
            try {
                JSONObject info = args.getJSONObject(0);
                if (info != null) {
                    try {
                        nid = info.getString("nid");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "NID ERROR : " + e);
                    }

                    try {
                        tag = info.getString("tag");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "TAG ERROR : " + e);
                    }

                    try {
                        time = info.getString("time");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "TIME ERROR : " + e);
                    }

                    try {
                        title = info.getString("title");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "TITLE ERROR : " + e);
                    }

                    try {
                        message = info.getString("message");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "MESSAGE ERROR : " + e);
                    }

                    try {
                        url = info.getString("url");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "URL ERROR : " + e);
                    }

                    try {
                        icon = info.getString("icon");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "ICON ERROR : " + e);
                    }

                    NotifyVisitorsApi.getInstance(mContext).
                            scheduleNotification(nid, tag, time, title, message, url, icon);

                } else {
                    Log.i(NVUtils.TAG, "ARGS WAS NULL !!");
                }

            } catch (Exception e) {
                Log.i(NVUtils.TAG, "SCHEDULE NOTIFICATION DATA PARSING ERROR : " + e);
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SCHEDULE NOTIFICATION ERROR : " + e);
        }
        return true;
    }

    private boolean user_identifier(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "USER IDENTIFIER !!");

        String userID = null;
        JSONObject jsonObject = null;
        try {
            JSONObject info = args.getJSONObject(0);
            if (info != null) {
                try {
                    userID = info.getString("userID");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "USER IDENTIFIER USER ID ERROR : " + e);
                }

                try {
                    jsonObject = info.getJSONObject("jsonObject");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "USER IDENTIFIER JSON OBJECT ERROR : " + e);
                }
                NotifyVisitorsApi.getInstance(mContext).
                        userIdentifier(userID, jsonObject);
            } else {
                Log.i(NVUtils.TAG, "ARGS WAS NULL !! ");
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "USER IDENTIFIER ERROR : " + e);
        }
        return true;
    }

    private boolean set_user_identifier(JSONArray args, Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "SET USER IDENTIFIER !!");

        String userID = null;
        JSONObject params = null;
        try {
            JSONObject info = args.getJSONObject(0);
            if (info != null) {
                try {
                    params = info.getJSONObject("jsonObject");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "USER IDENTIFIER JSON OBJECT ERROR : " + e);
                }
                //NotifyVisitorsApi.getInstance(mContext).userIdentifier(jsonObject);
                NotifyVisitorsApi.getInstance(mContext).userIdentifier(params, new OnUserTrackListener() {
                    @Override
                    public void onResponse(JSONObject data) {
                        backResponse.success(data.toString());
                    }
                });
            } else {
                Log.i(NVUtils.TAG, "ARGS WAS NULL !! ");
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "USER IDENTIFIER ERROR : " + e);
        }
        return true;
    }

    private boolean hit_event(JSONArray args, Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "EVENT !!");
        String eventName = null, ltv = null, scope = null;
        JSONObject attributes = null;

        try {
            JSONObject jObject = args.getJSONObject(0);
            if (jObject != null) {

                try {
                    eventName = jObject.getString("eventName");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "EVENT NAME ERROR : " + e);
                }

                try {
                    attributes = jObject.getJSONObject("attributes");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "EVENT ATTRIBUTES ERROR : " + e);
                }

                try {
                    ltv = jObject.getString("ltv");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "EVENT LIFE TIME VALUE ERROR : " + e);
                }

                try {
                    scope = jObject.getString("scope");
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "EVENT SCOPE ERROR : " + e);
                }


                if (ltv == null || ltv.isEmpty() || ltv.equals("null")) {
                    ltv = "10";
                }

                if (scope == null || scope.isEmpty() || scope.equals("null")) {
                    scope = "2";
                }

                eventCallback = backResponse;
                NotifyVisitorsApi.getInstance(mContext).event(eventName, attributes, ltv, scope);

            } else {
                Log.i(NVUtils.TAG, "EVENT PARSING JSON IS NULL :");
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "EVENT JSON OBJECT ERROR : " + e);
        }
        return true;
    }

    private boolean stop_banners(Context mContext) {
        Log.i(NVUtils.TAG, "STOP NOTIFICATION !!");
        try {
            NotifyVisitorsApi.getInstance(mContext).stopNotification();
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "STOP NOTIFICATION ERROR : " + e);
        }
        return true;
    }

    private boolean get_notification_center_data(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "NOTIFICATION CENTER DATA CALLBACK !!");
        try {
            NotifyVisitorsApi.getInstance(mContext).getNotificationCenterData(new OnCenterDataListener() {
                @Override
                public void getData(JSONObject response) {
                    backResponse.success(response.toString());
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "NOTIFICATION CENTER DATA CALLBACK ERROR : " + e);
        }
        return true;
    }

    private boolean get_notification_center_data_new(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "NOTIFICATION CENTER DATA CALLBACK NEW !!");
        try {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotifyVisitorsApi.getInstance(mContext).getNotificationCenterData(new OnCenterDataListener() {
                        @Override
                        public void getData(JSONObject response) {
                            //Log.e("NV", "Response = " + notificationListResponse);
                            backResponse.success(response.toString());
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "NOTIFICATION CENTER DATA NEW CALLBACK ERROR : " + e);
        }
        return true;
    }

    private boolean unread_notification_count(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "NOTIFICATION COUNT !!");
        try {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotifyVisitorsApi.getInstance(mContext)
                            .getNotificationCount(new NotificationCountInterface() {
                                @Override
                                public void getCount(int count) {
                                    Log.i(NVUtils.TAG, "COUNT : " + count);
                                    String strI = String.valueOf(count);
                                    backResponse.success(strI);
                                }
                            });
                }
            });

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "NOTIFICATION COUNT ERROR : " + e);
        }
        return true;
    }

    private boolean auto_start_android(Context mContext) {
        Log.i(NVUtils.TAG, "AUTO START ANDROID !!");
        try {
            Activity activity = this.cordova.getActivity();
            NotifyVisitorsApi.getInstance(mContext).setAutoStartPermission(activity);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "AUTO START ANDROID ERROR : " + e);
        }
        return true;
    }

    // private boolean start_chat_bot_screen(JSONArray args, Context mContext, CallbackContext backResponse) {
    //     Log.i(NVUtils.TAG, "CHAT BOT !!");
    //     String screenName = "empty";
    //     try {
    //         JSONObject jObject = args.getJSONObject(0);
    //         screenName = jObject.getString("screenName");
    //     } catch (Exception e) {
    //         Log.i(NVUtils.TAG, "CHAT BOT SCREEN NAME ERROR :" + e);
    //     }

    //     try {
    //         if (screenName == null || screenName.equalsIgnoreCase("empty")) {
    //             Log.i(NVUtils.TAG, "SCREEN NAME IS MISSING !!");
    //         } else {
    //             NotifyVisitorsApi.getInstance(mContext).startChatBot(screenName, new OnNotifyBotClickListener() {
    //                 @Override
    //                 public void onInAppRedirection(JSONObject data) {
    //                     resultLink = new PluginResult(PluginResult.Status.OK, data.toString());
    //                     resultLink.setKeepCallback(true);
    //                     backResponse.sendPluginResult(resultLink);
    //                 }
    //             });
    //         }
    //     } catch (Exception e) {
    //         Log.i(NVUtils.TAG, "CHAT BOT ERROR : " + e);

    //     }
    //     return true;
    // }

    private boolean create_notification_channel(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL !!");

        JSONObject jChannelData;
        String chId = null, chName = null, chDescription = null,
                chImportance = null,
                lightColor = null, soundFileName = null;
        boolean shouldVibrate = true, enableLights = true;

        int iChImportance = 3;

        try {
            jChannelData = args.getJSONObject(0);
            try {
                chId = jChannelData.getString("channelId");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL ID ERROR :" + e);
            }

            try {
                chName = jChannelData.getString("channelName");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL NAME ERROR :" + e);
            }

            try {
                chDescription = jChannelData.getString("channelDescription");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL DESCRIPTION ERROR :" + e);
            }

            try {
                chImportance = jChannelData.getString("channelImportance");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL IMPORTANCE ERROR :" + e);
            }

            try {
                enableLights = jChannelData.getBoolean("enableLights");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL ENABLE LIGHTS ERROR :" + e);
            }

            try {
                shouldVibrate = jChannelData.getBoolean("shouldVibrate");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL SHOULD VIBRATE ERROR :" + e);
            }

            try {
                lightColor = jChannelData.getString("lightColor");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL LIGHT COLOR ERROR :" + e);
            }
            try {
                soundFileName = jChannelData.getString("soundFileName");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL SOUND FILE NAME ERROR :" + e);
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL JSON PARSING ERROR :" + e);
        }

        try {
            if (lightColor == null || lightColor.isEmpty()) {
                lightColor = "#ffffff";
            }
            if (soundFileName == null || soundFileName.isEmpty()) {
                soundFileName = "";
            }
            if (chImportance != null && !chImportance.isEmpty()) {
                iChImportance = Integer.parseInt(chImportance);
            }

            NVNotificationChannels.Builder builder1 = new NVNotificationChannels.Builder();
            builder1.setChannelID(chId);
            builder1.setChannelName(chName);
            builder1.setImportance(iChImportance);
            builder1.setChannelDescription(chDescription);
            builder1.setEnableLights(enableLights);
            builder1.setLightColor(Color.parseColor(lightColor));
            builder1.setSoundFileName(soundFileName);
            builder1.setShouldVibrate(shouldVibrate);
            builder1.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            builder1.build();

            Set<NVNotificationChannels.Builder> nChannelSets = new HashSet<>();
            nChannelSets.add(builder1);

            NotifyVisitorsApi.getInstance(mContext).createNotificationChannel(nChannelSets);

        } catch (Exception e) {
            Log.i(NVUtils.TAG, " CREATE NOTIFICATION CHANNEL ERROR :" + e);
        }
        return true;
    }

    private boolean delete_notification_channel(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "DELETE NOTIFICATION CHANNEL !!");
        JSONObject jChannelData;
        try {
            jChannelData = args.getJSONObject(0);
            String chId = jChannelData.getString("channelId");
            NotifyVisitorsApi.getInstance(mContext).deleteNotificationChannel(chId);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "DELETE NOTIFICATION CHANNEL ERROR :" + e);
        }
        return true;
    }

    private boolean create_notification_channel_group(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL GROUP !!");
        JSONObject jChannelData;
        String groupId = "";
        String groupName = "";

        try {
            jChannelData = args.getJSONObject(0);

            try {
                groupId = jChannelData.getString("groupId");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL GROUP ID ERROR : " + e);
            }

            try {
                groupName = jChannelData.getString("groupName");
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "CREATE NOTIFICATION CHANNEL GROUP NAME ERROR : " + e);
            }
            NotifyVisitorsApi.getInstance(mContext).createNotificationChannelGroup(groupId, groupName);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, " CREATE NOTIFICATION CHANNEL GROUP ERROR :" + e);
        }
        return true;
    }

    private boolean delete_notification_channel_group(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "DELETE NOTIFICATION CHANNEL GROUP !!");
        JSONObject jChannelData;
        try {
            jChannelData = args.getJSONObject(0);
            String groupId = jChannelData.getString("groupId");
            NotifyVisitorsApi.getInstance(mContext).deleteNotificationChannelGroup(groupId);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "DELETE NOTIFICATION CHANNEL GROUP ERROR :" + e);
        }
        return true;
    }

    private boolean get_nv_uid(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "GET NV UID !!");
        try {
            String strI = NotifyVisitorsApi.getInstance(mContext).getNvUid();
            backResponse.success(strI);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, " GET NV UID ERROR :" + e);
        }
        return true;
    }

    private boolean event_survey_callback(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "GET EVENT SURVEY INFO !!");
        try {
            commonCallback = backResponse;
            if (eventName != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, eventName);
                result.setKeepCallback(true);
                backResponse.sendPluginResult(result);
            }
        } catch (Exception e) {
            Log.i(NVUtils.TAG, " GET LINK DATA ERROR :" + e);
        }
        return true;
    }

    private boolean known_user_identified(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "KNOWN USER IDENTIFIED !!");
        try {
            NotifyVisitorsApi.getInstance(mContext).knownUserIdentified(new OnKnownUserFound() {
                @Override
                public void getNvUid(JSONObject data) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, data);
                    result.setKeepCallback(true);
                    backResponse.sendPluginResult(result);
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, " KNOWN USER IDENTIFIED ERROR : " + e);
        }
        return true;
    }

    private boolean notification_click_callback(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "NOTIFICATION CLICK CALLBACK !!");
        try {            
            NotifyVisitorsApi.getInstance(mContext).notificationClickCallback(new OnNotificationClicksHandler() {
                @Override
                public void onClick(JSONObject response) {
                    //Log.i(NVUtils.TAG, "NOTIFICATION CLICK CALLBACK RESPONSE :" + response);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, response);
                    result.setKeepCallback(true);
                    backResponse.sendPluginResult(result);
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, " NOTIFICATION CLICK CALLBACK ERROR :" + e);
        }
        return true;
    }

    private boolean get_link_info(JSONArray args, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "GET LINK INFO !!");
        try {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        addHandler(args, backResponse);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, " GET LINK INFO ERROR : " + e);
        }
        return true;
    }

    private boolean get_click_info_cp(Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "GET CLICK INFO CP !!!!");
        try {
            JSONObject jsonData = NotifyVisitorsApi.getInstance(mContext).getClickInfoCP();
            if (jsonData != null) {
                final String str = jsonData.toString();
                //Log.i(NVUtils.TAG, "clickData = "+str);
                backResponse.success(str);
            }
            return true;

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "GET CLICK INFO CP ERROR : " + e);
            return false;
        }

    }

    private boolean get_link_data(JSONArray args, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "GET LINK DATA !!");
        try {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        addHandler(args, backResponse);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, " GET LINK DATA ERROR :" + e);
        }
        return true;
    }

    private boolean push_registration_token(CallbackContext backResponse, Context mContext) {
        Log.i(NVUtils.TAG, "PUSH REGISTRATION TOKEN !!");
        try {
            JSONObject nv_token = NotifyVisitorsApi.getInstance(mContext).getPushRegistrationToken();
            if (nv_token != null) {
                String token = nv_token.getString("subscriptionId");
                backResponse.success(token);
            } else {
                backResponse.success("unavailable");
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "PUSH REGISTRATION TOKEN ERROR : " + e);
        }
        return true;
    }

    private boolean get_session_data(CallbackContext backResponse, Context mContext) {
        Log.i(NVUtils.TAG, "GET SESSION DATA !!");
        try {
            JSONObject data = NotifyVisitorsApi.getInstance(mContext).getSessionData();
            if (data != null) {
                backResponse.success(data.toString());
            } else {
                backResponse.success("unavailable");
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "GET SESSION DATA ERROR : " + e);
        }
        return true;
    }

    private boolean track_screen(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "TRACK SCREEN !!");
        try {
            JSONObject data = args.getJSONObject(0);
            String name = data.getString("screenName");
            NotifyVisitorsApi.getInstance(mContext).trackScreen(name);

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "TRACK SCREEN ERROR : " + e);
        }
        return true;
    }

    private boolean enable_google_inApp_review(CallbackContext backResponse, Context mContext) {
        Log.i(NVUtils.TAG, "GOOGLE INAPP REVIEW !!");
        // try {
        //     Activity activity = this.cordova.getActivity();
        //     this.cordova.getActivity().runOnUiThread(new Runnable() {
        //         @Override
        //         public void run() {
        //             NotifyVisitorsApi.getInstance(activity).enableGoogleInAppReview(new OnReviewCompleteListener() {
        //                 @Override
        //                 public void onComplete(String s) {
        //                     backResponse.success(s);
        //                 }
        //             });
        //         }
        //     });
        // } catch (Exception e) {
        //     Log.i(NVUtils.TAG, "GOOGLE INAPP REVIEW  ERROR : " + e);
        // }
        return true;
    }

    private boolean subscribePushCategory(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "SUBSCRIBE PUSH CATEGORY !!");
        JSONArray categoryArray;
        boolean subscribe_signal;
        try {
            JSONObject jObject = args.getJSONObject(0);
            categoryArray = jObject.getJSONArray("categoryArray");
            subscribe_signal = jObject.getBoolean("subscribeSignal");
            NotifyVisitorsApi.getInstance(mContext).pushPreferences(categoryArray, subscribe_signal);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SUBSCRIBE PUSH CATEGORY ERROR : " + e);
        }
        return true;
    }

    private boolean getNotificationCenterCount(JSONArray args, Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT !!");
        try {
            JSONObject result;
            JSONObject appInboxInfo;

            result = null;
            tab1Label = null;
            tab1Name = null;
            tab2Label = null;
            tab2Name = null;
            tab3Label = null;
            tab3Name = null;

            try {
                result = args.getJSONObject(0);
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "Error :" + e);
            }

            if (result != null) {
                if (!result.isNull("tabCountInfo") && result.getJSONObject("tabCountInfo") != null
                        && result.getJSONObject("tabCountInfo").length() > 0) {
                    appInboxInfo = result.getJSONObject("tabCountInfo");
                    try {
                        tab1Label = appInboxInfo.getString("label_one");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT TAB1LABEL ERROR :" + e);
                    }

                    try {
                        tab1Name = appInboxInfo.getString("name_one");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT TAB1NAME ERROR :" + e);
                    }

                    try {
                        tab2Label = appInboxInfo.getString("label_two");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT TAB2LABEL ERROR :" + e);
                    }

                    try {
                        tab2Name = appInboxInfo.getString("name_two");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT TAB2NAME ERROR :" + e);
                    }

                    try {
                        tab3Label = appInboxInfo.getString("label_three");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT TAB3LABEL ERROR :" + e);
                    }

                    try {
                        tab3Name = appInboxInfo.getString("name_three");
                    } catch (Exception e) {
                        Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT TAB3NAME ERROR :" + e);
                    }

                    this.cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NVCenterStyleConfig config = new NVCenterStyleConfig();

                            if (tab1Label.equalsIgnoreCase("null")) {
                                tab1Label = null;
                            }

                            if (tab1Name.equalsIgnoreCase("null")) {
                                tab1Name = null;
                            }

                            if (tab2Label.equalsIgnoreCase("null")) {
                                tab2Label = null;
                            }

                            if (tab2Name.equalsIgnoreCase("null")) {
                                tab2Name = null;
                            }

                            if (tab3Label.equalsIgnoreCase("null")) {
                                tab3Label = null;
                            }

                            if (tab3Name.equalsIgnoreCase("null")) {
                                tab3Name = null;
                            }
                            config.setFirstTabDetail(tab1Label, tab1Name);
                            config.setSecondTabDetail(tab2Label, tab2Name);
                            config.setThirdTabDetail(tab3Label, tab3Name);

                            NotifyVisitorsApi.getInstance(mContext).getNotificationCenterCount(new OnCenterCountListener() {
                                @Override
                                public void getCount(JSONObject tabCount) {
                                    Log.i(NVUtils.TAG, "Tab Counts : " + tabCount);
                                    if (tabCount != null) {
                                        backResponse.success(tabCount.toString());
                                    } else {
                                        Log.i(NVUtils.TAG, "GETTING NULL COUNT OBJECT !!");
                                    }
                                }
                            }, config);
                        }
                    });
                } else {
                    Log.i(NVUtils.TAG, "INFO IS NULL GOING FOR STANDARD NOTIFICATION CENTER COUNT  !!");
                    this.cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NotifyVisitorsApi.getInstance(mContext).getNotificationCenterCount(new OnCenterCountListener() {
                                @Override
                                public void getCount(JSONObject tabCount) {
                                    Log.i(NVUtils.TAG, "Tab Counts : " + tabCount);
                                    if (tabCount != null) {
                                        backResponse.success(tabCount.toString());
                                    } else {
                                        Log.i(NVUtils.TAG, "GETTING NULL COUNT OBJECT !!");
                                    }
                                }
                            }, null);
                        }
                    });
                }
            } else {
                Log.i(NVUtils.TAG, "Arguments can't be null");
            }
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "GET NOTIFICATION CENTER COUNT ERROR : " + e);
        }
        return true;
    }


    private boolean activatePushPermissionPopup(JSONArray args, Context mContext, CallbackContext backResponse) {
        Log.i(NVUtils.TAG, "RUNTIME PUSH PERMISSION PROMPT");

        NVPopupDesign design = new NVPopupDesign();
        try {
            if(args != null) {
                JSONObject data = args.getJSONObject(0);
                if(data != null && data.has("nvPopupDesign")) {
                    JSONObject designInfo = data.getJSONObject("nvPopupDesign");
                    if(designInfo != null) {
                        if(designInfo.has("setTitle")) {
                            design.setTitle(designInfo.getString("setTitle"));
                        }
                        if(designInfo.has("setButtonTwoBorderColor")) {
                            design.setButtonTwoBorderColor(designInfo.getString("setButtonTwoBorderColor"));
                        }
                        if(designInfo.has("setButtonTwoBorderRadius")) {
                            design.setButtonTwoBorderRadius(designInfo.getInt("setButtonTwoBorderRadius"));
                        }
                        if(designInfo.has("setNumberOfSessions")) {
                            design.setNumberOfSessions(designInfo.getInt("setNumberOfSessions"));
                        }
                        if(designInfo.has("setResumeInDays")) {
                            design.setResumeInDays(designInfo.getInt("setResumeInDays"));
                        }
                        if(designInfo.has("setNumberOfTimesPerSession")) {
                            design.setNumberOfTimesPerSession(designInfo.getInt("setNumberOfTimesPerSession"));
                        }
                        if(designInfo.has("setTitleTextColor")) {
                            design.setTitleTextColor(designInfo.getString("setTitleTextColor"));
                        }
                        if(designInfo.has("setDescription")) {
                            design.setDescription(designInfo.getString("setDescription"));
                        }
                        if(designInfo.has("setDescriptionTextColor")) {
                            design.setDescriptionTextColor(designInfo.getString("setDescriptionTextColor"));
                        }
                        if(designInfo.has("setBackgroundColor")) {
                            design.setBackgroundColor(designInfo.getString("setBackgroundColor"));
                        }
                        if(designInfo.has("setButtonOneBorderColor")) {
                            design.setButtonOneBorderColor(designInfo.getString("setButtonOneBorderColor"));
                        }
                        if(designInfo.has("setButtonOneBackgroundColor")) {
                            design.setButtonOneBackgroundColor(designInfo.getString("setButtonOneBackgroundColor"));
                        }
                        if(designInfo.has("setButtonOneBorderRadius")) {
                            design.setButtonOneBorderRadius(designInfo.getInt("setButtonOneBorderRadius"));
                        }
                        if(designInfo.has("setButtonOneText")) {
                            design.setButtonOneText(designInfo.getString("setButtonOneText"));
                        }
                        if(designInfo.has("setButtonOneTextColor")) {
                            design.setButtonOneTextColor(designInfo.getString("setButtonOneTextColor"));
                        }
                        if(designInfo.has("setButtonTwoText")) {
                            design.setButtonTwoText(designInfo.getString("setButtonTwoText"));
                        }
                        if(designInfo.has("setButtonTwoTextColor")) {
                            design.setButtonTwoTextColor(designInfo.getString("setButtonTwoTextColor"));
                        }
                        if(designInfo.has("setButtonTwoBackgroundColor")) {
                            design.setButtonTwoBackgroundColor(designInfo.getString("setButtonTwoBackgroundColor"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "PARSE DESIGN DATA ERROR : " + e);
        }

        try {
            Activity activity = this.cordova.getActivity();
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotifyVisitorsApi.getInstance(activity).activatePushPermissionPopup(design, new OnPushRuntimePermission() {
                        @Override
                        public void getPopupInfo(JSONObject result) {
                            backResponse.success(result.toString());
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "DESIGN ERROR : " + e);
        }

        return true;
    }

    public boolean enablePushPermission(JSONArray args, Context context) {
        try {
            JSONObject data = args.getJSONObject(0);
            boolean isAllowed = true;
            if(data != null){
                 isAllowed = data.getBoolean("isAllowed");
                 NotifyVisitorsApi.getInstance(context).enablePushPermission(isAllowed);
            } else {
                NotifyVisitorsApi.getInstance(context).enablePushPermission(isAllowed);
            }
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "ENABLE PUSH PERMISSION ERROR :" + e);
        }

        return true;
    }

    public boolean nativePushPermissionPrompt(Context context, CallbackContext backResponse) {
        try {
            Activity activity = this.cordova.getActivity();
                this.cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NotifyVisitorsApi.getInstance(activity).nativePushPermissionPrompt(new OnPushRuntimePermission() {
                            @Override
                            public void getPopupInfo(JSONObject result) {
                                backResponse.success(result.toString());
                            }
                        });
                    }
            });
        } catch (Exception e) {
            Log.e(NVUtils.TAG, "NATIVE PUSH PERMISSION PROMPT ERROR :" + e);
        }
        return true;
    }

    private boolean is_payload_from_nv_platform(JSONArray args, CallbackContext backResponse, Context mContext) {
        Log.i(NVUtils.TAG, "IS PAYLOAD FROM NV PLATFORM !!");
        try {
            JSONObject data = args.getJSONObject(0);
            String pushPayload = data.getString("pushPayload");
            //Log.i(NVUtils.TAG, "pushPayload => " + pushPayload);

            JSONObject jsonObject = new JSONObject(pushPayload);
            Intent intent = new Intent();

            // Use keys() to iterate over the keys in the JSONObject
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next(); // Get the key
                Object value = jsonObject.get(key); // Get the value associated with the key

                // Add the key-value pair to the Intent jsonObject based on the type
                if (value instanceof Integer) {
                    intent.putExtra(key, (Integer) value);
                } else if (value instanceof String) {
                    intent.putExtra(key, (String) value);
                } else if (value instanceof Boolean) {
                    intent.putExtra(key, (Boolean) value);
                } else if (value instanceof Double) {
                    intent.putExtra(key, (Double) value);
                } else if (value instanceof Long) {
                    intent.putExtra(key, (Long) value);
                } else {
                    // Handle unknown or unsupported data types
                    intent.putExtra(key, value.toString());
                }
            }

            boolean b = NotifyVisitorsApi.getInstance(mContext).isPayloadFromNvPlatform(intent);
            if (b) {
                backResponse.success("true");
            } else {
                backResponse.success("false");
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "IS PAYLOAD FROM NV PLATFORM ERROR : " + e);
        }
        return true;
    }

    private boolean get_nv_fcm_payload(JSONArray args, Context mContext) {
        Log.i(NVUtils.TAG, "GET NV FCM PAYLOAD !!");
        try {
            JSONObject data = args.getJSONObject(0);
            String pushPayload = data.getString("pushPayload");
            //Log.i(NVUtils.TAG, "pushPayload => " + pushPayload);

            JSONObject jsonObject = new JSONObject(pushPayload);
            Intent intent = new Intent();

            // Use keys() to iterate over the keys in the JSONObject
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next(); // Get the key
                Object value = jsonObject.get(key); // Get the value associated with the key

                // Add the key-value pair to the Intent jsonObject based on the type
                if (value instanceof Integer) {
                    intent.putExtra(key, (Integer) value);
                } else if (value instanceof String) {
                    intent.putExtra(key, (String) value);
                } else if (value instanceof Boolean) {
                    intent.putExtra(key, (Boolean) value);
                } else if (value instanceof Double) {
                    intent.putExtra(key, (Double) value);
                } else if (value instanceof Long) {
                    intent.putExtra(key, (Long) value);
                } else {
                    // Handle unknown or unsupported data types
                    intent.putExtra(key, value.toString());
                }
            }
            NotifyVisitorsApi.getInstance(mContext).getNV_FCMPayload(intent);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "GET NV FCM PAYLOAD ERROR : " + e);
        }
        return true;
    }


    /* initialize method start */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.i(NVUtils.TAG, "INITIALIZE !!");
        Log.i(NVUtils.TAG, "CORDOVA PLUGIN VERSION :" + NVUtils.PLUGIN_VERSION);
        try {
            handleIntent(cordova.getActivity().getIntent());
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "GETTING INTENT FROM ACTIVITY : " + e);
        }

        try {
            Context context = this.cordova.getActivity();
            fetchEventSurvey(context);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "FETCHING EVENT SURVEY DATA : " + e);
        }
    }


    /* initialize method end */

    /* onNewIntent method start */
    @Override
    public void onNewIntent(Intent intent) {
        Log.i(NVUtils.TAG, "ON NEW INTENT !!");
        try {
            handleIntent(intent);
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "ON NEW INTENT ERROR : " + e);
        }
    }
    /* onNewIntent method end */


    /* handleIntent method start */
    private void handleIntent(Intent intent) {
        Log.i(NVUtils.TAG, " INSIDE HANDLE INTENT !!");

        JSONObject dataInfo, finalDataInfo;

        String action = intent.getAction();
        Uri url = intent.getData();
        finalDataInfo = new JSONObject();

        // if app was not launched by the url - ignore
        if (!Intent.ACTION_VIEW.equals(action) || url == null) {
            if ((intent.hasExtra("source") && intent.getStringExtra("source").equalsIgnoreCase("nv")) || (intent.hasExtra("notifyvisitors_cta"))) {
                try {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        dataInfo = new JSONObject();
                        String nv_type = "push";
                        String nvCtaData = (bundle.containsKey("notifyvisitors_cta")) ? bundle.getString("notifyvisitors_cta") : "";
                        
                        for (String key : bundle.keySet()) {
                            try {
                                dataInfo.put(key, JSONObject.wrap(bundle.get(key)));
                                if (key.equals("nv_type")) {
                                    try {
                                        nv_type = bundle.get(key).toString();
                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }

                        if (dataInfo.has("notifyvisitors_cta")) {
                            try {
                                dataInfo.remove("notifyvisitors_cta");
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }

                        dataInfo.put("type", nv_type);
                        finalDataInfo.put("parameters", dataInfo);
                        if(nvCtaData != null && !nvCtaData.isEmpty()) finalDataInfo.put("notifyvisitors_cta", new JSONObject(nvCtaData));
                        
                        lastEvent = finalDataInfo;
                        consumeEvents();
                    }
                } catch (Exception e) {
                    Log.i(NVUtils.TAG, "HANDLE INTENT PARSE DATA ERROR : " + e);
                }
            }
        } else {
            try {
                Set<String> queryParameter = url.getQueryParameterNames();
                dataInfo = new JSONObject();
                for (String s : queryParameter) {
                    String mValue = url.getQueryParameter(s);
                    dataInfo.put(s, mValue);
                }
                finalDataInfo.put("parameters", dataInfo);
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "QUERY PARAMETER ERROR : " + e);
            }

            try {
                JSONObject ourl = new JSONObject();
                String mSchema = url.getScheme();
                String host = url.getHost();
                String path = url.getPath();
                ourl.put("scheme", mSchema);
                ourl.put("host", host);
                ourl.put("path", path);
                //ourl.put("source", "nv");

                finalDataInfo.put("url", ourl);
                lastEvent = finalDataInfo;
                consumeEvents();
            } catch (Exception e) {
                Log.i(NVUtils.TAG, "PARSING JSON ERROR : " + e);
            }
        }
    }
    /* handleIntent method end */


    private void addHandler(JSONArray args, final CallbackContext callbackContext) {
        this._handlers.add(callbackContext);
        this.consumeEvents();
    }


    private void consumeEvents() {
        if (this._handlers.size() == 0 || lastEvent == null) {
            return;
        }

        for (CallbackContext callback : this._handlers) {
            sendToJs(lastEvent, callback);
        }
        lastEvent = null;
    }


    private void sendToJs(JSONObject event, CallbackContext callback) {
        final PluginResult result = new PluginResult(PluginResult.Status.OK, event.toString());
        result.setKeepCallback(true);
        callback.sendPluginResult(result);
    }


    private void sendToJs2(String data, CallbackContext callback) {
        final PluginResult result = new PluginResult(PluginResult.Status.OK, data.toString());
        result.setKeepCallback(true);
        callback.sendPluginResult(result);
    }

    private void canOpenApp(String uri, final CallbackContext callbackContext) {
        Context ctx = this.cordova.getActivity().getApplicationContext();
        final PackageManager pm = ctx.getPackageManager();

        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            callbackContext.success();
        } catch (PackageManager.NameNotFoundException e) {
        }

        callbackContext.error("");
    }


    @Override
    protected void pluginInitialize() {
        Log.i(NVUtils.TAG, "PLUGIN INITIALIZE !!!!");
    }


    @Override
    public void onPause(boolean multitasking) {
        Log.i(NVUtils.TAG, "ON PAUSE !!!!");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i(NVUtils.TAG, "ON ACTIVITY RESULT !!");
    }

    @Override
    public void onDestroy() {
        Log.i(NVUtils.TAG, "ON DESTROY !!");
    }

    @Override
    public void onResume(boolean multitasking) {
        Log.i(NVUtils.TAG, "ON RESUME !!");
    }

    @Override
    public void onStart() {
        Log.i(NVUtils.TAG, "ON START !!");
    }

    @Override
    public void onStop() {
        Log.i(NVUtils.TAG, "ON STOP !!");
    }


    private void fetchEventSurvey(Context context) {
        try {
            NotifyVisitorsApi.getInstance(context).getEventResponse(new OnEventTrackListener() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    //surveyCallbackData = jsonObject.toString();
                    sendResponse(jsonObject);
                }
            });
        } catch (Exception e) {
            Log.i(NVUtils.TAG, "FETCH EVENT SURVEY ERROR : " + e);
        }

    }

    private void sendResponse(JSONObject response) {
        try {
            if (response != null) {
                eventName = response.getString("eventName");

                // check clicked is banner or survey
                if (eventName.equalsIgnoreCase("Survey Submit") ||
                        eventName.equalsIgnoreCase("Survey Attempt") ||
                        eventName.equalsIgnoreCase("Banner Clicked") ||
                        eventName.equalsIgnoreCase("Banner Impression")) {
                    if (showCallback != null) {
                        PluginResult result = new PluginResult(PluginResult.Status.OK, response.toString());
                        result.setKeepCallback(true);
                        showCallback.sendPluginResult(result);
                    } else {
                        Log.i(NVUtils.TAG, "SHOW CALLBACK CONTEXT IS NULL !!");
                    }
                } else if (eventName.equalsIgnoreCase("Push_Registered")) {
                    if (pushRegisteredCallback != null) {
                        // do code next time
                    } else {
                        Log.i(NVUtils.TAG, "PUSH REGISTERED CALLBACK CONTEXT IS NULL !!");
                    }
                } else {
                    if (eventCallback != null) {
                        PluginResult result = new PluginResult(PluginResult.Status.OK, response.toString());
                        result.setKeepCallback(true);
                        eventCallback.sendPluginResult(result);
                    } else {
                        Log.i(NVUtils.TAG, "EVENT CALLBACK CONTEXT IS NULL !!");
                    }
                }

                // send commom callback
                if (commonCallback != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, response.toString());
                    result.setKeepCallback(true);
                    commonCallback.sendPluginResult(result);
                }
            } else {
                Log.i(NVUtils.TAG, "RESPONSE IS NULL !!");
            }

        } catch (Exception e) {
            Log.i(NVUtils.TAG, "SURVEY SEND RESPONSE ERROR : " + e);
        }
    }

}

/* class implementation end */

