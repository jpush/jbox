//
//  JBSharedDevkey.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBSharedDevkey.h"

static NSString *const JBUserDefaultsDevkey = @"JBUserDefaultsDevkey";

@implementation JBSharedDevkey

+(void)saveDevkey:(NSString*)devkey{
    [[NSUserDefaults standardUserDefaults] setValue:devkey forKey:JBUserDefaultsDevkey];
}

+(NSString*)getDevkey{
    NSString *devkey = [[NSUserDefaults standardUserDefaults] valueForKey:JBUserDefaultsDevkey];
    return devkey == nil ? @"" : devkey;
}

@end
