//
//  JBMessageTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBMessageTableViewCell.h"
#import "UILabel+ContentSize.h"
#import "JBDatabase.h"
#import "JBNetwork.h"

@interface JBMessageTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *icon_imageView;
@property (weak, nonatomic) IBOutlet UILabel *content_label;
@property (weak, nonatomic) IBOutlet UILabel *title_label;
@property (weak, nonatomic) IBOutlet UILabel *time_label;

@end

@implementation JBMessageTableViewCell

-(CGFloat)suitableHeight{
    [self setNeedsLayout];
    if (_suitableHeight < 60) {
        return 60;
    }else{
        CGSize size = [self.content_label caculatedSize];
        _suitableHeight = 60 + size.height - 16;
        return _suitableHeight;
    }
}

-(void)setMessage:(JBMessage *)message{
    _message = message;
    self.content_label.text = [NSString stringWithFormat:@"%@：%@",message.integation_name,message.content];
    self.title_label.text   = message.title;
    CGSize size = [self.content_label caculatedSize];
    self.suitableHeight = 60 + size.height - 16;

    NSDate *date = [NSDate dateWithTimeIntervalSince1970:[message.time integerValue]];
    NSDateFormatter *formatter = [NSDateFormatter new];
    [formatter setDateFormat:@"HH:mm"];
    NSString *time = [formatter stringFromDate:date];
    self.time_label.text    = time;
    if ([message.icon isEqualToString:@""]) {
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.icon_imageView.frame.size.width, self.icon_imageView.frame.size.height)];
        label.text = [message.content substringToIndex:1];
        label.textColor = [UIColor blackColor];
        label.font = [UIFont systemFontOfSize:36];
        [self.icon_imageView addSubview:label];
    }else{
        NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@",message.icon]];
        [self.icon_imageView sd_setImageWithURL:url];
    }
}

-(void)awakeFromNib{
    [super awakeFromNib];
    self.content_label.frame = CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width - 85, 16);
}

@end
