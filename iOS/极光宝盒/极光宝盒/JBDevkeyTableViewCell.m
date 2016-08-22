//
//  JBDevkeyTableViewCell.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/18.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDevkeyTableViewCell.h"
#import "JBNetwork.h"
#import "JBDevkeyManager.h"

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
    self.title_label.text = devkey;
    self.devkey_label.text = [[NSUserDefaults standardUserDefaults] valueForKey:[NSString stringWithFormat:@"%@%@",devkey,JBDevkeyChannelkey]];
    [JBNetwork getDevInfoWithDevkey:devkey complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        self.devkey_label.text = dict[@"dev_name"];
        [[NSUserDefaults standardUserDefaults] setValue:dict[@"dev_name"] forKey:[NSString stringWithFormat:@"%@%@",devkey,JBDevkeyChannelkey]];
    }];
}

@end
