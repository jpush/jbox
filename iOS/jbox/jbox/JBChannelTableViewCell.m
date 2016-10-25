//
//  JBChannelTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBChannelTableViewCell.h"
#import "JBDatabase.h"

IB_DESIGNABLE
@interface JBChannelTableViewCell ()

@property (weak, nonatomic) IBOutlet UIImageView *icon_imageView;
@property (weak, nonatomic) IBOutlet UILabel *channel_label;
@property (weak, nonatomic) IBOutlet UILabel *title_label;

@end

@implementation JBChannelTableViewCell

-(void)setChannel:(JBChannel *)channel{


//    self.badge_label.hidden = YES;
//
//    NSArray *messages = [JBDatabase getUnreadMessagesFromChannel:channel];
//    if (messages.count) {
//        self.badge_label.hidden = NO;
//        self.badge_label.text = [NSString stringWithFormat:@"%lu",(unsigned long)messages.count];
//        if (messages.count > 9) {
//            self.badgeHeightConstraint.constant = 24;
//        }else if (messages.count > 99) {
//            self.badge_label.text = @"99+";
//            self.badgeHeightConstraint.constant = 30;
//        }else{
//            self.badgeHeightConstraint.constant = 20;
//        }
//
//    }else{
//        self.badge_label.hidden = YES;
//    }
//
//    self.badgeLeadingConstraint.constant = - self.badgeHeightConstraint.constant/2;
//
//    if ([self.channel.isSubscribed isEqualToString:@"0"]) {
//        self.badge_label.hidden = YES;
//    }

}

@end
