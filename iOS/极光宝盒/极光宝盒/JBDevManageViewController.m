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
}

-(void)back{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

-(void)setScanedDevkey:(NSString *)scanedDevkey{
    _scanedDevkey = scanedDevkey;
    NSString *realDevkey = [scanedDevkey componentsSeparatedByString:@"_"][0];

    if ([JBDatabase devkeyInDatabase:realDevkey]) {
        [self updateUIWithDevkey:[JBDatabase getDevkeyInfoWithDevkey:realDevkey]];
        self.channels = [JBDatabase getChannelsFromDevkey:realDevkey];
    }

    WEAK_SELF(weakSelf);
    [JBNetwork getDevInfoWithDevkey:realDevkey complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        JBDevkey *devkey = [JBDevkey new];
        [devkey setValuesForKeysWithDictionary:dict];
        [JBDatabase insertDevkey:devkey];

        [weakSelf updateUIWithDevkey:devkey];

        [JBNetwork getChannelsWithDevkey:realDevkey complete:^(id responseObject) {
            NSArray *channels = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];

            [JBDatabase checkAndDeleteChannelsFromDevkey:realDevkey newChannels:channels];

            for (NSDictionary *dict in channels) {
                JBChannel *channel = [JBChannel new];
                [channel setValuesForKeysWithDictionary:dict];
                channel.dev_key       = realDevkey;
                channel.isSubscribed  = @"0";
                channel.name          = dict[@"channel"];
                [JBDatabase insertChannel:channel];
            }
            weakSelf.channels = [JBDatabase getChannelsFromDevkey:realDevkey];
        }];
    }];
}

-(void)updateUIWithDevkey:(JBDevkey*)devkey{
    self.devname_label.text     = devkey.dev_name;
    self.desc_label.text        = devkey.desc;
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
