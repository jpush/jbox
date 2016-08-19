//
//  JBMessageTableViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBChannelTableViewController.h"
#import "JBChannelTableViewCell.h"
#import "JBMessageViewController.h"

#import "JBNetwork.h"
#import "JBDevkeyManager.h"
#import "JBDatabase.h"
#import "JPUSHService.h"
#import "JBMessage.h"
#import <MJRefresh.h>

@interface JBChannelTableViewController ()

@property(nonatomic, retain)NSMutableArray *channels;

@end

@implementation JBChannelTableViewController

-(NSMutableArray *)channels{
    if (!_channels) {
        _channels = [NSMutableArray array];
    }
    return _channels;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.topItem.title = @"消息";

    [self refreshChannels];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveMessage:) name:kJPFNetworkDidReceiveMessageNotification object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshChannels) name:JBChannelTableViewControllerShouldUpdate object:nil];

    WEAK_SELF(weakSelf);
    self.tableView.mj_header = [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        [weakSelf refreshChannels];
        [weakSelf.tableView.mj_header endRefreshing];
    }];
}

-(void)refreshChannels{
    for (NSString *devkey in [JBDevkeyManager getDevkeys]) {
        [JBNetwork getChannelsWithDevkey:devkey complete:^(id responseObject) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
            for (NSString *name in dict[@"channels"]) {
                JBChannel *channel = [JBChannel new];
                channel.name       = name;
                channel.devkey     = devkey;
                channel.isTag      = @"1";
                [JBDatabase insertChannel:channel];
            }
            self.channels = [JBDatabase getAllSubscribedChannels];
            [self.tableView reloadData];
        }];
    }
}

-(void)didReceiveMessage:(NSNotification*)noti{
    NSDictionary *dict = [noti userInfo];
    JBMessage *message = [JBMessage new];
    message.title      = dict[@"title"];
    message.content    = dict[@"content"];
    message.devkey     = dict[@"extras"][@"dev_key"];
    message.channel    = dict[@"extras"][@"channel"];
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:[(NSString*)(dict[@"extras"][@"datetime"]) integerValue]];
    NSDateFormatter *formatter = [NSDateFormatter new];
    [formatter setDateFormat:@"HH:mm"];
    NSString *time = [formatter stringFromDate:date];
    message.time       = time;
    [JBDatabase insertMessages:@[message]];
    [self.tableView reloadData];
    [[NSNotificationCenter defaultCenter] postNotificationName:JBMessageViewControllerShouldUpdate object:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.channels.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    JBChannelTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellReuseIdentifier];
    if (!cell) {
        cell = [[NSBundle mainBundle]loadNibNamed:@"JBChannelTableViewCell" owner:nil options:nil][0];
    }
    cell.channel = self.channels[indexPath.row];
    cell.title_label.text = [JBDatabase getLastMessage:cell.channel];
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 60;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBMessageViewController *messageVC = [[JBMessageViewController alloc] init];
    JBChannelTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    messageVC.channel = cell.channel;
    [self.navigationController pushViewController:messageVC animated:YES];
}
 
@end
