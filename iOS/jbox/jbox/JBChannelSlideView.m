//
//  JBChannelSlideView.m
//  极光宝盒
//
//  Created by wuxingchen on 16/9/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBChannelSlideView.h"

#import "JBChannelSlideTableViewCell.h"
#import "JBScanViewController.h"
#import "JBDatabase.h"
#import "JBMessageViewController.h"
#import "JBDevManageViewController.h"

@interface JBChannelSlideView ()

- (IBAction)editBtnPressed:(UIButton *)sender;
@property(nonatomic, retain)NSMutableArray *tableViewDataArray;
@property(nonatomic, retain)UIButton *clearBtn;

@end

@implementation JBChannelSlideView

-(void)setDevkey:(JBDevkey *)devkey{
    _devkey = devkey;
    [self shouldUpdate];
    if (self.tableViewDataArray.count > 0) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:0];
        [self.channel_tableView selectRowAtIndexPath:indexPath animated:NO scrollPosition:UITableViewScrollPositionNone];
    }
}

-(void)awakeFromNib{
    [super awakeFromNib];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(shouldUpdate) name:JBChannelUpdated object:nil];

    [self shouldUpdate];
}

-(void)shouldUpdate{
    self.tableViewDataArray = [JBDatabase getSubscribedChannelsFromDevkey:_devkey.dev_key];
    [self.channel_tableView reloadData];
}

- (IBAction)editBtnPressed:(UIButton *)sender {
    JBDevManageViewController *controller = [[JBDevManageViewController alloc] initWithNibName:@"JBDevManageViewController" bundle:[NSBundle mainBundle]];
    controller.scanedDevkey = self.devkey.dev_key;
    JBMessageViewController *vc = ((UINavigationController*)[[UIApplication sharedApplication] keyWindow].rootViewController).viewControllers[0];
    [vc slide:nil];
    [(UINavigationController*)[[UIApplication sharedApplication] keyWindow].rootViewController pushViewController:controller animated:YES];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.tableViewDataArray.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBChannelSlideTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"JBChannelSlideTableViewCell"];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBChannelSlideTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.channel = self.tableViewDataArray[indexPath.row];
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 44;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBMessageViewController *vc = (JBMessageViewController*)JBSharedNavigationController.viewControllers[0];
    [vc slide:nil];
    JBChannelSlideTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    vc.channel = cell.channel;
}

@end
