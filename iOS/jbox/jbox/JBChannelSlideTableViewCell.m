//
//  JBChannelSlideTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/9/5.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBChannelSlideTableViewCell.h"
#import "JBDatabase.h"
#import "JBChannelSlideView.h"
#import "JBMessageViewController.h"

@interface JBChannelSlideTableViewCell ()

@property (weak, nonatomic) IBOutlet UILabel *name_label;
@property (weak, nonatomic) IBOutlet UILabel *count_label;

@end

@implementation JBChannelSlideTableViewCell

-(void)setChannel:(JBChannel *)channel{
    _channel = channel;
    self.name_label.text  = channel.name;
    self.backgroundColor = [UIColor clearColor];
    self.contentView.backgroundColor = [UIColor clearColor];
    [self updateCount];
}

- (void)awakeFromNib {
    [super awakeFromNib];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(messagesReaded) name:JBChannelMessagesReaded object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateCount) name:JBSlideTableViewCellShouldUpdate object:nil];
}

-(void)updateCount{
    if ([JBDatabase getUnreadMessagesFromChannel:self.channel].count) {
        self.count_label.text = [NSString stringWithFormat:@"%lu",(unsigned long)[JBDatabase getUnreadMessagesFromChannel:self.channel].count];
        self.count_label.hidden = NO;
    }else{
        self.count_label.hidden = YES;
    }
}

-(void)messagesReaded{
    self.count_label.hidden = YES;
    self.count_label.text   = @"";
}

-(void)setSelected:(BOOL)selected animated:(BOOL)animated{
    [super setSelected:selected animated:animated];
    if (selected) {
        JBMessageViewController *vc = (JBMessageViewController*)JBSharedNavigationController.viewControllers[0];
        vc.channel = self.channel;
    }
}

@end
