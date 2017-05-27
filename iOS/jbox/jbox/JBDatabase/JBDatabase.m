//
//  JBDatabase.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDatabase.h"
#import <FMDB.h>

#import "JBNetwork.h"
#import "JPUSHService.h"

#define JBDatabaseName @"jboxDatabase.sqlite"
#define JBChannelDatabaseName @"jboxChannelDatabase.sqlite"
#define JBChannelTableName @"JBChannelTableName"


#define JBSharedDatabase [JBDatabase sharedDatabase]
#define JBSharedChannelDatabase [JBDatabase sharedChannelDatabase]

#define JBTableName(name1,name2) [NSString stringWithFormat:@"%@%@",name1, name2]


static NSString *const JBUserDefaultsDevkey = @"JBUserDefaultsDevkey";

@implementation JBDatabase

//--------------------------------------- devkey ---------------------------------------//

+(void)insertDevkey:(JBDevkey *)devkey{
    NSMutableArray *devkeys = [JBDatabase getDevkeys];
    if (![JBDatabase devkeyInDatabase:devkey.dev_key] && devkey.dev_key) {
        [devkeys addObject:devkey];
    }else{
        for (int i = 0; i < devkeys.count; i++) {
            if ([devkey.dev_key isEqualToString:((JBDevkey*)devkeys[i]).dev_key]) {
                [devkeys replaceObjectAtIndex:i withObject:devkey];
                break;
            }
        }
    }
    NSMutableArray* dataArr = [NSMutableArray array];
    for (JBDevkey *tempkey in devkeys) {
        [dataArr addObject:[NSKeyedArchiver archivedDataWithRootObject:tempkey]];
    }
    [[NSUserDefaults standardUserDefaults] setObject:dataArr forKey:JBUserDefaultsDevkey];
    [JBDatabase createChannelTableWithDevkey:devkey];

    [[NSNotificationCenter defaultCenter] postNotificationName:JBDevkeyInserted object:nil];
}

+(BOOL)devkeyInDatabase:(NSString*)devkey{
    NSMutableArray *devkeys = [JBDatabase getDevkeys];
    BOOL inDatabase = NO;
    for (JBDevkey *tempKey in devkeys) {
        if ([tempKey.dev_key isEqualToString:devkey]) {
            inDatabase = YES;
        }
    }
    return inDatabase;
}

+(NSMutableArray *)getDevkeys{
    NSArray *arr = [[NSUserDefaults standardUserDefaults] objectForKey:JBUserDefaultsDevkey];
    NSMutableArray *devkeys = [NSMutableArray array];
    for (NSData *data in arr) {
        JBDevkey *devkey = [NSKeyedUnarchiver unarchiveObjectWithData:data];
        [devkeys addObject:devkey];
    }
    return devkeys;
}

+(JBDevkey*)getDevkeyInfoWithDevkey:(NSString*)devkeyStr{
    NSArray *devkeys = [JBDatabase getDevkeys];
    JBDevkey *devkey = [JBDevkey new];
    for (JBDevkey *tempkey in devkeys) {
        if ([tempkey.dev_key isEqualToString:devkeyStr]) {
            devkey = tempkey;
        }
    }
    return devkey;
}

+(void)updateDevkey:(JBDevkey *)devkey{
    NSMutableArray *devkeys = [JBDatabase getDevkeys];
    NSMutableArray *newkeys = [NSMutableArray array];
    for (__strong JBDevkey *temp in devkeys) {
        if ([temp.dev_key isEqualToString:devkey.dev_key]) {
            temp = devkey;
        }
        [newkeys addObject:[NSKeyedArchiver archivedDataWithRootObject:temp]];
    }
    [[NSUserDefaults standardUserDefaults] setObject:newkeys forKey:JBUserDefaultsDevkey];
}

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
        NSString *sqlCreateTable = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS '%@' (id integer PRIMARY KEY AUTOINCREMENT,title text ,message text ,devkey text ,channel text, time text, read text, icon text, integration_name text, url text)", JBTableName(channel.dev_key, channel.name)];
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
            NSString *sqlInsertTable = [NSString stringWithFormat:@"insert into '%@' (title,message,devkey,channel,time,read,icon,integration_name,url) values ('%@','%@','%@','%@','%@', '%@', '%@', '%@', '%@')",JBTableName(message.devkey, message.channel), message.title, message.content, message.devkey, message.channel, message.time, message.read, message.icon, message.integration_name, message.url];
            BOOL result = [JBSharedDatabase executeUpdate:sqlInsertTable];
            if (result) {
                [[NSNotificationCenter defaultCenter] postNotificationName:JBChannelMessageInserted object:nil];
            }
        }
        [JBSharedDatabase close];
    }
}

+(NSMutableArray*)getMessagesFromChannel:(JBChannel*)channel{
    NSMutableArray *modelArray = [NSMutableArray array];
    if ([JBSharedDatabase open]) {
        NSString *sqlSelect = [NSString stringWithFormat:@"select * from '%@'", JBTableName(channel.dev_key, channel.name)];
        FMResultSet *set = [JBSharedDatabase executeQuery:sqlSelect];
        while ([set next]) {
            JBMessage *message = [JBMessage new];
            message.title   = [set stringForColumn:@"title"];
            message.content = [set stringForColumn:@"message"];
            message.devkey  = [set stringForColumn:@"devkey"];
            message.channel = [set stringForColumn:@"channel"];
            message.time    = [set stringForColumn:@"time"];
            message.read    = [set stringForColumn:@"read"];
            message.icon    = [set stringForColumn:@"icon"];
            message.url     = [set stringForColumn:@"url"];
            message.integration_name = [set stringForColumn:@"integration_name"];
            [modelArray addObject:message];
        }
        [JBSharedDatabase close];
    }
    return modelArray;
}

+(void)setAllMessagesReadWithChannel:(JBChannel*)channel{
    if ([JBSharedDatabase open]) {
        NSString *read = @"1";
        NSString *sqlInsertTable = [NSString stringWithFormat:@"UPDATE '%@' SET read = '%@' WHERE devkey = '%@'",JBTableName(channel.dev_key, channel.name), read, channel.dev_key];
        BOOL result = [JBSharedDatabase executeUpdate:sqlInsertTable];
        if (result) {
            [[NSNotificationCenter defaultCenter] postNotificationName:JBChannelMessageReaded object:nil];
        }
        [JBSharedDatabase close];
    }

    NSArray *channels = [JBDatabase getAllChannels];
    NSMutableArray *unread = [NSMutableArray array];
    for (JBChannel *channel in channels) {
        NSArray *temp = [JBDatabase getUnreadMessagesFromChannel:channel];
        [unread addObjectsFromArray:temp];
    }
    if (unread.count == 0) {
        [UIApplication sharedApplication].applicationIconBadgeNumber = 0;
    }
}

+(NSMutableArray*)getUnreadMessagesFromChannel:(JBChannel*)channel{
    NSMutableArray *arr = [NSMutableArray array];
    for (JBMessage* message in [JBDatabase getMessagesFromChannel:channel]) {
        if ([message.read isEqualToString:@"0"]) {
            [arr addObject:message];
        }
    }
    return arr;
}

+(JBMessage*)getLastMessage:(JBChannel*)channel{
    JBMessage *message = [JBMessage new];
    if ([JBSharedDatabase open]) {
        NSString *sqlStr = [NSString stringWithFormat:@"select max(id),title,time from %@", JBTableName(channel.dev_key, channel.name)];
        FMResultSet *set = [JBSharedDatabase executeQuery:sqlStr];
        while ([set next]) {
            message.title = [set stringForColumn:@"title"];
            message.time  = [set stringForColumn:@"time"];
        }
        [JBSharedDatabase close];
    }
    return message;
}

+(void)clearChannelWithChannel:(JBChannel*)channel{
    NSString *sqlstr = [NSString stringWithFormat:@"DELETE FROM %@", JBTableName(channel.dev_key, channel.name)];
    [JBSharedDatabase executeUpdate:sqlstr];
}

+(void)deleteChannelWithChannel:(JBChannel*)channel{
    NSString *sqlstr = [NSString stringWithFormat:@"DROP TABLE %@", JBTableName(channel.dev_key, channel.name)];
    [JBSharedDatabase executeUpdate:sqlstr];
}

//--------------------------------------- channel ---------------------------------------//

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

+(void)createChannelTableWithDevkey:(JBDevkey*)devkey{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlCreateTable = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS '%@' (id integer PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, dev_key text, isSubscribed text, icon text);",JBTableName(JBChannelTableName, devkey.dev_key)];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlCreateTable];
        if (result) {

        }
        [JBSharedChannelDatabase close];
    }
}

+(void)insertChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlInsertTable = [NSString stringWithFormat:@"insert into '%@' (dev_key,name,isSubscribed) values ('%@','%@','%@')",JBTableName(JBChannelTableName, channel.dev_key), channel.dev_key, channel.name, channel.isSubscribed];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
        }
        [JBSharedChannelDatabase close];
    }
}

+(NSMutableArray*)getAllChannels{
    NSArray *devkeys = [JBDatabase getDevkeys];
    NSMutableArray *channels = [NSMutableArray array];
    for (JBDevkey *devkey in devkeys) {
        [channels addObjectsFromArray:[JBDatabase getChannelsFromDevkey:devkey.dev_key]];
    }
    return channels;
}

+(NSMutableArray*)getAllSubscribedChannels{
    NSMutableArray *channels = [JBDatabase getAllChannels];
    NSMutableArray *resultArr = [NSMutableArray array];
    for (JBChannel *channel in channels) {
        if ([channel.isSubscribed isEqualToString:@"1"]) {
            [resultArr addObject:channel];
        }
    }
    return resultArr;
}

+(NSMutableArray*)getChannelsFromDevkey:(NSString*)devkey{
    NSMutableArray *modelArray = [NSMutableArray array];
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlSelect = [NSString stringWithFormat:@"select * from '%@'", JBTableName(JBChannelTableName, devkey)];
        FMResultSet *set = [JBSharedChannelDatabase executeQuery:sqlSelect];
        while ([set next]) {
            JBChannel *channel   = [JBChannel new];
            channel.dev_key      = [set stringForColumn:@"dev_key"];
            channel.name         = [set stringForColumn:@"name"];
            channel.isSubscribed = [set stringForColumn:@"isSubscribed"];
            [modelArray addObject:channel];
        }
        [JBSharedChannelDatabase close];
    }
    return modelArray;
}

+(NSMutableArray*)getSubscribedChannelsFromDevkey:(NSString*)devkey{
    NSMutableArray *array = [JBDatabase getChannelsFromDevkey:devkey];
    for (JBChannel *channel in array.reverseObjectEnumerator) {
        if (!channel.isSubscribed.boolValue) {
            [array removeObject:channel];
            continue;
        }
    }
    return array;
}

+(void)checkAndDeleteChannelsFromDevkey:(NSString*)devkey newChannelNames:(NSArray*)newChannelNames{
    NSMutableArray *originChannels  = [JBDatabase getChannelsFromDevkey:devkey];
    for (JBChannel *channel in originChannels) {
        BOOL inNew = NO;
        for (NSString *name in newChannelNames) {
            if ([channel.name isEqualToString:name]) {
                inNew = YES;
            }
        }
        if (!inNew) {
            [JBDatabase deleteChannel:channel];
        }
    }
}

+(void)updateChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {//dev_key,name,isSubscribed,icon
        NSString *sqlInsertTable = [NSString stringWithFormat:@"UPDATE '%@' SET isSubscribed = '%@' WHERE name = '%@'",JBTableName(JBChannelTableName, channel.dev_key), channel.isSubscribed,channel.name];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
            //打tag
            NSMutableArray *channels = [JBDatabase getAllChannels];
            NSMutableSet *set = [NSMutableSet set];
            for (JBChannel *channel in channels) {
                if ([channel.isSubscribed boolValue]) {
                    NSString *tag = [NSString stringWithFormat:@"%@_%@",channel.dev_key,channel.name];
                    [set addObject:tag];
                }
            }
            [JPUSHService setTags:set alias:nil fetchCompletionHandle:^(int iResCode, NSSet *iTags, NSString *iAlias) {
                [[NSNotificationCenter defaultCenter] postNotificationName:JBChannelUpdated object:nil];
            }];
        }
        [JBSharedChannelDatabase close];
    }
}

+(void)deleteChannel:(JBChannel*)channel{
    if ([JBSharedChannelDatabase open]) {
        NSString *sqlInsertTable = [NSString stringWithFormat:@"delete from '%@' WHERE name = '%@'",JBTableName(JBChannelTableName, channel.dev_key), channel.name];
        BOOL result = [JBSharedChannelDatabase executeUpdate:sqlInsertTable];
        if (result) {
        }
        [JBSharedChannelDatabase close];
    }
}

+(NSMutableArray*)getSortedDevkeyAndChannel{
    NSMutableArray *sortedDevkeyAndChannel = [NSMutableArray array];
    NSMutableArray *allChannels = [JBDatabase getAllChannels];
    for (int i = 0 ; i < allChannels.count; i++) {
        JBChannel *channel = allChannels[i];
        if ([channel.isSubscribed boolValue]) {
            BOOL devExist = NO;
            for (NSMutableDictionary *devChannelDict in sortedDevkeyAndChannel) {
                if (devChannelDict[channel.dev_key]) {
                    NSMutableArray *aChannels = devChannelDict[channel.dev_key];
                    [aChannels addObject:channel];
                    devExist = YES;
                }
            }
            if (!devExist) {
                NSMutableArray *aChannels = [NSMutableArray arrayWithObject:channel];
                NSMutableDictionary *devChannelDict = [NSMutableDictionary dictionaryWithObject:aChannels forKey:channel.dev_key];
                [sortedDevkeyAndChannel addObject:devChannelDict];
            }
        }
    }
    return sortedDevkeyAndChannel;
}

@end


