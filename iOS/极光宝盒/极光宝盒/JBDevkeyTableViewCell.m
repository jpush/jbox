//
//  JBDevkeyTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/18.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDevkeyTableViewCell.h"

@interface JBDevkeyTableViewCell ()

@property (weak, nonatomic) IBOutlet UILabel *icon_label;
@property (weak, nonatomic) IBOutlet UILabel *devkey_label;
@property (weak, nonatomic) IBOutlet UILabel *title_label;

@end

@implementation JBDevkeyTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    self.title_label.text = @"";
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

}


-(void)setDevkey:(NSString *)devkey{
    _devkey = devkey;
    self.devkey_label.text = devkey;
}

@end
