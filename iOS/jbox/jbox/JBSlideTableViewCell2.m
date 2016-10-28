//
//  JBSlideTableViewCell2.m
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBSlideTableViewCell2.h"

@interface JBSlideTableViewCell2()

@property (weak, nonatomic) IBOutlet UIImageView *avatar_imageView;
@property (weak, nonatomic) IBOutlet UILabel *devname_label;

@end

@implementation JBSlideTableViewCell2

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
