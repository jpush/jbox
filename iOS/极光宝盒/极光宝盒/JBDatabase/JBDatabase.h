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
+(void)insertMessages:(NSMutableArray*)mArray;
+(NSMutableArray*)getMessagesFromChannel:(JBChannel*)channel;



@end
