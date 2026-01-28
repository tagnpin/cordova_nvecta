#import "nvPluginAppDelegate.h"
#import <objc/runtime.h>

#import "notifyvisitors/notifyvisitors.h"
#import "notifyvisitors_iOS.h"
typedef void (^nvPushClickCheckRepeatHandler)(BOOL isnvPushActionRepeat);
typedef void (^nvPushClickCheckRepeatBlock)(nvPushClickCheckRepeatHandler completionHandler);

int nvCheckPushClickTimeCounter = 0;
int watingTime = 45 ;  //in seconds

static char launchNotificationKey;

@implementation AppDelegate (notification)

- (id) getCommandInstance:(NSString*)className {
    return [self.viewController getCommandInstance:className];
}

// its dangerous to override a method from within a category.
// Instead we will use method swizzling. we set this up in the load call.
+ (void)load {
    [[NSNotificationCenter defaultCenter] addObserver:self selector: @selector(setupNotificationChecker:) name: UIApplicationDidFinishLaunchingNotification object: nil];
}

+ (void)setupNotificationChecker:(NSNotification*)notification {
    NSLog(@"setupNotificationChecker with notification name = %@\nuserinfo= %@", notification.name, notification.userInfo);
    [[self sharedInstance] createNotificationChecker: notification];
}

+ (instancetype)sharedInstance {
    static AppDelegate *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^ {
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

// This code will be called immediately after application:didFinishLaunchingWithOptions:. We need
// to process notifications in cold-start situations

- (void)createNotificationChecker:(NSNotification *)notification {
    if (notification) {
        NSDictionary *launchOptions = [notification userInfo];
        if (launchOptions)
            self.launchNotification = [launchOptions objectForKey: @"UIApplicationLaunchOptionsRemoteNotificationKey"];
    }
}

- (NSMutableArray *)launchNotification {
    return objc_getAssociatedObject(self, &launchNotificationKey);
}

- (void)setLaunchNotification:(NSDictionary *)aDictionary {
    objc_setAssociatedObject(self, &launchNotificationKey, aDictionary, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}


-(BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    NSLog(@"NotifyVisitors CORDOVA_PLUGIN_VERSION : 4.1.0 !!");
    NSLog(@"NotifyVisitors-Cordova didFinishLaunchingWithOptions !!");
    [self nvTurnOffAutomaticScreenViewEventForIonicCordovaPlugin];
    NSString *nvMode = nil;
#if DEBUG
    nvMode = @"debug";
#else
    nvMode = @"live";
#endif
    [notifyvisitors Initialize: nvMode];
    [notifyvisitors RegisterPushWithDelegate: self App: application launchOptions: launchOptions];
    
    return [super application: application didFinishLaunchingWithOptions: launchOptions];
}

-(BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    NSLog(@"NotifyVisitors-Cordova OpenUrl !!");
    [self sendLinkInfo:app openURL:url];
    return [super application: app openURL: url options: options];
}

- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> *))restorationHandler {
    NSLog(@"NotifyVisitors-Cordova continueUserActivity !!");
    if ([userActivity.activityType isEqualToString: NSUserActivityTypeBrowsingWeb]) {
        NSURL *nvAppULinkUrl = [userActivity webpageURL];
        if(nvAppULinkUrl != nil) {
            [self sendLinkInfo:application openURL:nvAppULinkUrl];
        }
    }
    return YES;
}

- (void)dealloc {
    self.launchNotification    = nil; // clear the association and release the object
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    NSLog(@"NotifyVisitors-Cordova applicationDidEnterBackground !!");
    [notifyvisitors applicationDidEnterBackground: application];
}

- (void)applicationWillEnterForeground:(UIApplication *)application{
    NSLog(@"NotifyVisitors-Cordova applicationWillEnterForeground !!");
    [notifyvisitors applicationWillEnterForeground:application];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    NSLog(@"NotifyVisitors-Cordova applicationDidBecomeActive !!");
    [notifyvisitors applicationDidBecomeActive: application];
}

- (void)applicationWillTerminate:(UIApplication *)application {
    NSLog(@"NotifyVisitors-Cordova applicationWillTerminate !!");
    [notifyvisitors applicationWillTerminate];
}

-(void)application: (UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken: (NSData *)deviceToken  {
    NSLog(@"NotifyVisitors-Cordova DeviceToken !!!" );
    [notifyvisitors DidRegisteredNotification:application deviceToken: deviceToken];
}

-(void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"Push Registration Failed Due to the Following Error = %@", [error localizedDescription]);
}

# pragma mark UNNotificationCenter Delegate Methods

- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler  API_AVAILABLE(ios(10.0)){
    NSLog(@"NotifyVisitors-Cordova willPresentNotification !!");
    [notifyvisitors willPresentNotification: notification withCompletionHandler: completionHandler];
    //    NSLog(@"Notification Payload = %@", notification);
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler  API_AVAILABLE(ios(10.0)){
    NSLog(@"NotifyVisitors-Cordova didReceiveNotificationResponse !!");
    //[[NSNotificationCenter defaultCenter] addObserver:self selector: @selector(triggerAction:) name: @"NVInAppViewConroller" object:nil];
    if(!nvPushObserverReady) {
        [self nvPushClickCheckInSeconds: 1 withBlock:^(nvPushClickCheckRepeatHandler completionHandler) {
            [notifyvisitors didReceiveNotificationResponse: response];
        }];
    } else {
        [notifyvisitors didReceiveNotificationResponse: response];
    }
    
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    NSLog(@"NotifyVisitors-Cordova didReceiveRemoteNotification !!");
    // NSLog(@"Notification Payload = %@", userInfo);
    [notifyvisitors didReceiveRemoteNotification: userInfo fetchCompletionHandler: completionHandler];
}

-(void)nvPushClickCheckInSeconds:(int)seconds withBlock:(nvPushClickCheckRepeatBlock)nvPushCheckBlock {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, seconds * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
        nvCheckPushClickTimeCounter = nvCheckPushClickTimeCounter + seconds;
        if(!nvPushObserverReady) {
            if (nvCheckPushClickTimeCounter < watingTime) {
                return [self nvPushClickCheckInSeconds: seconds withBlock: nvPushCheckBlock];
                //[self irDispatchReatforTrackingDataInSeconds: seconds withBlock: irBlock];
            } else {
                //irTempTrackResponse = @{@"Authentication" : @"failed",@"http_code": @"408"};
                nvPushCheckBlock(^(BOOL isRepeat) {
                    if (isRepeat) {
                        if (nvCheckPushClickTimeCounter < watingTime) {
                            return [self nvPushClickCheckInSeconds: seconds withBlock: nvPushCheckBlock];
                        }
                    }
                });
            }
        } else {
            nvPushCheckBlock(^(BOOL isRepeat) {
                if (isRepeat) {
                    if (nvCheckPushClickTimeCounter < watingTime) {
                        return [self nvPushClickCheckInSeconds: seconds withBlock: nvPushCheckBlock];
                    }
                }
            });
        }
    });
}

-(void)sendLinkInfo:(UIApplication *)app openURL:(NSURL *)url{
    @try{
        NSString *nvUrl = url.absoluteString;
        NSMutableDictionary * nvlinkInfo = [[NSMutableDictionary alloc] init] ;
        nvlinkInfo = [notifyvisitors OpenUrlGetDataWithApplication:app Url:url];
        [nvlinkInfo setValue:nvUrl forKey:@"url"];
        [nvlinkInfo setValue:@"nv" forKey:@"source"];
        [[NSNotificationCenter defaultCenter] postNotificationName: @"nvDeepLinkData"  object: 0 userInfo:nvlinkInfo];
        //[[notifyvisitors_iOS sharedInstance] sendLinkInfo:nvlinkInfo];
    }@catch (NSException *exception) {
        NSLog(@"exception in sendLinkInfo");
    }
}

-(void)nvTurnOffAutomaticScreenViewEventForIonicCordovaPlugin {
     @try{
    NSUserDefaults *nvIonicCdvCustomUserDefaults = [[NSUserDefaults alloc] initWithSuiteName: @"com.cp.plugin.notifyvisitors"];
    [nvIonicCdvCustomUserDefaults setBool: YES forKey: @"nv_isSDKRunningInCP"];
    [nvIonicCdvCustomUserDefaults synchronize];
    }@catch (NSException *exception) {
        NSLog(@"NotifyVisitors-Cordova: exception in turn off automatic screen_view event in cordova plugin");
    }
}

@end
