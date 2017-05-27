//
//  JBSlideView2.m
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBTeamSlideView.h"
#import "JBMessageViewController.h"
#import "JBDevManageViewController.h"
#import "JBDevSlideTableViewCell.h"
#import "JBScanViewController.h"
#import "JBDatabase.h"
#import "JBChannelSlideView.h"

@interface JBTeamSlideView ()

- (IBAction)scanBtnPressed:(id)sender;

@end

@implementation JBTeamSlideView

-(void)awakeFromNib{
    [super awakeFromNib];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(shouldUpdate) name:JBDevkeyInserted object:nil];
    [self shouldUpdate];
    if (self.devArray.count > 0) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:0];
        [self.team_tableView selectRowAtIndexPath:indexPath animated:NO scrollPosition:UITableViewScrollPositionNone];
    }
}

-(void)shouldUpdate{
    self.devArray = [JBDatabase getDevkeys];
    NSIndexPath *indexPath = [self.team_tableView indexPathForSelectedRow];
    [self.team_tableView reloadData];
    [self.team_tableView selectRowAtIndexPath:indexPath animated:YES scrollPosition:UITableViewScrollPositionNone];
}

- (IBAction)scanBtnPressed:(id)sender {
    UINavigationController *nav = (UINavigationController*)[UIApplication sharedApplication].keyWindow.rootViewController;
    for (UIViewController *con in nav.viewControllers) {
        if ([con isKindOfClass:[JBMessageViewController class]]) {
            JBMessageViewController *mVC = (JBMessageViewController*)con;
            mVC.isSlideOut = YES;
            [mVC slide:nil];
        }
    }
    JBScanViewController *scan  = [JBScanViewController new];
    [nav pushViewController:scan animated:YES];
}

-(NSMutableArray *)devArray{
    if (!_devArray) {
        _devArray = [JBDatabase getDevkeys];
    }
    return _devArray;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBDevSlideTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"JBDevSlideTableViewCell"];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBDevSlideTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.devkey = self.devArray[indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.devArray.count;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 167;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBDevSlideTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    for (UIView *view in self.superview.subviews) {
        if ([view isKindOfClass:[JBChannelSlideView class]]) {
            JBChannelSlideView *slide = (JBChannelSlideView*)view;
            slide.devkey = cell.devkey;
        }
    }
    UIScrollView *scroll = (UIScrollView*)self.superview;
    [UIView animateWithDuration:0.3 animations:^{
        [scroll setContentOffset:CGPointMake(SlideViewWidth, 0)];
    }];
}


@end
