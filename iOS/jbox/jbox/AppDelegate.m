//
//  AppDelegate.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/19.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "AppDelegate.h"
#import "JPUSHService.h"
#import "JBNetwork.h"
#import "JBDatabase.h"
#import "JBMessageViewController.h"
#import <UserNotifications/UserNotifications.h>

@interface AppDelegate ()<JPUSHRegisterDelegate>

@end

@implementation AppDelegate


- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken{ 
    // JPush sdk 
    [JPUSHService registerDeviceToken:deviceToken]; 
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo { 
    // JPush sdk 
    [JPUSHService handleRemoteNotification:userInfo];   
}

//IOS7 only  
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {    
    [JPUSHService handleRemoteNotification:userInfo];   
    completionHandler(UIBackgroundFetchResultNewData);  
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    //JPush sdk
    [JPUSHService setupWithOption:launchOptions appKey:@"1c29cb5814072b5b1f8ef829" channel:@"" apsForProduction:YES advertisingIdentifier:nil];

    if ([[UIDevice currentDevice].systemVersion floatValue] >= 10.0) {
#ifdef NSFoundationVersionNumber_iOS_9_x_Max
        JPUSHRegisterEntity * entity = [[JPUSHRegisterEntity alloc] init];
        entity.types = UNAuthorizationOptionAlert|UNAuthorizationOptionBadge|UNAuthorizationOptionSound;
        [JPUSHService registerForRemoteNotificationConfig:entity delegate:self];
#endif
    }
    
#if __IPHONE_OS_VERSION_MAX_ALLOWED > __IPHONE_7_1
    if ([[UIDevice currentDevice].systemVersion floatValue] >= 8.0) { 
        //可以添加自定义categories 
        [JPUSHService registerForRemoteNotificationTypes:(UIUserNotificationTypeBadge | 
                                                          UIUserNotificationTypeSound | 
                                                          UIUserNotificationTypeAlert)  
                                              categories:nil];  
    } else {   
        //categories 必须为nil 
        [JPUSHService registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge |   
                                                          UIRemoteNotificationTypeSound |   
                                                          UIRemoteNotificationTypeAlert)    
                                              categories:nil];  
    }   
#else   
    //categories 必须为nil 
    [JPUSHService registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge |   
                                                      UIRemoteNotificationTypeSound |   
                                                      UIRemoteNotificationTypeAlert)
                                          categories:nil];  
#endif    
    // Override point for customization after application launch.

    //显示状态栏
    [[UIApplication sharedApplication]setStatusBarHidden:NO];
    //样式白色
    [[UIApplication sharedApplication]setStatusBarStyle:UIStatusBarStyleLightContent];


    
    JBMessageViewController *mVC = [[JBMessageViewController alloc] initWithNibName:@"JBMessageViewController" bundle:[NSBundle mainBundle]];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:mVC];
    nav.navigationBar.barTintColor = [UIColor colorWithHexString:@"1490a1"];
    nav.navigationBar.translucent = NO;
    nav.navigationBar.opaque = YES;
    self.window.rootViewController = nav;
    [self.window makeKeyAndVisible];

    return YES;
}

 

@end
