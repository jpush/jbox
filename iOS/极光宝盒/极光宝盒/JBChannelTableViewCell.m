//
//  JBChannelTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBChannelTableViewCell.h"

IB_DESIGNABLE
@interface JBChannelTableViewCell ()

@property (weak, nonatomic) IBOutlet UILabel *icon_label;
@property (weak, nonatomic) IBOutlet UILabel *channel_label;

@end

@implementation JBChannelTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
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
