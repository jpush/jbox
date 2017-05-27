//
//  JBScrollViewController.m
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBScrollViewController.h"
#import "JBDatabase.h"

@interface JBScrollViewController ()<UIScrollViewDelegate>

@property (weak, nonatomic) IBOutlet UIPageControl *pageControl;

@end

@implementation JBScrollViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    [[[UIApplication sharedApplication] keyWindow] addSubview:self.view];

    self.automaticallyAdjustsScrollViewInsets = NO;
    self.navigationController.automaticallyAdjustsScrollViewInsets = NO;
    self.view.frame = CGRectMake(- SlideViewWidth, 0, SlideViewWidth, UIScreenHeight);

    self.scrollView.contentSize = CGSizeMake(SlideViewWidth * 2, 0);
    self.scrollView.contentOffset = CGPointMake(0, 0);

    self.teamSlideView = [[NSBundle mainBundle] loadNibNamed:@"JBTeamSlideView" owner:nil options:nil][0];
    [self.scrollView addSubview:_teamSlideView];

    self.channelSlideView = [[NSBundle mainBundle] loadNibNamed:@"JBChannelSlideView" owner:nil options:nil][0];
    if ([JBDatabase getDevkeys].count > 0) {
        self.channelSlideView.devkey = [JBDatabase getDevkeys][0];
    }
    [self.scrollView addSubview:_channelSlideView];
}

-(void)viewDidLayoutSubviews{
    [super viewDidLayoutSubviews];
    self.teamSlideView.frame = CGRectMake(0, 0, SlideViewWidth, UIScreenHeight);
    self.channelSlideView.frame  = CGRectMake(SlideViewWidth, 0, SlideViewWidth, UIScreenHeight);
}

-(void)scrollViewDidScroll:(UIScrollView *)scrollView{
    int page = 0;
    if (scrollView.contentOffset.x > 0) {
        page = 1;
    }else{
        page = 0;
    }
    self.pageControl.currentPage = page;
}

@end
