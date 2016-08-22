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

@property (weak, nonatomic) IBOutlet UILabel *icon_label;
@property (weak, nonatomic) IBOutlet UILabel *channel_label;
@property (weak, nonatomic) IBOutlet UILabel *badge_label;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *badgeHeightConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *badgeLeadingConstraint;

@end

@implementation JBChannelTableViewCell

-(void)setChannel:(JBChannel *)channel{
    _channel = channel;
    self.channel_label.text = channel.name;
    JBMessage *message = [JBDatabase getLastMessage:channel];
    self.title_label.text = message.title;
    self.subscribed = [channel.isTag intValue];
    if ([channel.isTag intValue]) {
        self.icon_label.backgroundColor = [UIColor colorWithHexString:@"#424242"];
    }else{
        self.icon_label.backgroundColor = [UIColor colorWithHexString:@"#c7c7cd"];
    }

    self.badge_label.hidden = YES;

    NSArray *messages = [JBDatabase getUnreadMessagesFromChannel:channel];
    if (messages.count) {
        self.badge_label.hidden = NO;
        self.badge_label.text = [NSString stringWithFormat:@"%lu",(unsigned long)messages.count];
        if (messages.count > 9) {
            self.badgeHeightConstraint.constant = 24;
        }else if (messages.count > 99) {
            self.badge_label.text = @"99+";
            self.badgeHeightConstraint.constant = 30;
        }else{
            self.badgeHeightConstraint.constant = 20;
        }

    }else{
        self.badge_label.hidden = YES;
    }

    self.badgeLeadingConstraint.constant = - self.badgeHeightConstraint.constant/2;

    if ([self.channel.isTag isEqualToString:@"0"]) {
        self.badge_label.hidden = YES;
    }

}

- (void)awakeFromNib {
    [super awakeFromNib];
    self.title_label.text = @"";
    self.badge_label.layer.cornerRadius = 10;
    self.badge_label.layer.masksToBounds = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

-(void)willTransitionToState:(UITableViewCellStateMask)state{
    if (state == UITableViewCellStateShowingDeleteConfirmationMask) {
        dispatch_after(0.001, dispatch_get_main_queue(), ^{
            WEAK_SELF(weakSelf);
            [self.delegate changeEditStyle:weakSelf];
        });
    }
}

@end
