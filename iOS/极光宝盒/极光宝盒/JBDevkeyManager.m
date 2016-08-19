//
//  JBSharedDevkey.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDevkeyManager.h"
#import "JPUSHService.h"
#import "JBNetwork.h"
#import "JBDatabase.h"

static NSString *const JBUserDefaultsDevkey = @"JBUserDefaultsDevkey";

@implementation JBDevkeyManager

+(void)saveDevkey:(NSString*)devkey{
    NSArray *devkeys = [JBDevkeyManager getDevkeys];
    NSMutableSet *devkeySet = [NSMutableSet setWithArray:devkeys];
    [devkeySet addObject:devkey];
    [[NSUserDefaults standardUserDefaults] setValue:devkeySet.allObjects forKey:JBUserDefaultsDevkey];

    [JBDatabase updateChannelDatabase];

    //获取全部 channel 打 tag
    [JBNetwork getChannelsWithDevkey:devkey complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        NSArray *channels = dict[@"channels"];
        NSMutableArray *tags = [NSMutableArray array];
        for (NSString *channel in channels) {
            NSString *tag = [NSString stringWithFormat:@"%@%@", devkey, channel];
            [tags addObject:tag];
        }
        [JPUSHService setTags:[NSSet setWithArray:tags] aliasInbackground:nil];
    }];

    //需要刷新消息页面
    [[NSNotificationCenter defaultCenter] postNotificationName:JBChannelTableViewControllerShouldUpdate object:nil];
}

+(NSArray*)getDevkeys{
    NSArray *devkeys = [[NSUserDefaults standardUserDefaults] valueForKey:JBUserDefaultsDevkey];
    return devkeys == nil ? @[] : devkeys;
}

@end
