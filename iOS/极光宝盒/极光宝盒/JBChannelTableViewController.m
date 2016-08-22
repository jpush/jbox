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

@synthesize channels = _channels;

-(NSMutableArray *)channels{
    if (!_channels) {
        _channels = [NSMutableArray array];
    }
    return _channels;
}

-(void)setChannels:(NSMutableArray *)channels{
    _channels = [self sortChannel:channels];
    [self.tableView reloadData];
}

- (void)viewDidLoad {
    [super viewDidLoad];

    [self refreshChannels];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveMessage:) name:kJPFNetworkDidReceiveMessageNotification object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshChannels) name:JBChannelTableViewControllerShouldUpdate object:nil];

    WEAK_SELF(weakSelf);
    self.tableView.mj_header = [MJRefreshNormalHeader headerWithRefreshingBlock:^{
        [weakSelf refreshChannels];
        [weakSelf.tableView.mj_header endRefreshing];
    }];
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.topItem.title = @"消息";
    [self refreshChannels];
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
            NSMutableArray *getChannels = [JBDatabase getAllSubscribedChannels];
            NSMutableSet *set = [NSMutableSet set];
            for (JBChannel *channel in getChannels) {
                [set addObject:[NSString stringWithFormat:@"%@%@",channel.devkey,channel.name]];
            }
            [JPUSHService setTags:set aliasInbackground:nil];
            self.channels = getChannels;
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
    message.time       = dict[@"extras"][@"datetime"];
    message.read       = @"0";
    [JBDatabase insertMessages:@[message]];
    [self.tableView reloadData];
    [[NSNotificationCenter defaultCenter] postNotificationName:JBMessageViewControllerShouldUpdate object:nil];
    self.channels = [JBDatabase getAllSubscribedChannels];
    [self.tableView reloadData];
}

-(NSMutableArray*)sortChannel:(NSMutableArray*)channels{
    [channels sortUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        JBChannel *channel1 = obj1;
        JBChannel *channel2 = obj2;
        JBMessage *message1 = [JBDatabase getLastMessage:channel1];
        JBMessage *message2 = [JBDatabase getLastMessage:channel2];
        return [message1.time intValue] < [message2.time intValue];
    }];
    return channels;
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
