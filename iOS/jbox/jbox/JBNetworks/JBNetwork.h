//
//  JBNetwork.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/1.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JBNetwork : NSObject

+(void)getDevInfoWithDevkey:(NSString*)devkey complete:(void (^)(id responseObject))complete;

+(void)getChannelsWithDevkey:(NSString*)devkey complete:(void (^)(id responseObject))complete;

@end
