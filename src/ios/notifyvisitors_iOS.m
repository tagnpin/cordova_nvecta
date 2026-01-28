/********* notifyvisitors_iOS.m Cordova Plugin Implementation *******/
#import "notifyvisitors_iOS.h"
#import <Cordova/CDVAvailability.h>

BOOL nvPushObserverReady;

CDVInvokedUrlCommand *chatBotCallback;
CDVInvokedUrlCommand *showCallback = NULL;
CDVInvokedUrlCommand *eventCallback = NULL;
CDVInvokedUrlCommand *commonCallback = NULL;
CDVInvokedUrlCommand *knownUserIdentifiedCallback = NULL;
CDVInvokedUrlCommand *notificationCenterCallback = NULL;
CDVInvokedUrlCommand *pushRegisteredCallback = NULL;

@implementation notifyvisitors_iOS

- (void)pluginInitialize {
    [self setNvDeepLinkObserver];
    _handlers = [[NSMutableArray alloc] init];
    [notifyvisitors sharedInstance].delegate = self;
}

- (void)onAppTerminate {
    _handlers = nil;
    [super onAppTerminate];
}

+ (instancetype)sharedInstance {
    static notifyvisitors_iOS *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[notifyvisitors_iOS alloc] init];
    });
    return sharedInstance;
}

#pragma mark - Show inAppMessage (Banner/Survey methods)

- (void)showInAppMessage:(CDVInvokedUrlCommand*_Nonnull)command {
    NSLog(@"NotifyVisitors-Ionic SHOW-INAPP-MESSAGE !!");
    [self show: command];
}

- (void)show:(CDVInvokedUrlCommand*)command {
    NSLog(@"NotifyVisitors-Ionic SHOW !!");
    
    NSMutableDictionary *nvUserToken = [[NSMutableDictionary alloc] init];
    NSMutableDictionary *nvCustomRule = [[NSMutableDictionary alloc] init];
    
    //NSLog(@"show command = %@", command.arguments);
    if ([command.arguments count] > 0) {
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        
        if ([nvArgument count] > 0) {
            if (nvArgument[@"tokens"] && ![nvArgument[@"tokens"] isEqual: [NSNull null]]) {
                nvUserToken = nvArgument[@"tokens"];
            }
            if (nvArgument[@"customObjects"] && ![nvArgument[@"customObjects"] isEqual: [NSNull null]]) {
                nvCustomRule = nvArgument[@"customObjects"];
            }
        }
    }
    
    @try {
        NSLog(@"NotifyVisitors-Ionic Tokens = %@", nvUserToken);
    }
    @catch (NSException * e) {
       NSLog(@"Exception: %@", e);
    }
    
    @try {
        NSLog(@"NotifyVisitors-Ionic CustomRules = %@", nvCustomRule);
    }
    @catch (NSException * e) {
       NSLog(@"Exception: %@", e);
    }
    
    showCallback = command;
    if ([nvUserToken count] == 0 && [nvCustomRule count] == 0) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [notifyvisitors Show: nil CustomRule: nil];
        });
    } else if ([nvUserToken count] > 0 && [nvCustomRule count] == 0) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [notifyvisitors Show: nvUserToken CustomRule: nil];
        });
    } else if ([nvUserToken count] == 0 && [nvCustomRule count] > 0) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [notifyvisitors Show: nil CustomRule: nvCustomRule];
        });
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [notifyvisitors Show: nvUserToken CustomRule: nvCustomRule];
        });
    }
}

-(void)stopNotification:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic STOP NOTIFICATIONS !!");
    @try{
        [notifyvisitors DismissAllNotifyvisitorsInAppNotifications];
    }@catch(NSException *exception){
        NSLog(@"exception in stopNotification");
    }
}

-(void)scrollViewDidScroll_iOS_only:(CDVInvokedUrlCommand *)command {
    NSLog(@"SCROLL VIEW DID SCROLL !!");
    UIScrollView *nvScrollview;
    if ([command.arguments count] > 0) {
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        if ([nvArgument count] > 0) {
            if (nvArgument[@"scrollView"] && ![nvArgument[@"scrollView"] isEqual: [NSNull null]]) {
                nvScrollview = (UIScrollView *)nvArgument[@"scrollView"];
            }
        }
    }
    [notifyvisitors scrollViewDidScroll: nvScrollview];
}

-(void)getEventSurveyInfo:(CDVInvokedUrlCommand *)command {
    NSLog(@"GET EVENT SURVEY INFO !!");
    commonCallback = command;
}

#pragma mark - Open Notification Center old and new method with Close button Callback

-(void)openNotificationCenter:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic OPEN NOTIFICATION CENTER !!");
    notificationCenterCallback = command;
    [self showNotifications: command];
//    if(notificationCenterCallback != NULL) {
//        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
//        [pluginResult setKeepCallbackAsBool: YES];
//        [self.commandDelegate sendPluginResult:  pluginResult callbackId: notificationCenterCallback.callbackId];
//    }
    
}

-(void)showNotifications:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic NOTIFICATION CENTER !!");
    NSString * tab1Label;
    NSString * tab1Name;
    NSString * tab2Label;
    NSString * tab2Name;
    NSString * tab3Label;
    NSString * tab3Name;
    
    NSString * selectedTabTextColor;
    NSString * unselectedTabTextColor;
    NSString * selectedTabBgColor;
    NSString * unselectedTabBgColor;
    
    NSString * tabTextFontName;
    NSInteger tabTextFontSize;
    NSInteger selectedTabIndex;
    
    UIColor * mSelectedTabTextColor;
    UIColor * mUnselectedTabTextColor;
    UIColor * mSelectedTabBgColor;
    UIColor * mUnselectedTabBgColor;
    
    NSMutableDictionary * appInboxInfo;
    
    @try{
        tab1Label = nil;
        tab1Name = nil;
        tab2Label = nil;
        tab2Name = nil;
        tab3Label = nil;
        tab3Name = nil;
        selectedTabTextColor = nil;
        unselectedTabTextColor = nil;
        selectedTabBgColor = nil;
        unselectedTabBgColor = nil;
        tabTextFontName = nil;
        tabTextFontSize = 13;
        selectedTabIndex = 0;
        mSelectedTabTextColor = nil;
        mUnselectedTabTextColor = nil;
        mSelectedTabBgColor = nil;
        mUnselectedTabBgColor = nil;
        
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        
        if ([nvArgument count] > 0){
            appInboxInfo = [[NSMutableDictionary alloc] init];
            
            if(nvArgument[@"appInboxInfo"] && ![nvArgument[@"appInboxInfo"] isEqual: [NSNull null]]){
                appInboxInfo = nvArgument[@"appInboxInfo"];
                if([appInboxInfo count] > 0){
                    if (appInboxInfo[@"label_one"] && ![appInboxInfo[@"label_one"] isEqual: [NSNull null]]) {
                        tab1Label = appInboxInfo[@"label_one"];
                    }
                    
                    if (appInboxInfo[@"name_one"] && ![appInboxInfo[@"name_one"] isEqual: [NSNull null]]) {
                        tab1Name = appInboxInfo[@"name_one"];
                    }
                    
                    if (appInboxInfo[@"label_two"] && ![appInboxInfo[@"label_two"] isEqual: [NSNull null]]) {
                        tab2Label = appInboxInfo[@"label_two"];
                    }
                    
                    if (appInboxInfo[@"name_two"] && ![appInboxInfo[@"name_two"] isEqual: [NSNull null]]) {
                        tab2Name = appInboxInfo[@"name_two"];
                    }
                    
                    if (appInboxInfo[@"label_three"] && ![appInboxInfo[@"label_three"] isEqual: [NSNull null]]) {
                        tab3Label = appInboxInfo[@"label_three"];
                    }
                    
                    if (appInboxInfo[@"name_three"] && ![appInboxInfo[@"name_three"] isEqual: [NSNull null]]) {
                        tab3Name = appInboxInfo[@"name_three"];
                    }
                    
                    if (appInboxInfo[@"selectedTabTextColor"] && ![appInboxInfo[@"selectedTabTextColor"] isEqual: [NSNull null]]) {
                        selectedTabTextColor =  appInboxInfo[@"selectedTabTextColor"];
                        mSelectedTabTextColor = [self GetColor:selectedTabTextColor];
                        
                    }
                    
                    if (appInboxInfo[@"unselectedTabTextColor"] && ![appInboxInfo[@"unselectedTabTextColor"] isEqual: [NSNull null]]) {
                        unselectedTabTextColor = appInboxInfo[@"unselectedTabTextColor"];
                        mUnselectedTabTextColor =  [self GetColor:unselectedTabTextColor];
                    }
                    
                    if (appInboxInfo[@"selectedTabBgColor"] && ![appInboxInfo[@"selectedTabBgColor"] isEqual: [NSNull null]]) {
                        selectedTabBgColor = appInboxInfo[@"selectedTabBgColor"];
                        mSelectedTabBgColor = [self GetColor:selectedTabBgColor];
                    }
                    
                    if (appInboxInfo[@"unselectedTabBgColor_ios"] && ![appInboxInfo[@"unselectedTabBgColor_ios"] isEqual: [NSNull null]]) {
                        unselectedTabBgColor = appInboxInfo[@"unselectedTabBgColor_ios"];
                        mUnselectedTabBgColor = [self GetColor:unselectedTabBgColor];
                    }
                    
                    if (appInboxInfo[@"selectedTabIndex_ios"] && ![appInboxInfo[@"selectedTabIndex_ios"] isEqual: [NSNull null]]) {
                        selectedTabIndex = [appInboxInfo[@"selectedTabIndex_ios"]integerValue];
                    }
                    
                    if (appInboxInfo[@"tabTextFontName_ios"] && ![appInboxInfo[@"tabTextFontName_ios"] isEqual: [NSNull null]]) {
                        tabTextFontName = appInboxInfo[@"tabTextFontName_ios"];
                    }
                    
                    if (appInboxInfo[@"tabTextFontSize_ios"] && ![appInboxInfo[@"tabTextFontSize_ios"] isEqual: [NSNull null]]) {
                        tabTextFontSize = [appInboxInfo[@"tabTextFontSize_ios"]integerValue];
                    }
                    
                    NVCenterStyleConfig *nvConfig = [[NVCenterStyleConfig alloc] init];
                    
                    if (tab1Label != nil && [tab1Label length] > 0 && ![tab1Label isEqual: [NSNull null]]){
                        if(tab1Name!= nil && [tab1Name length] > 0 && ![tab1Name isEqual: [NSNull null]]){
                            [nvConfig setFirstTabWithTabLable: tab1Label TagDisplayName: tab1Name];
                        }
                    }
                    
                    if (tab2Label != nil && [tab2Label length] > 0 && ![tab2Label isEqual: [NSNull null]]){
                        if(tab2Name!= nil && [tab2Name length] > 0 && ![tab2Name isEqual: [NSNull null]]){
                            [nvConfig setSecondTabWithTabLable: tab2Label TagDisplayName: tab2Name];
                        }
                    }
                    
                    if (tab3Label != nil && [tab3Label length] > 0 && ![tab3Label isEqual: [NSNull null]]){
                        if(tab3Name!= nil && [tab3Name length] > 0 && ![tab3Name isEqual: [NSNull null]]){
                            [nvConfig setThirdTabWithTabLable: tab3Label TagDisplayName: tab3Name];
                        }
                    }
                    
                    if (mSelectedTabTextColor != nil && ![mSelectedTabTextColor isEqual: [NSNull null]]){
                        nvConfig.selectedTabTextColor = mSelectedTabTextColor;
                    }
                    
                    if (mUnselectedTabTextColor != nil && ![mUnselectedTabTextColor isEqual: [NSNull null]]){
                        nvConfig.unselectedTabTextColor = mUnselectedTabTextColor;
                    }
                    
                    if (mSelectedTabBgColor != nil && ![mSelectedTabBgColor isEqual: [NSNull null]]){
                        nvConfig.selectedTabBgColor = mSelectedTabBgColor;
                    }
                    
                    if (mUnselectedTabBgColor != nil && ![mUnselectedTabBgColor isEqual: [NSNull null]]){
                        nvConfig.unselectedTabBgColor = mUnselectedTabBgColor;
                    }
                    
                    if(selectedTabIndex != 0){
                        nvConfig.selectedTabIndex = selectedTabIndex;
                    }
                    
                    if (tabTextFontName != nil && [tabTextFontName length] > 0 && ![tabTextFontName isEqual: [NSNull null]]){
                        nvConfig.tabTextfont = [UIFont fontWithName: tabTextFontName size: tabTextFontSize];
                    }
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [notifyvisitors notificationCenterWithConfiguration: nvConfig];
                    });
                } else{
                    NSLog(@"Empty JSON Object ! Going for Standard App Inbox ");
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [notifyvisitors notificationCenter];
                    });
                }
            } else{
                NSLog(@"AppInboxInfo is null ! Going for Standard App Inbox");
                dispatch_async(dispatch_get_main_queue(), ^{
                    [notifyvisitors notificationCenter];
                });
            }
            
        } else{
            NSLog(@"arguments can't be null");
        }
        
    }@catch(NSException *exception){
        NSLog(@"exception in showNotifications");
    }
}

- (void)getNotificationCenterData:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic GET NOTIFICATION CENTER DATA IN CALLBACK !!");
    [notifyvisitors getNotificationCenterData:^(NSDictionary * nvNotificationCenterData) {
        CDVPluginResult* nvPluginResult = nil;
                NSError *nvError = nil;
                NSData *nvJsonData = nil;
                NSString *nvJsonString = nil;
        
                if([nvNotificationCenterData count] > 0) {
                    nvJsonData = [NSJSONSerialization dataWithJSONObject: nvNotificationCenterData options:NSJSONWritingPrettyPrinted error: &nvError];
                } else {
                    NSDictionary *nvErrorDataResponse = @{@"message" : @"no notification(s)", @"notifications": @[]};
                    nvJsonData = [NSJSONSerialization dataWithJSONObject: nvErrorDataResponse options:NSJSONWritingPrettyPrinted error: &nvError];
                }
                nvJsonString = [[NSString alloc] initWithData: nvJsonData encoding: NSUTF8StringEncoding];
        nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
              [self.commandDelegate sendPluginResult:  nvPluginResult callbackId: command.callbackId];
            }];
}

-(void)notificationCount:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic NOTIFICATION COUNT !!");
    [notifyvisitors GetUnreadPushNotification:^(NSInteger nvUnreadPushCount) {
        CDVPluginResult* nvPluginResult = nil;
        NSString *jCount = nil;
        jCount = [@(nvUnreadPushCount) stringValue];
        nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString:jCount];
        [self.commandDelegate sendPluginResult: nvPluginResult callbackId: command.callbackId];
    }];
}

-(void)getNotificationCenterCount:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic NOTIFICATION CENTER TAB COUNT !!");
    NSString * tab1Label;
    NSString * tab1Name;
    NSString * tab2Label;
    NSString * tab2Name;
    NSString * tab3Label;
    NSString * tab3Name;
    
    NSMutableDictionary * appInboxInfo;
    
    @try{
        tab1Label = nil;
        tab1Name = nil;
        tab2Label = nil;
        tab2Name = nil;
        tab3Label = nil;
        tab3Name = nil;
       
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        
        if ([nvArgument count] > 0){
            appInboxInfo = [[NSMutableDictionary alloc] init];
            
            if(nvArgument[@"tabCountInfo"] && ![nvArgument[@"tabCountInfo"] isEqual: [NSNull null]]){
                appInboxInfo = nvArgument[@"tabCountInfo"];
                if([appInboxInfo count] > 0){
                    if (appInboxInfo[@"label_one"] && ![appInboxInfo[@"label_one"] isEqual: [NSNull null]]) {
                        tab1Label = appInboxInfo[@"label_one"];
                    }
                    
                    if (appInboxInfo[@"name_one"] && ![appInboxInfo[@"name_one"] isEqual: [NSNull null]]) {
                        tab1Name = appInboxInfo[@"name_one"];
                    }
                    
                    if (appInboxInfo[@"label_two"] && ![appInboxInfo[@"label_two"] isEqual: [NSNull null]]) {
                        tab2Label = appInboxInfo[@"label_two"];
                    }
                    
                    if (appInboxInfo[@"name_two"] && ![appInboxInfo[@"name_two"] isEqual: [NSNull null]]) {
                        tab2Name = appInboxInfo[@"name_two"];
                    }
                    
                    if (appInboxInfo[@"label_three"] && ![appInboxInfo[@"label_three"] isEqual: [NSNull null]]) {
                        tab3Label = appInboxInfo[@"label_three"];
                    }
                    
                    if (appInboxInfo[@"name_three"] && ![appInboxInfo[@"name_three"] isEqual: [NSNull null]]) {
                        tab3Name = appInboxInfo[@"name_three"];
                    }
                    
                    NVCenterStyleConfig *nvConfig = [[NVCenterStyleConfig alloc] init];
                    
                    if (tab1Label != nil && [tab1Label length] > 0 && ![tab1Label isEqual: [NSNull null]]){
                        if(tab1Name!= nil && [tab1Name length] > 0 && ![tab1Name isEqual: [NSNull null]]){
                            [nvConfig setFirstTabWithTabLable: tab1Label TagDisplayName: tab1Name];
                        }
                    }
                    
                    if (tab2Label != nil && [tab2Label length] > 0 && ![tab2Label isEqual: [NSNull null]]){
                        if(tab2Name!= nil && [tab2Name length] > 0 && ![tab2Name isEqual: [NSNull null]]){
                            [nvConfig setSecondTabWithTabLable: tab2Label TagDisplayName: tab2Name];
                        }
                    }
                    
                    if (tab3Label != nil && [tab3Label length] > 0 && ![tab3Label isEqual: [NSNull null]]){
                        if(tab3Name!= nil && [tab3Name length] > 0 && ![tab3Name isEqual: [NSNull null]]){
                            [nvConfig setThirdTabWithTabLable: tab3Label TagDisplayName: tab3Name];
                        }
                    }
                    
                    [notifyvisitors getNotificationCenterCountWithConfiguration: nvConfig countResult:^(NSDictionary * nvCenterCounts) {
                        [self sendTabCountResponse:nvCenterCounts responseToSend:command];
                    }];

                } else{
                    NSLog(@"Empty JSON Object ! Going for Standard Tab count ");
                    [notifyvisitors getNotificationCenterCountWithConfiguration: Nil countResult:^(NSDictionary * nvCenterCounts) {
                        [self sendTabCountResponse:nvCenterCounts responseToSend:command];
                    }];
                }
                
            } else{
                NSLog(@"TabCountInfo is null ! Going for Standard Tab Count");
                [notifyvisitors getNotificationCenterCountWithConfiguration: Nil countResult:^(NSDictionary * nvCenterCounts) {
                    NSLog(@"nvCenterCounts = %@", nvCenterCounts);
                    [self sendTabCountResponse:nvCenterCounts responseToSend:command];
                }];
            }
            
        } else{
            NSLog(@"arguments can't be null");
        }
        
    }@catch(NSException *exception){
        NSLog(@"exception in notification center tab count !!");
    }
    
}

-(void)ncDataCallback:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic NOTIFICATION CENTER DATA CALLBACK !!");
    [notifyvisitors getNotificationCenterData:^(NSDictionary * nvNotificationCenterData) {
        CDVPluginResult* nvPluginResult = nil;
                NSError *nvError = nil;
                NSData *nvJsonData = nil;
                NSString *nvJsonString = nil;
        
                if([nvNotificationCenterData count] > 0) {
                    nvJsonData = [NSJSONSerialization dataWithJSONObject: nvNotificationCenterData options:NSJSONWritingPrettyPrinted error: &nvError];
                } else {
                    NSDictionary *nvErrorDataResponse = @{@"message" : @"no notification(s)", @"notifications": @[]};
                    nvJsonData = [NSJSONSerialization dataWithJSONObject: nvErrorDataResponse options:NSJSONWritingPrettyPrinted error: &nvError];
                }
                nvJsonString = [[NSString alloc] initWithData: nvJsonData encoding: NSUTF8StringEncoding];
        nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
              [self.commandDelegate sendPluginResult:  nvPluginResult callbackId: command.callbackId];
            }];
}

- (void) sendTabCountResponse : (NSDictionary *) nvCenterCounts responseToSend:(CDVInvokedUrlCommand *_Nonnull)command {
    NSLog(@"nvCenterCounts = %@", nvCenterCounts);
    CDVPluginResult* nvPluginResult = nil;
    NSError *nvError = nil;
    NSData *nvJsonData = nil;
    NSString *nvJsonString = nil;
    nvJsonData = [NSJSONSerialization dataWithJSONObject: nvCenterCounts options:NSJSONWritingPrettyPrinted error: &nvError];
    nvJsonString = [[NSString alloc] initWithData: nvJsonData encoding: NSUTF8StringEncoding];
    nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
    [self.commandDelegate sendPluginResult: nvPluginResult callbackId: command.callbackId];
}

-(void)notificationClickCallback:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"NotifyVisitors-Ionic NOTIFICATION CLICK CALLBACK !!");
        [self _addHandlers:command];
        nvPushObserverReady = YES;
        [[NSNotificationCenter defaultCenter] addObserverForName: @"nvNotificationClickCallback" object: nil queue: nil usingBlock:^(NSNotification *notification) {
            CDVPluginResult* pluginResult = nil;
            NSDictionary *nvUserInfo = [notification userInfo];
            if ([nvUserInfo count] > 0) {
                NSError * err;
                NSData * jsonData = [NSJSONSerialization dataWithJSONObject:nvUserInfo options:0 error:&err];
                NSString * myString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
                pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: myString];
                [pluginResult setKeepCallbackAsBool: YES];
                [self.commandDelegate sendPluginResult:  pluginResult callbackId: command.callbackId];
            }else{
                [pluginResult setKeepCallbackAsBool: YES];
            }
        }];
    }
    @catch (NSException *exception) {
        NSLog(@"exception in notificationClickCallback()");
    }
}

#pragma mark - Track Events method

-(void)event:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic EVENTS !!");
    NSString *nvEvent = nil;
    NSMutableDictionary *nvAttributes = [[NSMutableDictionary alloc] init];
    NSString *nvLtv = nil;
    NSString *nvScopeStr = nil;
    int nvScope = 0;
    
    if ([command.arguments count] > 0) {
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        if ([nvArgument count] > 0) {
            NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
            if ([nvArgument count] > 0) {
                if (nvArgument[@"eventName"] && ![nvArgument[@"eventName"] isEqual: [NSNull null]]) {
                    nvEvent = nvArgument[@"eventName"];
                    if ([nvEvent length] > 0 && ![nvEvent isEqualToString: @""] && ![nvEvent isEqual: [NSNull null]]) {
                        nvEvent = nvArgument[@"eventName"];
                    } else {
                        nvEvent = nil;
                    }
                } else {
                    nvEvent = nil;
                }
                if (nvArgument[@"attributes"] && ![nvArgument[@"attributes"] isEqual: [NSNull null]]) {
                    nvAttributes = nvArgument[@"attributes"];
                }
                
                if (nvArgument[@"ltv"] && ![nvArgument[@"ltv"] isEqual: [NSNull null]]) {
                    nvLtv = nvArgument[@"ltv"];
                    if ([nvLtv length] > 0 && ![nvLtv isEqualToString: @""] && ![nvLtv isEqual: [NSNull null]]) {
                        nvLtv = nvArgument[@"ltv"];
                    } else {
                        nvLtv = nil;
                    }
                } else {
                    nvLtv = nil;
                }
                if (nvArgument[@"scope"] && ![nvArgument[@"scope"] isEqual: [NSNull null]]) {
                    nvScopeStr = nvArgument[@"scope"];
                    if ([nvScopeStr length] > 0 && ![nvScopeStr isEqualToString: @""] && ![nvScopeStr isEqual: [NSNull null]]) {
                        nvScopeStr = nvArgument[@"scope"];
                    } else {
                        nvScopeStr = nil;
                    }
                } else {
                    nvScopeStr = nil;
                }
                
                if ([nvScopeStr length] > 0 && ![nvScopeStr isEqualToString: @""] && ![nvScopeStr isEqual: [NSNull null]]) {
                    nvScope = [nvScopeStr intValue];
                }
                
            }
        }
    }
    
    
    @try {
        NSLog(@"NotifyVisitors-Ionic Event Name = %@", nvEvent);
    }
    @catch (NSException * e) {
       NSLog(@"Exception: %@", e);
    }
  
    @try {
        NSLog(@"NotifyVisitors-Ionic Attributes = %@", nvAttributes);
    }
    @catch (NSException * e) {
       NSLog(@"Exception: %@", e);
    }
    
    eventCallback = command;
    if ([nvAttributes count] > 0) {
        [notifyvisitors trackEvents: nvEvent Attributes: nvAttributes lifetimeValue: nvLtv Scope: nvScope];
    } else {
        [notifyvisitors trackEvents: nvEvent Attributes: nil lifetimeValue: nvLtv Scope: nvScope];
    }
}

- (void)trackScreen:(CDVInvokedUrlCommand*_Nonnull)command {
  NSLog(@"NotifyVisitors-Ionic TRACK-SCREEN !!");
  @try {
    NSString *nvScreenName = @"";
    if ([command.arguments count] > 0) {
      NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
      if ([nvArgument count] > 0) {
        if ([nvArgument count] > 0) {
          NSString *nvTempScreenNameStr = @"";
          if (nvArgument[@"screenName"]) {
            nvTempScreenNameStr = [NSString stringWithFormat: @"%@", nvArgument[@"screenName"]];
          }
          if ([nvTempScreenNameStr length] > 0 && ![nvTempScreenNameStr isEqualToString: @""] && ![nvTempScreenNameStr isEqual: [NSNull null]]) {
            nvScreenName = nvTempScreenNameStr;
          }
        }
      }
    }
    if ([nvScreenName length] > 0 && ![nvScreenName isEqualToString: @""] && ![nvScreenName isEqual: [NSNull null]]) {
      [notifyvisitors trackScreen: nvScreenName];
    } else {
      NSLog(@"NotifyVisitors-Ionic ERROR : Invalid or empty screen name found in trackScreen() method");
    }
    
  }  @catch (NSException *exception) {
    NSLog(@"exception in trackScreen()");
  }
}

#pragma mark - Push Notifications related methods

-(void)scheduleNotification:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic SCHEDULE NOTIFICATION !!");
    
    NSString *nvNid = nil;
    NSString *nvTag = nil;
    NSString *nvMessage = nil;
    NSString *nvUrl = nil;
    NSString *nvTitle = nil;
    NSString *nvIcon = nil;
    NSString *nvTime = nil;

    if ([command.arguments count] > 0) {
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        if ([nvArgument count] > 0) {
            if (nvArgument[@"nid"] && ![nvArgument[@"nid"] isEqual: [NSNull null]]) {
                nvNid = nvArgument[@"nid"];
                if ([nvNid length] > 0 && ![nvNid isEqualToString: @""] && ![nvNid isEqual: [NSNull null]]) {
                    nvNid = nvArgument[@"nid"];
                } else {
                    nvNid = nil;
                }
            } else {
                nvNid = nil;
            }
            
            if (nvArgument[@"icon"] && ![nvArgument[@"icon"] isEqual: [NSNull null]]) {
                nvIcon = nvArgument[@"icon"];
                if ([nvIcon length] > 0 && ![nvIcon isEqualToString: @""] && ![nvIcon isEqual: [NSNull null]]) {
                    nvIcon = nvArgument[@"icon"];
                } else {
                    nvIcon = nil;
                }
            } else {
                nvIcon = nil;
            }
            
            if (nvArgument[@"message"] && ![nvArgument[@"message"] isEqual: [NSNull null]]) {
                nvMessage = nvArgument[@"message"];
                if ([nvMessage length] > 0 && ![nvMessage isEqualToString: @""] && ![nvMessage isEqual: [NSNull null]]) {
                    nvMessage = nvArgument[@"message"];
                } else {
                    nvMessage = nil;
                }
            } else {
                nvMessage = nil;
            }
            
            if (nvArgument[@"tag"] && ![nvArgument[@"tag"] isEqual: [NSNull null]]) {
                nvTag = nvArgument[@"tag"];
                if ([nvTag length] > 0 && ![nvTag isEqualToString: @""] && ![nvTag isEqual: [NSNull null]]) {
                    nvTag = nvArgument[@"tag"];
                } else {
                    nvTag = nil;
                }
            } else {
                nvTag = nil;
            }
            
            if (nvArgument[@"time"] && ![nvArgument[@"time"] isEqual: [NSNull null]]) {
                nvTime = nvArgument[@"time"];
                if ([nvTime length] > 0 && ![nvTime isEqualToString: @""] && ![nvTime isEqual: [NSNull null]]) {
                    nvTime = nvArgument[@"time"];
                } else {
                    nvTime = nil;
                }
            } else {
                nvTime = nil;
            }
            
            if (nvArgument[@"title"] && ![nvArgument[@"title"] isEqual: [NSNull null]]) {
                nvTitle = nvArgument[@"title"];
                if ([nvTitle length] > 0 && ![nvTitle isEqualToString: @""] && ![nvTitle isEqual: [NSNull null]]) {
                    nvTitle = nvArgument[@"title"];
                } else {
                    nvTitle = nil;
                }
            } else {
                nvTitle = nil;
            }
            
            if (nvArgument[@"url"] && ![nvArgument[@"url"] isEqual: [NSNull null]]) {
                nvUrl = nvArgument[@"url"];
                if ([nvUrl length] > 0 && ![nvUrl isEqualToString: @""] && ![nvUrl isEqual: [NSNull null]]) {
                    nvUrl = nvArgument[@"url"];
                } else {
                    nvUrl = nil;
                }
            } else {
                nvUrl = nil;
            }
        }
    }
    [notifyvisitors schedulePushNotificationwithNotificationID: nvNid Tag: nvTag TimeinSecond: nvTime Title: nvTitle Message: nvMessage URL: nvUrl Icon: nvIcon];
}

-(void)subscribePushCategory:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"NotifyVisitors-Ionic SUBSCRIBE PUSH CATEGORY !!");
        NSArray * categoryArray;
        Boolean subscribeSignal;
        if ([command.arguments count] > 0) {
            NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
            if ([nvArgument count] > 0) {
                if (nvArgument[@"categoryArray"] && ![nvArgument[@"categoryArray"] isEqual: [NSNull null]]) {
                    categoryArray = nvArgument[@"categoryArray"];
                }
                
                if (nvArgument[@"subscribeSignal"] && ![nvArgument[@"subscribeSignal"] isEqual: [NSNull null]]) {
                    subscribeSignal = [nvArgument[@"subscribeSignal"] boolValue];
                }
                
                if ([categoryArray count] > 0) {
                    [notifyvisitors pushPreferences: categoryArray isUnsubscribeFromAll: subscribeSignal ? YES : NO];
                } else{
                    [notifyvisitors pushPreferences: nil isUnsubscribeFromAll: subscribeSignal ? YES : NO];
                }
                
            }
        }
        
    }
    @catch (NSException *exception) {
        NSLog(@"exception in SUBSCRIBE PUSH CATEGORY");
    }

}

-(void)stopGeofencePushforDateTime:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic STOP GEOFENCE PUSH NOTIFICATION !!");
    NSString *nvDateTime = nil;
    NSString *nvAddHrsStr = nil;
    NSInteger nvAdditionalHours = 0;
    if ([command.arguments count] > 0) {
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        if ([nvArgument count] > 0) {
            if (nvArgument[@"stopforDateTime"] && ![nvArgument[@"stopforDateTime"] isEqual: [NSNull null]]) {
                nvDateTime = nvArgument[@"stopforDateTime"];
                if ([nvDateTime length] > 0 && ![nvDateTime isEqualToString: @""] && ![nvDateTime isEqual: [NSNull null]]) {
                    nvDateTime = nvArgument[@"stopforDateTime"];
                } else {
                    nvDateTime = nil;
                }
            } else {
                nvDateTime = nil;
            }
            if (nvArgument[@"additionalHrs"] && ![nvArgument[@"additionalHrs"] isEqual: [NSNull null]]) {
                nvAddHrsStr = nvArgument[@"additionalHrs"];
                if ([nvAddHrsStr length] > 0 && ![nvAddHrsStr isEqualToString: @""] && ![nvAddHrsStr isEqual: [NSNull null]]) {
                    nvAddHrsStr = nvArgument[@"additionalHrs"];
                } else {
                    nvAddHrsStr = nil;
                }
            } else {
                nvAddHrsStr = nil;
            }
            
            if ([nvAddHrsStr length] > 0 && ![nvAddHrsStr isEqualToString: @""] && ![nvAddHrsStr isEqual: [NSNull null]]) {
                nvAdditionalHours = [nvAddHrsStr integerValue];
            }
        }
    }
//   [notifyvisitors stopGeofencePushforDateTime: nvDateTime additionalHours: nvAdditionalHours];
}

-(void)getRegistrationToken:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic GET REGISTRATION TOKEN !!");
    NSString *nvPushToken = [notifyvisitors getPushRegistrationToken];
    CDVPluginResult* nvPluginResult = nil;
    if([nvPushToken length] > 0){
        nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString:nvPushToken];
    } else {
        nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString:@"unavailable"];
    }
    [self.commandDelegate sendPluginResult: nvPluginResult callbackId: command.callbackId];
}

#pragma mark - Track User Methods

-(void)setUserIdentifier:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic SET-USER-IDENTIFIER !!");
    
    NSDictionary *nvFinalUserParams = nil;
    if ([command.arguments count] > 0) {
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        if ([nvArgument count] > 0) {
            if (nvArgument[@"jsonObject"] && ![nvArgument[@"jsonObject"] isEqual: [NSNull null]]) {
                nvFinalUserParams =  nvArgument[@"jsonObject"];
            }
        }
    }
    
    @try {
        NSLog(@"NotifyVisitors-Ionic User Params = %@", nvFinalUserParams);
    }
    @catch (NSException * e) {
        NSLog(@"Exception: %@", e);
    }
    if ([nvFinalUserParams count] > 0) {
        [notifyvisitors userIdentifierWithUserParams: nvFinalUserParams onUserTrackListener:^(NSDictionary * userTrackingResponseDict) {
            CDVPluginResult* nvPluginResult = nil;
            NSError *nvError = nil;
            NSData *nvUserTrackingResJsonData = [NSJSONSerialization dataWithJSONObject: userTrackingResponseDict options: NSJSONWritingPrettyPrinted error: &nvError];
            NSString *nvUserTrackingResJsonStr = [[NSString alloc] initWithData: nvUserTrackingResJsonData encoding: NSUTF8StringEncoding];
                nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvUserTrackingResJsonStr];
            [self.commandDelegate sendPluginResult:  nvPluginResult callbackId: command.callbackId];
        }];
    } else {
        [notifyvisitors userIdentifierWithUserParams: nil onUserTrackListener:^(NSDictionary * userTrackingResponseDict) {
            CDVPluginResult* nvPluginResult = nil;
            NSError *nvError = nil;
            NSData *nvUserTrackingResJsonData = [NSJSONSerialization dataWithJSONObject: userTrackingResponseDict options: NSJSONWritingPrettyPrinted error: &nvError];
            NSString *nvUserTrackingResJsonStr = [[NSString alloc] initWithData: nvUserTrackingResJsonData encoding: NSUTF8StringEncoding];
                nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvUserTrackingResJsonStr];
            [self.commandDelegate sendPluginResult:  nvPluginResult callbackId: command.callbackId];
        }];
    }
}

-(void)userIdentifier:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic USER IDENTIFIER !!");

    NSString *nvUserID = nil;
    NSMutableDictionary *nvUserParams = [[NSMutableDictionary alloc] init];
    if ([command.arguments count] > 0) {
        NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
        if ([nvArgument count] > 0) {
            if (nvArgument[@"jsonObject"] && ![nvArgument[@"jsonObject"] isEqual: [NSNull null]]) {
                nvUserParams = nvArgument[@"jsonObject"];
            }
            if (nvArgument[@"userID"] && ![nvArgument[@"userID"] isEqual: [NSNull null]]) {
                nvUserID = nvArgument[@"userID"];
                if ([nvUserID length] > 0 && ![nvUserID isEqualToString: @""] && ![nvUserID isEqual: [NSNull null]]) {
                    nvUserID = nvArgument[@"userID"];
                } else {
                    nvUserID = nil;
                }
                
            } else {
                nvUserID = nil;
            }
        }
    }
    
    @try {
        NSLog(@"NotifyVisitors-Ionic User-Id = %@", nvUserID);
    }
    @catch (NSException * e) {
       NSLog(@"Exception: %@", e);
    }
    
    @try {
        NSLog(@"NotifyVisitors-Ionic User Params = %@", nvUserParams);
    }
    @catch (NSException * e) {
       NSLog(@"Exception: %@", e);
    }
    
    if ([nvUserParams count] > 0) {
        [notifyvisitors UserIdentifier: nvUserID UserParams: nvUserParams];
    } else {
        [notifyvisitors UserIdentifier: nvUserID UserParams: nil];
    }
}

-(void)getNvUID:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"NotifyVisitors-Ionic GET NV UID !!");
        
        NSString * nvUIDStr = [notifyvisitors getNvUid];
        CDVPluginResult* nvPluginResult = nil;
        if ([nvUIDStr length] > 0 && ![nvUIDStr isEqualToString: @""] && ![nvUIDStr isEqual: [NSNull null]] && ![nvUIDStr isEqualToString: @"(null)"]) {
            nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvUIDStr];
            } else {
                nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nil];
            }
        [self.commandDelegate sendPluginResult: nvPluginResult callbackId: command.callbackId];
    }
    @catch (NSException *exception) {
        NSLog(@"exception in getNvUID");
    }
    
}

-(void)knownUserIdentified:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic GET KNOWN USER IDENTIFIED INFO !!");
    @try{
        knownUserIdentifiedCallback = command;
    }
    @catch(NSException *exception){
        NSLog(@"exception in knownUserIdentified %@", exception.reason);
    }
}

#pragma mark - ChatBot Methods

-(void)startChatBot:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic START CHAT BOT !!");
    @try {
        chatBotCallback = command;
        NSString *screenName = nil;
        if ([command.arguments count] > 0) {
            NSDictionary *nvArgument = [command.arguments objectAtIndex: 0];
            if ([nvArgument count] > 0) {
                if (nvArgument[@"screenName"] && ![nvArgument[@"screenName"] isEqual: [NSNull null]]) {
                    screenName = nvArgument[@"screenName"];
                    // [notifyvisitors setChatBotDelegate: self];
                    //[notifyvisitors startChatBotWithScreenName: screenName];
                }else{
                    NSLog(@"screenName is missing !!");
                }
            }
        }
    }
    @catch (NSException *exception) {
        NSLog(@"exception in startChatBot ");
    }
    
}

- (void)NotifyvisitorsChatBotActionCallbackWithUserInfo:(NSDictionary *)userInfo {
    NSLog(@"CHAT BOT ACTION CALLBACK !!");
    @try {
        CDVPluginResult* pluginResult = nil;
        if ([userInfo count] > 0) {
            NSError *nvError = nil;
            NSData *nvJsonData = [NSJSONSerialization dataWithJSONObject: userInfo options: NSJSONWritingPrettyPrinted error: &nvError];
            NSString *nvJsonString = [[NSString alloc] initWithData: nvJsonData encoding: NSUTF8StringEncoding];
            pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
            [pluginResult setKeepCallbackAsBool: YES];
            [self.commandDelegate sendPluginResult:  pluginResult callbackId: chatBotCallback.callbackId];
        }else{
            [pluginResult setKeepCallbackAsBool: YES];
        }
        
    }
    @catch (NSException *exception) {
        NSLog(@"exception in ChatBotActionCallback !!");
    }
    
}

#pragma mark - GetLinkInfo and other callbacks handler methods

-(void)getLinkInfo:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"NotifyVisitors-Ionic GET LINK INFO !!");
        [self _addHandlers:command];
        nvPushObserverReady = YES;
        [[NSNotificationCenter defaultCenter] addObserverForName: @"nvNotificationClickCallback" object: nil queue: nil usingBlock:^(NSNotification *notification) {
            CDVPluginResult* pluginResult = nil;
            NSDictionary *nvUserInfo = [notification userInfo];
            if ([nvUserInfo count] > 0) {
                NSError * err;
                NSData * jsonData = [NSJSONSerialization dataWithJSONObject:nvUserInfo options:0 error:&err];
                NSString * myString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
                pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: myString];
                [pluginResult setKeepCallbackAsBool: YES];
                [self.commandDelegate sendPluginResult:  pluginResult callbackId: command.callbackId];
            }else{
                [pluginResult setKeepCallbackAsBool: YES];
            }
        }];
    }
    @catch (NSException *exception) {
        NSLog(@"exception in getLinkInfo.");
    }
}

-(void)getClickInfoCP:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic GET CLICK INFO CP !!");
    nvPushObserverReady = YES;
    [[NSNotificationCenter defaultCenter] addObserverForName: @"nvNotificationClickCallback" object:nil queue:nil usingBlock:^(NSNotification *notification) {
        CDVPluginResult* pluginResult = nil;
        NSDictionary *nvUserInfo = [notification userInfo];
        
        if ([nvUserInfo count] > 0) {
            NSError *nvError = nil;
            NSData *nvJsonData = [NSJSONSerialization dataWithJSONObject: nvUserInfo options: NSJSONWritingPrettyPrinted error: &nvError];
            NSString *nvJsonString = [[NSString alloc] initWithData: nvJsonData encoding: NSUTF8StringEncoding];
            pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
            [pluginResult setKeepCallbackAsBool: YES];
            [self.commandDelegate sendPluginResult:  pluginResult callbackId: command.callbackId];
        }else{
            [pluginResult setKeepCallbackAsBool: YES];
        }
    }];
}

-(void)getLinkData:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"NotifyVisitors-Ionic GET LINK DATA !!");
        nvPushObserverReady = YES;
        [[NSNotificationCenter defaultCenter] addObserverForName: @"nvNotificationClickCallback" object:nil queue:nil usingBlock:^(NSNotification *notification) {
            CDVPluginResult* pluginResult = nil;
            NSDictionary *nvUserInfo = [notification userInfo];
            if ([nvUserInfo count] > 0) {
                pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsDictionary: nvUserInfo];
                [pluginResult setKeepCallbackAsBool: YES];
                [self.commandDelegate sendPluginResult:  pluginResult callbackId: command.callbackId];
            }else{
                [pluginResult setKeepCallbackAsBool: YES];
            }
        }];
    }
    @catch (NSException *exception) {
        NSLog(@"exception in getLinkData");
    }
}
- (void)notifyvisitorsEventsResponseCallback:(NSDictionary *)callback {
    NSLog(@"Notifyvisitors-Ionic Get Event Response WithUser Info !!");
    @try {
        if([callback count] > 0){
            NSError *nvError = nil;
            NSData *nvJsonData = [NSJSONSerialization dataWithJSONObject: callback options: NSJSONWritingPrettyPrinted error: &nvError];
            NSString *nvJsonString = [[NSString alloc] initWithData: nvJsonData encoding: NSUTF8StringEncoding];
            
            NSString * eventName = callback[@"eventName"];
            // clicked is event or survey
            if([eventName isEqualToString: @"Survey Submit"] || [eventName isEqualToString: @"Survey Attempt"] || [eventName isEqualToString: @"Banner Clicked"] || [eventName isEqualToString: @"Banner Impression"]) {
                if(showCallback != NULL) {
                    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
                    [pluginResult setKeepCallbackAsBool: YES];
                    [self.commandDelegate sendPluginResult:  pluginResult callbackId: showCallback.callbackId];
                }
            } else {
                if(eventCallback != NULL){
                    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
                    [pluginResult setKeepCallbackAsBool: YES];
                    [self.commandDelegate sendPluginResult:  pluginResult callbackId: eventCallback.callbackId];
                }
            }
            
            if(commonCallback != NULL) {
                CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
                [pluginResult setKeepCallbackAsBool: YES];
                [self.commandDelegate sendPluginResult:  pluginResult callbackId: commonCallback.callbackId];
            }
            
        }
    }
    @catch (NSException *exception) {
        NSLog(@"exception in Notifyvisitors Get Event Response WithUser Info !!");
    }
}

- (void)notifyvisitorsKnownUserIdentified:(NSDictionary *)userInfo {
    NSLog(@"RN-NotifyVisitors : GET DATA WHEN KNOWN USER IDENTIFIED !!");
    @try {
        if([userInfo count] > 0){
            if(knownUserIdentifiedCallback != NULL) {
                CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsDictionary: userInfo];
                [pluginResult setKeepCallbackAsBool: YES];
                [self.commandDelegate sendPluginResult:  pluginResult callbackId: knownUserIdentifiedCallback.callbackId];
            }
        }
    }
    @catch (NSException *exception) {
        NSLog(@"RN-NotifyVisitors ERROR : %@", exception.reason);
    }
}

#pragma mark - Other Methods
-(void)googleInAppReview:(CDVInvokedUrlCommand *)command {
    NSLog(@"NotifyVisitors-Ionic Request InApp Review !!");
    @try{
        [notifyvisitors requestAppleAppStoreInAppReview];
    }@catch(NSException *exception){
        NSLog(@"exception in stopNotification");
    }
}

- (void)getSessionData:(CDVInvokedUrlCommand *_Nonnull)command {
  @try {
    NSLog(@"NotifyVisitors-Ionic GET SESSION DATA !!");
    NSDictionary * nvSessionDataResponse = [notifyvisitors getSessionData];
    CDVPluginResult* nvPluginResult = nil;
    NSError *nvError = nil;
    NSData *nvJsonData = nil;
    NSString *nvJsonString = nil;
    if ([nvSessionDataResponse count] > 0) {
      nvJsonData = [NSJSONSerialization dataWithJSONObject: nvSessionDataResponse options: NSJSONWritingPrettyPrinted error: &nvError];
    } else {
      NSDictionary *nvErrorDataResponse = @{};
      nvJsonData = [NSJSONSerialization dataWithJSONObject: nvErrorDataResponse options:NSJSONWritingPrettyPrinted error: &nvError];
    }
    
    nvJsonString = [[NSString alloc] initWithData: nvJsonData encoding: NSUTF8StringEncoding];
    nvPluginResult = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: nvJsonString];
    [self.commandDelegate sendPluginResult:  nvPluginResult callbackId: command.callbackId];
  }
  @catch (NSException *exception) {
    NSLog(@"exception in getSessionData()");
  }
}


#pragma mark - Plugin's Internal Methods

- (void)_addHandlers:(CDVInvokedUrlCommand *)command {
    NSLog(@"ADD HANDLERS !!");
    @try{
        [_handlers addObject:command.callbackId];
        [self sendToJs];
    }@catch (NSException *exception) {
        NSLog(@"exception in sendLinkInfo : %@", exception);
    }
}

- (void)sendLinkInfo:(NSNotification *)notification{
    @try{
        NSLog(@"SEND LINK INFO !!");
        NSDictionary *nvUserInfo = [notification userInfo];
        _lastEvent  = [self createResult:nvUserInfo];
        [self sendToJs];
    }@catch (NSException *exception) {
        NSLog(@"exception in sendLinkInfo : %@", exception);
    }
    
}

- (void)sendToJs{
    @try{
        if (_handlers.count == 0 || _lastEvent == nil) {
            return;
        }
        
        // Iterate our handlers and send the event
        for (id callbackID in _handlers) {
            [self.commandDelegate sendPluginResult:_lastEvent callbackId:callbackID];
        }
        
        // Clear out the last event
        _lastEvent = nil;
    }@catch (NSException *exception) {
        NSLog(@"exception in sendLinkInfo : %@", exception);
    }
}

- (CDVPluginResult *)createResult: (NSDictionary *)nvUserInfo {
    CDVPluginResult *result;
    if ([nvUserInfo count] > 0) {
        NSError * err;
        NSData * jsonData = [NSJSONSerialization dataWithJSONObject:nvUserInfo options:0 error:&err];
        NSString * data = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:data];
        [result setKeepCallbackAsBool:YES];
    }
    return result;
}

- (void)setNvDeepLinkObserver{
    @try{
        [[NSNotificationCenter defaultCenter] removeObserver:self name:@"nvDeepLinkData" object: nil];
        [[NSNotificationCenter defaultCenter] addObserver: self selector: @selector(sendLinkInfo:) name: @"nvDeepLinkData" object: nil];
    }@catch (NSException *exception) {
        NSLog(@"exception in sendLinkInfo: %@", exception);
    }
}

- (void)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler {
    NSLog(@"CONTINUE USER ACTIVITY FROM OTHER PLUGIN !*!");
    @try{
        if ([userActivity.activityType isEqualToString: NSUserActivityTypeBrowsingWeb]) {
            NSURL *nvAppULinkUrl = [userActivity webpageURL];
            if(nvAppULinkUrl != nil){
                NSString *nvUrl = nvAppULinkUrl.absoluteString;
                NSMutableDictionary * nvlinkInfo = [[NSMutableDictionary alloc] init] ;
                nvlinkInfo = [notifyvisitors OpenUrlGetDataWithApplication:application Url:nvAppULinkUrl];
                [nvlinkInfo setValue:nvUrl forKey:@"url"];
                [nvlinkInfo setValue:@"nv" forKey:@"source"];
                [[NSNotificationCenter defaultCenter] postNotificationName: @"nvDeepLinkData"  object: 0 userInfo:nvlinkInfo];
            }
        }
    }@catch (NSException *exception) {
        NSLog(@"exception in continueUserActivity");
    }
}

-(UIColor*)GetColor:(NSString *)ColorString {
    if ([[ColorString substringToIndex:1]isEqualToString:@"#"]) {
        unsigned int c;
        if ([ColorString characterAtIndex:0] == '#') {
            [[NSScanner scannerWithString:[ColorString substringFromIndex:1]] scanHexInt:&c];
        } else {
            [[NSScanner scannerWithString:ColorString] scanHexInt:&c];
        }
        return [UIColor colorWithRed:((c & 0xff0000) >> 16)/255.0 green:((c & 0xff00) >> 8)/255.0 blue:(c & 0xff)/255.0 alpha:1.0];
    } else {
        NSString *sep = @"()";
        NSCharacterSet *set = [NSCharacterSet characterSetWithCharactersInString:sep];
        NSString *rgba = [ColorString componentsSeparatedByCharactersInSet:set][1];
        CGFloat R = [[rgba componentsSeparatedByString:@","][0] floatValue];
        CGFloat G = [[rgba componentsSeparatedByString:@","][1] floatValue];
        CGFloat B = [[rgba componentsSeparatedByString:@","][2] floatValue];
        CGFloat alpha = [[rgba componentsSeparatedByString:@","][3] floatValue];
        UIColor *ResultColor = [UIColor colorWithRed:R/255 green:G/255 blue:B/255 alpha:alpha];
        return ResultColor;
    }
}

#pragma mark - Android Specific dummy function

-(void)autoStartAndroid:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"autoStartAndroid called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

-(void)createNotificationChannel:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"createNotificationChannel called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

-(void)deleteNotificationChannel:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"deleteNotificationChannel called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

-(void)createNotificationChannelGroup:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"createNotificationChannelGroup called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

-(void)deleteNotificationChannelGroup:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"deleteNotificationChannelGroup called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

-(void)enablePushPermission:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"enablePushPermission called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}


-(void)activatePushPermissionPopup:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"activatePushPermissionPopup called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

- (void)isPayloadFromNvPlatform:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"isPayloadFromNvPlatform called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

- (void)getNV_FCMPayload:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"getNV_FCMPayload called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

- (void)nativePushPermissionPrompt:(CDVInvokedUrlCommand *)command {
    @try {
        NSLog(@"nativePushPermissionPrompt called !!");
        NSLog(@"This feature is available android only !!");
    }
    @catch (NSException *exception) {
        NSLog(@"Something missing...");
    }
}

@end
