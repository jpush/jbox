//
//  JBAppsTableViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/26.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBAppsTableViewController.h"
#import "JBChannelTableViewCell.h"
#import "JBNetwork.h"
#import "JBChannel.h"
#import "JBDatabase.h"
#import "JPUSHService.h"
#import "JBMessageViewController.h"

@interface JBAppsTableViewController ()<JBChannelTableViewCellChangeEditStyleDelegate>

@property(nonatomic, retain)NSMutableArray *channelsArr;

@end

@implementation JBAppsTableViewController

@synthesize channelsArr = _channelsArr;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.backItem.title = @"返回";
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveMessage) name:kJPFNetworkDidReceiveMessageNotification object:nil];
}

-(void)didReceiveMessage{
    self.devkey = _devkey;
}

-(void)setDevkey:(NSString *)devkey{
    _devkey = devkey;
    WEAK_SELF(weakSelf);
    //每次获取 channels 本地不存在的，保存，并打 tag
    [JBNetwork getChannelsWithDevkey:devkey complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        NSArray *channleNameArr = dict[@"channels"];

        for (NSString *name in channleNameArr) {
            JBChannel *channel = [JBChannel new];
            channel.name   = name;
            channel.devkey = devkey;
            channel.isTag  = @"1";
            [JBDatabase insertChannel:channel];
            NSString *tag = [NSString stringWithFormat:@"%@%@", devkey, name];
            [JPUSHService setTags:[NSSet setWithObject:tag] aliasInbackground:nil];
        }
        weakSelf.channelsArr = [JBDatabase getChannelsFromDevkey:devkey];

    }];
}

-(NSMutableArray*)sortChannelArr:(NSMutableArray*)aChannelArr{
    [aChannelArr sortUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        JBChannel *channel1 = obj1;
        JBChannel *channel2 = obj2;
        return [channel1.isTag intValue] < [channel2.isTag intValue];
    }];
    return aChannelArr;
}

-(void)setChannelsArr:(NSMutableArray *)channelsArr{
    _channelsArr = [self sortChannelArr:channelsArr];
    [self.tableView reloadData];
}

-(NSMutableArray *)channelsArr{
    if (!_channelsArr) {
        _channelsArr = [NSMutableArray array];
    }
    return _channelsArr;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.channelsArr.count;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 60;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    JBChannelTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellReuseIdentifier];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBChannelTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.arrow_imageView.hidden = YES;
    cell.delegate = self;
    cell.channel = self.channelsArr[indexPath.row];
    return cell;
}



// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        JBChannelTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
        JBChannel *channel = cell.channel;
        channel.isTag = cell.subscribed ? @"0" : @"1";
        [JBDatabase updateChannel:channel];
        cell.subscribed = !cell.subscribed;
        NSSet *set = [NSSet new];
        [JPUSHService setTags:set alias:nil fetchCompletionHandle:^(int iResCode, NSSet *iTags, NSString *iAlias) {
            NSArray *channels = [JBDatabase getChannelsFromDevkey:channel.devkey];
            NSMutableArray *tags = [NSMutableArray array];
            for (JBChannel *channel in channels) {
                [tags addObject:[NSString stringWithFormat:@"%@%@",cell.channel.devkey,channel.name]];
            }
            NSSet *set = [NSSet setWithArray:tags];
            [JPUSHService setTags:set aliasInbackground:nil];
        }];
        [self.tableView setEditing:NO animated:YES];
    }
    self.channelsArr = [self sortChannelArr:_channelsArr];
    [self.tableView reloadData];
    [[NSNotificationCenter defaultCenter] postNotificationName:JBChannelTableViewControllerShouldUpdate object:nil];
}

-(void)changeEditStyle:(JBChannelTableViewCell *)cell{
    for (UIView *view in cell.subviews) {
        if ([[[view class] description] isEqualToString:@"UITableViewCellDeleteConfirmationView"]) {
            UIButton *button = view.subviews[0];
            if (!cell.subscribed) {
                button.backgroundColor = [UIColor colorWithHexString:@"ffcd00"];
                button.titleLabel.text = @"订阅";
            }else{
                button.backgroundColor = [UIColor colorWithHexString:@"ff3b30"];
                button.titleLabel.text = @"删除";
            }
        }
    }
}

-(NSString *)tableView:(UITableView *)tableView titleForDeleteConfirmationButtonForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBChannelTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    if (!cell.subscribed) {
        return @"订阅";
    }else{
        return @"删除";
    }
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.devkey = _devkey;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBChannelTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    JBMessageViewController *msgVC = [JBMessageViewController new];
    msgVC.channel = cell.channel;
    [self.navigationController pushViewController:msgVC animated:YES];
}

@end
