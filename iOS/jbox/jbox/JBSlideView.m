//
//  JBSlideView.m
//  极光宝盒
//
//  Created by wuxingchen on 16/9/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBSlideView.h"

#import "JBSlideTableViewCell.h"
#import "JBScanViewController.h"
#import "JBDatabase.h"
#import "JBMessageViewController.h"

@interface JBSlideView ()

- (IBAction)editBtnPressed:(UIButton *)sender;
@property(nonatomic, retain)NSMutableArray *tableViewDataArray;
@property(nonatomic, retain)UIButton *clearBtn;

@end

@implementation JBSlideView

-(void)awakeFromNib{
    [super awakeFromNib];

    self.tableViewDataArray = [JBDatabase getSortedDevkeyAndChannel];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(shouldUpdate) name:JBSlideViewShouldUpdate object:nil];

    self.isEditing = NO;
}

-(void)shouldUpdate{
    self.tableViewDataArray = [JBDatabase getSortedDevkeyAndChannel];
    [self.channel_tableView reloadData];
}

- (IBAction)editBtnPressed:(UIButton *)sender {
    self.isEditing = !self.isEditing;
    [self.channel_tableView reloadData];
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return self.tableViewDataArray.count == 0 ? 1 : self.tableViewDataArray.count;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    NSInteger count = 0;
    if (self.tableViewDataArray.count) {
        count = ((NSArray*)((NSDictionary*)self.tableViewDataArray[section]).allValues.lastObject).count;
    }
    return count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBSlideTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"JBSlideTableViewCell"];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBSlideTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.channel = ((NSArray*)[(NSDictionary*)self.tableViewDataArray[indexPath.section] allValues].lastObject)[indexPath.row];

//    self.isEditing ? [cell becomeToEditing] : [cell endToEditing];
    
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 44;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBMessageViewController *vc = (JBMessageViewController*)JBSharedNavigationController.viewControllers[0];
    [vc slide:nil];
    JBSlideTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    vc.channel = cell.channel;
}

@end
