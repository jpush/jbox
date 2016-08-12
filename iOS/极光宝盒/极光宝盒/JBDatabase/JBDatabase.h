//
//  JBDatabase.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JBDatabase : NSObject

+(void)createChannelWithName:(NSString*)name;
+(void)insertToChannel:(NSString*)channel Messages:(NSMutableArray*)mArray;
+(NSMutableArray*)getMessagesFromChannel:(NSString*)channel;

@end
