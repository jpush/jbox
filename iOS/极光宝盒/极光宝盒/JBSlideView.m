//
//  JBSlideView.m
//  极光宝盒
//
//  Created by wuxingchen on 16/9/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBSlideView.h"

#import "JBSlideHeaderView.h"
#import "JBSlideTableViewCell.h"
#import "JBScanViewController.h"
#import "JBDatabase.h"
#import "JBMessageViewController.h"

@interface JBSlideView ()<UISearchBarDelegate>


- (IBAction)editBtnPressed:(UIButton *)sender;
- (IBAction)scanBtnPressed:(UIButton *)sender;

@property(nonatomic, retain)NSMutableArray *tableViewDataArray;

@property(nonatomic, retain)UIButton *clearBtn;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *tableViewContraintBottom;

@end

@implementation JBSlideView

-(void)awakeFromNib{
    [super awakeFromNib];
    
    self.tableViewDataArray = [JBDatabase getSortedDevkeyAndChannel];

    self.searchBar.delegate = self;

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(shouldUpdate) name:JBSlideViewShouldUpdate object:nil];

    self.isEditing = NO;

    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
    tap.cancelsTouchesInView = NO;
    [self.channel_tableView addGestureRecognizer:tap];
}

-(void)tap{
    [self searchBarCancelButtonClicked:_searchBar];
}

-(void)shouldUpdate{
    self.tableViewDataArray = [JBDatabase getSortedDevkeyAndChannel];
    [self.channel_tableView reloadData];
}

-(void)keyboardWasShown:(NSNotification*)noti{
    NSDictionary *dict = [noti userInfo];
    NSValue *value     = [dict valueForKey:UIKeyboardFrameBeginUserInfoKey];
    CGRect frame       = [value CGRectValue];
    self.tableViewContraintBottom.constant = frame.size.height - 45;
}

-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    self.tableViewContraintBottom.constant = 15;
    [searchBar resignFirstResponder];
}

-(void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    if (searchText.length) {
        self.tableViewDataArray = [JBDatabase getSortedDevkeyAndChannel];
        for (int i = 0; i < self.tableViewDataArray.count; i++) {
            NSDictionary *dict = self.tableViewDataArray[i];
            NSMutableArray *channels = dict.allValues[0];
            for (int j = 0; j < channels.count; j++) {
                JBChannel *channel = channels[j];
                if ([channel.name containsString:searchText]) {
                }else{
                    [channels removeObject:channel];
                    if (channels.count == 0) {
                        [self.tableViewDataArray removeObject:dict];
                        i--;
                    }
                    j--;
                }
            }

        }
    }else{
        self.tableViewDataArray = [JBDatabase getSortedDevkeyAndChannel];
    }
    [self.channel_tableView reloadData];
}

-(void)searchBarTextDidEndEditing:(UISearchBar *)searchBar{
    searchBar.showsCancelButton = NO;
}


-(BOOL)searchBarShouldBeginEditing:(UISearchBar *)searchBar{
    searchBar.showsCancelButton = YES;
    for (UIView *view in searchBar.subviews[0].subviews) {
        if ([view isKindOfClass:NSClassFromString(@"UINavigationButton")]) {
            UIButton *btn = (UIButton*)view;
            [btn setTitle:@"取消" forState:UIControlStateNormal];
            [btn setTintColor:[UIColor whiteColor]];
        }
    }
    return YES;
}

-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    [searchBar resignFirstResponder];
    self.tableViewContraintBottom.constant = 15;
}

- (IBAction)editBtnPressed:(UIButton *)sender {
    self.isEditing = !self.isEditing;
    [self.channel_tableView reloadData];
}

- (IBAction)scanBtnPressed:(UIButton *)sender {
    UINavigationController *nav = (UINavigationController*)[UIApplication sharedApplication].keyWindow.rootViewController;
    for (UIViewController *con in nav.viewControllers) {
        if ([con isKindOfClass:[JBMessageViewController class]]) {
            JBMessageViewController *mVC = (JBMessageViewController*)con;
            mVC.isSlideOut = YES;
            [mVC slide:nil];
        }
    }
    JBScanViewController *scan  = [JBScanViewController new];
    [nav pushViewController:scan animated:YES];
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

-(UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    JBSlideHeaderView *header = (JBSlideHeaderView*)[tableView headerViewForSection:section];
    if (self.tableViewDataArray.count) {
        if (!header) {
            header = [[NSBundle mainBundle] loadNibNamed:@"JBSlideHeaderView" owner:nil options:nil][0];
        }
        NSString *devkey = [(NSDictionary*)self.tableViewDataArray[section] allKeys].lastObject;
        header.devkey = [JBDatabase getDevkeyInfoWithDevkey:devkey];
    }
    if (!header) {
        header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, 50)];
        header.backgroundColor = [UIColor clearColor];

    }
    return header;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBSlideTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"JBSlideTableViewCell"];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBSlideTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.channel = ((NSArray*)[(NSDictionary*)self.tableViewDataArray[indexPath.section] allValues].lastObject)[indexPath.row];

    self.isEditing ? [cell becomeToEditing] : [cell endToEditing];
    
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 44;
}

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 50;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBMessageViewController *vc = (JBMessageViewController*)JBSharedNavigationController.viewControllers[0];
    [vc slide:nil];
    JBSlideTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    vc.channel = cell.channel;
}

@end
