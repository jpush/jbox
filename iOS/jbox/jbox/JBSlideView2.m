//
//  JBSlideView2.m
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBSlideView2.h"
#import "JBMessageViewController.h"
#import "JBDevManageViewController.h"
#import "JBSlideTableViewCell2.h"
#import "JBScanViewController.h"
#import "JBDatabase.h"

@interface JBSlideView2 ()<UITableViewDelegate, UITableViewDataSource>

- (IBAction)scanBtnPressed:(id)sender;
@property (weak, nonatomic) IBOutlet UITableView *team_tableView;
@property(nonatomic, retain)NSMutableArray *devArray;

@end

@implementation JBSlideView2

- (IBAction)scanBtnPressed:(id)sender {
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

-(NSMutableArray *)devArray{
    if (!_devArray) {
        _devArray = [JBDatabase getDevkeys];
    }
    return _devArray;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    JBSlideTableViewCell2 *cell = [tableView dequeueReusableCellWithIdentifier:@"JBSlideTableViewCell2"];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBSlideTableViewCell2" owner:nil options:nil].lastObject;
    }
    cell.devkey = self.devArray[indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.devArray.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    JBSlideTableViewCell2 *cell = [tableView cellForRowAtIndexPath:indexPath];
    JBDevManageViewController *controller = [[JBDevManageViewController alloc] initWithNibName:@"JBDevManageViewController" bundle:[NSBundle mainBundle]];
    controller.scanedDevkey     = cell.devkey.dev_key;
    JBMessageViewController *vc = ((UINavigationController*)[[UIApplication sharedApplication] keyWindow].rootViewController).viewControllers[0];
    [vc slide:nil];
    [(UINavigationController*)[[UIApplication sharedApplication] keyWindow].rootViewController pushViewController:controller animated:YES];
}


@end
