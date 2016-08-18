//
//  JBSharedDevkey.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDevkeyManager.h"
#import "JPUSHService.h"

static NSString *const JBUserDefaultsDevkey = @"JBUserDefaultsDevkey";

@implementation JBDevkeyManager

+(void)saveDevkey:(NSString*)devkey{
    NSArray *devkeys = [JBDevkeyManager getDevkeys];
    NSMutableSet *devkeySet = [NSMutableSet setWithArray:devkeys];
    [devkeySet addObject:devkey];
    [[NSUserDefaults standardUserDefaults] setValue:devkeySet.allObjects forKey:JBUserDefaultsDevkey];
    NSSet *tags = [NSSet setWithObjects:devkey, nil];
    [JPUSHService setTags:tags aliasInbackground:nil ];
}

+(NSArray*)getDevkeys{
    NSArray *devkeys = [[NSUserDefaults standardUserDefaults] valueForKey:JBUserDefaultsDevkey];
    return devkeys == nil ? @[] : devkeys;
}

@end
