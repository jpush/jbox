//
//  JBDevkey.h
//  极光宝盒
//
//  Created by wuxingchen on 16/9/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JBDevkey : NSObject <NSCoding>

@property(nonatomic, retain)NSString *dev_key;
@property(nonatomic, retain)NSString *dev_name;
@property(nonatomic, retain)NSString *desc;
@property(nonatomic, retain)NSString *avatar;
@property(nonatomic, retain)NSString *platform;

@end
