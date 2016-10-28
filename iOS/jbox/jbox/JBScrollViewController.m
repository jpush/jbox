//
//  JBScrollViewController.m
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBScrollViewController.h"
#import "JBSlideView.h"
#import "JBSlideView2.h"

@interface JBScrollViewController ()<UIScrollViewDelegate>

@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property(nonatomic, retain)JBSlideView *slideView;
@property(nonatomic, retain)JBSlideView2 *slideView2;
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

    self.slideView2 = [[NSBundle mainBundle] loadNibNamed:@"JBSlideView2" owner:nil options:nil][0];
    [self.scrollView addSubview:_slideView2];

    self.slideView = [[NSBundle mainBundle] loadNibNamed:@"JBSlideView" owner:nil options:nil][0];
    [self.scrollView addSubview:_slideView];

}

-(void)viewDidLayoutSubviews{
    [super viewDidLayoutSubviews];
    self.slideView2.frame = CGRectMake(0, 0, SlideViewWidth, UIScreenHeight);
    self.slideView.frame  = CGRectMake(SlideViewWidth, 0, SlideViewWidth, UIScreenHeight);
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
