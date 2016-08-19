//
//  JBDatabase.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JBChannel.h"

@interface JBDatabase : NSObject

+(void)createChannels:(NSArray*)channels;
+(void)insertMessages:(NSArray*)mArray;
+(NSMutableArray*)getMessagesFromChannel:(JBChannel*)channel;
+(NSString*)getLastMessage:(JBChannel*)channel;

+(void)updateChannelDatabase;
+(void)insertChannel:(JBChannel*)channel;
+(NSMutableArray*)getAllChannels;
+(NSMutableArray*)getAllSubscribedChannels;
+(NSMutableArray*)getChannelsFromDevkey:(NSString*)devkey;
+(void)updateChannel:(JBChannel*)channel;
+(void)deleteChannel:(JBChannel*)channel;

@end
