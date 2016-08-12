//
//  JBSharedDevkey.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JBSharedDevkey : NSObject
+(void)saveDevkey:(NSString*)devkey;
+(NSString*)getDevkey;
@end
