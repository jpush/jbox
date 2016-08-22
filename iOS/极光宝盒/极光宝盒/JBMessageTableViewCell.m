//
//  JBMessageTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBMessageTableViewCell.h"
#import "UILabel+ContentSize.h"

@interface JBMessageTableViewCell()

@property (weak, nonatomic) IBOutlet UILabel *content_label;
@property (weak, nonatomic) IBOutlet UILabel *title_label;
@property (weak, nonatomic) IBOutlet UILabel *icon_label;
@property (weak, nonatomic) IBOutlet UILabel *time_label;

@end

@implementation JBMessageTableViewCell

-(void)setMessage:(JBMessage *)message{
    _message = message;
    self.content_label.text = message.content;
    self.title_label.text   = message.title;
    CGSize size = [self.content_label caculatedSize];
    self.suitableHeight = 60 + size.height - 16;

    NSDate *date = [NSDate dateWithTimeIntervalSince1970:[message.time integerValue]];
    NSDateFormatter *formatter = [NSDateFormatter new];
    [formatter setDateFormat:@"HH:mm"];
    NSString *time = [formatter stringFromDate:date];
    self.time_label.text    = time;
}

- (void)awakeFromNib {
    [super awakeFromNib];
    self.suitableHeight = 60;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
