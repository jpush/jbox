//
//  JBSlideTableViewCell2.m
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDevSlideTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>
#import "JBDatabase.h"

@interface JBDevSlideTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *avatar_imageView;
@property (weak, nonatomic) IBOutlet UILabel *teamName_label;
@property (weak, nonatomic) IBOutlet UILabel *badge_label;

@end

@implementation JBDevSlideTableViewCell

-(void)awakeFromNib{
    [super awakeFromNib];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateBadge) name:JBChannelMessageReaded object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateBadge) name:JBChannelMessageInserted object:nil];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    self.avatar_imageView.alpha   = selected ? 1.0 : 0.4;
    self.teamName_label.textColor = selected ? [UIColor colorWithHexString:@"f9f9f9"] : [UIColor colorWithHexString:@"d3d3d3"];
}

-(void)setDevkey:(JBDevkey *)devkey{
    _devkey = devkey;
    [self.avatar_imageView sd_setImageWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@",devkey.avatar]] placeholderImage:[UIImage imageNamed:@"team_slide_icon"]];
    self.teamName_label.text = devkey.dev_name;
    
    [self updateBadge];
}

-(void)updateBadge{
    NSArray *channels = [JBDatabase getChannelsFromDevkey:self.devkey.dev_key];
    NSMutableArray *messages = [NSMutableArray array];
    for (JBChannel *channel in channels) {
        [messages addObjectsFromArray:[JBDatabase getUnreadMessagesFromChannel:channel]];
    }
    self.badge_label.text = [NSString stringWithFormat:@"%lu",(unsigned long)messages.count];
    self.badge_label.hidden = !messages.count;
}

@end
