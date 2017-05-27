//
//  JBMessageViewController.h
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "JBChannel.h"
#import "JBScrollViewController.h"

@interface JBMessageViewController : UIViewController
@property(nonatomic, retain)JBChannel *channel;
@property(nonatomic, assign)BOOL isSlideOut;
-(void)slide:(UIGestureRecognizer*)gesture;
@property (weak, nonatomic) IBOutlet UITableView *message_tableView;
@property(nonatomic, retain)JBScrollViewController *scrollViewController;

@end
