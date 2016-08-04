//
//  JBNetwork.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/1.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JBNetwork : NSObject
//获取 developer 信息
+(void)getDevInfo:(NSString*)devkey complete:(void (^)(NSDictionary* devInfo))complete;
//获取 devkey 下的 channel 列表
+(void)getChannels:(NSString*)devkey complete:(void (^)(NSDictionary* devInfo))complete;
//获取 devkey 下的所有自定义应用的 appid
+(void)getAppidUnderDevkey:(NSString*)devkey complete:(void (^)(NSDictionary* devInfo))complete;
@end
