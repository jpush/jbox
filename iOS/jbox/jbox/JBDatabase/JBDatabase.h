//
//  JBDatabase.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JBChannel.h"
#import "JBMessage.h"
#import "JBDevkey.h"

@interface JBDatabase : NSObject

+(void)insertDevkey:(JBDevkey*)devkey;
+(void)updateDevkey:(JBDevkey*)devkey;
+(NSMutableArray*)getDevkeys;
+(BOOL)devkeyInDatabase:(NSString*)devkey;
+(JBDevkey*)getDevkeyInfoWithDevkey:(NSString*)devkeyStr;

+(void)createChannels:(NSArray*)channels;
+(void)insertMessages:(NSArray*)mArray;
+(NSMutableArray*)getMessagesFromChannel:(JBChannel*)channel;
+(JBMessage*)getLastMessage:(JBChannel*)channel;
+(NSMutableArray*)getUnreadMessagesFromChannel:(JBChannel*)channel;
+(void)setAllMessagesReadWithChannel:(JBChannel*)channel;

+(void)insertChannel:(JBChannel*)channel;
+(NSMutableArray*)getAllSubscribedChannels;
+(NSMutableArray*)getChannelsFromDevkey:(NSString*)devkey;
+(void)checkAndDeleteChannelsFromDevkey:(NSString*)devkey newChannelNames:(NSArray*)newChannelNames;
+(void)updateChannel:(JBChannel*)channel;
+(void)deleteChannel:(JBChannel*)channel;

+(NSMutableArray*)getSortedDevkeyAndChannel;

@end
