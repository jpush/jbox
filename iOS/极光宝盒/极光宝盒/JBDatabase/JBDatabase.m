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
#define JBSharedDatabase [JBDatabase sharedDatabase]

@implementation JBDatabase

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

+(void)createChannelWithName:(NSString*)name{
    if ([JBSharedDatabase open]) {
        NSString *sqlCreateTable = [NSString stringWithFormat:@"create table if not exists %@ (id integer primary key autoincrement ,title text ,message text)", name];
        BOOL result = [JBSharedDatabase executeUpdate:sqlCreateTable];
        if (result) {

        }
        [JBSharedDatabase close];
    }
}

+(void)insertToChannel:(NSString*)channel Messages:(NSMutableArray*)mArray{
    if ([JBSharedDatabase open]) {
        for (JBMessage *message in mArray) {
            NSString *sqlInsertTable = [NSString stringWithFormat:@"insert into %@ (title,message) values ('%@','%@')",channel, message.title, message.message];
            BOOL result = [JBSharedDatabase executeUpdate:sqlInsertTable];
            if (result) {
            }
        }
        [JBSharedDatabase close];
    }
}

+(NSMutableArray*)getMessagesFromChannel:(NSString*)channel{
    NSMutableArray *modelArray = [NSMutableArray array];
    if ([JBSharedDatabase open]) {
        NSString *sqlSelect = [NSString stringWithFormat:@"select * from %@", channel];
        FMResultSet *set = [JBSharedDatabase executeQuery:sqlSelect];
        while ([set next]) {
            JBMessage *message = [JBMessage new];
            message.title = [set stringForColumn:@"title"];
            message.message = [set stringForColumn:@"message"];
            [modelArray addObject:message];
        }
        [JBSharedDatabase close];
    }
    return modelArray;
}

+(void)clearChannelWithName:(NSString*)name{
    NSString *sqlstr = [NSString stringWithFormat:@"DELETE FROM %@", name];
    [JBSharedDatabase executeUpdate:sqlstr];
}

+(void)deleteChannelWithName:(NSString*)name{
    NSString *sqlstr = [NSString stringWithFormat:@"DROP TABLE %@", name];
    [JBSharedDatabase executeUpdate:sqlstr];
}

@end


