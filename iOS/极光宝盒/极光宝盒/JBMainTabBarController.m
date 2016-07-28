//
//  JBMessageTabBarController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/19.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBMainTabBarController.h"
#import "JBAccountViewController.h"

@implementation JBMainTabBarController

-(void)viewDidLoad{
    [[UITabBarItem appearance] setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], NSForegroundColorAttributeName, nil] forState:UIControlStateNormal];
    [[UITabBarItem appearance] setTitleTextAttributes:                                                         [NSDictionary dictionaryWithObjectsAndKeys:[UIColor colorWithHexString:@"8b8ece"],NSForegroundColorAttributeName, nil]forState:UIControlStateSelected];
}

-(void)awakeFromNib{
    [[NSBundle mainBundle] loadNibNamed:@"JBAccountViewController" owner:self.viewControllers[1] options:nil];
    [super awakeFromNib];
}


@end
