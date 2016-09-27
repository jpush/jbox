//
//  JBSlideTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/9/5.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBSlideTableViewCell.h"
#import "JBDatabase.h"
#import "JBSlideView.h"

@interface JBSlideTableViewCell ()

@property (weak, nonatomic) IBOutlet UIButton *delete_button;
@property (weak, nonatomic) IBOutlet UILabel *name_label;
@property (weak, nonatomic) IBOutlet UILabel *count_label;
- (IBAction)deleteChannel:(UIButton *)sender;

@end

@implementation JBSlideTableViewCell

-(void)setChannel:(JBChannel *)channel{
    _channel = channel;
    self.name_label.text  = channel.name;
    self.delete_button.hidden = YES;
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

-(void)becomeEditing{
    self.delete_button.hidden = NO;
}

- (IBAction)deleteChannel:(UIButton *)sender {
    self.channel.isSubscribed = @"0";
    [JBDatabase updateChannel:self.channel];
    [(JBSlideView*)self.superview.superview shouldUpdate];
}

@end
