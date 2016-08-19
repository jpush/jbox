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

@end

@implementation JBChannelTableViewCell

-(void)setChannel:(JBChannel *)channel{
    _channel = channel;
    self.channel_label.text = channel.name;
    JBMessage *message = [JBDatabase getMessagesFromChannel:channel].lastObject;
    self.title_label.text = message.title;
    self.subscribed = [channel.isTag intValue];
}

- (void)awakeFromNib {
    [super awakeFromNib];
    self.title_label.text = @"";
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
