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

@interface JBDevSlideTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *avatar_imageView;
@property (weak, nonatomic) IBOutlet UILabel *teamName_label;

@end

@implementation JBDevSlideTableViewCell

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    self.avatar_imageView.alpha   = selected ? 1.0 : 0.4;
    self.teamName_label.textColor = selected ? [UIColor colorWithHexString:@"f9f9f9"] : [UIColor colorWithHexString:@"d3d3d3"];
}

-(void)setDevkey:(JBDevkey *)devkey{
    _devkey = devkey;
    [self.avatar_imageView sd_setImageWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@",devkey.avatar]] placeholderImage:[UIImage imageNamed:@"team_slide_icon"]];
    self.teamName_label.text = devkey.dev_name;
}

@end
