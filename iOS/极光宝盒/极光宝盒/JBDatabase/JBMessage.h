//
//  JBMessage.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JBMessage : NSObject
@property(nonatomic, retain)NSString *title;
@property(nonatomic, retain)NSString *content;
@property(nonatomic, retain)NSString *devkey;
@property(nonatomic, retain)NSString *channel;
@property(nonatomic, retain)NSString *time;
@property(nonatomic, retain)NSString *read;
@end
