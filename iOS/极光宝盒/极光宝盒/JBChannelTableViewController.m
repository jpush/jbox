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
#import "JBSharedDevkey.h"

#import "JPUSHService.h"

@interface JBChannelTableViewController ()

@property(nonatomic, retain)NSArray *channels;

@end

@implementation JBChannelTableViewController

-(NSArray *)channels{
    if (!_channels) {
        _channels = [NSArray array];
        self.channels = _channels;
    }
    return _channels;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.topItem.title = @"消息";

    [JBSharedDevkey saveDevkey:@"hVkbyLdeA7K0Cm9BUgY6"];

    WEAK_SELF(weakSelf);
    [JBNetwork getChannelsWithDevkey:[JBSharedDevkey getDevkey] complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        weakSelf.channels = dict[@"channels"];
        [weakSelf.tableView reloadData];
    }];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveMessage:) name:kJPFNetworkDidReceiveMessageNotification object:nil];


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
    cell.channel_label.text = self.channels[indexPath.row];
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 60;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBMessageViewController *messageVC = [[JBMessageViewController alloc] init];
    [self.navigationController pushViewController:messageVC animated:YES];
}
 
@end
