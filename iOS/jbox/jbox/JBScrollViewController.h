//
//  JBScrollViewController.h
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "JBTeamSlideView.h"
#import "JBChannelSlideView.h"

@interface JBScrollViewController : UIViewController

@property(nonatomic, retain)JBTeamSlideView *teamSlideView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property(nonatomic, retain)JBChannelSlideView *channelSlideView;

@end
