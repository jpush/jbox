//
//  JBEditChannelTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/18.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBEditChannelTableViewCell.h"
#import "JBNetwork.h"
#import "JBDatabase.h"

@interface JBEditChannelTableViewCell ()

@property (weak, nonatomic) IBOutlet UILabel *icon_label;
@property (weak, nonatomic) IBOutlet UILabel *name_label;
@property (weak, nonatomic) IBOutlet UIButton *subscribe_button;
- (IBAction)subscribeBtnPressed:(UIButton *)sender;

@property(nonatomic, assign)BOOL isSubscribe;

@end

@implementation JBEditChannelTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    self.subscribe_button.titleLabel.textAlignment = NSTextAlignmentLeft;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

}

-(void)setChannel:(JBChannel *)channel{
    _channel = channel;
    self.name_label.text = channel.name;
    self.isSubscribe = [channel.isSubscribed boolValue];
    self.icon_label.text = [channel.name substringToIndex:1];
}

-(void)setIsSubscribe:(BOOL)isSubscribe{
    _isSubscribe = isSubscribe;
    if (isSubscribe) {
        [self.subscribe_button setImage:[UIImage imageNamed:@"channel_subscribe"] forState:UIControlStateNormal];
        [self.subscribe_button setTitle:@"" forState:UIControlStateNormal];
        self.icon_label.backgroundColor = [UIColor colorWithHexString:@"424242"];
    }else{
        [self.subscribe_button setImage:nil forState:UIControlStateNormal];
        [self.subscribe_button setTitle:@"订阅" forState:UIControlStateNormal];
        self.icon_label.backgroundColor = [UIColor colorWithHexString:@"c7c7cd"];
    }
    self.channel.isSubscribed = isSubscribe ? @"1" : @"0";
    [JBDatabase updateChannel:self.channel];
}

- (IBAction)subscribeBtnPressed:(UIButton *)sender {
    self.isSubscribe = !self.isSubscribe;
    self.channel.isSubscribed = self.isSubscribe ? @"1" : @"0";
    //重新打所有 tag
    [JBDatabase updateChannel:self.channel];
}

@end
