//
//  notifyvisitors_iOS.h
//  notifyvisitors_iOS
//
//  Created by Tagnpin on 04/02/19.
//  Copyright © 2019 Tagnpin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import "notifyvisitors/notifyvisitors.h"

extern  NSDictionary * _Nonnull  nvPushDataForCallback;
extern BOOL nvPushObserverReady;
@interface notifyvisitors_iOS : CDVPlugin <notifyvisitorsDelegate>{
    // Handlers for URL events
    NSMutableArray *_handlers;
    CDVPluginResult *_lastEvent;
  }

typedef void(^nvContentHandler)(UNNotificationContent *_Nonnull);
+(instancetype _Nonnull )sharedInstance;

#pragma mark - Show inAppMessage (Banner/Survey methods)

- (void)showInAppMessage:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)show:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)stopNotification:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)scrollViewDidScroll_iOS_only:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)getEventSurveyInfo:(CDVInvokedUrlCommand *_Nonnull)command;

#pragma mark - Open Notification Center old and new method with Close button Callback

- (void)openNotificationCenter:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)showNotifications:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)getNotificationCenterData:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)getNotificationCenterCount:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)notificationCount:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)ncDataCallback:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)sendTabCountResponse: (NSDictionary *_Nonnull)nvCenterCounts responseToSend:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)notificationClickCallback:(CDVInvokedUrlCommand *_Nonnull)command;

#pragma mark - Track Events method

- (void)event:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)trackScreen:(CDVInvokedUrlCommand*_Nonnull)command;

#pragma mark - Push Notifications related methods

- (void)scheduleNotification:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)subscribePushCategory:(CDVInvokedUrlCommand *_Nullable)command ;
- (void)stopGeofencePushforDateTime:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getRegistrationToken:(CDVInvokedUrlCommand *_Nonnull)command;

#pragma mark - Track User Methods

- (void)setUserIdentifier:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)userIdentifier:(CDVInvokedUrlCommand*_Nonnull)command;
- (void)getNvUID:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)knownUserIdentified:(CDVInvokedUrlCommand *_Nonnull)command;

#pragma mark - ChatBot Methods
- (void)startChatBot:(CDVInvokedUrlCommand *_Nonnull)command;

#pragma mark - GetLinkInfo and other callbacks handler methods

- (void)getLinkInfo:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getClickInfoCP:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getLinkData:(CDVInvokedUrlCommand *_Nonnull)command;


#pragma mark - Other Methods
- (void)googleInAppReview:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getSessionData:(CDVInvokedUrlCommand *_Nonnull)command;


#pragma mark - Plugin's Internal Methods

- (void)_addHandlers:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)sendLinkInfo:(NSNotification *_Nonnull)notification;
- (void)sendToJs;
- (CDVPluginResult *_Nonnull)createResult: (NSDictionary *_Nonnull)nvUserInfo;
- (void)setNvDeepLinkObserver;

- (void)application:(UIApplication *_Nullable)application continueUserActivity:(NSUserActivity *_Nonnull)userActivity restorationHandler:(void (^_Nullable)(NSArray * _Nullable restorableObjects))restorationHandler;

#pragma mark - Android Specific dummy function

- (void)autoStartAndroid:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)createNotificationChannel:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)deleteNotificationChannel:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)createNotificationChannelGroup:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)deleteNotificationChannelGroup:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)enablePushPermission:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)activatePushPermissionPopup:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)isPayloadFromNvPlatform:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)getNV_FCMPayload:(CDVInvokedUrlCommand *_Nonnull)command;
- (void)nativePushPermissionPrompt:(CDVInvokedUrlCommand *_Nonnull)command;

@end
