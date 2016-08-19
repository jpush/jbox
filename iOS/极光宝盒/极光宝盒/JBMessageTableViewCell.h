//
//  JBMessageTableViewCell.h
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "JBMessage.h"

@interface JBMessageTableViewCell : UITableViewCell

@property(nonatomic, retain)JBMessage *message;
@property(nonatomic, assign)CGFloat suitableHeight;

@end
