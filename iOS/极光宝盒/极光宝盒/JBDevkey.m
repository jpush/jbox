//
//  JBDevkey.m
//  极光宝盒
//
//  Created by wuxingchen on 16/9/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDevkey.h"

@implementation JBDevkey

-(void)setValue:(id)value forUndefinedKey:(NSString *)key{

}

-(void)encodeWithCoder:(NSCoder *)aCoder{
    [aCoder encodeObject:self.dev_key  forKey:@"dev_key"];
    [aCoder encodeObject:self.dev_name forKey:@"dev_name"];
    [aCoder encodeObject:self.desc     forKey:@"desc"];
    [aCoder encodeObject:self.avatar   forKey:@"avatar"];
    [aCoder encodeObject:self.platform forKey:@"platform"];
}


- (instancetype)initWithCoder:(NSCoder *)coder{
    self = [super init];
    if (self) {
        self.dev_key  = [coder decodeObjectForKey:@"dev_key"];
        self.dev_name = [coder decodeObjectForKey:@"dev_name"];
        self.desc     = [coder decodeObjectForKey:@"desc"];
        self.avatar   = [coder decodeObjectForKey:@"avatar"];
        self.platform = [coder decodeObjectForKey:@"platform"];
    }
    return self;
}

@end
