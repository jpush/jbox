//
//  JBSlideHeaderView.m
//  极光宝盒
//
//  Created by wuxingchen on 16/9/5.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBSlideHeaderView.h"
#import "JBDevManageViewController.h"
#import "JBMessageViewController.h"

@interface JBSlideHeaderView ()

@property (weak, nonatomic) IBOutlet UILabel *devname_label;
- (IBAction)showDetailPressed:(UIButton *)sender;

@end

@implementation JBSlideHeaderView

- (IBAction)showDetailPressed:(UIButton *)sender {
    [self push];
}

-(void)setDevkey:(JBDevkey *)devkey{
    _devkey = devkey;
    self.devname_label.text = devkey.dev_name;
}

-(void)awakeFromNib{
    [super awakeFromNib];
    UIView *view = [[UIView alloc] initWithFrame:self.frame];
    view.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
    [self setBackgroundView:view];

    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(push)];
    [self addGestureRecognizer:tap];
}

-(void)push{
    JBDevManageViewController *controller = [[JBDevManageViewController alloc] initWithNibName:@"JBDevManageViewController" bundle:[NSBundle mainBundle]];
    controller.scanedDevkey = self.devkey.dev_key;
    JBMessageViewController *vc = ((UINavigationController*)[[UIApplication sharedApplication] keyWindow].rootViewController).viewControllers[0];
    [vc slide:nil];
    [(UINavigationController*)[[UIApplication sharedApplication] keyWindow].rootViewController pushViewController:controller animated:YES];
}

@end
