//
//  JBDatabase.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDatabase.h"
#import <FMDB.h>
#import "JBMessage.h"
#import "JBDevkeyManager.h"

#define JBDatabaseName @"jboxDatabase.sqlite"
#define JBChannelDatabaseName @"jboxChannelDatabase.sqlite"
#define JBChannelTableName @"JBChannelTableName"


#define JBSharedDatabase [JBDatabase sharedDatabase]
#define JBSharedChannelDatabase [JBDatabase sharedChannelDatabase]

#define JBTableName(name1,name2) [NSString stringWithFormat:@"%@%@",name1, name2]

@implementation JBDatabase


//--------------------------------------- message ---------------------------------------//

+(FMDatabase*)sharedDatabase{
    static FMDatabase *database = nil;
    static dispatch_once_t predicate; dispatch_once(&predicate, ^{
        NSArray *array = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *docPath = [array lastObject];
        NSString *dbPath = [docPath stringByAppendingPathComponent:JBDatabaseName];
        database = [FMDatabase databaseWithPath:dbPath];
    });
    return database;
}

+(void)createChannel:(JBChannel*)channel{
    if ([JBSharedDatabase open]) {
        NSString *sqlCreateTable = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS %@ (id integer PRIMARY KEY AUTOINCREMENT,title text ,message text ,devkey text ,channel text, time text)", JBTableName(channel.devkey, channel.name)];
        BOOL result = [JBSharedDatabase executeUpdate:sqlCreateTable];
        if (result) {

        }
        [JBSharedDatabase close];
    }
}

+(void)createChannels:(NSArray*)channels{
    for (JBChannel *channel in channels) {
        [JBDatabase createChannel:channel];
    }
}

+(void)insertMessages:(NSArray*)mArray{
    if ([JBSharedDatabase open]) {
        for (JBMessage *message in mArray) {
            NSString *sqlInsertTable = [NSString stringWithFormat:@"insert into %@ (title,message,devkey,channel,time) values ('%@','%@','%@','%@','%@')",JBTableName(message.devkey, message.channel), message.title, message.content, message.devkey, message.title, message.time];
            BOOL result = [JBSharedDatabase executeUpdate:sqlInsertTable];
            if (result) {
            }
        }
        [JBSharedDatabase close];
    }
}

+(NSMutableArray*)getMessagesFromChannel:(JBChannel*)channel{
    NSMutableArray *modelArray = [NSMutableArray array];
    if ([JBSharedDatabase open]) {
        NSString *sqlSelect = [NSString stringWithFormat:@"select * from %@", JBTableName(channel.devkey, channel.name)];
        FMResultSet *set = [JBSharedDatabase executeQuery:sqlSelect];
        while ([set next]) {
            JBMessage *message = [JBMessage new];
            message.title   = [set stringForColumn:@"title"];
            message.content = [set stringForColumn:@"message"];
            message.devkey  = [set stringForColumn:@"devkey"];
            message.channel = [set stringForColumn:@"channel"];
            message.time    = [set stringForColumn:@"time"];
            [modelArray addObject:message];
        }
        [JBSharedDatabase close];
    }
    return modelArray;
}

+(NSString*)getLastMessage:(JBChannel*)channel{
    NSString *lastMessage;
    if ([JBSharedDatabase open]) {
        NSString *sqlStr = [NSString stringWithFormat:@"select max(id),title from %@", JBTableName(channel.devkey, channel.name)];
        FMResultSet *set = [JBSharedDatabase executeQuery:sqlStr];
        while ([set next]) {
            lastMessage = [set stringForColumn:@"title"];
        }
        [JBSharedDatabase close];
    }
    return lastMessage;
}

+(void)clearChannelWithChannel:(JBChannel*)channel{
    NSString *sqlstr = [NSString stringWithFormat:@"DELETE FROM %@", JBTableName(channel.devkey, channel.name)];
    [JBSharedDatabase executeUpdate:sqlstr];
}

+(void)deleteChannelWithChannel:(JBChannel*)channel{
    NSString *sqlstr = [NSString stringWithFormat:@"DROP TABLE %@", JBTableName(channel.devkey, channel.name)];
    [JBSharedDatabase executeUpdate:sqlstr];
}

//--------------------------------------- channel ---------------------------------------//

+(void)updateChannelDatabase{
    NSArray *devkeys = [JBDevkeyManager getDevkeys];
    for (NSString *devkey in devkeys) {
        [JBDatabase createChannelTableWithDevkey:devkey];
    }
}

+(FMDatabase*)sharedChannelDatabase{
    static FMDatabase *database = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        NSArray *array = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *docPath = [array lastObject];
        NSString *dbPath = [docPath stringByAppendingPathComponent:JBChannelDatabaseName];
        database = [FMDatabase databaseWithPath:dbPath];
    });
    return database;
}

+(void)createChannelTableWithDevkey:(NSString*)devkey{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlCreateTable = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS %@ (id integer PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, devkey text, isTag text);",JBTableName(JBChannelTableName, devkey)];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlCreateTable];
        if (result) {

        }
        [JBSharedChannelDatabase close];
    }
}

+(void)insertChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlInsertTable = [NSString stringWithFormat:@"insert into %@ (devkey,name,isTag) values ('%@','%@','%@')",JBTableName(JBChannelTableName, channel.devkey), channel.devkey, channel.name, channel.isTag];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
        }
        [JBSharedChannelDatabase close];
    }
}

+(NSMutableArray*)getAllChannels{
    NSArray *devkeys = [JBDevkeyManager getDevkeys];
    NSMutableArray *channels = [NSMutableArray array];
    for (NSString *devkey in devkeys) {
        [channels addObjectsFromArray:[JBDatabase getChannelsFromDevkey:devkey]];
    }
    return channels;
}

+(NSMutableArray*)getAllSubscribedChannels{
    NSMutableArray *channels = [JBDatabase getAllChannels];
    for (JBChannel *channel in channels) {
        if ([channel.isTag isEqualToString:@"0"]) {
            [channels delete:channel];
        }
    }
    return channels;
}


+(NSMutableArray*)getChannelsFromDevkey:(NSString*)devkey{
    NSMutableArray *modelArray = [NSMutableArray array];
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlSelect = [NSString stringWithFormat:@"select * from %@", JBTableName(JBChannelTableName, devkey)];
        FMResultSet *set = [JBSharedChannelDatabase executeQuery:sqlSelect];
        while ([set next]) {
            JBChannel *channel = [JBChannel new];
            channel.devkey     = [set stringForColumn:@"devkey"];
            channel.name       = [set stringForColumn:@"name"];
            channel.isTag      = [set stringForColumn:@"isTag"];
            [modelArray addObject:channel];
        }
        [JBSharedChannelDatabase close];
    }
    return modelArray;
}

+(void)updateChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlInsertTable = [NSString stringWithFormat:@"UPDATE %@ SET isTag = %@ WHERE devkey = %@ AND name = %@",JBTableName(JBChannelTableName, channel.devkey), channel.isTag, channel.devkey, channel.name];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
        }
        [JBSharedChannelDatabase close];
    }
}

+(void)deleteChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlInsertTable = [NSString stringWithFormat:@"delete from %@ WHERE name = %@",JBTableName(JBChannelTableName, channel.devkey), channel.name];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
        }
        [JBSharedChannelDatabase close];
    }
}

@end


