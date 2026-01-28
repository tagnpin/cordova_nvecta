
//  Created by Siddharth gupta on 29/11/18.
//  Copyright © 2018 notifyvisitors. All rights reserved.

#import "AppDelegate.h"
#import <UserNotifications/UserNotifications.h>

//@interface irnvPluginAppDelegate (notification)
@interface AppDelegate (notification) <UNUserNotificationCenterDelegate>

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options;

- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> *))restorationHandler;

- (void)applicationDidEnterBackground:(UIApplication *)application;
- (void)applicationWillEnterForeground:(UIApplication *)application;
- (void)applicationDidBecomeActive:(UIApplication *)application;
- (void)applicationWillTerminate:(UIApplication *)application;

- (id) getCommandInstance:(NSString*)className;

@property (nonatomic, retain) NSDictionary *launchNotification;
@end
