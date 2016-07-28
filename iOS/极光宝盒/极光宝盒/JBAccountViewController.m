//
//  JBAccountViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBAccountViewController.h"
#import "JBChannelTableViewCell.h"
#import "JBAppsTableViewController.h"

@interface JBAccountViewController ()<UITableViewDelegate, UITableViewDataSource>
- (IBAction)scan_button:(id)sender;
@property (weak, nonatomic) IBOutlet UITableView *channel_tableView;

@end

@implementation JBAccountViewController

#pragma mark - default
- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void)viewWillAppear:(BOOL)animated{
    self.navigationController.navigationBarHidden = YES;
    [super viewWillAppear:animated];
}

-(void)viewWillDisappear:(BOOL)animated{
    self.navigationController.navigationBarHidden = NO;
    [super viewWillDisappear:animated];
}

#pragma mark - tableView
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 20;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 60;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBChannelTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellReuseIdentifier];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBChannelTableViewCell" owner:nil options:nil].lastObject;
    }
    return cell;
}

#pragma mark - scan
- (IBAction)scan_button:(id)sender {
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBAppsTableViewController *appsVC = [[JBAppsTableViewController alloc] init];
    JBChannelTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    appsVC.navigationItem.title = cell.title_label.text;
    [self.navigationController pushViewController:appsVC animated:YES];
}



@end
