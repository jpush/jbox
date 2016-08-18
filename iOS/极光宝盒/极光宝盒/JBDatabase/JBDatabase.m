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

#define JBDatabaseName @"jboxDatabase.sqlite"
#define JBChannelDatabaseName @"jboxChannelDatabase.sqlite"

#define JBSharedDatabase [JBDatabase sharedDatabase]
#define JBSharedChannelDatabase [JBDatabase sharedChannelDatabase]

#define DevChannelName(devkey,channel) [NSString stringWithFormat:@"%@-%@",devkey, channel]

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
    if (![JBDatabase isTableExist:channel]) {
        if ([JBSharedDatabase open]) {
            NSString *sqlCreateTable = [NSString stringWithFormat:@"create table if not exists %@ (id integer primary key autoincrement ,title text ,message text ,devkey text ,channel text)", DevChannelName(channel.devkey, channel.name)];
            BOOL result = [JBSharedDatabase executeUpdate:sqlCreateTable];
            if (result) {

            }
            [JBSharedDatabase close];
        }
    }
}

+(void)createChannels:(NSArray*)channels{
    for (JBChannel *channel in channels) {
        [JBDatabase createChannel:channel];
    }
}

+(BOOL)isTableExist:(JBChannel*)channel{
    FMResultSet *rs = [JBSharedDatabase executeQuery:@"select count(*) as 'count' from sqlite_master where type ='table' and name = ?", DevChannelName(channel.devkey, channel.name)];
    while ([rs next]){
        NSInteger count = [rs intForColumn:@"count"];
        if (0 == count){
            return NO;
        }else{
            return YES;
        }
    }
    return NO;
}

+(void)insertMessages:(NSMutableArray*)mArray{
    if ([JBSharedDatabase open]) {
        for (JBMessage *message in mArray) {
            NSString *sqlInsertTable = [NSString stringWithFormat:@"insert into %@ (title,message,devkey,channel) values ('%@','%@','%@','%@')",DevChannelName(message.devkey, message.channel), message.title, message.message, message.devkey, message.title];
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
        NSString *sqlSelect = [NSString stringWithFormat:@"select * from %@", DevChannelName(channel.devkey, channel.name)];
        FMResultSet *set = [JBSharedDatabase executeQuery:sqlSelect];
        while ([set next]) {
            JBMessage *message = [JBMessage new];
            message.title   = [set stringForColumn:@"title"];
            message.message = [set stringForColumn:@"message"];
            message.devkey  = [set stringForColumn:@"devkey"];
            message.channel = [set stringForColumn:@"channel"];
            [modelArray addObject:message];
        }
        [JBSharedDatabase close];
    }
    return modelArray;
}

+(void)clearChannelWithChannel:(JBChannel*)channel{
    NSString *sqlstr = [NSString stringWithFormat:@"DELETE FROM %@", DevChannelName(channel.devkey, channel.name)];
    [JBSharedDatabase executeUpdate:sqlstr];
}

+(void)deleteChannelWithChannel:(JBChannel*)channel{
    NSString *sqlstr = [NSString stringWithFormat:@"DROP TABLE %@", DevChannelName(channel.devkey, channel.name)];
    [JBSharedDatabase executeUpdate:sqlstr];
}

//--------------------------------------- channel ---------------------------------------//

+(FMDatabase*)sharedChannelDatabase{
    static FMDatabase *database = nil;
    static dispatch_once_t predicate; dispatch_once(&predicate, ^{
        NSArray *array = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *docPath = [array lastObject];
        NSString *dbPath = [docPath stringByAppendingPathComponent:JBChannelDatabaseName];
        database = [FMDatabase databaseWithPath:dbPath];
        if (![JBDatabase isChannelTableExist]) {
            [JBDatabase createChannelTable];
        }
    });
    return database;
}

+(void)createChannelTable{
    if ([JBSharedDatabase open]) {
        NSString *sqlCreateTable = [NSString stringWithFormat:@"create table if not exists %@ (id integer primary key autoincrement ,devkey text ,name text ,isTag text)",JBChannelDatabaseName];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlCreateTable];
        if (result) {

        }
        [JBSharedChannelDatabase close];
    }
}

+(void)insertChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlInsertTable = [NSString stringWithFormat:@"insert into %@ (devkey,name,isTag) values ('%@','%@','%@')",JBChannelDatabaseName, channel.devkey, channel.name, channel.isTag];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
        }
        [JBSharedChannelDatabase close];
    }
}

+(void)changeChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlInsertTable = [NSString stringWithFormat:@"UPDATE %@ SET isTag = %@ WHERE devkey = %@ AND name = %@",JBChannelDatabaseName, channel.isTag, channel.devkey, channel.name];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
        }
        [JBSharedChannelDatabase close];
    }
}

+(BOOL)isChannelTableExist{
    FMResultSet *rs = [JBSharedChannelDatabase executeQuery:@"select count(*) as 'count' from sqlite_master where type ='table' and name = ?", JBChannelDatabaseName];
    while ([rs next]){
        // just print out what we've got in a number of formats.
        NSInteger count = [rs intForColumn:@"count"];
        if (0 == count){
            return NO;
        }else{
            return YES;
        }
    }
    return NO;
}

@end


