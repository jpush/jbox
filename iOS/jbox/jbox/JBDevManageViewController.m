//
//  JBAccountViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBDevManageViewController.h"
#import "JBEditChannelTableViewCell.h"

#import "JBScanViewController.h"
#import "JBNetwork.h"
#import "JPUSHService.h"
#import "JBDatabase.h"
#import "JBChannel.h"
#import "JBDevkey.h"

@interface JBDevManageViewController ()<UITableViewDelegate, UITableViewDataSource>

@property(nonatomic, retain)NSMutableArray *channels;
@property (weak, nonatomic) IBOutlet UITableView *channel_tableView;
@property (weak, nonatomic) IBOutlet UIImageView *avatar_imageView;
@property (weak, nonatomic) IBOutlet UILabel *devname_label;
@property (weak, nonatomic) IBOutlet UILabel *desc_label;

@end

@implementation JBDevManageViewController

@synthesize channels = _channels;

-(NSMutableArray *)channels{
    if (!_channels) {
        _channels = [NSMutableArray array];
    }
    return _channels;
}

-(void)setChannels:(NSMutableArray *)channels{
    _channels = channels;
    [_channel_tableView reloadData];
}

-(void)viewDidLoad{
    [super viewDidLoad];

    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"返回" style:UIBarButtonItemStylePlain target:self action:@selector(back)];

    self.automaticallyAdjustsScrollViewInsets = NO;

    self.title = @"订阅频道";
}

-(void)back{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(void)setScanedDevkey:(NSString *)scanedDevkey{
    _scanedDevkey = scanedDevkey;
    if (scanedDevkey && ![scanedDevkey isEqualToString:@""]) {
        NSString *realDevkey = [scanedDevkey componentsSeparatedByString:@"_"][0];

        WEAK_SELF(weakSelf);
        [JBNetwork getDevInfoWithDevkey:realDevkey complete:^(JBDevkey *devkey) {

            [JBDatabase insertDevkey:devkey];

            [weakSelf updateUIWithDevkey:devkey];

            [JBNetwork getChannelsWithDevkey:realDevkey complete:^(id responseObject) {

                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
                NSArray *channelNames = dict[@"channels"];

                [JBDatabase checkAndDeleteChannelsFromDevkey:realDevkey newChannelNames:channelNames];

                for (NSString *name in channelNames) {
                    JBChannel *channel = [JBChannel new];
                    channel.dev_key       = realDevkey;
                    channel.isSubscribed  = @"0";
                    channel.name          = name;
                    [JBDatabase insertChannel:channel];
                }
                weakSelf.channels = [JBDatabase getChannelsFromDevkey:realDevkey];
            }];
        }];

    }
}

-(void)updateUIWithDevkey:(JBDevkey*)devkey{
    self.devname_label.text = devkey.dev_name;
    self.desc_label.text    = [devkey.desc isEqualToString:@""] ? @"这个人很懒什么都没有留下" : devkey.desc;
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@",devkey.avatar]];

    [self.avatar_imageView sd_setImageWithURL:url placeholderImage:[UIImage imageNamed:@"devname_defaultAvatar"]];
}

#pragma mark - tableView
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.channels.count;
}



-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 60;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBEditChannelTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellReuseIdentifier];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBEditChannelTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.channel = self.channels[indexPath.row];
    return cell;
}

@end
