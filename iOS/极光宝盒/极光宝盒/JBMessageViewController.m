//
//  JBMessageViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBMessageViewController.h"
#import "JBMessageTableViewCell.h"
#import "JBMessage.h"
#import "JBDatabase.h"

@interface JBMessageViewController ()<UITableViewDataSource, UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITableView *message_tableView;
@property(nonatomic ,retain)NSArray *messageArray;

@end

@implementation JBMessageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.backItem.title = @"返回";
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(update) name:JBMessageViewControllerShouldUpdate object:nil];
    [self update];
}

-(void)setChannel:(JBChannel *)channel{
    _channel = channel;
    [NSTimer scheduledTimerWithTimeInterval:0.01 target:self selector:@selector(update) userInfo:nil repeats:NO];
}

-(void)update{
    self.messageArray = [JBDatabase getMessagesFromChannel:_channel];
    [self.message_tableView reloadData];
    if (self.messageArray.count > 0) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:self.messageArray.count - 1 inSection:0];
        [self.message_tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
        [self.view layoutIfNeeded];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(NSArray *)messageArray{
    if (!_messageArray) {
        _messageArray = [NSArray array];
    }
    return _messageArray;
}

#pragma mark - tableView
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.messageArray.count;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBMessageTableViewCell *cell = (JBMessageTableViewCell*)[self tableView:_message_tableView cellForRowAtIndexPath:indexPath];
    if (!cell) {
        return 60;
    }
    return cell.suitableHeight;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBMessageTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellReuseIdentifier];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBMessageTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.message = self.messageArray[indexPath.row];
    return cell;
}

@end
