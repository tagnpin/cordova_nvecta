var argscheck = require("cordova/argscheck"),
  utils = require("cordova/utils"),
  exec = require("cordova/exec");

var PLUGIN_NAME = "NotifyVisitors";
DEFAULT_EVENT_NAME = "didLaunchAppFromLink";

function NotifyVisitors() {
  console.log("NotifyVisitors Plugin Constructor !!");
}

/* show in-app banner */
NotifyVisitors.prototype.show = function (
  tokens,
  customObjects,
  fragmentName,
  callback
) {
  window.cordova.exec(callback, function () {}, PLUGIN_NAME, "show", [
    {
      tokens: tokens,
      customObjects: customObjects,
      fragmentName: fragmentName,
    },
  ]);
};

/* show in-app banner */
NotifyVisitors.prototype.showInAppMessage = function (
  tokens,
  customObjects,
  fragmentName,
  callback
) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "showInAppMessage",
    [
      {
        tokens: tokens,
        customObjects: customObjects,
        fragmentName: fragmentName,
      },
    ]
  );
};

/* open notification-center */
NotifyVisitors.prototype.showNotifications = function (appInboxInfo, dismiss) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "showNotifications",
    [
      {
        appInboxInfo: appInboxInfo,
        dismiss: dismiss,
      },
    ]
  );
};

/* open notification-center */
NotifyVisitors.prototype.openNotificationCenter = function (
  appInboxInfo,
  dismiss,
  callback
) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "openNotificationCenter",
    [
      {
        appInboxInfo: appInboxInfo,
        dismiss: dismiss,
      },
    ]
  );
};

/* stop banners and surveys */
NotifyVisitors.prototype.stopNotification = function () {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "stopNotification",
    []
  );
};

/* schedule push notification */
NotifyVisitors.prototype.scheduleNotification = function (
  nid,
  tag,
  time,
  title,
  message,
  url,
  icon
) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "scheduleNotification",
    [
      {
        nid: nid,
        tag: tag,
        time: time,
        title: title,
        message: message,
        url: url,
        icon: icon,
      },
    ]
  );
};

/* send user-details to panel */
NotifyVisitors.prototype.userIdentifier = function (userID, jsonObject) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "userIdentifier",
    [{ userID: userID, jsonObject: jsonObject }]
  );
};

/* send user-details to panel */
NotifyVisitors.prototype.setUserIdentifier = function (jsonObject, callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "setUserIdentifier",
    [{ jsonObject: jsonObject }]
  );
};

/* hit a event in panel */
NotifyVisitors.prototype.event = function (
  eventName,
  attributes,
  ltv,
  scope,
  callback
) {
  window.cordova.exec(callback, function () {}, PLUGIN_NAME, "event", [
    {
      eventName: eventName,
      attributes: attributes,
      ltv: ltv,
      scope: scope,
    },
  ]);
};

/* get notification-center data in JSON (deprecated) */
NotifyVisitors.prototype.getNotificationDataListener = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "ncDataCallback",
    []
  );
};

/* get notification-center data in JSON */
NotifyVisitors.prototype.getNotificationCenterData = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "getNotificationCenterData",
    []
  );
};

/* notification count */
NotifyVisitors.prototype.getNotificationCount = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "notificationCount",
    []
  );
};

/* notification  center count */
NotifyVisitors.prototype.getNotificationCenterCount = function (
  tabCountInfo,
  callback
) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "getNotificationCenterCount",
    [
      {
        tabCountInfo: tabCountInfo,
      },
    ]
  );
};

/* get fcm subscribe token */
NotifyVisitors.prototype.getRegistrationToken = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "getRegistrationToken",
    []
  );
};

/* get session data */
NotifyVisitors.prototype.getSessionData = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "getSessionData",
    []
  );
};

/* track screen */
NotifyVisitors.prototype.trackScreen = function (screenName) {
  window.cordova.exec(function () {}, function () {}, PLUGIN_NAME, "trackScreen", [
    {
      screenName: screenName,
    },
  ]);
};

/* notification click callback [JS Functions] */
NotifyVisitors.prototype.notificationClickCallback = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "notificationClickCallback",
    []
  );
};

/* stop Geofence push notification for date and time */
NotifyVisitors.prototype.stopGeofencePushforDateTime = function (
  stopforDateTime,
  additionalHrs
) {
  window.cordova.exec(
    function () {},
    function () {}.PLUGIN_NAME,
    "stopGeofencePushforDateTime",
    [
      {
        stopforDateTime: stopforDateTime,
        additionalHrs: additionalHrs,
      },
    ]
  );
};

/* open chat-bot webview */
// NotifyVisitors.prototype.startChatBot = function (screenName, callback) {
// 	window.cordova.exec(callback, function () {}, PLUGIN_NAME, 'startChatBot',
// 	[{ 'screenName': screenName }]);
// };

/* create Notification Channel in android */
NotifyVisitors.prototype.createNotificationChannel = function (
  chId,
  chName,
  chDescription,
  chImportance,
  enableLights,
  shouldVibrate,
  lightColor,
  soundFileName
) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "createNotificationChannel",
    [
      {
        channelId: chId,
        channelName: chName,
        channelDescription: chDescription,
        channelImportance: chImportance,
        enableLights: enableLights,
        shouldVibrate: shouldVibrate,
        lightColor: lightColor,
        soundFileName: soundFileName,
      },
    ]
  );
};

/* delete Notification Channel in android */
NotifyVisitors.prototype.deleteNotificationChannel = function (channelId) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "deleteNotificationChannel",
    [{ channelId: channelId }]
  );
};

/* create Notification Channel group in android */
NotifyVisitors.prototype.createNotificationChannelGroup = function (
  groupId,
  groupName
) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "createNotificationChannelGroup",
    [
      {
        groupId: groupId,
        groupName: groupName,
      },
    ]
  );
};

/* delete Notification Channel group in android */
NotifyVisitors.prototype.deleteNotificationChannelGroup = function (groupId) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "deleteNotificationChannelGroup",
    [{ groupId: groupId }]
  );
};

/* unique uid will get from panel*/
NotifyVisitors.prototype.getNvUID = function (callback) {
  window.cordova.exec(callback, function () {}, PLUGIN_NAME, "getNvUID", []);
};

/* callback response of event and surveys */
NotifyVisitors.prototype.getEventSurveyInfo = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "getEventSurveyInfo",
    []
  );
};

/* scroll View ios only  */
NotifyVisitors.prototype.scrollViewDidScroll_iOS_only = function () {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "scrollViewDidScroll_iOS_only",
    [{ scrollView: scrollView }]
  );
};

/* permission for reciving push notification in background or app is killed. android-only */
NotifyVisitors.prototype.autoStart_android_only = function () {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "autoStartAndroid",
    []
  );
};

/* get push or banner click data in callback */
NotifyVisitors.prototype.getClickInfoCP = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "getClickInfoCP",
    []
  );
};

/* get push or banner click data in callback through deep-link */
NotifyVisitors.prototype.getLinkInfo = function (callback) {
  window.cordova.exec(callback, function () {}, PLUGIN_NAME, "getLinkInfo", []);
};

/* get push or banner click data in callback through deep-link */
NotifyVisitors.prototype.requestInAppReview = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "googleInAppReview",
    []
  );
};

/* */
NotifyVisitors.prototype.subscribe = function (eventName, callback) {
  if (!callback) {
    console.warn("Can't subscribe to event without a callback");
    return;
  }
  if (!eventName) {
    eventName = DEFAULT_EVENT_NAME;
  }
  var innerCallback = function (msg) {
    callback(msg);
  };

  window.cordova.exec(
    innerCallback,
    function () {},
    PLUGIN_NAME,
    "getLinkData",
    [{ eventName: eventName }]
  );
};

/* */
NotifyVisitors.prototype.unsubscribe = function (eventName) {
  if (!eventName) {
    eventName = DEFAULT_EVENT_NAME;
  }

  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "unsubscribe",
    [{ eventName: eventName }]
  );
};

/* get push or banner click data in callback through deep-link */
NotifyVisitors.prototype.subscribePushCategory = function (
  categoryArray,
  subscribeSignal
) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "subscribePushCategory",
    [
      {
        categoryArray: categoryArray,
        subscribeSignal: subscribeSignal,
      },
    ]
  );
};

/* push permission prompt for android 12+ devices */
NotifyVisitors.prototype.activatePushPermissionPopup = function (
  nvPopupDesign,
  callback
) {
  if (nvPopupDesign == null) {
    nvPopupDesign = {
      setButtonTwoBorderColor: "#6db76c",
      setButtonTwoBorderRadius: 25,
      setNumberOfSessions: 1,
      setResumeInDays: 1,
      setNumberOfTimesPerSession: 1,
      setTitle: "Get Notified  🔔",
      setTitleTextColor: "#000000",
      setDescription:
        "Please Enable Push Notifications on Your Device For Latest Updates !!",
      setDescriptionTextColor: "#000000",
      setBackgroundColor: "#EBEDEF",
      setButtonOneBorderColor: "#6db76c",
      setButtonOneBackgroundColor: "#26a524",
      setButtonOneBorderRadius: 25,
      setButtonOneText: "Allow",
      setButtonOneTextColor: "#FFFFFF",
      setButtonTwoText: "Cancel",
      setButtonTwoTextColor: "#FFFFFF",
      setButtonTwoBackgroundColor: "#FF0000",
    };
  }
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "activatePushPermissionPopup",
    [{ nvPopupDesign: nvPopupDesign }]
  );
};

/* manually enabling push permission for android 12+ devices */
NotifyVisitors.prototype.enablePushPermission = function (isAllowed) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "enablePushPermission",
    [{ isAllowed: isAllowed }]
  );
};

/* native push permission prompt for android 12+ devices */
NotifyVisitors.prototype.nativePushPermissionPrompt = function (callback) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "nativePushPermissionPrompt",
    []
  );
};

/* callback response of knownUserIdentified */
NotifyVisitors.prototype.knownUserIdentified = function (callback) {
  //console.log('knownUserIdentified function called');
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "knownUserIdentified",
    []
  );
};

/* verify push payload */
NotifyVisitors.prototype.isPayloadFromNvPlatform = function (
  pushPayload,
  callback
) {
  window.cordova.exec(
    callback,
    function () {},
    PLUGIN_NAME,
    "isPayloadFromNvPlatform",
    [{ pushPayload: pushPayload }]
  );
};

/* send push payload to NotifyVisitors SDK */
NotifyVisitors.prototype.getNV_FCMPayload = function (pushPayload) {
  window.cordova.exec(
    function () {},
    function () {},
    PLUGIN_NAME,
    "getNV_FCMPayload",
    [{ pushPayload: pushPayload }]
  );
};

/* export module to project*/
if (typeof module != "undefined" && module.exports) {
  var NotifyVisitors = new NotifyVisitors();
  module.exports = NotifyVisitors;
}
