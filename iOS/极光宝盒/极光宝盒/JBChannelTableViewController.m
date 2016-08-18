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

@interface JBChannelTableViewController ()

@property(nonatomic, retain)NSMutableArray *channels;

@end

@implementation JBChannelTableViewController

-(NSMutableArray *)channels{
    if (!_channels) {
        _channels = [NSMutableArray array];
        self.channels = _channels;
    }
    return _channels;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.topItem.title = @"消息";

    [self refreshChannels];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveMessage:) name:kJPFNetworkDidReceiveMessageNotification object:nil];
}

-(void)refreshChannels{
    WEAK_SELF(weakSelf);
    [JBNetwork getChannelsWithDevkeys:[JBDevkeyManager getDevkeys] complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        [weakSelf.channels addObjectsFromArray:dict[@"channels"]];
        [weakSelf.tableView reloadData];
    }];
}

-(void)didReceiveMessage:(NSNotification*)noti{
    NSLog(@"%@",noti);
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
    messageVC.messageArray = [JBDatabase getMessagesFromChannel:cell.channel];
    [self.navigationController pushViewController:messageVC animated:YES];
}
 
@end
